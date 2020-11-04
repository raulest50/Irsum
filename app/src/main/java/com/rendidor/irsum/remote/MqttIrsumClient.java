package com.rendidor.irsum.remote;



import com.rendidor.irsum.HomeFragment.PushCallback;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttIrsumClient {

    public final String subTopic = "orondo/comandos"; // topico a publicar
    public final String pubTopic = "listasVentas/"; // topico a subcribir

    int qos = 0; // grado de prioridad
    private String broker = "tcp://localhost:1883"; // direccion del broker
    public final String clientId; // identificacion del cliente
    private MemoryPersistence persistence = new MemoryPersistence();

    private String port="1883";

    private MqttClient client;

    public MqttIrsumClient(String clientId) {
        this.clientId = clientId;
    }

    public void Conectar(PushCallback pb, String satelink_ip) throws MqttException{
        broker = "tcp://${satelink_ip}:${port}"; // broker aedes en satelink
        client = new MqttClient(broker, clientId, persistence);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        client.setCallback(pb);

        System.out.println("Connecting to broker: " + broker);
        client.connect(options);

        System.out.println("Connected");
        // Subscribe
        client.subscribe(subTopic);
    }

    public void Desconectar() throws MqttException{
        client.disconnect();
        System.out.println("Disconnected");
        client.close();
    }

    public void Send(String msg) throws MqttException{
        // Required parameters for message publishing
        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(qos);
        client.publish(pubTopic, message);
        System.out.println("Message published");
    }

    public boolean isConnected(){ return client.isConnected(); }
}
