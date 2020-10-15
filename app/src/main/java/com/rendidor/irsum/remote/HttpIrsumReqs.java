package com.rendidor.irsum.remote;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
public class HttpIrsumReqs {

    MainAct main_act; //actividad principal
    String satelink_product_req_url;

    /**
     * se inicializa main_act y la url a la que se hara el http request
     * @param main_act
     */
    public HttpIrsumReqs(MainAct main_act){
        this.main_act = main_act;

        // se obtiene la ip de satelink guardada previamente con java preference api
        SharedPreferences pre = main_act.getSharedPreferences("configuracion_irsum", 0);
        Editor edit = pre.edit();
        String satelink_ip = "192.168.77.101";//pre.getString("host", "");
        this.satelink_product_req_url = "http://${satelink_ip}:3000/buscar_producto";
    }

    /*
    public void startProductoRequestThread(String tp, String b, List<Venta> lv){
        Executors.newSingleThreadExecutor().execute(() -> {
            ArrayList<Producto> lpr = ProductoHttpRequest(tp, b);
            if(lpr.isEmpty()){
                main_act.runOnUiThread(new Runnable() {
                    public void run() {
                        //main_act.ShowWarningNocod();
                    }
                });
            } else{
                Producto p = lpr.get(0);
                lv.add(new Venta(p.getDescripcion(), Integer.parseInt(p.getPv_publico())));
                main_act.runOnUiThread(new Runnable() { // se actualiza la lista
                    public void run() {
                        main_act.MostrarListaVentas(lv);
                    }
                });
            }
        });
    }
    */

    /**
     * implementa el http request para buscar prductos. generacion de hilo nuevo
     * no se implementa aqui
     * @param tp
     * @param b
     * @return
     */
    public ArrayList<Producto> ProductoHttpRequest(String tp, String b){

        ArrayList<Producto> lpr = new ArrayList<>();// lista products result
        try {
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(satelink_product_req_url).newBuilder();
            urlBuilder.addQueryParameter("tipo_busqueda", tp); // se agrega parameto tipo de busqueda
            urlBuilder.addQueryParameter("busqueda", b); // se arega el string de busqueda
            String url = urlBuilder.build().toString(); // se construye la url para get request

            Request request = new Request.Builder().url(url).build();

            String r;
            Call call = client.newCall(request);

            try (Response res = call.execute()) {
                r = res.body().string();
            }
            Object obj = JSONValue.parse(r);
            JSONArray json_array = (JSONArray) obj;

            for (Object x : json_array) {
                JSONObject y = (JSONObject) x;

                //int costo = ((Number) y.get("costo")).intValue(); casting directo a int, Integer etc no funciona
                // tomado de https://coderanch.com/t/675211/java/JSON-java-lang-Integer-cast

                String _id = y.get("_id").toString();
                String descripcion = y.get("descripcion").toString();
                String costo = y.get("costo").toString();
                String pv_mayor = y.get("pv_mayor").toString();
                String pv_publico = y.get("pv_publico").toString();
                String iva = y.get("iva").toString();
                String last_updt = y.get("last_updt").toString();
                String keywords = y.get("keywords").toString();

                //lpr.add(new Producto(_id, descripcion, costo, pv_mayor, pv_publico, iva, last_updt, keywords));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return lpr;
    }
}
