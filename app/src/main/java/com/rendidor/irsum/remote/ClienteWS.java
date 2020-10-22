package com.rendidor.irsum.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.rendidor.irsum.MainActivity;
import com.rendidor.irsum.fragmentDialogs.GenericDialogs;


import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;


/**
 * Implementacion de cliente websocket para irsum.
 * su proposito y poder visualizar en tiempo real desde otro equipo, lo que se va vendiendo.
 */
public class ClienteWS {

    WebSocketFactory factory;
    WebSocket ws;
    String server_ip;
    String ws_host;

    MainActivity context;

    String nombre; // nombre asignado a la tablet por el usuario
    String mac; // permite que aunque a varios dispositivos el user le asigne mismos nombres, que cada tablet tenga un id unico
    String id; // nombre + mac

    /**
     * Este string se usa en el cuerpo de un mensaje de identificacion para que el servidor
     * en node sepa que debe guardar la conexion a ese nodo en la lista de las tablets, las
     * cuales son las que proveen informacion. los otros dos tipos de nodo, orondo y browser,
     * son consumidores.
     */
    public final String TABLET_ORIG = "IRSUM";

    /**
     * se usa en los mensajes para indicar el proposito de los mismos. este indica que el proposito
     * es identificarse.
     */
    public final String PROPOSITO_ID = "IDENTIFICARSE";

    /**
     * se usa en los mensajes para indicar el proposito de los mismos. es notificar el estado
     * de la lista de venta de la app.
     */
    public final String PROPOSITO_UPDT_VENTA = "ACTUALIZAR_ESTADO_VENTA";

    public ClienteWS(MainActivity context){
        this.context =context;
        // la ip del servidor guardada con javapreferences
        SharedPreferences pre = context.getSharedPreferences("configuracion_irsum", 0);
        Editor edit = pre.edit();
        server_ip = pre.getString("host", "");
        // actividad Main donde tiene sentido que se use cliente websocket
        ws_host = "ws://${server_ip}:8080";

        nombre = pre.getString("nombre", "");
        mac = getMac();
        id = "${nombre}+${mac}"; // to split(+) to show only nombre, not mac, mac only for internals

        InicializacionWS(); // inicializa los objetos de la websocket
    }

    /**
     * se inicializan los objetos relacionados con websocket. se movieron del constructor para mayor
     * claridad. En este metodo se define onMessage function pero no se inicia la conexion, ya que es
     * preferible hacerlo en un hilo dedicado. Al usarse dentro del constructor se asegura
     * que no se pueda usar el metodo conectar sin antes inicialzar debidamente todas las variables
     * involucradas en la comunicacion.
     */
    public void InicializacionWS(){
        try {
            factory = new WebSocketFactory().setConnectionTimeout(5000);
            ws = factory.createSocket(ws_host);
            // Register a listener to receive WebSocket events.
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    System.out.println(message);
                }
            });
        } catch (IOException e){
            GenericDialogs.Dialogs.ShowDiaglog("Exception WebSocket client class", e.getMessage(), context);
        }
    }


    /**
     * se inicia un nuevo hilo para hacer conexion al servidor de websocket. visto desde adentro
     * del hilo, eel proceso de conexion se hace de manera sincrona, se hace esta aclaracion porque
     * la libreria soporta operaciones asynchronous
     */
    public void Conectar(){
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                ws.connect();
                // se indica que el proposito del mensaje es identificar este nodo.
                // para lo cual se manda en el cuerpo que se trata de una tablet origin
                // y se envia la identificacion.
                String mensaje = buildMessage(this.PROPOSITO_ID, this.id, this.TABLET_ORIG);
                ws.sendText(mensaje);
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
        });
    }



    /**
     * entrega la direccion mac de la tablet
     * @return
     */
    public String getMac(){
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * Para encapsular la informcion en formato JSON y facilitar la comunicacion con el server
     */
    public String buildMessage(String proposito, String id, String body){
        HashMap<String, String> m = new HashMap<>();
        m.put("proposito", proposito);
        m.put("body", TABLET_ORIG);
        m.put("id", id);
        return JSONObject.toJSONString(m);
    }
}
