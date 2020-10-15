package com.rendidor.irsum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.databinding.ItemVentaBinding
import java.util.*

class ItemVentaAdapter(var listaCompra : LinkedList<ItemVenta>):RecyclerView.Adapter<ItemVentaAdapter.ItemVentaViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVentaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemVentaViewHolder(layoutInflater.inflate(R.layout.item_venta, parent, false))
    }

    override fun onBindViewHolder(holder: ItemVentaViewHolder, position: Int) {
        holder.render(listaCompra[position])
    }

    override fun getItemCount(): Int {
        return listaCompra.size
    }

    fun removeItem(i:Int){
        listaCompra.removeAt(i)
        notifyItemRemoved(i)
    }


    inner class ItemVentaViewHolder(var view: View):RecyclerView.ViewHolder(view){

        fun render(item:ItemVenta){
            val binding:ItemVentaBinding = ItemVentaBinding.bind(view)
            binding.tvDescripcion.text = item.getDescripcion()
            binding.tvPventa.text = Integer.toString(item.getPVenta())
            binding.tvSubtotal.text = Integer.toString(item.getSubTotal())
            binding.btnCantidad.text = Integer.toString(item.getCantidad())
            binding.tvSubtotal.text = Integer.toString(item.getSubTotal())
            binding.btnCantidad.setOnClickListener({dialogModCantidad()})
            binding.btnAddOne.setOnClickListener({addOne(item, binding)})
            binding.btnMinusOne.setOnClickListener({minusOne(item, binding)})
        }

        private fun dialogModCantidad() {
            TODO("Not yet implemented")
        }

        fun minusOne(item:ItemVenta, binding:ItemVentaBinding){
            if(item.getCantidad() >=2){
                item.Add2Cantidad(-1)
                updateFieldViews(item, binding)
            } else{ // en caso que cantidad sea 1, al resta queda 0, es decir que se remueve de la lista
                this@ItemVentaAdapter.removeItem(this.adapterPosition)
            }
        }

        fun addOne(item:ItemVenta, binding:ItemVentaBinding){
            item.Add2Cantidad(1)
            updateFieldViews(item, binding)
        }

        fun updateFieldViews(item:ItemVenta, binding:ItemVentaBinding){
            binding.btnCantidad.text = Integer.toString(item.getCantidad())
            binding.tvSubtotal.text = Integer.toString(item.getSubTotal())
        }

    }

}