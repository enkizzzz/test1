package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitnumber);

        // 바로 UpdateWaitNumber 액티비티로 이동하길 원한다면 아래 코드를 추가
        Intent intent = new Intent(this, UpdateWaitNumber.class);
        startActivity(intent);

        // 이후에는 MainActivity는 종료되거나, 사용자가 뒤로 가기를 통해 돌아올 수 있음
    }
}