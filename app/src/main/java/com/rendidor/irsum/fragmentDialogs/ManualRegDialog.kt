package com.rendidor.irsum.fragmentDialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rendidor.irsum.HomeFragment
import com.rendidor.irsum.PrefLoader
import com.rendidor.irsum.databinding.FragmentManualRegDialogBinding
import com.rendidor.irsum.remote.HttpIrsumReqs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


/**
 * En el redmi8 (android 9) funciona normal del dialog fragment con constrained Layout y con
 * FrameLayout, pero en android 4.4 solo funciono con FrameLayout. con constrained layout el
 * vio se veia por unos milisegundos y desaparecia, quedando el dialog vacio. al cambiar por
 * FrameLayout desaparecio el problema en la tabletlenovo.
 * (esto es sobre el xml del fragment)
 */
class ManualRegDialog(var homeFragment: HomeFragment,var prefloader: PrefLoader): DialogFragment() {

    private var _binding: FragmentManualRegDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var elContexto:Context
    private lateinit var imm:InputMethodManager

    private lateinit var rvBusquedaAdapter: RV_BusquedaAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    var toggle_keyboard = false


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentManualRegDialogBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        isCancelable = true

        viewManager = LinearLayoutManager(this.activity)
        rvBusquedaAdapter = RV_BusquedaAdapter(this)

        binding.recyclerViewListaBusqueda.layoutManager = viewManager
        binding.recyclerViewListaBusqueda.adapter = rvBusquedaAdapter

        binding.imgbtnShowKeyboard.setOnClickListener{
            if(toggle_keyboard){
                toggle_keyboard = false
                binding.etBusqCodigo.requestFocus()
                imm.showSoftInput(binding.etBusqCodigo, 0)
            } else{
                imm.hideSoftInputFromWindow(binding.etBusqCodigo.windowToken, 0)
                toggle_keyboard = true
            }
        }

        binding.btnBuscar.setOnClickListener{
            var tp = "0"
            imm.hideSoftInputFromWindow(binding.imgbtnShowKeyboard.windowToken, 0)
            if(binding.rgTpBusqueda.checkedRadioButtonId == binding.rbtnCodExacto.id) tp="0" // ver index.js en satelink
            if(binding.rgTpBusqueda.checkedRadioButtonId == binding.rbtnUltimosCod.id) tp="2"
            Buscar_Mostrar(binding.etBusqCodigo.text.toString(), tp)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etBusqCodigo.requestFocus()
        imm.showSoftInput(binding.etBusqCodigo, 0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        elContexto = context
        imm = elContexto.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }


    fun Buscar_Mostrar(busqueda:String, tp:String) {
        viewLifecycleOwner.lifecycleScope.launch {
            var lit = withContext(Dispatchers.IO) {
                HttpIrsumReqs().ProductoHttpRequest(prefloader.getUsedSatelinkIp(), tp, busqueda)
            }
            rvBusquedaAdapter.setListaBusqueda(LinkedList(lit))
        }
    }


}