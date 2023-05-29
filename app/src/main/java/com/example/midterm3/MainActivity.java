package com.example.midterm3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button restartButton1;
    private Button restartButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AccelerometerServiceの開始
        StartAccService(0);

        restartButton1 = findViewById(R.id.restartButton1);
        restartButton2 = findViewById(R.id.restartButton2);

        // ボタンの動作定義
        restartButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                StopAccService();
                StartAccService(0);
            }
        });
        restartButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                StopAccService();
                StartAccService(1);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        // AccelerometerServiceの停止
        StopAccService();
    }

    private void StartAccService(int mode){
        // AccelerometerServiceの開始 (0:アクティビティ起動による警告。1:通知による警告)
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        serviceIntent.putExtra("mode", mode);
        startService(serviceIntent);
    }

    private void StopAccService(){
        // AccelerometerServiceの停止
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        stopService(serviceIntent);
    }


}