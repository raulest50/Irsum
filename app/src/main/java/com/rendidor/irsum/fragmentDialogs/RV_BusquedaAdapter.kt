package com.rendidor.irsum.fragmentDialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.R
import com.rendidor.irsum.databinding.ItemBusquedaBinding
import java.util.*

class RV_BusquedaAdapter(var manualRegDialog: ManualRegDialog):RecyclerView.Adapter<RV_BusquedaAdapter.ItemVentaViewHolder>(){

    private var listaResult = LinkedList<ItemVenta>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVentaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemVentaViewHolder(layoutInflater.inflate(R.layout.item_busqueda, parent, false))
    }

    override fun onBindViewHolder(holder: ItemVentaViewHolder, position: Int) {
        holder.render(listaResult[position])
    }

    override fun getItemCount(): Int {
        return listaResult.size
    }

    fun setListaBusqueda(lista: LinkedList<ItemVenta>){
        listaResult = lista
        notifyDataSetChanged()
    }

    inner class ItemVentaViewHolder(var view: View):RecyclerView.ViewHolder(view){

        fun render(item:ItemVenta){
            val binding: ItemBusquedaBinding = ItemBusquedaBinding.bind(view)
            binding.tvCodigo.text = item.getProducto_id()
            binding.tvDescripcion.text = item.getDescripcion()
            binding.tvPventa.text = item.getPVenta().toString()
            binding.tvLastUpdt.text = item.getProducto_last_updt()
            binding.btnSend.setOnClickListener{sendItem2ListaCompra(item)}
        }

        fun sendItem2ListaCompra(item:ItemVenta){
            manualRegDialog.homeFragment.itemVentaAdapter.AgregarItemVenta(item)
            manualRegDialog.dismiss()
        }
    }
}