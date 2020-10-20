package com.rendidor.irsum.Definiciones

/**
 * PesoUnitario:
 * solo tiene sentido si fraccionable=true. es el peso en gramos del producto
 * el cual se usa en regla de 3 con el precio de venta para calcular
 * el valor correspondiente a la fraccion. Ej. frijol por libra 450g
 * a 4000. si este producto se establece como fraccionable entonces
 * el software permitira que se registren 225g de frijol en una venta.
 * con el peso unitario se calcularia el valor correspondiente a cobrar
 * y descontaria 0.5 en stock.
 */
class Producto(val _id:String, // el codigo del producto
               val descripcion:String, // nombre del producto
               val costo:Int, // precio de costo
               val pv_mayor:Int, // precio de venta mayorista
               val pv_publico:Int, // precio de venta al publico
               val iva:Double, // si no tiene iva se pone 0
               val last_updt:String, // fecha de la ultima actualizacion en formato String
               val keywords:String, // palabras claves para asistir a la busqueda y/o descripcion del producto
               val fraccionable:Boolean, // determina si el producto se puede vender por partes o no
               val PesoUnitario:Int, // peso o cantidad de unidades que conforman el producto
               val grupo:String
) {

    /**
     * En kotlin no hay variables ni metodos estaticos toca usar companion objects
     */
    companion object{
        val NORMAL = "normal"
        val STK_PRIOR = "prioritario"
    }
}