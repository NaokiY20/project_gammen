package com.example.midterm3;

// AccelerometerService.java

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AccelerometerService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private static final String TAG = "AccService";

    private long lastStartActivityTime = -1;
    private static long marginTime = 5000; // 再度警告までの猶予

    private int mode = 0; //監視Serviceのモード。(0:アクティビティ起動による警告。1:通知による警告)

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
    public int onStartCommand(Intent intent, int flags, int startId){
        // Intentからデータを取得
        mode = intent.getIntExtra("mode",0);

        return START_STICKY;
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
            float yValue = event.values[1];


            // z軸の加速度が-9を下回った場合、アクティビティを起動
            if (zValue < -9) {
                long time_now = SystemClock.elapsedRealtime();
//                Log.d(TAG, String.format("time now = %d", time_now));
//                Log.d(TAG, String.format("last now = %d", lastStartActivityTime));
                if (lastStartActivityTime+marginTime<time_now){
                    notification();
                    lastStartActivityTime = time_now;
                }
            }
        }
    }

    public void notification() {
        Log.d(TAG,String.format("start noti mode = %d",mode));
        switch (mode){
            case 0:
                Intent intent = new Intent(this, MyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case 1:
                // 通知の実行
                showNotification();
                break;
        }
//        Intent intent = new Intent(this, MyActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
        // 通知の実行
//        showNotification();
//        showPopupNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
        // 通知を作成
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.smartphone_neru_boy_smash)
                .setContentTitle("顔面衝突防止アプリ")
                .setContentText("現在危険な状態です。体勢を変えてください！")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 通知を表示
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "notification_failed");
            return;
        }
        notificationManager.notify(1, builder.build());
    }

//    private void showPopupNotification() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("ポップアップ通知");
//        builder.setMessage("危険な状態です！");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
}
