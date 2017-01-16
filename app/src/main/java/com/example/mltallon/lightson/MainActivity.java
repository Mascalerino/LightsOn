package com.example.mltallon.lightson;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private Handler handler;
    private Timer timer;
    private Switch mySwitch1,mySwitch2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.activity_main );

        this.handler = new Handler();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.setStatus( R.string.status_init );
        mySwitch1 = (Switch) findViewById(R.id.switch1);
        mySwitch2 = (Switch) findViewById(R.id.switch2);

        mySwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    MainActivity.this.handler.post(new Runnable() {
                        public void run() {
                            try {
                                new LedManagament(MainActivity.this).execute(new URL(LedManagament.SALON_1));
                            } catch (MalformedURLException e) {
                                MainActivity.this.setStatus(R.string.status_incorrect_url);
                            }
                        }
                    });
                } else {
                    MainActivity.this.handler.post(new Runnable() {
                        public void run() {
                            try {
                                new LedManagament(MainActivity.this).execute(new URL(LedManagament.SALON_0));
                            } catch (MalformedURLException e) {
                                MainActivity.this.setStatus(R.string.status_incorrect_url);
                            }
                        }
                    });
                }

            }
        });

        mySwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    MainActivity.this.handler.post(new Runnable() {
                        public void run() {
                            try {
                                new LedManagament(MainActivity.this).execute(new URL(LedManagament.JARDIN_1));
                            } catch (MalformedURLException e) {
                                MainActivity.this.setStatus(R.string.status_incorrect_url);
                            }
                        }
                    });
                } else {
                    MainActivity.this.handler.post(new Runnable() {
                        public void run() {
                            try {
                                new LedManagament(MainActivity.this).execute(new URL(LedManagament.JARDIN_0));
                            } catch (MalformedURLException e) {
                                MainActivity.this.setStatus(R.string.status_incorrect_url);
                            }
                        }
                    });
                }

            }
        });
    }

    public void setStatus(int msgId) {
        final TextView lblStatus = (TextView) this.findViewById( R.id.lblStatus );

        lblStatus.setText( msgId );
    }
}