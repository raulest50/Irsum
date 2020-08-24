package com.rendidor.irsum.Definiciones;

import android.os.Parcel;
import android.os.Parcelable;

public class Producto implements Parcelable {
    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };
    public String _id;
    public String descripcion;
    public String costo;
    public String pv_mayor;
    public String pv_publico;
    public String iva;
    public String last_updt;
    public String keywords;

    public Producto(String _id, String descripcion, String costo, String pv_mayor, String pv_publico, String iva, String lastUp, String keywords) {
        this._id = _id;
        this.descripcion = descripcion;
        this.costo = costo;
        this.pv_mayor = pv_mayor;
        this.pv_publico = pv_publico;
        this.last_updt = lastUp;
        this.iva = iva;
        this.keywords =  keywords;
    }

    protected Producto(Parcel in) {
        this.descripcion = in.readString();
        this.costo = in.readString();
        this.pv_mayor = in.readString();
        this.pv_publico = in.readString();
        this.last_updt = in.readString();
        this._id = in.readString();
        this.iva = in.readString();
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public String getCosto() {
        return this.costo;
    }

    public String getPv_mayor() {
        return this.pv_mayor;
    }

    public String getPv_publico() {
        return this.pv_publico;
    }

    public String getLast_updt() {
        return this.last_updt;
    }

    public String getCodigo() {
        return this._id;
    }

    public String getIva() {
        return this.iva;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public void setPv_mayor(String pv_mayor) {
        this.pv_mayor = pv_mayor;
    }

    public void setPv_publico(String pv_publico) {
        this.pv_publico = pv_publico;
    }

    public void setLast_updt(String last_updt) {
        this.last_updt = last_updt;
    }

    public void setCodigo(String _id) {
        this._id = _id;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.descripcion);
        dest.writeString(this.costo);
        dest.writeString(this.pv_mayor);
        dest.writeString(this.pv_publico);
        dest.writeString(this.last_updt);
        dest.writeString(this._id);
        dest.writeString(this.iva);
    }
}
