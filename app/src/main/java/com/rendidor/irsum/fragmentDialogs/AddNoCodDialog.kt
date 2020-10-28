package com.rendidor.irsum.fragmentDialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.Definiciones.Producto
import com.rendidor.irsum.HomeFragment
import com.rendidor.irsum.databinding.FragmentAddNoCodDialogBinding
import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException


/**
 * A simple [Fragment] subclass.
 * Use the [AddNoCodDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddNoCodDialog(var homeFragment: HomeFragment): DialogFragment() {

    private var _binding: FragmentAddNoCodDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAddNoCodDialogBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        isCancelable = true

        var etPrecioV:EditText = binding.etPrecioV
        // cada boton agrega un digito a etCantidad como en una calculadora
        binding.btnUno.setOnClickListener{etPrecioV.setText("${etPrecioV.text}1")}
        binding.btnDos.setOnClickListener{etPrecioV.setText("${etPrecioV.text}2")}
        binding.btnTres.setOnClickListener{etPrecioV.setText("${etPrecioV.text}3")}
        binding.btnCuatro.setOnClickListener{etPrecioV.setText("${etPrecioV.text}4")}
        binding.btnCinco.setOnClickListener{etPrecioV.setText("${etPrecioV.text}5")}
        binding.btnSeis.setOnClickListener{etPrecioV.setText("${etPrecioV.text}6")}
        binding.btnSiete.setOnClickListener{etPrecioV.setText("${etPrecioV.text}7")}
        binding.btnOcho.setOnClickListener{etPrecioV.setText("${etPrecioV.text}8")}
        binding.btnNueve.setOnClickListener{etPrecioV.setText("${etPrecioV.text}9")}
        binding.btnCero.setOnClickListener{etPrecioV.setText("${etPrecioV.text}0")}

        binding.btnClear.setOnClickListener{etPrecioV.setText("")}

        binding.btnBackspace.setOnClickListener{
            try{ etPrecioV.setText(etPrecioV.text.subSequence(0, etPrecioV.text.length-1))}
            catch (e: IndexOutOfBoundsException){}
        }

        binding.btnCancel.setOnClickListener{
            dismiss()
        }

        binding.btnOk.setOnClickListener{
            try{
                var pv = etPrecioV.text.toString().toInt()
                var p = Producto("_NaN", "Ingresado sin codigo", pv-1, pv, pv,
                        0.0, "", "", false, 1, "")
                var itventa = ItemVenta(p, 1, pv, false)
                homeFragment.itemVentaAdapter.AgregarItemVenta(itventa)
                dismiss()
            } catch (e:NumberFormatException){}
        }

        return view
    }

}