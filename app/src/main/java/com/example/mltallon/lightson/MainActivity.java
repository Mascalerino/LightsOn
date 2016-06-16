package com.example.mltallon.lightson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity {

    private Switch mySwitch1,mySwitch2;
    private TextView myTextViewOutput;
    private final String IP_RBPI = "192.168.0.246";
    private final int PORT_LISTEN = 8080;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySwitch1 = (Switch) findViewById(R.id.switch1);
        mySwitch2 = (Switch) findViewById(R.id.switch2);
        myTextViewOutput = (TextView) findViewById(R.id.textView3);


        // Listener para los cambios de estado del Switch 1
        mySwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {

                    //Manda el mensaje L#1#ON para la RbPi
                    String msg = "L#1#ON";
                    new upnpContact().execute(msg);
                    Toast.makeText(getApplicationContext(), "La luz del Salón está encendida",Toast.LENGTH_SHORT).show();

                } else {

                    //Manda el mensaje L#1#OFF para la RbPi
                    String msg = "L#1#OFF";
                    new upnpContact().execute(msg);
                    Toast.makeText(getApplicationContext(),"La luz del Salón está apagada", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Listener para los cambios de estado del Switch 2
        mySwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {

                //Manda el mensaje L#2#ON para la RbPi
                String msg = "L#2#ON";
                new upnpContact().execute(msg);
                Toast.makeText(getApplicationContext(), "La luz del Jardín está encendida",Toast.LENGTH_SHORT).show();

                } else {

                //Manda el mensaje L#2#OFF para la RbPi
                String msg = "L#2#OFF";
                new upnpContact().execute(msg);
                Toast.makeText(getApplicationContext(),"La luz del Jardín está apagada", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    public class upnpContact extends AsyncTask<String,String,String>{


        @Override
        protected void onPreExecute() { //Before to do the task

            //Mandando un mensaje antes del switch
            myTextViewOutput.setText("Conectando...");

        }

        @Override
        protected String doInBackground(String... params) {

            String msgR = "";
            int attempts = 0;
            boolean received = false;


            while(!received && attempts <10) {

                try{

                    //Create a DatagramSocket and an InetAddress
                    DatagramSocket sk = new DatagramSocket(8080);
                    InetAddress addr = InetAddress.getByName(IP_RBPI);

                    //Get the size of message
                    int sizeMsg = params[0].length();

                    //Create the DatagramPacket with the size, ip and port to send
                    DatagramPacket dp = new DatagramPacket(params[0].getBytes(),sizeMsg, addr, PORT_LISTEN);

                    //Enviamos el paquete
                    sk.send(dp);

                    //Duración del circuito
                    sk.setSoTimeout(500);
                    while(!received){
                        try{
                            byte[] message = new byte[1500];

                            DatagramPacket p = new DatagramPacket(message, message.length);

                            sk.receive(p);
                            msgR = new String(message, 0, p.getLength());

                            if(msgR == "OK"){
                                msgR = "Cambiado";
                            }else{
                                msgR = "No cambiado";
                            }
                            received = true;
                            Log.e("MSG","Rec");

                            sk.close();

                        }catch (SocketTimeoutException e) {
                            sk.close();
                            msgR = ("Error SocketTimeoutException: " + e.toString());
                        }
                    }



                }catch (SocketException e) {

                    msgR = ("Error SocketException: " + e.toString());

                } catch (IOException e){

                    msgR = ("Error IOException: " + e.toString());

                }

                attempts++;

            }
            if(!received){

                msgR = "No se pudo establecer la conexión";
            }


            return msgR;
        }

        @Override
        protected void onPostExecute(String s) { //After to do the task

            //Set textViewOutput with the status or an error
            myTextViewOutput.setText(s);

        }


        }
    }



