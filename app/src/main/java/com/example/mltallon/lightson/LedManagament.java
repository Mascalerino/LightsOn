package com.example.mltallon.lightson;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class LedManagament extends AsyncTask<URL, Void, Boolean> {

    public static final String LOG_TAG = "LedManagament";

    //URLs para el encendido y apagado de los leds
    public static final String SALON_0 = "http://193.146.46.24:46464/control.html?salon=off";
    public static final String SALON_1 = "http://193.146.46.24:46464/control.html?salon=on";
    public static final String JARDIN_0 = "http://193.146.46.24:46464/control.html?jardin=off";
    public static final String JARDIN_1 = "http://193.146.46.24:46464/control.html?jardin=on";

    private MainActivity activity;

    public LedManagament(MainActivity activity) {
        this.activity = activity;
    }
    public URL actual = null;

    @Override
    protected void onPreExecute() {
        final TextView lblStatus = (TextView) this.activity.findViewById( R.id.lblStatus );

        this.activity.setStatus( R.string.status_connecting );

    }

    @Override
    protected Boolean doInBackground(URL... urls) {
        InputStream is = null;
        boolean toret = false;

        try {
            // Comprobando Conexion
            Log.d( LOG_TAG, " in doInBackground(): checking connectivity" );
            ConnectivityManager connMgr = (ConnectivityManager)  this.activity.getSystemService( Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            boolean connected = ( networkInfo != null && networkInfo.isConnected() );
            Log.d( LOG_TAG, " in doInBackground(), connected: " + connected );

            if ( !connected ) {
                this.activity.setStatus( R.string.status_not_connected );
            } else {
                // Conexión
                Log.d( LOG_TAG, " in doInBackground(): connecting" );
                HttpURLConnection conn = (HttpURLConnection) urls[ 0 ].openConnection();
                conn.setReadTimeout( 1000  );
                conn.setConnectTimeout( 1000  );
                conn.setRequestMethod( "GET" );
                conn.setDoInput( true );

                // Obtenemos la respuesta
                conn.connect();
                int responseCode = conn.getResponseCode();
                Log.d( LOG_TAG, String.format( " in doInBackground(): server response is: %s(%d)",
                        conn.getResponseMessage(),
                        responseCode ) );
                is = conn.getInputStream();

                toret = true;
                actual = urls[0];
                Log.d( LOG_TAG, " in doInBackground(): finished" );
            }
        }
        catch(IOException exc) {
            Log.e( LOG_TAG, " in doInBackground(), connecting: " + exc.getMessage() );
        } finally {
            if ( is != null ) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e( LOG_TAG, " in doInBackGround(): error closing input stream" );
                }
            }
        }

        return toret;
    }

    @Override
    public void onPostExecute(Boolean result)
    {
        final TextView lblStatus = (TextView) this.activity.findViewById( R.id.lblStatus );
        int idFinalStatus = R.string.status_ok;

        if ( !result ) {
            idFinalStatus = R.string.status_error;
            Log.i( LOG_TAG, " in onPostExecute(): led managament incorrecto" );
        } else {
            Log.i( LOG_TAG, " in onPostExecute(): led managament correcto" );
            guardaTipoLed(actual, this.activity);
        }

        lblStatus.setText( idFinalStatus );

    }

    protected void guardaTipoLed(URL url, Context c){
        if (url.toString().equalsIgnoreCase(SALON_0)){
            SharedPreferences.Editor editor = c.getSharedPreferences("APP", Context.MODE_PRIVATE).edit();
            editor.putBoolean("salon", false);
            editor.commit();
        }else if (url.toString().equalsIgnoreCase(SALON_1)){
            SharedPreferences.Editor editor = c.getSharedPreferences("APP", Context.MODE_PRIVATE).edit();
            editor.putBoolean("salon", true);
            editor.commit();
        }else if (url.toString().equalsIgnoreCase(JARDIN_0)){
            SharedPreferences.Editor editor = c.getSharedPreferences("APP", Context.MODE_PRIVATE).edit();
            editor.putBoolean("jardin", false);
            editor.commit();
        } else if (url.toString().equalsIgnoreCase(JARDIN_1)){
            SharedPreferences.Editor editor = c.getSharedPreferences("APP", Context.MODE_PRIVATE).edit();
            editor.putBoolean("jardin", true);
            editor.commit();
        }
    }
}