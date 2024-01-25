package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener; //데이터 변경있을 때

    private Handler handler = new Handler();
    private Runnable runnable;
    private ArrayList<Orders> ordersList = new ArrayList<>();
    private TextView textViewOrders;
    private TextView textViewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewOrders = findViewById(R.id.textViewOrders); //대기번호 뿌리기
        textViewDate = findViewById(R.id.textViewDate); //날짜 뿌리기

        // 현재 날짜 설정
        updateCurrentDate();

        // Firebase 데이터베이스 초기화 및 ValueEventListener 설정
        setupFirebaseListener();

        // Runnable 초기화 및 시작
        setupRunnable();
    }

    private void setupFirebaseListener() {
        database = FirebaseDatabase.getInstance();
        String referencePath = String.format("Orders/2024/3/%d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        databaseReference = database.getReference(referencePath);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ordersList.clear(); //리스트 초기화(최신 데이터만 유지되도록)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //데베 경로에 있는 모든 자식 노드 순회
                    Orders order = snapshot.getValue(Orders.class); //json형식 데이터 -> 자바 객체로 매핑
                    //스냅샷: 데이터 앱으로 가져오는 데 사용, db변화 감지할 때마다 생성

                    if (order != null) { //스냅샷에 데이터 있으면 리스트에 추가
                        ordersList.add(order);
                    }
                }
                checkOrdersAgainstCurrentTime(); //리스트 업뎃한 후 메소드 호출
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Database error", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private void setupRunnable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                checkOrdersAgainstCurrentTime(); // 현재 시간에 해당하는 대기번호를 확인
                handler.postDelayed(this, 1000); // 1초마다 실행
            }
        };

        handler.post(runnable); // Runnable 실행
    }

    private void updateCurrentDate() { //화면에 현재날짜 표시
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        textViewDate.setText(currentDate);
    }

    private void checkOrdersAgainstCurrentTime() {
        Calendar now = Calendar.getInstance(); //인스턴스 생성하여 현재 시간 가져옴
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        int currentSecond = now.get(Calendar.SECOND);

        // StringBuilder를 사용하여 일치하는 모든 대기번호를 문자열로 빌드
        StringBuilder ordersText = new StringBuilder(); //변경 가능한 문자열 생성, 여러문자열 연결할 때 사용
        for (Orders order : ordersList) { //orderList: db에 저장된 주문 정보 담고 있음, 이 리스트에 저장된 Orders객체 순회
            Orders.Time orderTime = order.getTime(); //Orders객체에서 Time객체 가져옴
            if (orderTime.getHour() == currentHour &&
                    orderTime.getMinute() == currentMinute &&
                    orderTime.getSecond() == currentSecond) {
                // 시, 분, 초가 현재 시간과 정확히 일치할 때
                ordersText.append("대기번호: ").append(order.getWaitNumber()).append("\n");
            }
        }

        // 빌드된 문자열을 TextView에 설정
        if (ordersText.length() > 0) { //StringBuilder에 저장된 문자열의 길이 확인, 대기번호가 1개라도 있다면
            textViewOrders.setText(ordersText.toString());
        } else {
            textViewOrders.setText("현재 시간대에 대기번호가 없습니다.");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
        handler.removeCallbacks(runnable);
    }
}