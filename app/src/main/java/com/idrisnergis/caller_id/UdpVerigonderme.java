package com.idrisnergis.caller_id;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UdpVerigonderme extends AppCompatActivity {

    private TextView mTextViewReplyFromServer;
    private EditText mEditTextSendMessage;
    String IPbilgi,portbilgi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_verigonderme);

        Button buttonSend = (Button) findViewById(R.id.btn_send);

        mEditTextSendMessage = (EditText) findViewById(R.id.edt_send_message);
        mTextViewReplyFromServer = (TextView) findViewById(R.id.tv_reply_from_server);

        //MainActivityden IP,Port blgisi almak
        Intent intent = getIntent();
        IPbilgi = intent.getStringExtra("IP");
        portbilgi = intent.getStringExtra("PORT");

    }


    public void   getipport (String ipbilgisi,String portbilgisi) {

        //Toast.makeText(this, String.valueOf(telefongsm), Toast.LENGTH_SHORT).show();
        //telefonnumara.setText(telefongsm);
        Log.i("IP ve Port Bilgisi  ", "IP " +ipbilgisi + "Port : " + portbilgisi);

    }


    //Butona tıkladığında mesaj gönderme
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_send:
                sendMessage(mEditTextSendMessage.getText().toString(),"192.168.1.2","1111");
                break;
        }
    }

    public void sendMessage(final String message,final String IP , final String Port) {


        Thread thread = new Thread(new Runnable() {

            String stringData;

            @Override
            public void run() {

                try {

                    Log.i("Numara Bilgisi En Son", IP + " " + Port  + " " + message);

                    DatagramSocket  ds = new DatagramSocket();
                    // IP Address below is the IP address of that Device where server socket is opened.
                    InetAddress serverAddr = null;
                    serverAddr =  InetAddress.getByName(IPbilgi);
                    ds.setBroadcast(true);
                    int mesaj_length = message.length();
                    byte[] numaramesaji=message.getBytes();
                    DatagramPacket dp = new DatagramPacket(numaramesaji , mesaj_length, serverAddr, 61010);//Integer.parseInt(portbilgi));
                    ds.send(dp);
                    android.util.Log.w("UDP", "Güzel Çalıştı...");
                    //byte[] lMsg = new byte[1000];
                    //dp = new DatagramPacket(lMsg, lMsg.length);
                    //ds.receive(dp);
                    //stringData = new String(lMsg, 0, dp.getLength());

                    Log.i("Numara Bilgisi : ", IPbilgi + " " + portbilgi  + " " + message);

                }catch (SocketException e)
                {
                    e.printStackTrace();
                }
                catch (UnknownHostException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    android.util.Log.w("UDP", "Catched here.");
                    e.printStackTrace();
                }
            }

        });

        thread.start();
    }
}