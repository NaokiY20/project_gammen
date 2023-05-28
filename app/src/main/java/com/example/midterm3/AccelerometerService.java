package com.example.midterm3;

// AccelerometerService.java

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class AccelerometerService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private static final String TAG = "AccService";

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // 加速度センサを取得
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 加速度センサの値を監視
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Log.d(TAG, "Start Service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // サービスが破棄されるときにセンサーの監視を停止
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 何もしない
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // z軸の加速度の値を取得
            float zValue = event.values[2];

            // z軸の加速度が-9を下回った場合、アクティビティを起動
            if (zValue < -9) {
                Intent intent = new Intent(this, MyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
