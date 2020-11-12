package com.rendidor.irsum


import android.content.Context.WIFI_SERVICE
import android.media.AudioManager
import android.media.SoundPool
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.Definiciones.ListaVentaNotifyChangeMsg
import com.rendidor.irsum.databinding.FragmentHomeBinding
import com.rendidor.irsum.fragmentDialogs.AddNoCodDialog
import com.rendidor.irsum.fragmentDialogs.CodExactDialog
import com.rendidor.irsum.fragmentDialogs.GenericDialogs
import com.rendidor.irsum.fragmentDialogs.ManualRegDialog
import com.rendidor.irsum.remote.HttpIrsumReqs
import com.rendidor.irsum.remote.MqttIrsumClient
import com.rendidor.irsum.remote.SatelinkFinder
import kotlinx.coroutines.*
import org.eclipse.paho.client.mqttv3.*
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException;
import java.lang.reflect.InvocationTargetException
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private lateinit var act:MainActivity

    // this property is only valid between onCreateView and onDestroyView
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    lateinit var itemVentaAdapter: ItemVentaAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var sp:SoundPool

    private lateinit var prefloader:PrefLoader

    private lateinit var mqttIrsumClient: MqttIrsumClient

    private lateinit var mqttJob: Job
    /**
     * al ejecutarse este metodo ya se asegura que la actividad asociada ya se inicio.
     * contrario del onCreate del fragmento, que podria ejecutarse antes que de el onCreate
     * de la actividad haya terminado.
     */
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //View binding framework - _binding replace findViewById()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        act = this.activity as MainActivity


        //recycler view list initialization
        viewManager = LinearLayoutManager(this.activity)
        itemVentaAdapter = ItemVentaAdapter(binding.tvSumaVenta, { ntFragment() }, act.supportFragmentManager)

        binding.recyclerViewListaCompra.layoutManager = viewManager
        binding.recyclerViewListaCompra.adapter = itemVentaAdapter

        sp = this.buildSoundPool()

        prefloader = PrefLoader(act)

        act.binding.btnImprimir.setOnClickListener{
            viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO){HttpIrsumReqs().printRemisionPOST(prefloader.getUsedSatelinkIp(), itemVentaAdapter.getCopiaListaCompra())}
            }
        }

        binding.btnAddProdManual.setOnClickListener{
            var manualRegisDialog = ManualRegDialog(this, prefloader)
            manualRegisDialog.show(act.supportFragmentManager, "Codigo Manualmente")
        }

        binding.btnNocod.setOnClickListener{
            var addNoCodDialog = AddNoCodDialog(this)
            addNoCodDialog.show(act.supportFragmentManager, "Agregar item no codificado")
        }

        binding.imgbtnClear.setOnClickListener{
            if(itemVentaAdapter.itemCount>0)
                GenericDialogs.ConfirmationDiaglo("Esta Seguro de borrar la lista de compra?", act, {
                    itemVentaAdapter.ClearListaCompra()
                })
        }

        binding.imgbtnCodExact.setOnClickListener{
            var codExactDialog = CodExactDialog(this)
            codExactDialog.show(act.supportFragmentManager, "Busqueda Codigo Exacto")
        }

        binding.btnRegistrar.setOnClickListener{
            
        }

        return view
    }


    /**
     * cargar preferencias aqui
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        InfLoopEtReqFocus()
        EtCodigoListenerEnabled(true)

        if(prefloader.sp.getBoolean(PrefLoader.autofind_ip, false)){ // si autoip activado
            val st = CustomSFinder(10000, act.getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager)
            st.Scan() // cada que se inicia el fragmento se busca la ip del servidor
        }

        // se conecta al Broker si mqtt habilitado en config
        if(prefloader.sp.getBoolean(PrefLoader.mqtt_enabled, false)) {
            mqttJob = viewLifecycleOwner.lifecycleScope.launch { withContext(Dispatchers.IO) { IniciarMqtt() } }
        }

        // se restaura la lista de venta
        try{ RestaurarLista(savedInstanceState) } catch (e:NullPointerException){}

    }

    /**
     * cuando se hace una rotacion de pantalla este metodo en algun momento del cierre del fragmento
     * se ejecuta. en outState se guarda en formato json la lista de ventas, ya que si se guarda
     * directamente, es necesario implementar una interfaz parcelable. por lo anterior pense que
     * era mas practico guardarlo como json. Esto es para la persistencia en caso de rotacion de la
     * pantalla. Cuando se cambia de actividad este metodo no se dispara.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("save_list", Gson().toJson(itemVentaAdapter.getCopiaListaCompra()))
        super.onSaveInstanceState(outState)
    }


    /**
     * se ejecuta cuando se navega a otro fragmento y cuando se rota pantalla.
     * se guarda la lista de venta en la actividad padre y se cancela la corrutina del mqtt
     * para evitar que queden conexiones abiertas que interfieran con nuevas conexiones en el futuro.
     */
    override fun onDestroyView() {
        act.saved_list = itemVentaAdapter.getCopiaListaCompra()
        // para evitar crash si se intenta desconectar antes de existir una conexion mqtt

        if(this::mqttIrsumClient.isInitialized && this::mqttJob.isInitialized){
            mqttJob.cancel()
            try { if(mqttIrsumClient.isConnected){ mqttIrsumClient.Desconectar() } } catch (e:InvocationTargetException){}
        }
        super.onDestroyView()
        _binding = null
    }


    /**
     * para lograr persistencia de los datos del recycler view ante la rotacion se usa el metodo
     * onSaveInstanceState para guardar antes de la destruccion del fragmento. en onCreateView o en
     * onViewCreated se reconstruye leyendo nuevamente la informacion guardada en json en el Bundle.
     * Cuando se navega entre fragmentos onSaveInstanceState no se ejecuta por lo que en su lugar
     * se guarda la lista en una variable dentro de la actividad padre del fragmento.
     * por eso la restauracion tiene un if para para usar un metodo u otro segun el evento.
     */
    fun RestaurarLista(b:Bundle?){
        if(act.saved_list.isEmpty()){
            try {
                var lista_itv = LinkedList<ItemVenta>()
                var json_lista: JSONArray = JSONParser().parse(b?.getString("save_list")) as JSONArray
                for (x in json_lista) {
                    lista_itv.addLast(ItemVenta(x as JSONObject))
                }
                itemVentaAdapter.RestaurarListaItemVenta(lista_itv)
            } catch (e:NullPointerException){} // en caso de que el key no exista en bundle evitar crash
        } else {
            itemVentaAdapter.RestaurarListaItemVenta(act.saved_list)
        }
    }

    /**
     * sirve para instanciar un Obj para comunicaicon Mqtt y se conecta al broker.
     * la funcion de conectar puede bloquear la UI.
     */
    suspend fun IniciarMqtt(){
        try {
            mqttIrsumClient = MqttIrsumClient(prefloader.sp.getString(PrefLoader.user_defined_name, "default"))
            mqttIrsumClient.Conectar(PushCallback(), prefloader.getUsedSatelinkIp()) // se conecta al broker
        } catch (e: MqttException){
            Log.println(Log.ERROR, "MqttComm", e.message)
        }
    }


    /**
     * https://stackoverflow.com/questions/47298935/handling-enter-key-on-edittext-kotlin-android
     * con argumento false, el campo de busqueda no realizara ninguna accion al hacer enter
     * con true se realizara una busqueda de producto con el texto en el campo de busqueda
     * al detectar enter.
     *
     * si en else no hace return false (event not sonsumed) entonces en la tab lenovo (android 4.4)
     * el EditText no le entran numeros. pero en el redmi si funciona asi se retorne siempre true.
     * por compatibilidad se retorna true en caso de enter y false en caso de cualquier otro key
     * al parecer indicar que el evento se consume puede inhibir el ingreso de algunos caracteres
     */
    fun EtCodigoListenerEnabled(enable: Boolean){
        if(enable){
            binding.etCodigo.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == 66 && !binding.etCodigo.text.equals("")) {
                    BuscarProducto(binding.etCodigo.text.toString())
                    return@OnKeyListener true // indica que el evento si se consumio
                } else return@OnKeyListener false // el evento no se consume
            })
        } else binding.etCodigo.setOnKeyListener(null)
    }


    /**
     * Lanza una corrutina para hacer el httprequest.
     * las corrutinas alternan de manera automatica la ejecucion entre el hilo principal y otros
     * hilos de forma que no se bloquee la GUI, de manera trasparente para el programador
     */
    fun BuscarProducto(busqueda: String){
        EtCodigoListenerEnabled(false) // se inhabilita escuhca de enters en el campo de texto
        binding.etCodigo.setText("")

        viewLifecycleOwner.lifecycleScope.launch {
            val lit = withContext(Dispatchers.IO){
                HttpIrsumReqs().ProductoHttpRequest(prefloader.getUsedSatelinkIp(), "0", busqueda)
            }

            if(lit.isEmpty()) {
                sp.play(1, 1F, 1F, 0, 0, 1F)

                GenericDialogs.ShowDiaglog("Producto No Encontrado",
                        "Es posible que el producto no este codificado o que haya ocurrido un error \n " +
                                "con el lector, intente escanenando nuevamente. Si el problema persiste comuniquese \n " +
                                "con la administracion", act)
            }
            else{ itemVentaAdapter.AgregarItemVenta(lit[0]) }
            // un lector de codigo de barras puede generar varios enters, con este delay
            // se reactiva el listening al enter del editText 100ms despues para eliminar la posibilidad
            // de un trigger indeseado de BuscarProducto()
            delay(100)
            EtCodigoListenerEnabled(true) // se rehabilita el listener
            binding.etCodigo.requestFocus()
        }
    }


    /**
     * en un lazo infinito hace un request del editText (para ingresar el codigo del producto)
     * cada segundo. Esta funcionalidad ser una decision muy asertada en la version anterio de la
     * aplicacion cuando se usaba java.
     *
     * https://www.youtube.com/watch?v=ShNhJ3wMpvQ
     * Las corutinas permiten lograr las mismas tareas que se hacen con thread pero con ventajas
     * adicionales. Las corutinas pueden cambiar de contexto o hilo durante la ejecucion y son
     * mucho mas ligeras que un hilo. Mientras 1000 corutinas son faciles para la cpu, 1000 hilos
     * harian crashear muchos dispositivos con cpus normales.
     */
    private fun InfLoopEtReqFocus(){
        viewLifecycleOwner.lifecycleScope.launch {
            while(true){
                binding.etCodigo.requestFocus()
                delay(1000)
            }
        }
    }

    /**
     * construye un obj para reproducir mp3 files
     * https://stackoverflow.com/questions/39184157/android-why-is-the-constructor-for-soundpool-deprecated
     */
    fun buildSoundPool():SoundPool{
        lateinit var s:SoundPool
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            s = SoundPool.Builder().setMaxStreams(10).build()
        } else { // para versiones inferiores a lollipop
            s = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        }
        s.load(this.context, R.raw.code_not_found, 1)
        return s
    }

    /**
     * Realizar alguna accion cada que ocurra un cambio en listaCompra
     */
    fun ntFragment(){
        try {
            if (mqttIrsumClient.isConnected) {
                mqttIrsumClient.Send(Gson().toJson(
                        ListaVentaNotifyChangeMsg(mqttIrsumClient.clientId, itemVentaAdapter.getCopiaListaCompra())))
            }
        } catch (e:Exception){}
    }

    inner class PushCallback:MqttCallbackExtended{
        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            viewLifecycleOwner.lifecycleScope.launch { withContext(Dispatchers.IO) { ntFragment() } }
        }
        override fun connectionLost(cause: Throwable?) {
        }
        override fun messageArrived(topic: String?, message: MqttMessage?) {
        }
        override fun deliveryComplete(token: IMqttDeliveryToken?) {
        }
    }

    /**
     * se personaliza satelink finder que provee el metodo scan para buscar la ip del servidor.
     * cuando scan() termina de manera exitosa, se ejecuta onSatelinkFound()
     */
    inner class CustomSFinder(TIME_OUT_MILLIS: Int, wifiMgr: WifiManager):SatelinkFinder(TIME_OUT_MILLIS, wifiMgr){
        override fun onSatelinkFound(ipServer: String?) { // en caso de encontrar la ip del servidor
            prefloader.edit.putString(PrefLoader.autoip_value, ipServer)
            prefloader.edit.commit() // se actualiza el valor en preferencias
            act.runOnUiThread({ Toast.makeText(act, ipServer, Toast.LENGTH_SHORT).show() })
        }
        // se ejcuta si no se descubre la ip de satelink en menos de TIME_OUT_MILLIS (tiempo en ms)
        override fun onTimeOut() {
            act.runOnUiThread({ Toast.makeText(act, "Servidor no encontrado", Toast.LENGTH_SHORT).show() })
        }
        // se ejecuta si ocurre una excepcion fatal durante la busqueda de la ip del servidor
        override fun onError(e: Exception?) {
            act.runOnUiThread({ Toast.makeText(act, e?.message, Toast.LENGTH_SHORT).show() })
        }
        //se ejecuta si se tiene una respuesta en menos de TIME_OUT_MILLIS, pero no es de satelink
        //entonces no ocurre timeout pero tampoco se sabe la ip de satelink.
        override fun onNotFound() {
            act.runOnUiThread({ Toast.makeText(act, "datos recibidos no coinciden con satelink", Toast.LENGTH_LONG).show() })
        }
    }


}