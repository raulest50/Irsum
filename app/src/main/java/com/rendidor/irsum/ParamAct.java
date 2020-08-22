package com.rendidor.irsum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


// explicacion de completable future
// https://levelup.gitconnected.com/completablefuture-a-new-era-of-asynchronous-programming-86c2fe23e246

public class ParamAct extends SuperAct {
    Editor edit;
    Button Button_Buscar_Satelink;
    SharedPreferences pre;

    Button Button_volver;
    TextView TXView_show_serverIP; // etiqueta para mostrar la direccion ip del servidor.

    public final String BLANK_IP_LABEL = "--- . --- . --- . ---";

    public SatelinkFinder sf = new SatelinkFinder(this); // se usa para encontrar la direccion ip del server con udp broadcast

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_param);

        // Instacias elementos UI
        this.Button_volver = findViewById(R.id.Button_volver);
        this.Button_Buscar_Satelink = findViewById(R.id.Button_Buscar_Satelink);

        this.TXView_show_serverIP = findViewById(R.id.TXView_show_serverIP);

        // java preference api
        this.pre = getSharedPreferences("configuracion_irsum", 0);
        this.edit = this.pre.edit();
        this.TXView_show_serverIP.setText(this.pre.getString("host", BLANK_IP_LABEL));
        //this.EditText_Host.setText(this.pre.getString("host", ""));

        // Button Listeners
        this.Button_Buscar_Satelink.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BuscarSatelink();
            }
        });

        this.Button_volver.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ParamAct.this.startActivity(new Intent(ParamAct.this, MainAct.class));
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_param, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public void BuscarSatelink() {
        sf.Scan();
    }

    /**
     * La clase satelinkFinder contiene lo necesario para hacer una deteccion de la direccion
     * ip de satelink (el servidor de nodejs) usando udp broadcasting
     */
    public class SatelinkFinder{
        // --- VARIABLES
        DatagramSocket socket; // para enviar y recivir informacion
        boolean running; // para indicar si el hilo esta o no corriendo
        byte[] buf = new byte[31];
        InetAddress broadcastIP; // direccion local de broadcast, depende de la subnet

        // para definir en que fase de la comunicacion se encuentra, como una maquina de estados
        // 0 > hacer broadcast. 1 > esperar ip del servidor. 2 > ip recivida fin del hilo
        public int estado;

        Context contexto;

        // --- CONSTANTES
        // tiempo de espera en milisegundos para escuchar respuesta del servidor
        public int TIME_OUT_SEC = 7;

        // byte String que se envia a satelink por medio de udp para indicarle que envie su direccion ip
        final byte[] SATELINK_IP_COMMAND = "satelink.ip".getBytes();


        public class SFTimer extends Thread {
            public Thread udp_thread;
            @Override
            public void run() {
                try {
                    sleep(7000);
                    udp_thread.interrupt();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        }

        //CONSTRUCTOR
        public SatelinkFinder(Context contexto) {
            try {
                this.socket = new DatagramSocket(12100);
                this.running = false;
                this.estado = 0;
                this.contexto = contexto;
                // se determina la direccion ip de broadcast
                this.broadcastIP = GetBroadcast();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }

        // Executors.newSingleThreadExecutor().execute(() -> {});

        /**
         *
         */
        public void Scan(){
            if(!running) { // prevent more than one thread.
                Toast toast = Toast.makeText(getApplicationContext(), "Buscando servidor...", Toast.LENGTH_SHORT);
                toast.show();
                Thread udp_thread = new Thread(){
                    @Override
                    public void run() {
                        SFTimer tmout = new SFTimer();
                        tmout.udp_thread = this;
                        tmout.start();
                        String ip = UDP_Broadcast();
                        tmout.interrupt();
                        DoWhenComplete(ip);
                        reset();
                    }

                    @Override
                    public void interrupt() {
                        DoWhenComplete(BLANK_IP_LABEL);
                        //Toast toast2 = Toast.makeText(contexto, "No se encontro el servidor", Toast.LENGTH_SHORT);
                        //toast2.show();
                        super.interrupt();
                    }
                };
                udp_thread.start();
            } else{
                Toast toast = Toast.makeText(getApplicationContext(), "Ya se inicio la busqueda del servidor", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        /**
         * En este metodo se obtiene la ip de la subnet para construir la correspondiente direccion
         * de broadcast.
         * @return InetAddress broadcast : direccion ip de broadcast correspondiente a la subnet actual
         * @throws SocketException
         * @throws UnknownHostException
         */
        public InetAddress GetBroadcast() throws SocketException, UnknownHostException {
            InetAddress broadcast = InetAddress.getByName("0.0.0.0");
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback())
                    continue;    // Do not want to use the loopback interface.
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) continue;
                }
            }
            return broadcast;
        }

        public String UDP_Broadcast(){
            running = true;
            String r = "";
            try {
                DatagramPacket data_send = new DatagramPacket( // satelink siempre escucha datagramas udp en el puerto 2100
                        this.SATELINK_IP_COMMAND, this.SATELINK_IP_COMMAND.length, this.broadcastIP, 2100);
                socket.send(data_send);
                this.estado = 1; // se pasa a la fase de escucha
                while (estado == 1) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    String received = new String(packet.getData(), packet.getOffset(), packet.getLength()); // received reply.
                    String[] res = received.split(":");
                    if (res[0].equals("satelink.ip.ans")) {// if split returns in [0] 'satelink.ip.ans' then is from satelink
                        running = false;
                        this.estado = 2; // server ip found
                        r = res[1];
                        //DoWhenServerFound(res[1]); // split in [1] is server ip
                    }
                }
            } catch (IOException | NullPointerException e) {
                reset();
                e.printStackTrace();
                setTXView_show_serverIP("Exepcion en udp broadcast");
            }
            return r;
        }


        /**
         * cuando se encuentra el servidor se guarda la ip en preferencias y se muestra en
         * en los textview de la actividad.
         * @param ip
         */
        public void DoWhenComplete(final String ip){
            edit.putString("host", ip);// se guarda la ip del servidor en preferencias.
            edit.apply();
            setTXView_show_serverIP(ip);
            reset();
        }

        public void reset(){
            this.estado=0;
            this.running=false;
            this.socket.close();
            try {
                this.broadcastIP = this.GetBroadcast();
                this.socket = new DatagramSocket(12100);
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTXView_show_serverIP(final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TXView_show_serverIP.setText(txt);
            }
        });
    }

}
