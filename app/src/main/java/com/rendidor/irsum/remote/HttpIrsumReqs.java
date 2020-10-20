package com.rendidor.irsum.remote;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.rendidor.irsum.Definiciones.ItemVenta;
import com.rendidor.irsum.Definiciones.Producto;
import com.rendidor.irsum.MainAct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * clase para procesar los http request de irsum app a satelink nodejs app
 */
public class HttpIrsumReqs{

    /**
     * implementa el http request para buscar prductos. this method is blocking, must to be called
     * within new thread or using async programming so the UI dont get blocked.
     * @param tp
     * @param b
     * @return
     */
    public ArrayList<ItemVenta> ProductoHttpRequest(String satelink_ip, String tp, String b){

        ArrayList<ItemVenta> lpr = new ArrayList<>();// lista products result
        try {
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://${satelink_ip}:3000/buscar_producto").newBuilder();
            urlBuilder.addQueryParameter("tipo_busqueda", tp); // se agrega parameto tipo de busqueda
            urlBuilder.addQueryParameter("busqueda", b); // se arega el string de busqueda
            String url = urlBuilder.build().toString(); // se construye la url para get request

            Request request = new Request.Builder().url(url).build();

            String r;
            Call call = client.newCall(request);
            try (Response res = call.execute()) { r = res.body().string();
            }
            Object obj = JSONValue.parse(r);
            JSONArray json_array = (JSONArray) obj;

            for (Object x : json_array) {
                JSONObject y = (JSONObject) x;

                //int costo = ((Number) y.get("costo")).intValue(); casting directo a int, Integer etc no funciona
                // tomado de https://coderanch.com/t/675211/java/JSON-java-lang-Integer-cast

                String _id = y.get("_id").toString();
                String descripcion = y.get("descripcion").toString();
                int costo = Integer.parseInt(y.get("costo").toString());
                int pv_mayor = Integer.parseInt(y.get("pv_mayor").toString());
                int pv_publico = Integer.parseInt(y.get("pv_publico").toString());
                Double iva = Double.parseDouble(y.get("iva").toString());
                String last_updt = y.get("last_updt").toString();
                String keywords = y.get("keywords").toString();
                boolean fraccionable = Boolean.parseBoolean((y.get("fraccionable").toString()));
                int PesoUnitario = Integer.parseInt(y.get("PesoUnitario").toString());
                String grupo = y.get("grupo").toString();

                Producto p = new Producto(_id, descripcion, costo, pv_mayor, pv_publico, iva,
                        last_updt, keywords, fraccionable, PesoUnitario, grupo);
                ItemVenta itv = new ItemVenta(p, 1, p.getPv_publico(), false);
                lpr.add(itv);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return lpr;
    }


}