package com.rendidor.irsum

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.Definiciones.Producto
import com.rendidor.irsum.databinding.FragmentHomeBinding
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

        var prod1 : Producto = Producto("T20", "bolsa plastica t20", 1450, 1600,
                1700, 19.0, "", "", false, 1, Producto.NORMAL)

        var prod2 : Producto = Producto("L19", "bolsa aluminizada L19", 1700, 2000,
                2100, 19.0, "", "", false, 1, Producto.NORMAL)

        var prod3 : Producto = Producto("L25", "bolsa aluminizada L25", 2000, 2400,
                2800, 19.0, "", "", false, 1, Producto.NORMAL)

        var itv1 = ItemVenta(prod1, 1, prod1.pv_publico, false)
        var itv2 = ItemVenta(prod2, 1, prod2.pv_publico, false)


        listaCompra = LinkedList()
        listaCompra.addFirst(itv1)
        listaCompra.addFirst(itv2)

        //recycler view list initialization
        viewManager = LinearLayoutManager(this.activity)
        viewAdapter = ItemVentaAdapter(listaCompra)

        binding.recyclerViewListaCompra.layoutManager = viewManager
        binding.recyclerViewListaCompra.adapter = viewAdapter

        act = this.activity as MainActivity
        act.binding.btnImprimir.setOnClickListener({
            //val toast = Toast.makeText(act, "producto agregado a la lista", Toast.LENGTH_SHORT)
            //toast.show()
            listaCompra.addFirst(ItemVenta(prod3, 2, prod3.pv_publico, false))
            viewAdapter.notifyDataSetChanged()
        })

        return view
    }


    /**
     * cargar preferencias aqui
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}