package com.rendidor.irsum.Definiciones;

import android.os.Parcel;
import android.os.Parcelable;

public class ListaVentaParcelable implements Parcelable {
    protected ListaVentaParcelable(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListaVentaParcelable> CREATOR = new Creator<ListaVentaParcelable>() {
        @Override
        public ListaVentaParcelable createFromParcel(Parcel in) {
            return new ListaVentaParcelable(in);
        }

        @Override
        public ListaVentaParcelable[] newArray(int size) {
            return new ListaVentaParcelable[size];
        }
    };
}
