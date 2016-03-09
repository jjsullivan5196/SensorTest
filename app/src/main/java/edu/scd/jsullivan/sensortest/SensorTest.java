package edu.scd.jsullivan.sensortest;

import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class SensorTest extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private TextView xrot, yrot, zrot, xacc, yacc, zacc;
    private boolean record;
    private OutputStreamWriter gyroStream;
    private OutputStreamWriter accStream;
    private Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        record = false;

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        xrot = (TextView)findViewById(R.id.xval);
        yrot = (TextView)findViewById(R.id.yval);
        zrot = (TextView)findViewById(R.id.zval);

        xacc = (TextView)findViewById(R.id.xaval);
        yacc = (TextView)findViewById(R.id.yaval);
        zacc = (TextView)findViewById(R.id.zaval);

        go = (Button)findViewById(R.id.button_go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(!record) {
                    try {
                        gyroStream = new OutputStreamWriter(openFileOutput(("gyro" + Long.toString(System.currentTimeMillis()/1000) + ".csv"), Context.MODE_PRIVATE));
                        accStream = new OutputStreamWriter(openFileOutput(("acc" + Long.toString(System.currentTimeMillis()/1000) + ".csv"), Context.MODE_PRIVATE));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    record = true;
                    go.setText("Stop");
                }
                else if(record) {
                    record = false;
                    go.setText("Go");
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if(sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //Super-duper CSV writer ft. Java
            String writeline = Float.toString(event.values[0]) + ',' + Float.toString(event.values[1]) + ',' + Float.toString(event.values[2]) + "\n";
            xrot.setText(Float.toString(event.values[0]));
            yrot.setText(Float.toString(event.values[1]));
            zrot.setText(Float.toString(event.values[2]));
            try {
                if (record) gyroStream.write(writeline);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            String writeline = Float.toString(event.values[0]) + ',' + Float.toString(event.values[1]) + ',' + Float.toString(event.values[2]) + "\n";
            xacc.setText(Float.toString(event.values[0]));
            yacc.setText(Float.toString(event.values[1]));
            zacc.setText(Float.toString(event.values[2]));
            try {
                if (record) accStream.write(writeline);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }
}
