package com.example.mltallon.lightson;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LedManagament extends AsyncTask<URL, Void, Boolean> {

    public static final String LOG_TAG = "LedManagament";

    public static final String SALON_0 = "http://193.146.46.24:46464/control.html?salon=off";
    public static final String SALON_1 = "http://193.146.46.24:46464/control.html?salon=on";
    public static final String JARDIN_0 = "http://193.146.46.24:46464/control.html?jardin=off";
    public static final String JARDIN_1 = "http://193.146.46.24:46464/control.html?jardin=on";

    private MainActivity activity;

    public LedManagament(MainActivity activity) {
        this.activity = activity;
    }


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
            // Check connectivty
            Log.d( LOG_TAG, " in doInBackground(): checking connectivity" );
            ConnectivityManager connMgr = (ConnectivityManager)  this.activity.getSystemService( Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            boolean connected = ( networkInfo != null && networkInfo.isConnected() );
            Log.d( LOG_TAG, " in doInBackground(), connected: " + connected );

            if ( !connected ) {
                this.activity.setStatus( R.string.status_not_connected );
            } else {
                // Connection
                Log.d( LOG_TAG, " in doInBackground(): connecting" );
                HttpURLConnection conn = (HttpURLConnection) urls[ 0 ].openConnection();
                conn.setReadTimeout( 1000  );
                conn.setConnectTimeout( 1000  );
                conn.setRequestMethod( "GET" );
                conn.setDoInput( true );

                // Obtain the answer
                conn.connect();
                int responseCode = conn.getResponseCode();
                Log.d( LOG_TAG, String.format( " in doInBackground(): server response is: %s(%d)",
                        conn.getResponseMessage(),
                        responseCode ) );
                is = conn.getInputStream();

                toret = true;
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

    private String getStringFromStream(InputStream is)
    {
        BufferedReader reader = null;
        StringBuilder toret = new StringBuilder();
        String line;

        try {
            reader = new BufferedReader( new InputStreamReader( is ) );
            while( ( line = reader.readLine() ) != null ) {
                toret.append( line );
            }
        } catch (IOException e) {
            Log.e( LOG_TAG, " in getStringFromString(): error converting net input to string"  );
        }

        return toret.toString();
    }

    @Override
    public void onPostExecute(Boolean result)
    {
        final TextView lblStatus = (TextView) this.activity.findViewById( R.id.lblStatus );
        int idFinalStatus = R.string.status_ok;

        if ( !result ) {
            idFinalStatus = R.string.status_error;
            Log.i( LOG_TAG, " in onPostExecute(): led managament incorrectly" );
        } else {
            Log.i( LOG_TAG, " in onPostExecute(): led managament ok" );
        }

        lblStatus.setText( idFinalStatus );

    }
}