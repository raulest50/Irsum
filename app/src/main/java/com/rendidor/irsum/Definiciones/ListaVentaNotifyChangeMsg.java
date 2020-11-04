package com.rendidor.irsum.Definiciones;

import java.util.LinkedList;

public class ListaVentaNotifyChangeMsg {

    public String mqttClientId;

    public LinkedList<ItemVenta> lv;

    public ListaVentaNotifyChangeMsg(String mqttClientId, LinkedList<ItemVenta> lv) {
        this.mqttClientId = mqttClientId;
        this.lv = lv;
    }
}
