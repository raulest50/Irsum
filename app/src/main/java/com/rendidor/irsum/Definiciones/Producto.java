package com.rendidor.irsum.Definiciones;

import org.json.simple.JSONObject;

/**
 *
 * @author Raul Alzate
 * Representacion de un producto
 */

public class Producto {

    /**
     * equivalente al codigo de barras del producto o solo codigo.
     * pero para mejor claridad se dejo _id que es el nombre por defecto que
     * usa mongo y jongo para la llave primaria. para cambiar el nombre por
     * defecto a uno personalizado toca agregar otra anotacion, entonces preferi
     * dejarlo asi para mas simplisidad.
     */
    public String _id;

    public String descripcion;

    public int costo;

    /**
     * precio de venta para tienda o pormmayor
     */
    public int pv_mayor;

    /**
     * precio de venta al publico
     */
    public int pv_publico;

    /**
     * si no tiene iva se pone 0
     */
    public Double iva;

    // ultima frecha de actualizacion del producto
    public String last_updt;

    /** palabras clave que se deseen asignar al producto. se pueden usar para
     * efectos de busqueda o tambien de analisis de productos pero aun no lo he
     * definido,  de todas formas deje este atributo para dejar abierta la posibilidad.
     */
    public String keywords;


    /**
     * indica si el producto se puede dividir para ser vendido. el caso mas
     * comun es el queso cuajada granos en paquetes de libra salchichones etc.
     */
    public boolean fraccionable;

    /**
     * solo tiene sentido si fraccionable=true. es el peso en gramos del producto
     * el cual se usa en regla de 3 con el precio de venta para calcular
     * el valor correspondiente a la fraccion. Ej. frijol por libra 450g
     * a 4000. si este producto se establece como fraccionable entonces
     * el software permitira que se registren 225g de frijol en una venta.
     * con el peso unitario se calcularia el valor correspondiente a cobrar
     * y descontaria 0.5 en stock.
     */
    public int PesoUnitario;

    public String grupo;

    public static final transient String NORMAL = "normal";
    public static final transient String STK_PRIOR = "prioritario";

    /**
     * principalmente para usar en la importacion de base de datos de productos
     * o cuando no se desea especificar el grupo del producto
     * @param codigo
     * @param descripcion
     * @param costo
     * @param pv_mayor
     * @param pv_publico
     * @param iva
     * @param last_updt
     * @param keywords
     */
    public Producto(String codigo, String descripcion, int costo,
                    int pv_mayor, int pv_publico, Double iva, String last_updt, String keywords) {
        this._id = codigo;
        this.descripcion = descripcion;
        this.costo = costo;
        this.pv_mayor = pv_mayor;
        this.pv_publico = pv_publico;
        this.iva = iva;
        this.last_updt = last_updt;
        this.keywords = keywords;

        //atributos que no estaban en la version anterior de la bd
        this.fraccionable = false;
        this.PesoUnitario = 1; // 1 seria equivalente a no fraccionable
        this.grupo = Producto.NORMAL;
    }


    /**
     * El constructor que debe usarse para las demas partes del programa
     * ya que acepta todos los parametros
     * @param codigo
     * @param descripcion
     * @param costo
     * @param pv_mayor
     * @param pv_publico
     * @param iva
     * @param last_updt
     * @param keywords
     * @param fraccionable
     * @param PesoUnitario
     * @param grupo
     */
    public Producto(String codigo, String descripcion, int costo,
                    int pv_mayor, int pv_publico, Double iva, String last_updt, String keywords,
                    boolean fraccionable, int PesoUnitario, String grupo) {
        this._id = codigo;
        this.descripcion = descripcion;
        this.costo = costo;
        this.pv_mayor = pv_mayor;
        this.pv_publico = pv_publico;
        this.iva = iva;
        this.last_updt = last_updt;
        this.keywords = keywords;
        this.fraccionable = fraccionable;
        this.PesoUnitario = PesoUnitario;
        this.grupo = grupo;
    }

    /**
     * construye el obj producto a partir de un json con los keys de la nueva bd.
     * NO USAR EN LA IMPORTACION DE LA VIEJA BD DE SQL.
     * @param pjson
     */
    public Producto(JSONObject pjson){
        this._id = (String) pjson.get("_id");
        this.descripcion = (String) pjson.get("descripcion");
        this.costo = ((Number) pjson.get("costo")).intValue();
        this.pv_mayor = ((Number) pjson.get("pv_mayor")).intValue();
        this.pv_publico = ((Number) pjson.get("pv_publico")).intValue();
        this.iva = ((Number) pjson.get("iva")).doubleValue();
        this.last_updt = (String) pjson.get("last_updt");
        this.keywords = (String) pjson.get("keywords");
        this.PesoUnitario = ((Number) pjson.get("PesoUnitario")).intValue();
        this.grupo = (String) pjson.get("grupo");
        this.fraccionable = (boolean) pjson.get("fraccionable");
    }

    public Producto(){}

    public String getId() {
        return _id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCosto() {
        return costo;
    }

    public int getPv_mayor() {
        return pv_mayor;
    }

    public int getPv_publico() {
        return pv_publico;
    }

    public Double getIva() {
        return iva;
    }

    public String getLast_updt() {
        return last_updt;
    }

    public String getKeywords() {
        return keywords;
    }

}
