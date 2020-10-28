package com.rendidor.irsum.Definiciones

import kotlin.math.roundToInt

class ItemVenta {

    /**
     * el producto correspondiente a este item de venta.
     * de aqui se toman los datos como codigo nombre costo etc
     * transitorio porque en Orondo no se desea guardar este en mongo
     */
    //@Transient
    private var p: Producto

    /**
     * codigo del producto
     */
    private var producto_id: String

    private var Cantidad = 0

    private var subTotal = 0

    private var fraccionado = false

    /**
     * precio de venta unitario que puede oscilar entre el valor de costo
     * y el precio de venta al publico, segun como se negocee
     */
    private var UnitPrecio = 0


    /**
     * En esta clase si hago uso del esquema tipico java de hacer los
     * atributos privados y solo accecibles desde los metodos provistos por la
     * clase, ya que hay una relacion estricta entre cantidad, subtotal y precio
     * de venta. De esta forma dificulto que por error haga cambios en estas
     * variables que generen inconsistencias. si se cambia
     * el precio de venta o la cantidad obligatoriamente se debe
     * poner el valor correspondiente en el subtotal para mantener la consistencia.
     * @param p
     * @param Cantidad
     * @param UnitPrecio
     * @param fraccionado
     */
    constructor(p: Producto, Cantidad: Int, UnitPrecio: Int, fraccionado: Boolean) {
        this.p = p
        producto_id = p._id
        this.Cantidad = Cantidad
        this.UnitPrecio = UnitPrecio
        this.fraccionado = fraccionado
        RefreshSubTotal()
    }


    fun Add2Cantidad(add: Int) {
        Cantidad += add
        RefreshSubTotal()
    }

    fun setCantidad(c: Int) {
        Cantidad = c
        RefreshSubTotal()
    }

    fun setUnitPrecio(nuevoPrecio: Int) {
        if (nuevoPrecio >= p.costo) {
            UnitPrecio = nuevoPrecio
        } else {
            UnitPrecio = p.costo
        }
        RefreshSubTotal()
    }

    /**
     * se debe invocar despues de cambiar la cantidad o el precio unitario,
     * para actualizar el subtotal
     */
    fun RefreshSubTotal() {
        if (fraccionado) {
            subTotal = Math.ceil(Cantidad * dividir(UnitPrecio, p.PesoUnitario)).toInt()
        } else {
            subTotal = UnitPrecio * Cantidad
        }
    }

    /**
     * a veces por comodidad tal vez se desee negociar no el precio por unidad
     * de un producto sino sobre el total.Ej si se tienen 20 und de un producto
     * x, y las 20 cuestan 30200. si se desea dejar a 30000 las 20 habria que
     * calcular el valor unitario tal que por 20 de 30000 pero seria engorroso
     * para un usuario. en su lugar se la  posibilidad de poner directamente los
     * 30000 y que el software calcule el correspondiente precio unitario
     * y verifique que no este por debajo del costo. no es una funcion
     * necesaria sino mas por comodidad y agilidad para el usuario.
     * @param sut
     */
    fun setSubTotal(sut: Int) {
        val minsut: Int = p.costo * Cantidad
        if (sut >= minsut) {
            subTotal = sut
            UnitPrecio = dividir(sut, Cantidad).roundToInt()
        } else {
            subTotal = minsut
        }
    }

    fun setFraccionado(fraccionado: Boolean) {
        this.fraccionado = fraccionado
        RefreshSubTotal()
    }


    fun getDescripcion(): String {
        return p.descripcion
    }

    fun getPVenta(): Int {
        return UnitPrecio
    }

    fun getCantidad(): Int {
        return Cantidad
    }

    fun getCantidad_str(): String {
        return if (fraccionado) "\${Cantidad} \${getMeasureUnit()}" else "\${Cantidad}"
    }

    fun getSubTotal(): Int {
        return subTotal
    }

    fun getUnitPrecio() = UnitPrecio
    fun getProducto_id() = producto_id
    fun getProducto_last_updt() = p.last_updt


    fun isFraccionado(): Boolean {
        return fraccionado
    }

    /**
     * retorna atributo del producto que indica si se puede no fraccionar en una operacion de venta
     */
    fun isFraccionable():Boolean{
        return p.fraccionable
    }


    /**
     * La idea es implementar en la clase producto un atributo para especificar
     * la unidad de medida en el caso de fraccionar un producto. De esta forma
     * si el itemventa es fraccionado, al momento de entregra la cantidad
     * para uso en el table view, el item venta puede adicionar al String
     * la unidad de medida. sin embargo para agilizar la primera implementacion
     * lo dejare para agregar despues y dejo este metodo declarado para
     * recordar el patron en un futuro.
     */
    fun getMeasureUnit(): String {
        return "g"
    }

    /**
     * el metodo de rendondeo funciona bien pero cuando se combina con
     * la dinamica del item venta muestra un comportamiento buggy
     */
    fun RedondearCOP() {
        throw UnsupportedOperationException()
        //setSubTotal(Util.COP_Round(subTotal));
    }

    fun dividir(a: Int, b: Int): Double {
        return a * 1.0 / (b * 1.0)
    }
}