package com.rendidor.irsum.Definiciones;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Producto implements Parcelable {
    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };
    public String Codigo;
    public String Costo;
    public String Descripcion;
    public String Familia;
    public String Iva;
    public String LastUp;
    public String Pvpublico;
    public String Pvtienda;

    public Producto(String codigo, String costo, String descripcion, String familia, String iva, String lastUp, String pvpublico, String pvtienda) {
        this.Descripcion = descripcion;
        this.Costo = costo;
        this.Pvtienda = pvtienda;
        this.Pvpublico = pvpublico;
        this.LastUp = lastUp;
        this.Codigo = codigo;
        this.Iva = iva;
        this.Familia = familia;
    }

    protected Producto(Parcel in) {
        this.Descripcion = in.readString();
        this.Costo = in.readString();
        this.Pvtienda = in.readString();
        this.Pvpublico = in.readString();
        this.LastUp = in.readString();
        this.Codigo = in.readString();
        this.Iva = in.readString();
        this.Familia = in.readString();
    }

    public String getDescripcion() {
        return this.Descripcion;
    }

    public String getCosto() {
        return this.Costo;
    }

    public String getPvtienda() {
        return this.Pvtienda;
    }

    public String getPvpublico() {
        return this.Pvpublico;
    }

    public String getLastUp() {
        return this.LastUp;
    }

    public String getCodigo() {
        return this.Codigo;
    }

    public String getIva() {
        return this.Iva;
    }

    public String getFamilia() {
        return this.Familia;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }

    public void setCosto(String costo) {
        this.Costo = costo;
    }

    public void setPvtienda(String pvtienda) {
        this.Pvtienda = pvtienda;
    }

    public void setPvpublico(String pvpublico) {
        this.Pvpublico = pvpublico;
    }

    public void setLastUp(String lastUp) {
        this.LastUp = lastUp;
    }

    public void setCodigo(String codigo) {
        this.Codigo = codigo;
    }

    public void setIva(String iva) {
        this.Iva = iva;
    }

    public void setFamilia(String familia) {
        this.Familia = familia;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Descripcion);
        dest.writeString(this.Costo);
        dest.writeString(this.Pvtienda);
        dest.writeString(this.Pvpublico);
        dest.writeString(this.LastUp);
        dest.writeString(this.Codigo);
        dest.writeString(this.Iva);
        dest.writeString(this.Familia);
    }
}
