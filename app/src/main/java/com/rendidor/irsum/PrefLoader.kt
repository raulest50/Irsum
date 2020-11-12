package com.rendidor.irsum

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PrefLoader(context:Context) {

    var sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    var edit:SharedPreferences.Editor = sp.edit()

    /**
     * retorna la ip que se usara como la ip Satelink, esto depende
     * del switch preference de auto ip. si esta activo se usa la ip que se encuentre con el UDP
     * broadcast, de lo contrario se usara la que se establesca manualmente en settings fragment
     */
    fun getUsedSatelinkIp():String{
        lateinit var r:String
        if(sp.getBoolean(PrefLoader.autofind_ip, false)){
            r = sp.getString(PrefLoader.autoip_value, "NaN")
        } else{
            r = sp.getString(PrefLoader.manual_ip_value, "NaN")
        }
        return r
    }

    companion object Keys{
        val autofind_ip = "stkey_settings_autoip"
        val autoip_value = "stkey_settings_autoip_value"
        val manual_ip_value = "stkey_settings_serverip_txf"
        val user_defined_name = "et_serverip_summary"
        val mqtt_enabled = "stkey_mqtt_enabled"
    }
}