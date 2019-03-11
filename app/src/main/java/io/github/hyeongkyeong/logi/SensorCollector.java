package io.github.hyeongkyeong.logi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorCollector implements SensorEventListener {
    private static final String TAG = "SensorCollector";

    /* 센서 관련 */
    private SensorManager mSensorManager = null;
    private Sensor mGyroSensor = null;
    private Sensor mAccSensor = null;
    private Sensor mMagSensor = null;
    private Sensor mLightSensor = null;
    private Sensor mProximitySensor = null;

    public double gyroSensor_val_x =0.0;
    public double gyroSensor_val_y =0.0;
    public double gyroSensor_val_z =0.0;
    public double accSensor_val_x =0.0;
    public double accSensor_val_y =0.0;
    public double accSensor_val_z =0.0;
    public double magSensor_val_x =0.0;
    public double magSensor_val_y =0.0;
    public double magSensor_val_z =0.0;
    public double lightSensor_val =0.0;
    public double proximity_val =0.0;

    public SensorCollector(Context context) {


        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG,"onSensorChanged()");
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_GYROSCOPE :
                gyroSensor_val_x = sensorEvent.values[0];
                gyroSensor_val_y = sensorEvent.values[1];
                gyroSensor_val_z = sensorEvent.values[2];
                Log.d("TAG", "[TYPE_GYROSCOPE]"+ "x:"+ gyroSensor_val_x +", y:"+ gyroSensor_val_y +", z:"+ gyroSensor_val_z);
                break;
            case Sensor.TYPE_ACCELEROMETER :
                accSensor_val_x = sensorEvent.values[0];
                accSensor_val_y = sensorEvent.values[1];
                accSensor_val_z = sensorEvent.values[2];
                Log.d("TAG", "[TYPE_ACCELEROMETER]"+ "x:"+ accSensor_val_x +", y:"+ accSensor_val_y +", z:"+ accSensor_val_z);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD :
                magSensor_val_x = sensorEvent.values[0];
                magSensor_val_y = sensorEvent.values[1];
                magSensor_val_z = sensorEvent.values[2];
                Log.d("TAG", "[TYPE_MAGNETIC_FIELD]"+ "x:"+ magSensor_val_x +", y:"+ magSensor_val_y +", z:"+ magSensor_val_z);
                break;
            case Sensor.TYPE_LIGHT:
                lightSensor_val = sensorEvent.values[0];
                Log.d("TAG", "[TYPE_LIGHT]"+ lightSensor_val);
                break;
            case Sensor.TYPE_PROXIMITY:
                proximity_val = sensorEvent.values[0];
                Log.d("TAG", "[TYPE_PROXIMITY]"+ proximity_val);
                break;
            default :
                break;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG,"onAccuracyChanged()");
    }

    public void register(){
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(){
        mSensorManager.unregisterListener(this);
    }
}
