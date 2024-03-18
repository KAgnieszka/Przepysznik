package com.example.przepysznik;

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

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


//Uzyto Ecplise Paho library

public class Chat extends AppCompatActivity {

    private static final String Broker_URL = "tcp://test.mosquitto.org:1883";
    private static final String Client_ID="client_ID";
    private MqttHandler mqttHandler;

    private Button poleczeniePrzycisk, wyslijWiadomosc;
    private TextView polaczenieTest, mqttMessage;
    private EditText wiadomoscText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        mqttHandler = new MqttHandler();
        poleczeniePrzycisk = findViewById(R.id.poleczeniePrzycisk);
        wyslijWiadomosc = findViewById(R.id.wyslijWiadomosc);
        polaczenieTest = findViewById(R.id.connectionText);
        wiadomoscText = findViewById(R.id.wiadomoscText);
        mqttMessage = findViewById(R.id.mqttMessage);

        poleczeniePrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttHandler.connect(Broker_URL, Client_ID);
                polaczenieTest.setText("Połączono z MQTT");
            }
        });

        wyslijWiadomosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = wiadomoscText.getText().toString();
                publishMessage("Test", messageText);
                subscribeToTopic("Test");

                //messageArrived();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mqttHandler.disconnect();
        super.onDestroy();
    }

    private void publishMessage(String topic, String message) {
        Toast.makeText(this, "Publishing message: " + message, Toast.LENGTH_SHORT).show();
        mqttHandler.publish(topic, message);
    }

    private void subscribeToTopic(String topic){
        Toast.makeText(this, "Subscribing to topic: " + topic, Toast.LENGTH_SHORT).show();
        mqttHandler.subscribe(topic);
        MqttCallback(String topic, MqttMessage message) {

        }
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d("tag","message>>" + new String(message.getPayload()));
        Log.d("tag","topic>>" + topic);
        mqttMessage.setText(new String(message.getPayload()));

    }
}
