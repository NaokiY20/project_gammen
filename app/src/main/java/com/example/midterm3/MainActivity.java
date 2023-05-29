package com.example.midterm3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button restartButton1;
    private Button restartButton2;
    private Button stopButton;
    private int AccMode;
    private TextView textView_mode;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_mode = findViewById(R.id.textView_mode);
        restartButton1 = findViewById(R.id.restartButton1);
        restartButton2 = findViewById(R.id.restartButton2);
        stopButton = findViewById(R.id.button_stop);

        // ボタンの動作定義
        restartButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                StopAccService();
                StartAccService(0);
                AccMode=0;
                updateModeText();
            }
        });
        restartButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                StopAccService();
                StartAccService(1);
                AccMode=1;
                updateModeText();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                StopAccService();
                AccMode=-1;
                updateModeText();
            }
        });


        AccMode = -1;
        updateModeText();
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

    // Serviceがどのmodeで動作しているかを確認するメソッド
    private boolean isAccServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AccelerometerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private int getModeAccService(){
        if(isAccServiceRunning()==false) return -1;
        else{
            return AccelerometerService.getMode();
        }
    }

    private void updateModeText(){
        String[] text_list={
                "まだ実行されていません。",
                String.format("%sを実行中です。",getString(R.string.mode1)),
                String.format("%sを実行中です。",getString(R.string.mode2))
        };
        String newText=text_list[this.AccMode+1];
        textView_mode.setText(newText);


    }



}