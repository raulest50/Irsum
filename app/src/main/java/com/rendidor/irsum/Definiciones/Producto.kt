package com.rendidor.irsum.Definiciones

class Producto {
    /**
     * equivalente al codigo de barras del producto o solo codigo.
     * pero para mejor claridad se dejo _id que es el nombre por defecto que
     * usa mongo y jongo para la llave primaria. para cambiar el nombre por
     * defecto a uno personalizado toca agregar otra anotacion, entonces preferi
     * dejarlo asi para mas simplisidad.
     */
    lateinit var _id: String

    lateinit var descripcion: String

    var costo = 0

    /**
     * precio de venta para tienda o pormmayor
     */
    var pv_mayor = 0

    /**
     * precio de venta al publico
     */
    var pv_publico = 0

    /**
     * si no tiene iva se pone 0
     */
    var iva: Double = 0.0

    // ultima frecha de actualizacion del producto
    lateinit var last_updt: String

    /** palabras clave que se deseen asignar al producto. se pueden usar para
     * efectos de busqueda o tambien de analisis de productos pero aun no lo he
     * definido, en todo caso deje este atributo para dejar abierta la posibilidad.
     */
    lateinit var keywords: String


    /**
     * indica si el producto se puede dividir para ser vendido. el caso mas
     * comun es el queso cuajada granos en paquetes de libra salchichones etc.
     */
    var fraccionable = false

    /**
     * solo tiene sentido si fraccionable=true. es el peso en gramos del producto
     * el cual se usa en regla de 3 con el precio de venta para calcular
     * el valor correspondiente a la fraccion. Ej. frijol por libra 450g
     * a 4000. si este producto se establece como fraccionable entonces
     * el software permitira que se registren 225g de frijol en una venta.
     * con el peso unitario se calcularia el valor correspondiente a cobrar
     * y descontaria 0.5 en stock.
     */
    var PesoUnitario = 0

    lateinit var grupo: String

    constructor(_id: String, descripcion: String, costo: Int,
                pv_mayor: Int, pv_publico: Int, iva: Double, last_updt: String, keywords: String,
                fraccionable: Boolean, PesoUnitario: Int, grupo: String){
        this._id = _id
        this.descripcion = descripcion
        this.costo = costo
        this.pv_mayor = pv_mayor
        this.pv_publico = pv_publico
        this.iva = iva
        this.last_updt = last_updt
        this.keywords = keywords
        this.fraccionable = fraccionable
        this.PesoUnitario = PesoUnitario
        this.grupo = grupo
    }


    fun getId(): String {
        return _id
    }

    /**
     * En kotlin no hay variables ni metodos estaticos toca usar companion objects
     */
    companion object{
        val NORMAL = "normal"
        val STK_PRIOR = "prioritario"
    }

}