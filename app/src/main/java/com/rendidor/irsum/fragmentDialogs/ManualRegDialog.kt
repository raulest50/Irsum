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
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.properties.Delegates


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

    var toggle_keyboard_visible = true




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
            if(toggle_keyboard_visible){
                toggle_keyboard_visible = false
                binding.etBusqCodigo.requestFocus()
                binding.alphaNumKeyboardInc.alphaNumKeyboard.visibility = View.GONE
            } else{
                binding.alphaNumKeyboardInc.alphaNumKeyboard.visibility = View.VISIBLE
                toggle_keyboard_visible = true
            }
        }

        binding.btnBuscar.setOnClickListener{
            var tp = "0"
            //imm.hideSoftInputFromWindow(binding.imgbtnShowKeyboard.windowToken, 0)
            binding.alphaNumKeyboardInc.alphaNumKeyboard.visibility = View.GONE
            toggle_keyboard_visible = false
            if(binding.rgTpBusqueda.checkedRadioButtonId == binding.rbtnCodExacto.id) tp="0" // ver index.js en satelink
            if(binding.rgTpBusqueda.checkedRadioButtonId == binding.rbtnUltimosCod.id) tp="2"
            Buscar_Mostrar(binding.etBusqCodigo.text.toString(), tp)
        }

        binding.btnSalir.setOnClickListener{dismiss()}

        setUp_alphanum_keyboard()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etBusqCodigo.requestFocus()
        //imm.showSoftInput(binding.etBusqCodigo, 0)
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

    fun setUp_alphanum_keyboard(){
        var et = binding.etBusqCodigo
        var mayus = binding.alphaNumKeyboardInc.toggbtnMayus.isChecked
        var btn_mayus = binding.alphaNumKeyboardInc.toggbtnMayus
        binding.alphaNumKeyboardInc.btnUno.setOnClickListener{et.setText("${et.text}1")}
        binding.alphaNumKeyboardInc.btnDos.setOnClickListener{et.setText("${et.text}2")}
        binding.alphaNumKeyboardInc.btnTres.setOnClickListener{et.setText("${et.text}3")}
        binding.alphaNumKeyboardInc.btnCuatro.setOnClickListener{et.setText("${et.text}4")}
        binding.alphaNumKeyboardInc.btnCinco.setOnClickListener{et.setText("${et.text}5")}
        binding.alphaNumKeyboardInc.btnSeis.setOnClickListener{et.setText("${et.text}6")}
        binding.alphaNumKeyboardInc.btnSiete.setOnClickListener{et.setText("${et.text}7")}
        binding.alphaNumKeyboardInc.btnOcho.setOnClickListener{et.setText("${et.text}8")}
        binding.alphaNumKeyboardInc.btnNueve.setOnClickListener{et.setText("${et.text}9")}
        binding.alphaNumKeyboardInc.btnCero.setOnClickListener{et.setText("${et.text}0")}

        binding.alphaNumKeyboardInc.btnQ.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}Q") else et.setText("${et.text}q")}
        binding.alphaNumKeyboardInc.btnW.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}W") else et.setText("${et.text}w")}
        binding.alphaNumKeyboardInc.btnE.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}E") else et.setText("${et.text}e")}
        binding.alphaNumKeyboardInc.btnR.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}R") else et.setText("${et.text}r")}
        binding.alphaNumKeyboardInc.btnT.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}T") else et.setText("${et.text}t")}
        binding.alphaNumKeyboardInc.btnY.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}Y") else et.setText("${et.text}y")}
        binding.alphaNumKeyboardInc.btnU.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}U") else et.setText("${et.text}u")}
        binding.alphaNumKeyboardInc.btnI.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}I") else et.setText("${et.text}i")}
        binding.alphaNumKeyboardInc.btnO.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}O") else et.setText("${et.text}o")}
        binding.alphaNumKeyboardInc.btnP.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}P") else et.setText("${et.text}p")}

        binding.alphaNumKeyboardInc.btnA.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}A") else et.setText("${et.text}a")}
        binding.alphaNumKeyboardInc.btnS.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}S") else et.setText("${et.text}s")}
        binding.alphaNumKeyboardInc.btnD.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}D") else et.setText("${et.text}d")}
        binding.alphaNumKeyboardInc.btnF.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}F") else et.setText("${et.text}f")}
        binding.alphaNumKeyboardInc.btnG.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}G") else et.setText("${et.text}g")}
        binding.alphaNumKeyboardInc.btnH.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}H") else et.setText("${et.text}h")}
        binding.alphaNumKeyboardInc.btnJ.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}J") else et.setText("${et.text}j")}
        binding.alphaNumKeyboardInc.btnK.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}K") else et.setText("${et.text}k")}
        binding.alphaNumKeyboardInc.btnL.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}L") else et.setText("${et.text}l")}
        binding.alphaNumKeyboardInc.btnEgne.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}Ñ") else et.setText("${et.text}ñ")}

        binding.alphaNumKeyboardInc.btnZ.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}Z") else et.setText("${et.text}z")}
        binding.alphaNumKeyboardInc.btnX.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}X") else et.setText("${et.text}x")}
        binding.alphaNumKeyboardInc.btnC.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}C") else et.setText("${et.text}c")}
        binding.alphaNumKeyboardInc.btnV.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}V") else et.setText("${et.text}v")}
        binding.alphaNumKeyboardInc.btnB.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}B") else et.setText("${et.text}b")}
        binding.alphaNumKeyboardInc.btnN.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}N") else et.setText("${et.text}n")}
        binding.alphaNumKeyboardInc.btnM.setOnClickListener{if(btn_mayus.isChecked) et.setText("${et.text}M") else et.setText("${et.text}m")}

        binding.alphaNumKeyboardInc.btnBack.setOnClickListener{
            try{ et.setText(et.text.subSequence(0, et.text.length-1))}
        catch (e: IndexOutOfBoundsException){}
        }

        binding.alphaNumKeyboardInc.btnEspacio.setOnClickListener{et.setText("${et.text} ")}
        binding.alphaNumKeyboardInc.btnClear.setOnClickListener({et.setText("")})

        //binding.alphaNumKeyboardInc.btnClear.setOnClickListener{et.setText("")}
    }


}