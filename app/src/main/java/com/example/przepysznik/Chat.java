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
import androidx.appcompat.widget.AppCompatTextView;

import com.example.przepysznik.register.LogIn;
import com.hivemq.client.mqtt.datatypes.MqttTopicFilter;
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
import java.util.Arrays;
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
    public EditText wiadomoscText, poleTytulText;
    public String message, titleText;
    private AppCompatTextView textLogs;

    //String clientId = MqttClient.generateClientId();

    final Mqtt5Client client5 = Mqtt5Client.builder()
            .identifier("Uzytkownik: " + USERNAME) // use a unique identifier
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
        //mqttMessage = findViewById(R.id.mqttMessage);
        //poleTytulText = findViewById(R.id.tytulText);
        textLogs = findViewById(R.id.textLogs);


        //client = new MqttAndroidClient(this.getApplicationContext(), Broker_URL, hardwareAddress);

        options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());


        poleczeniePrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                client5.toBlocking().connectWith()
                        .simpleAuth()
                        .username(USERNAME)
                        .password(PASSWORD.getBytes())
                        .applySimpleAuth()
                        .willPublish()
                        .topic("Connection")
                        .payload(("User: "+USERNAME+" connected").getBytes())
                        .applyWillPublish()
                        .send();

                polaczenieTest.setText("Połączono z MQTT");
                Log.d(TAG, "MQTT connected");

            }
        });

        wyslijWiadomosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //titleText = poleTytulText.getText().toString();
                message = wiadomoscText.getText().toString();
                Toast.makeText(Chat.this, "Wysłano wiadomość: " + message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Topic: " + titleText + "\n");
                Log.d(TAG, "message: " + message.getBytes());
                MqttMessage messageText= new MqttMessage(message.getBytes());

                client5.toBlocking().publishWith()
                        //.topic(titleText) //Pobiera tekst z pola tytulu wiadomosci
                        .topic("Main")
                        .payload(messageText.getPayload()) //Pobiera tekst z pola do wpisania wiadomosci
                        .send();

                //poleTytulText.setText("");
                wiadomoscText.setText("");
            }
        });

        client5.toAsync().subscribeWith()
                .topicFilter("Main")
                .callback(publish -> {
                    runOnUiThread(() -> {
                        String currentText = textLogs.getText().toString();
                        textLogs.setText(String.format("%s\n%s", new String(publish.getPayloadAsBytes()), currentText));
                    });
                    //mqttMessage.setText(String.format("Received message on topic %s: %s", publish.getTopic(), new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8)));
                })
                .send();

        /*
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
        */
    }
}