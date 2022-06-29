package com.idrisnergis.caller_id;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;



@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {


    private boolean detectEnabled;
    public TextView textViewDetectState; //, txtip ,txtport,telefonnumara;
    private Button buttonToggleDetect;
    private Button buttonExit;//,buttonmesaj;

    public static String IPbilgi;
    public static String portbilgi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] TELEFON_IZNI = {Manifest.permission.READ_PHONE_STATE};
            if (!izinKontrol(MainActivity.this, TELEFON_IZNI)) { // izin verilmiş mi
                requestPermissions(TELEFON_IZNI, 100); // verilmediyse, izin isteme penceresi aç
            } else
                telefonizin(); // zaten izin verilmişse kamera aç
        } else
            telefonizin(); // api 6.0 altındaysa izne bakmadan doğrudan kamera aç


       /*txtip = (TextView)findViewById(R.id.textip);
       txtport = (TextView)findViewById(R.id.textport);
       telefonnumara = (TextView)findViewById(R.id.texttelnumara);*/

            //Setting Activity kısmından bilgileri alma
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            IPbilgi = prefs.getString("urladres", "IP Adres Yok");
            portbilgi = prefs.getString("PORT", "Port Bilgisi Yok");
         /*Toast.makeText(this, String.valueOf(IPbilgi), Toast.LENGTH_SHORT).show();
         txtip.setText(IPbilgi);
         txtport.setText(portbilgi);*/


            Intent udpbilgi = new Intent(this, UdpVerigonderme.class);
            udpbilgi.putExtra("IP", IPbilgi);
            udpbilgi.putExtra("PORT", portbilgi);


            //Mesaj menusune gitmesi için ayar
      /*  buttonmesaj = (Button) findViewById(R.id.buttonmesaj);
        buttonmesaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UdpVerigonderme.class);
                startActivity(intent);
            }
        });*/

            textViewDetectState = (TextView) findViewById(R.id.textViewDetectState);

            buttonToggleDetect = (Button) findViewById(R.id.buttonDetectToggle);
            buttonToggleDetect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDetectEnabled(!detectEnabled);
                }
            });


            buttonExit = (Button) findViewById(R.id.buttonExit);
            buttonExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDetectEnabled(false);
                    MainActivity.this.finish();
                }
            });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ayarlar) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



    public void ayar(View v){
        Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
        startActivity(intent);
        ///Mesajgonder mesajgonder1 = new Mesajgonder();
        //Toast.makeText(getApplicationContext(),SERVER_IP,Toast.LENGTH_SHORT).show();
        // Toast.makeText(this,String.valueOf( SERVER_PORT),Toast.LENGTH_SHORT).show();//integer değerin toast mesajına yanısılması
  }



    private void setDetectEnabled(boolean enable) {
        detectEnabled = enable;

        Intent intent = new Intent(this, CallDetectService.class);
        if (enable) {
            // Denetleme Servisi Başlangıç
            startService(intent);
            buttonToggleDetect.setText("Durdur");
            textViewDetectState.setText("Kontrol Başlatıldı...");

        }
        else {
            // Denetleme Durudurma
            stopService(intent);
            buttonToggleDetect.setText("Başlat");
            textViewDetectState.setText("Kontrol Durdu...");
        }
    }


           //CallHelper Sınıfından incoming değişkenini almak için
           public void telefongonder (String telefongsm) {

               //Toast.makeText(this, String.valueOf(telefongsm), Toast.LENGTH_SHORT).show();
               //telefonnumara.setText(telefongsm);
               //UdpVerigonderme verigonderme = new UdpVerigonderme();
               //verigonderme.sendMessage(telefongsm,IPbilgi,portbilgi);

               BackgroundTask2 b1= new BackgroundTask2();
               b1.execute(telefongsm);
               Log.i("IP ve Port Bilgisi  ", " IP " +IPbilgi + " Port : " + portbilgi + " Telefon Numarası : " + telefongsm);
           }



            //TCP portuna gönderme bu gönderiyor
           class  BackgroundTask extends AsyncTask<String,Void,Void>
           {
                Socket s;
                PrintWriter writer;

               @Override
               protected Void doInBackground(String... voids) {
                try
                {

                    String numara = voids[0];
                    Log.i("IP ve Port , numara  ", "IP " +IPbilgi + "Port : " + portbilgi + "Telefon : "+numara);
                    s = new Socket(IPbilgi, Integer.parseInt(portbilgi));
                    writer = new PrintWriter(s.getOutputStream()) ;
                    writer.write(numara);
                    writer.flush();
                    writer.close();

                }catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return  null;
               }
           }



        //UDP  portuna gönderme deneme yaptım
        class  BackgroundTask2 extends AsyncTask<String,Void,Void>
        {
            @Override
            protected Void doInBackground(String... voids) {
                try
                {
                    String numara = voids[0];
                    DatagramSocket  ds = new DatagramSocket();
                    // IP Address below is the IP address of that Device where server socket is opened.
                    InetAddress serverAddr = null;
                    serverAddr =  InetAddress.getByName(IPbilgi);
                    ds.setBroadcast(true);
                    int mesaj_length = numara.length();
                    byte[] numaramesaji=numara.getBytes();
                    DatagramPacket dp = new DatagramPacket(numaramesaji , mesaj_length, serverAddr,Integer.parseInt(portbilgi));
                    ds.send(dp);
                    android.util.Log.w("UDP", "Güzel Çalıştı...");

                }catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return  null;
            }
        }


    //İzinleri isteme
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0) {
                    boolean kameraIzniVerildiMi = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (kameraIzniVerildiMi) {
                        telefonizin();
                    } else {
                        Toast.makeText(getApplicationContext(), "Arama İzni Verilmedi..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Arama İzni Verilmedi..", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public static boolean izinKontrol(Context context, String... izinler) {
        if (context != null && izinler != null) {
            for (String izin : izinler) {
                if (ActivityCompat.checkSelfPermission(context, izin) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }
    public void telefonizin() {
        // burası doldurulabilir
    }
}
