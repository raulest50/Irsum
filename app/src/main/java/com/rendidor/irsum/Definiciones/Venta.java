package com.rendidor.irsum.Definiciones;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Venta implements Parcelable {
    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };
    public int Cant = 1;
    public String NombreProducto;
    public int PvPublico;

    public Venta(String NombreProducto2, int PvPublico2) {
        this.NombreProducto = NombreProducto2;
        this.PvPublico = PvPublico2;
    }

    public String getNombreProducto() {
        return this.NombreProducto;
    }

    public int getPvPublico() {
        return this.PvPublico;
    }

    public void setNombreProducto(String nombreProducto) {
        this.NombreProducto = nombreProducto;
    }

    public void setPvPublico(int pvPublico) {
        this.PvPublico = pvPublico;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

    public int getCant() {
        return this.Cant;
    }

    public void setCant(int cant) {
        this.Cant = cant;
    }
}
