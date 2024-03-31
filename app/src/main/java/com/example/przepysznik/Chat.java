package com.example.przepysznik;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.przepysznik.register.LogIn;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.android.service.MqttAndroidClient;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;


//Uzyto Ecplise Paho library

public class Chat extends AppCompatActivity {

    private String Broker_URL = "27df6e50fa3b437fa84505b6f2acd7a0.s1.eu.hivemq.cloud";

    private MqttAndroidClient client;
    private MqttConnectOptions options;
    private static String USERNAME = "testclient";
    private static String PASSWORD = "TestClient1";
    private Button poleczeniePrzycisk, wyslijWiadomosc;
    private TextView polaczenieTest, mqttMessage;
    public EditText wiadomoscText, tytulText;
    public String message, titleText;

    String clientId = MqttClient.generateClientId();

    final Mqtt5Client client5 = Mqtt5Client.builder()
            .identifier("sensor-" + clientId) // use a unique identifier
            .serverHost(Broker_URL)
            .automaticReconnectWithDefaultConfig() // the client automatically reconnects
            .serverPort(8883) // this is the port of your cluster, for mqtt it is the default port 8883
            .sslWithDefaultConfig() // establish a secured connection to HiveMQ Cloud using TLS
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        poleczeniePrzycisk = findViewById(R.id.poleczeniePrzycisk);
        wyslijWiadomosc = findViewById(R.id.wyslijWiadomosc);
        polaczenieTest = findViewById(R.id.connectionText);
        wiadomoscText = findViewById(R.id.wiadomoscText);
        mqttMessage = findViewById(R.id.mqttMessage);
        tytulText = findViewById(R.id.tytulText);


        client = new MqttAndroidClient(this.getApplicationContext(), Broker_URL, clientId);

        options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());


        poleczeniePrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    client.connect(options);
                    Log.d(TAG, "MQTT connected");
                    Log.d(TAG, client.getServerURI());
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
                polaczenieTest.setText("Połączono z MQTT");
                try {
                    client.subscribe("Test", 0);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }*/

                client5.toBlocking().connectWith()
                        .simpleAuth() // using authentication, which is required for a secure connection
                        .username(USERNAME) // use the username and password you just created
                        .password(PASSWORD.getBytes())
                        .applySimpleAuth()
                        .willPublish() // the last message, before the client disconnects
                        .topic("Test")
                        .payload(("User: "+clientId+" connected").getBytes())
                        .applyWillPublish()
                        .send();

                polaczenieTest.setText("Połączono z MQTT");


            }
        });

        wyslijWiadomosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleText = tytulText.getText().toString();
                message = wiadomoscText.getText().toString();
                //Toast.makeText(this, "Publishing message: " + message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Topic: " + titleText + "\n");
                Log.d(TAG, "message: " + message.getBytes());
                MqttMessage messageText= new MqttMessage(message.getBytes());
                /*
                try {
                    client.publish(titleText, messageText);
                    client.subscribe("Test", 0);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }*/


                client5.toBlocking().publishWith()
                        .topic(titleText)
                        .payload(messageText.getPayload()) //Pobiera tekst z pola do wpisania wiadomosci
                        .send();
            }
        });

        MqttCallback cb = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "connectionLost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "Topic: " + topic + "\n");
                Log.d(TAG, "message: " + new String(message.getPayload()));
                mqttMessage.setText(new String(message.getPayload()));

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //Toast.makeText(this, "Messaged delivered: " + topic, Toast.LENGTH_SHORT).show();
            }
        };

        try {
            client.connect(options);
            client.setCallback(cb);

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }


        //client.subscribe("Test", 0);
    }

    /*
    @Override
    protected void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    private void publishMessage(String topic, String message) {
        Toast.makeText(this, "Publishing message: " + message, Toast.LENGTH_SHORT).show();
        publish(topic, message);
    }

    public void connect() {
        try {
            //MemoryPersistence persistence = new MemoryPersistence();
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connected!");
                    subscribe("Test");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Not connected!");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            client.publish(topic, mqttMessage);
            Log.d(TAG, "Topic: " + topic + "\n" + message);
            subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic, 0);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "connectionLost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG, "Topic: " + topic + "\n");
                    Log.d(TAG, "message: " + new String(message.getPayload()));
                    mqttMessage.setText(new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //Toast.makeText(this, "Messaged delivered: " + topic, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    */
}