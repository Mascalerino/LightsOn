package com.example.mltallon.lightson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Switch mySwitch1,mySwitch2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySwitch1 = (Switch) findViewById(R.id.switch1);
        mySwitch2 = (Switch) findViewById(R.id.switch2);


        // Listener para los cambios de estado del Switch 1
        mySwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "La luz del Salón está encendida",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"La luz del Salón está apagada", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Listener para los cambios de estado del Switch 2
        mySwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "La luz del Jardín está encendida",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"La luz del Jardín está apagada", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }




}

