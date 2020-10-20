package com.rendidor.irsum

import android.content.Context
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
import androidx.core.text.set
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.databinding.FragmentHomeBinding
import com.rendidor.irsum.remote.HttpIrsumReqs
import com.rendidor.irsum.remote.SatelinkFinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import java.lang.Exception
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


    private lateinit var viewAdapter: ItemVentaAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var listaCompra : LinkedList<ItemVenta>

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

        listaCompra = LinkedList() // LinkedList para usar addFirst(), ArrayList solo tiene add()

        //recycler view list initialization
        viewManager = LinearLayoutManager(this.activity)
        viewAdapter = ItemVentaAdapter(listaCompra)

        binding.recyclerViewListaCompra.layoutManager = viewManager
        binding.recyclerViewListaCompra.adapter = viewAdapter

        sp = this.buildSoundPool()

        act = this.activity as MainActivity
        prefloader = PrefLoader(act)
        act.binding.btnImprimir.setOnClickListener({

        })

        return view
    }


    /**
     * cargar preferencias aqui
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
     * https://stackoverflow.com/questions/47298935/handling-enter-key-on-edittext-kotlin-android
     * con argumento false, el campo de busqueda no realizara ninguna accion al hacer enter
     * con true se realizara una busqueda de producto con el texto en el campo de busqueda
     * al detectar enter
     */
    fun EtCodigoListenerEnabled(enable:Boolean){
        if(enable){
            binding.etCodigo.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == 66 && !binding.etCodigo.text.equals(""))
                    BuscarProducto(binding.etCodigo.text.toString())
                return@OnKeyListener true
            })
        } else binding.etCodigo.setOnKeyListener(null)
    }

    fun BuscarProducto(b:String){
        // se inhabilita escuhca de enters en el campo de texto
        EtCodigoListenerEnabled(false)
        lifecycleScope.launch {
            val satelink_ip = prefloader.getUsedSatelinkIp()
            val busqueda:String = binding.etCodigo.text.toString()

            val lit = withContext(Dispatchers.IO){HttpIrsumReqs().ProductoHttpRequest(satelink_ip, "0", busqueda)}
            if(lit.isEmpty()) {
                sp.play(1, 1F, 1F, 0, 0, 1F)

                GenericDialogs.ShowDiaglog("Producto No Encontrado",
                        "Es posible que el producto no este codificado o que haya ocurrido un error \n " +
                                "con el lector, intente escanenando nuevamente. Si el problema persiste comuniquese \n " +
                                "con la administracion", act)
            }
            else{
                listaCompra.addFirst(lit[0])
                viewAdapter.notifyDataSetChanged()
            }
            EtCodigoListenerEnabled(true) // se rehabilita el listener
            binding.etCodigo.setText("")
            binding.etCodigo.requestFocus()
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
            act.runOnUiThread({Toast.makeText(act, ipServer, Toast.LENGTH_SHORT).show()})

        }

        override fun onTimeOut() {
            act.runOnUiThread({Toast.makeText(act, "Servidor no encontrado", Toast.LENGTH_SHORT).show()})
        }

        override fun onError(e: Exception?) {
            act.runOnUiThread({Toast.makeText(act, e?.message, Toast.LENGTH_SHORT).show()})
        }

        override fun onNotFound() {
            act.runOnUiThread({Toast.makeText(act, "datos recibidos no coinciden con satelink", Toast.LENGTH_LONG).show()})
        }
    }


}