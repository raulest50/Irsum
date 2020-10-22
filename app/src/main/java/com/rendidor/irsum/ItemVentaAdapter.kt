package com.rendidor.irsum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.databinding.ItemVentaBinding
import java.lang.IndexOutOfBoundsException
import java.util.*

/**
 * listaCompra: LinkedList que contiene todos los ItemVenta correpsondientes a una operacion de Venta
 *
 * ntFragment()->Unit: funcion callbak, que es pasada por el parent fragment. cuando se modifica la
 * cantidad de un ItemVenta, La suma del total de la venta debe actualizarse. Este callback es pasado
 * por el fragmento padre y el Adapter lo invoca cuando hay cambios en listaCompra.
 */
class ItemVentaAdapter(var listaCompra : LinkedList<ItemVenta>, var ntFragment:()->Unit):RecyclerView.Adapter<ItemVentaAdapter.ItemVentaViewHolder>(){


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
        try{ // cuando se oprime el boton de remover varias veces puede ocurrir indexOutofBounds
            listaCompra.removeAt(i)
            notifyItemRemoved(i)
            ntFragment() // se actualiza el label, suma_ListaCompra y se notifica orondo por websocket
        } catch (e:IndexOutOfBoundsException){ } //do nothing just to avoid crash
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
            if(!item.isFraccionable()) binding.imgbtnFraccionar.visibility = View.GONE
            else binding.imgbtnFraccionar.visibility = View.VISIBLE
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
            ntFragment()
        }

    }

}