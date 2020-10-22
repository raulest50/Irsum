package com.rendidor.irsum.fragmentDialogs

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.rendidor.irsum.PrefLoader
import com.rendidor.irsum.databinding.FragmentInfoIpDialogBinding
import com.rendidor.irsum.remote.SatelinkFinder
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 * Use the [InfoIpDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoIpDialog : DialogFragment() {

    private var _binding: FragmentInfoIpDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var elContexto:Context


    private lateinit var pl: PrefLoader

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentInfoIpDialogBinding.inflate(layoutInflater, container, false)
        val view = binding.root



        isCancelable = true // con false no se cierra tocando afuera del dialog.
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pl = PrefLoader(elContexto)
        binding.tvInUseIp.text = pl.getUsedSatelinkIp()
        binding.btnOk.setOnClickListener({
            dismiss()
        })
        binding.btnScan.setOnClickListener({
            var sf = CustomSFinder(10000, elContexto.getSystemService(WIFI_SERVICE) as WifiManager)
            Toast.makeText(elContexto, "buscando servidor...", Toast.LENGTH_SHORT).show()
            sf.Scan()
        })
    }

    /*
     * https://stackoverflow.com/questions/15464263/passing-context-as-argument-of-dialogfragment
     * se ejecuta primero que onCreate
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        elContexto = context
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    inner class CustomSFinder(TIME_OUT_MILLIS: Int, wifiMgr: WifiManager):SatelinkFinder(TIME_OUT_MILLIS, wifiMgr){
        override fun onSatelinkFound(ipServer: String?) {
            pl.edit.putString(PrefLoader.autoip_value, ipServer)
            pl.edit.commit()
            activity?.runOnUiThread {
                binding.tvInUseIp.text = ipServer
                Toast.makeText(elContexto, "Servidor Encontrado: ${ipServer}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onTimeOut() {
            activity?.runOnUiThread({binding.tvInUseIp.text = "no Encontrado"})
        }
        override fun onError(e: Exception?) {
            activity?.runOnUiThread({binding.tvInUseIp.text = e?.message})
        }
        override fun onNotFound() {
            activity?.runOnUiThread({binding.tvInUseIp.text="datos recibidos no coinciden con satelink"})
        }
    }
}