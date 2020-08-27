package com.rendidor.irsum;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * para hacer uso de la api de preferencias de una manera mas ordenada.
 * se guarda la direccion ip del servidor y nombre de identificacion para el dispositivo
 */
public class PreferenciasIrsum {

    public final String PKEYFAM = "configuracion_irsum";
    public final String PKEY_HOST = "host";
    public final String PKEY_NOMBRE = "nombre_id";

    public static final String BLANK_IP_LABEL = "--- . --- . --- . ---";

    SharedPreferences pre;
    SharedPreferences.Editor edit;

    Context context;

    public PreferenciasIrsum(Context context){
        this.context = context;
        this.pre = context.getSharedPreferences(PKEYFAM, 0);
        this.edit = this.pre.edit();
    }

    public String getHost(){
        return pre.getString(PKEY_HOST, BLANK_IP_LABEL);
    }

    public String getNombreId(){
        return pre.getString(PKEY_NOMBRE, "");
    }

    public void setHost(String host){
        edit.putString(PKEY_HOST, host);
        edit.apply();
    }

    public void setNombreId(String nomId){
        edit.putString(PKEY_NOMBRE, nomId);
        edit.apply();
    }
}
