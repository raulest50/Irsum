package com.rendidor.irsum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.rendidor.irsum.Definiciones.ItemVenta
import com.rendidor.irsum.databinding.ItemVentaBinding
import com.rendidor.irsum.fragmentDialogs.ModNumDialog
import java.lang.IndexOutOfBoundsException
import java.util.*

/**
 * listaCompra: LinkedList que contiene todos los ItemVenta correpsondientes a una operacion de Venta
 *
 * ntFragment()->Unit: funcion callbak, que es pasada por el parent fragment. cuando se modifica la
 * cantidad de un ItemVenta, La suma del total de la venta debe actualizarse. Este callback es pasado
 * por el fragmento padre y el Adapter lo invoca cuando hay cambios en listaCompra.
 */
class ItemVentaAdapter(var tvSumaVenta:TextView,
                       var ntFragment: () -> Unit,
                       var supportFragmentManager: FragmentManager):
        RecyclerView.Adapter<ItemVentaAdapter.ItemVentaViewHolder>(){

    /**
     * solo se puede alterar lista compra mediante las interfaces proporcionadas por este adapter,
     * centralizando la logica en esta clase y reduciendo la posibilidad errores del programador
     *
     * LinkedList para usar addFirst(), ArrayList solo tiene add()
     */
    private var listaCompra = LinkedList<ItemVenta>()



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
            ActualizarSumaVenta()
            ntFragment() // se actualiza el label, suma_ListaCompra y se notifica orondo por websocket
        } catch (e:IndexOutOfBoundsException){ } //do nothing just to avoid crash
    }

    fun AgregarItemVenta(item:ItemVenta){
        listaCompra.addFirst(item)
        AplicarCambios()
        ntFragment() // EL fragmento reacciona a los cambios de listaCompra
    }

    fun ClearListaCompra(){
        listaCompra.clear()
        AplicarCambios()
        ntFragment() // EL fragmento reacciona a los cambios de listaCompra
    }

    /**
     * Hace la suma de todos los itemVenta en listaCompra y actualiza el correspondiente TextView
     *
     * la variable es it por defecto, para usar nombre personalizdo al estilo java (x)->{ code }
     * usar: listaCompra.forEach{x -> suma+= x.getSubTotal()}
     */
    fun ActualizarSumaVenta(){
        var suma = 0
        listaCompra.forEach{suma+= it.getSubTotal()}
        tvSumaVenta.text = suma.toString()
    }

    fun AplicarCambios(){
        ActualizarSumaVenta() // actualiza suma venta textView
        notifyDataSetChanged() // se dibujan los cambios
        ntFragment()
    }


    // clase que representa cada View en el recyclerView
    inner class ItemVentaViewHolder(var view: View):RecyclerView.ViewHolder(view){

        // es empleado por la api para dibujara cada itemVenta en el recyclerView
        fun render(item:ItemVenta){
            val binding:ItemVentaBinding = ItemVentaBinding.bind(view)
            binding.tvDescripcion.text = item.getDescripcion()
            binding.tvPventa.text = Integer.toString(item.getPVenta())
            binding.tvSubtotal.text = Integer.toString(item.getSubTotal())
            binding.btnCantidad.text = Integer.toString(item.getCantidad())
            binding.tvSubtotal.text = Integer.toString(item.getSubTotal())
            binding.btnCantidad.setOnClickListener({dialogModCantidad(item)})
            binding.btnAddOne.setOnClickListener({addOne(item, binding)})
            binding.btnMinusOne.setOnClickListener({minusOne(item, binding)})
            if(!item.isFraccionable()) binding.imgbtnFraccionar.visibility = View.GONE
            else binding.imgbtnFraccionar.visibility = View.VISIBLE
        }

        // abre un dialogo modal para modificar la cantidad del itemVenta seleccionado
        private fun dialogModCantidad(item:ItemVenta) {
            var modNumDialog = ModNumDialog(item, adapterPosition, this@ItemVentaAdapter)
            modNumDialog.show(supportFragmentManager, "Modificar Cantidad")
        }

        // reduce -1 a la cantidad del itemVenta correspondiente
        fun minusOne(item:ItemVenta, binding:ItemVentaBinding){
            if(item.getCantidad() >=2){
                item.Add2Cantidad(-1)
                updateFieldViews(item, binding)
            } else{ // en caso que cantidad sea 1, al resta queda 0, es decir que se remueve de la lista
                this@ItemVentaAdapter.removeItem(this.adapterPosition)
            }
        }

        // agrega +1 a la cantidad del itemVenta correspondiente
        fun addOne(item:ItemVenta, binding:ItemVentaBinding){
            item.Add2Cantidad(1)
            updateFieldViews(item, binding)
        }

        fun updateFieldViews(item:ItemVenta, binding:ItemVentaBinding){
            binding.btnCantidad.text = Integer.toString(item.getCantidad())
            binding.tvSubtotal.text = Integer.toString(item.getSubTotal())
            AplicarCambios()
        }

    }

}