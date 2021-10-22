package com.example.cclaptopcom.helloworld;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    MQTTHelper mqttHelper;
    TextView txtTemp, txtHummi;
    ToggleButton btnLED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btnLED = findViewById(R.id.btnLED);

        txtTemp = findViewById(R.id.txtTemperature);
        txtHummi = findViewById(R.id.txtHumidity);
/*
        txtTemp.setText("40" + "°C");
        txtHummi.setText("80" + "%");
*/
        startMQTT();
        toogleButton();

        //setupScheduler();
    }
//    private void receivedDataMQTT(String topic, MqttMessage message) throws Exception {
//
//
//    }
private void toogleButton() {
    btnLED.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {

            if (isCheck == true) {
                Log.d("mqtt", "ON");
                sendDataMQTT("hoanhtai123/feeds/bbc-led", "1");
                //sendDataMQTT("mqtt.eclipseprojects.io/TEMP", "1");
            }
            if (isCheck == false) {
                Log.d("mqtt", "OFF");
                sendDataMQTT("hoanhtai123/feeds/bbc-led", "0");
            }
        }
    });
}
    int waiting_period = 0;
    boolean send_message_again = false;

    ArrayList<MQTTMessage> list = new ArrayList<>();

    private void setupScheduler(){

        Timer aTimer = new Timer();
        TimerTask scheduler = new TimerTask() {
            @Override
            public void run() {

                Log.d("mqtt", "Timer is executed");
                //btnLED.setVisibility(View.VISIBLE);

                if(waiting_period > 0){
                    waiting_period--;
                    if(waiting_period == 0){
                        send_message_again = true;
                    }
                }
                if(send_message_again == true){
                    sendDataMQTT(list.get(0).topic,list.get(0).mess);
                    list.remove(0);
                }
            }
        };
        aTimer.schedule(scheduler, 0,1000);
    }
    private void sendDataMQTT(String topic, String value){
        waiting_period = 3;
        send_message_again = false;
        MQTTMessage aMessage = new MQTTMessage();
        aMessage.topic = topic;
        aMessage.mess = value;
        list.add(aMessage);


        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);


        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);

        }catch (MqttException e){

        }
    }

    private void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext(), "291020");

        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("mqtt", "Connection is successful");
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d("mqtt", "Connection lost please check your internet connection");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                if(topic.equals("hoanhtai123/feeds/bbc-temp")){
                    Log.d("mqtt", "Received: " + message.toString() );
                    txtTemp.setText(message.toString()+"°C");

                }
                if(topic.equals("hoanhtai123/feeds/bbc-temp")){
                    //Log.d("mqtt", "Received: " + message.toString() );
                    Log.d("mqtt", "Received: " + message.toString() );
                    txtHummi.setText(message.toString()+"°C");
                }
                if(topic.equals("hoanhtai123/feeds/bbc-led")){
                    Log.d("mqtt", "Received: " + message.toString() );
                    if(message.toString().equals("1")){
                        btnLED.setChecked(true);
                    }
                    if(message.toString().equals("0")){
                        btnLED.setChecked(false);
                    }

                }
                if(topic.contains("hoanhtai123/feeds/bbc-led") && message.toString().contains("123")){
                    waiting_period = 0;
                    send_message_again = false;
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
//    private String randomID(){
//        return "1234";
//    }
}
