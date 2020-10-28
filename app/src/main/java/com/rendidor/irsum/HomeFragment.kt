package com.rendidor.irsum


import android.content.Context.WIFI_SERVICE
import android.media.AudioManager
import android.media.SoundPool
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rendidor.irsum.databinding.FragmentHomeBinding
import com.rendidor.irsum.fragmentDialogs.AddNoCodDialog
import com.rendidor.irsum.fragmentDialogs.GenericDialogs
import com.rendidor.irsum.fragmentDialogs.ManualRegDialog
import com.rendidor.irsum.remote.HttpIrsumReqs
import com.rendidor.irsum.remote.SatelinkFinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


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
        itemVentaAdapter = ItemVentaAdapter(binding.tvSumaVenta, {ntFragment()}, act.supportFragmentManager)

        binding.recyclerViewListaCompra.layoutManager = viewManager
        binding.recyclerViewListaCompra.adapter = itemVentaAdapter

        sp = this.buildSoundPool()

        prefloader = PrefLoader(act)

        act.binding.btnImprimir.setOnClickListener{
            viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO){HttpIrsumReqs().printRemision(prefloader.getUsedSatelinkIp(), itemVentaAdapter.getCopiaListaCompra())}
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
                    itemVentaAdapter.ClearListaCompra() })
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
            val st = CustomSFinder(7000, act.getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager)
            st.Scan() // cada que se inicia el fragmento se busca la ip del servidor
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
    fun EtCodigoListenerEnabled(enable:Boolean){
        if(enable){
            binding.etCodigo.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == 66 && !binding.etCodigo.text.equals("")){
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
    fun BuscarProducto(busqueda:String){
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

    }

    /**
     * se personaliza satelink finder que provee el metodo scan para buscar la ip del servidor.
     * cuando scan() termina de manera exitosa, se ejecuta onSatelinkFound()
     */
    inner class CustomSFinder(TIME_OUT_MILLIS: Int, wifiMgr: WifiManager):SatelinkFinder(TIME_OUT_MILLIS, wifiMgr){
        override fun onSatelinkFound(ipServer: String?) { // en caso de encontrar la ip del servidor
            prefloader.edit.putString(PrefLoader.autoip_value, ipServer)
            prefloader.edit.commit() // se actualiza el valor en preferencias
            act.runOnUiThread({Toast.makeText(act, ipServer, Toast.LENGTH_SHORT).show()})
        }
        // se ejcuta si no se descubre la ip de satelink en menos de TIME_OUT_MILLIS (tiempo en ms)
        override fun onTimeOut() {
            act.runOnUiThread({Toast.makeText(act, "Servidor no encontrado", Toast.LENGTH_SHORT).show()})
        }
        // se ejecuta si ocurre una excepcion fatal durante la busqueda de la ip del servidor
        override fun onError(e: Exception?) {
            act.runOnUiThread({Toast.makeText(act, e?.message, Toast.LENGTH_SHORT).show()})
        }
        //se ejecuta si se tiene una respuesta en menos de TIME_OUT_MILLIS, pero no es de satelink
        //entonces no ocurre timeout pero tampoco se sabe la ip de satelink.
        override fun onNotFound() {
            act.runOnUiThread({Toast.makeText(act, "datos recibidos no coinciden con satelink", Toast.LENGTH_LONG).show()})
        }
    }


}