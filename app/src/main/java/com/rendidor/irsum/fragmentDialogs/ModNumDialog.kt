package com.rendidor.irsum.fragmentDialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.ItemVentaAdapter
import com.rendidor.irsum.databinding.FragmentModNumDialogBinding
import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException


class ModNumDialog(var item:ItemVenta, var itemIndex:Int, var adapter:ItemVentaAdapter): DialogFragment() {

    private var _binding: FragmentModNumDialogBinding? = null
    private val binding get() = _binding!!

    lateinit var elContexto: Context



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentModNumDialogBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        isCancelable=false // no se puede cerrar tocando afuera de la ventana

        val etCantidad= binding.etCantidad

        // cada boton agrega un digito a etCantidad como en una calculadora
        binding.btnUno.setOnClickListener{etCantidad.setText("${etCantidad.text}1")}
        binding.btnDos.setOnClickListener{etCantidad.setText("${etCantidad.text}2")}
        binding.btnTres.setOnClickListener{etCantidad.setText("${etCantidad.text}3")}
        binding.btnCuatro.setOnClickListener{etCantidad.setText("${etCantidad.text}4")}
        binding.btnCinco.setOnClickListener{etCantidad.setText("${etCantidad.text}5")}
        binding.btnSeis.setOnClickListener{etCantidad.setText("${etCantidad.text}6")}
        binding.btnSiete.setOnClickListener{etCantidad.setText("${etCantidad.text}7")}
        binding.btnOcho.setOnClickListener{etCantidad.setText("${etCantidad.text}8")}
        binding.btnNueve.setOnClickListener{etCantidad.setText("${etCantidad.text}9")}
        binding.btnCero.setOnClickListener{
            if(!etCantidad.text.equals("0")) etCantidad.setText("${etCantidad.text}0")
        }

        binding.btnClear.setOnClickListener{etCantidad.setText("")}

        binding.btnBackspace.setOnClickListener{
            try{ etCantidad.setText(etCantidad.text.subSequence(0, etCantidad.text.length-1))}
            catch (e:IndexOutOfBoundsException){}
        }

        binding.btnCancel.setOnClickListener{ dismiss() }

        /**
         * Se hace el cambio en el item seleccinado y se notifica el cambio al adaptador.
         * si se pone 0 en cantidad entonces el item se remueve.
         */
        binding.btnOk.setOnClickListener{
            var N:Int
            try {
                N = etCantidad.text.toString().toInt()
                if(N==0) adapter.removeItem(itemIndex)
                if(N>=1){
                    item.setCantidad(etCantidad.text.toString().toInt())
                }
                adapter.AplicarCambios()
                dismiss()
            } catch (e:NumberFormatException){}
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    /**
     * se ejecuta primero que onCreateView, se aprovecha que por defecto recive el contexto para
     * inicializarlo en este fragmento
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.elContexto = context
    }

}