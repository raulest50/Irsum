package com.rendidor.irsum.remote;


import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.bosphere.filelogger.FL;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * implementa la logica con datagramas para encontrar el servidor Satelink
 * y ademas provee de la interfaz para
 */
public abstract class SatelinkFinder {

    private DatagramSocket socket; // para enviar y recivir informacion
    private boolean running; // para indicar si el hilo esta o no corriendo
    private byte[] buf = new byte[128];

    //public Context context;
    public WifiManager wifiMgr;

    /**
     * para definir en que fase de la comunicacion se encuentra, como una maquina de estados
     * 0 > hacer broadcast. 1 > esperar ip del servidor. 2 > ip recivida fin del hilo
     */
    private int estado;

    // tiempo de espera en milisegundos para escuchar respuesta del servidor
    public int TIME_OUT_MILLIS;


    // byte String que se envia a satelink por medio de udp para indicarle que envie su direccion ip
    private final byte[] SATELINK_IP_COMMAND = "satelink.ip".getBytes();
    private final int UDP_PORT_BIND = 12100;
    private final int UDP_PORT_SENDTO = 2100;


    public SatelinkFinder(int TIME_OUT_MILLIS, WifiManager wifiMgr) {
        try {
            this.socket = new DatagramSocket(UDP_PORT_BIND);
            this.socket.setBroadcast(true);
            this.running = false;
            this.estado = 0;
            this.TIME_OUT_MILLIS = TIME_OUT_MILLIS;
            //this.context = context;
            this.wifiMgr = wifiMgr;
        } catch (SocketException e) {
            socket.close();
            FL.e("SatelinkFinder Constructor", e);
            this.onError(e);
        }
    }

    /**
     * temporizador que se invoca dentro de Scan() para implementar TimeOut
     * usando solo la API de Thread.
     */
    private class SFTimer extends Thread {
        public Thread udp_thread;
        @Override
        public void run() {
            try {
                sleep(TIME_OUT_MILLIS);
                udp_thread.interrupt();
            } catch (InterruptedException e) {
                // si se logra encontrar la ip de satelink lo normal es que el hilo de busqueda
                // pare este hilo de temporizador, seria el funcionamiento normal del algoritmo
                // y por tanto no se deberia hacer nada
            }
        }
    }

    /**
     * Se crean 2 hilos. El primer hilo genera al segundo el cual es un temporizador. despues
     * de iniciar el temporizador, hara un llamado al metodo UDP_Broadcast para descubrir la
     * ip del seridor. Si el primer hilo termina antes que se cumpla el temporizador, entonces
     * para el temporizador con interrupt() y guarda la ip en preference, ademas de mostrarla
     * en el respectivo textView. En caso contrario sera el temporizador el que hara interrupt
     * del hilo buscador y guardara un String vacio como la ip para indicar que el servidor
     * no se pudo encontrar. En la practica el udp boradcast es tan veloz que un timeout
     * de mas de 2 o 3 segundos indica altisima probabilidad de que el servidor no este en operacion.
     */
    public void Scan(){
        if(!running) { // prevent more than one thread.
            FL.d("Scan()");
            //Toast.makeText(context, "Buscando servidor...", Toast.LENGTH_SHORT).show();
            Thread udp_thread = new Thread(){
                @Override
                public void run() {
                    try {
                        SFTimer tmout = new SFTimer();
                        tmout.udp_thread = this;
                        tmout.start();
                        ArrayList ans = SendListenUDP_broadcast();//solo retorna si recibe respuesta udp
                        tmout.interrupt(); // se detiene el hilo de temporizador
                        if ((boolean) ans.get(0)) onSatelinkFound((String) ans.get(1));
                        else onNotFound();
                    } catch(NullPointerException e){
                        socket.close();
                        onError(e);
                    }
                }

                /**
                 * si este que es el hilo de busqueda es interrupido, quiere decir que la ip
                 * de satelink no se pudo encontrar dentro del timeout especificado en el constructor.
                 * Este no seria el caso normal de funcionamiento y se debe llamar la funcion onTimeOut()
                 * la cual debe ser @override por el usuario de la clase
                 */
                @Override
                public void interrupt() {
                    super.interrupt();
                    socket.close();
                    onTimeOut();
                }
            };
            udp_thread.start();
        } else{ // en caso de que el hilo udp este corriendo
            // aun no he decidido que hacer
        }
    }



    /**
     * Este es el metodo mas importante de la clase. Usando DatagramPacket hace un broadcast
     * en la subred con un mensaje al que solo Satelink respondera. La respuesta de satelink
     * contiene un mensaje que permite confirmar que es satelink y mas imoportante su direccion
     * ip. de esta manera el cliente puede descubrir la ip del servidor sin ninguna informacion
     * introducida por el usuario pero si es requisito estar en la misma subred.
     * http://cms.digi.com/resources/documentation/digidocs/90001537/references/r_android_udp_client.htm?TocPath=Digi%20Demo%20Applications%7C_____10
     * @return
     */
    private ArrayList SendListenUDP_broadcast(){
        running = true;
        ArrayList r = new ArrayList();

        try {
            DatagramPacket data_send = new DatagramPacket( // satelink siempre escucha datagramas udp en el puerto 2100
                    this.SATELINK_IP_COMMAND, this.SATELINK_IP_COMMAND.length, GetBroadcastWifi(), UDP_PORT_SENDTO);
            socket.send(data_send);
            socket.send(data_send);
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
                    r.add(true); // el primer elemento indica si se encontro o no el servidor
                    r.add(res[1]); // se guarda la ip
                    //DoWhenServerFound(res[1]); // split in [1] is server ip
                }
            }
        } catch (IOException | NullPointerException e) {
            onError(e);
            FL.e("Exception en UDP broadcast", e);
        }
        socket.close();// ocurran o no una excepcion se cierra el socket.
        return r;
    }

    /**
     * En este metodo se obtiene la ip de la subnet para construir la correspondiente direccion
     * de broadcast.
     * @return InetAddress broadcast : direccion ip de broadcast correspondiente a la subnet actual
     * @throws SocketException
     * @throws UnknownHostException
     */
    @Deprecated
    private InetAddress GetBroadcast() throws SocketException, UnknownHostException {
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
            FL.d("${networkInterface.getName()} >> ${broadcast.getHostAddress()}");
        }
        FL.d("return >>> ${broadcast.getHostAddress()}");
        return broadcast;
    }

    /**
     * Una version mas robusta para obtener la direccion ip de broadcast
     * @return
     * @throws UnknownHostException
     */
    public InetAddress GetBroadcastWifi() throws UnknownHostException {
        InetAddress broadcast;
        try {
            //WifiManager wifiMgr = (WifiManager) context.getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = this.wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            String ipAddress = Formatter.formatIpAddress(ip);

            String[] ipA = ipAddress.split("\\.");
            broadcast = InetAddress.getByName("${ipA[0]}.${ipA[1]}.${ipA[2]}.255");
            FL.d("GetBroadcastWifi()   ${broadcast}");
        } catch (NullPointerException e){
            broadcast = InetAddress.getByName("255.255.255.255");
        }
        return broadcast;
    }


    /**
     * EL hilo de busqueda del servidor llama este metodo cuando encuentra el servidor.
     * @param ipServer es la direccion ip de satelink.
     */
    protected abstract void onSatelinkFound(String ipServer);

    /**
     * esta funcion es llamada una vez se determina que no se pudo encontrar el servidor satelink
     * debido a un timeout
     */
    protected abstract void onTimeOut();

    /**
     * para que usuario pueda definir su propia accion en caso de un error.
     * @param e
     */
    protected abstract void onError(Exception e);

    /**
     * Cuando se logra recibir datos en el buffer udp, el hilo de busqueda detiene al
     * hilo del temporizador y no se produce un timeout. sin embargo si los datos recividos no
     * corresponden con la clave acordada por el servidor, se llama este metodo
     * ya que no se puede saber la ip de satelink a pesar de haber recibido una respuesta en el
     * buffer udp
     * ip de satelink.
     */
    protected abstract void onNotFound();

}
