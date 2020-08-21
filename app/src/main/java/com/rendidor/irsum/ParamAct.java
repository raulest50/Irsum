package com.rendidor.irsum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

public class ParamAct extends SuperAct {
    Editor edit;
    Button Button_Buscar_Satelink;
    SharedPreferences pre;
    EditText EditText_Host;
    Button Button_volver;
    TextView TXView_show_serverIP; // etiqueta para mostrar la direccion ip del servidor.

    public SatelinkFinder sf; // se usa para encontrar la direccion ip del server con udp broadcast

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_param);
        this.Button_volver = findViewById(R.id.Button_volver);
        this.Button_Buscar_Satelink = findViewById(R.id.Button_Buscar_Satelink);
        this.EditText_Host = findViewById(R.id.Tx_Direccion);
        this.pre = getSharedPreferences("configuracion_irsum", 0);
        this.TXView_show_serverIP = findViewById(R.id.TXView_show_serverIP);
        this.edit = this.pre.edit();
        this.EditText_Host.setText(this.pre.getString("host", ""));

        this.Button_Buscar_Satelink.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                /*
                edit.putString("host", ParamAct.this.EditText_Host.getText().toString());
                edit.apply();
                */
                BuscarSatelink();
                ShowT("buscando satelink server...");
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
        sf = new SatelinkFinder();
        //try {sf.reestablecer();} catch (NullPointerException e){ sf = new SatelinkFinder(); }
        if (!sf.running) {
            sf.start();
            Toast toast = Toast.makeText(getApplicationContext(), "Buscando servidor satelink ...", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * La clase satelinkFinder contiene lo necesario para hacer una deteccion de la direccion
     * ip de satelink (el servidor de nodejs) usando udp broadcasting
     */
    public class SatelinkFinder extends Thread {
        DatagramSocket socket; // para enviar y recivir informacion
        boolean running; // para indicar si el hilo esta o no corriendo
        byte[] buf = new byte[31];
        // String que se envia a satelink por medio de udp para indicarle que envie su direccion ip
        final byte[] SATELINK_IP_COMMAND = "satelink.ip".getBytes();
        InetAddress BROADCAST_IP; // direccion local de broadcast, depende de la subnet

        // para definir en que fase de la comunicacion se encuentra, como una maquina de estados
        // 0 > hacer broadcast. 1 > esperar ip del servidor. 2 > ip recivida fin del hilo
        public int estado = 0;


        public SatelinkFinder() {
            try {
                socket = new DatagramSocket(12100);
                // se determina la direccion ip de broadcast
                this.BROADCAST_IP = GetBroadcast();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
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

        public void run() {
            running = true;
            try {
                DatagramPacket data_send = new DatagramPacket( // satelink siempre escucha datagramas udp en el puerto 2100
                        this.SATELINK_IP_COMMAND, this.SATELINK_IP_COMMAND.length, this.BROADCAST_IP, 2100);
                socket.send(data_send);
                this.estado = 1; // se pasa a la fase de escucha
                while (estado == 1) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    String received = new String(packet.getData(), packet.getOffset(), packet.getLength());
                    //Log.i("**********", received);
                    String[] res = received.split(":");
                    if (res[0].equals("satelink.ip.ans")) {// si al hacer split del string
                        // recivido el primer elemento es 'satelink.ip.ans' entonces el datagrama
                        // proviene de satelink
                        running = false;
                        this.estado = 2; // ya se ha encontrado la direccion ip del servidor
                        //Log.i("+_+_+_+_+_+_+_", res[1]);
                        // despues de hacer split al string de respuesta de satelink
                        // su direccion ip queda en la segunda posicion del arreglo.
                        DoWhenServerFound(res[1]);
                    }
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void reestablecer(){
            this.estado=0;
            try {
                this.BROADCAST_IP = this.GetBroadcast();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }

        /**
         * cuando se encuentra el servidor se guarda la ip en preferencias y se muestra en
         * en los textview de la actividad.
         * @param ip
         */
        public void DoWhenServerFound(final String ip){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TXView_show_serverIP.setText(ip);
                }
            });
        }
    }


}
