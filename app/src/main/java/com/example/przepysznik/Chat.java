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


//Uzyto Ecplise Paho library

public class Chat extends AppCompatActivity {

    private static final String Broker_URL = "tcp://27df6e50fa3b437fa84505b6f2acd7a0.s1.eu.hivemq.cloud:8883";
    public static final String Client_ID = "client_ID";

    private MqttAndroidClient client;
    private Button poleczeniePrzycisk, wyslijWiadomosc;
    private TextView polaczenieTest, mqttMessage;
    public EditText wiadomoscText;
    public String messageText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        poleczeniePrzycisk = findViewById(R.id.poleczeniePrzycisk);
        wyslijWiadomosc = findViewById(R.id.wyslijWiadomosc);
        polaczenieTest = findViewById(R.id.connectionText);
        wiadomoscText = findViewById(R.id.wiadomoscText);
        mqttMessage = findViewById(R.id.mqttMessage);

        client = new MqttAndroidClient(this.getApplicationContext(), Broker_URL, Client_ID);

        poleczeniePrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
                polaczenieTest.setText("Połączono z MQTT");
            }
        });

        wyslijWiadomosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageText = wiadomoscText.getText().toString();
                publishMessage("Test", messageText);
            }
        });
    }

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
            IMqttToken token = client.connect();
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
}