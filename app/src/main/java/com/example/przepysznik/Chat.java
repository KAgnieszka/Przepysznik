package com.example.przepysznik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.przepysznik.register.LogIn;


//Uzyto Ecplise Paho library

public class Chat extends AppCompatActivity {

    private static final String Broker_URL = "tcp://your-broker-url:1883";
    private static final String Client_ID="client_ID";
    private MqttHandler mqttHandler;

    private Button testChatu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        mqttHandler = new MqttHandler();
        mqttHandler.connect(Broker_URL, Client_ID);
        testChatu = findViewById(R.id.testChatu);

        testChatu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishMessage("test", "test2");
                System.out.println("test przycisku");
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
        System.out.println("Test button");
    }

    private void subscribeToTopic(String topic){
        Toast.makeText(this, "Subscribing to topic: " + topic, Toast.LENGTH_SHORT).show();
        mqttHandler.subscribe(topic);
    }
}
