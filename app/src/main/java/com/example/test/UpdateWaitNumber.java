package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
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

public class UpdateWaitNumber extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener; //데이터 변경있을 때
    private Handler handler = new Handler();
    private Runnable runnable;
    private ArrayList<Orders> ordersList = new ArrayList<>();
    private TextView textViewOrders;
    private TextView textViewDate;
    private int totalCount = 0; //전체 대기 수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waitnumber);

        textViewOrders = findViewById(R.id.textViewOrders); //대기번호 뿌리기
        textViewDate = findViewById(R.id.textViewDate); //날짜 뿌리기
        // 현재 날짜 설정
        updateCurrentDate();

        // Firebase 데이터베이스 초기화 및 ValueEventListener 설정
        setupFirebaseListener();

        // Runnable 초기화 및 시작
        setupRunnable();
    }

    //db 변경사항 실시간 감지하기 위한 ValueEventListener설정
    private void setupFirebaseListener() {
        database = FirebaseDatabase.getInstance();
        String referencePath = "Orders/2024/3/" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        databaseReference = database.getReference(referencePath);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ordersList.clear(); // 리스트 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Orders order = snapshot.getValue(Orders.class);

                    if (order != null) {
                        ordersList.add(order); // 업데이트된 주문 리스트에 추가
                    }
                }
               checkOrdersTime();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UpdateWaitNumber", "Database error", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private void setupRunnable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                checkOrdersTime(); // 현재 시간에 해당하는 대기번호를 확인
                handler.postDelayed(this, 1000); // 1초마다 실행
            }
        };

        handler.post(runnable); // Runnable 실행
    }

    private void updateCurrentDate() { //화면에 현재날짜 표시
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        textViewDate.setText(currentDate);
    }


    //현재시간에 해당하는 대기번호 확인 후 화면에 표시하는 로직
    private void checkOrdersTime() {
        Calendar now = Calendar.getInstance();
        long nowMillis = now.getTimeInMillis();

        //영업시간 설정
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 9);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        long startTimeMillis = startTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, 20);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.SECOND, 0);
        long endTimeMillis = endTime.getTimeInMillis();

        if (nowMillis < startTimeMillis || nowMillis > endTimeMillis){
            updateUIWithClosed();
            return;
        }
        int currentCount = 0;

        // 주문 목록을 반복하여 현재 대기 번호 수를 계산
        for (Orders order : ordersList) {
            Calendar orderCalendar = Calendar.getInstance();
            Orders.Time orderTime = order.getTime();
            orderCalendar.set(Calendar.HOUR_OF_DAY, orderTime.getHour());
            orderCalendar.set(Calendar.MINUTE, orderTime.getMinute());
            orderCalendar.set(Calendar.SECOND, orderTime.getSecond());
            long orderTimeMillis = orderCalendar.getTimeInMillis();

            // 메뉴 아이템의 총 대기 시간 계산
            long totalWaitTimeMillis = order.getMenu().stream()
                    .mapToLong(item -> item.getQuantity() * 2 * 60 * 1000)
                    .sum();

            long orderEndTimeMillis = orderTimeMillis + totalWaitTimeMillis;

            // 주문이 현재 시간 이전에 들어왔다면
            if (orderTimeMillis <= nowMillis) {
                // 주문 완료 시간이 현재 시간을 지나지 않았으면 대기번호 증가
                if (orderEndTimeMillis > nowMillis) {
                    currentCount++;
                } else {
                    // 주문의 완료 시간이 현재 시간을 지났고 currentCount가 0보다 크면 대기번호 감소
                    if (currentCount > 0) {
                        currentCount--;
                    }
                }
            }
        }
        totalCount = currentCount; // 현재 계산된 대기 수를 totalCount에 반영
        updateUIWithCurrentCount(); // UI 업데이트
    }

    private void updateUIWithClosed() {
        String closedText = "영업종료";
        textViewOrders.setText(closedText);
    }

    public void addOrder(Orders newOrder){
        // 새 주문을 주문 리스트에 추가
        ordersList.add(newOrder);
        //대기번호 증가
        totalCount++;
        // UI 업데이트
        updateUIWithCurrentCount();
    }

    //totalCount 사용자에게 표시하는 역할
    private void updateUIWithCurrentCount() {
        String countText = "현재 대기 수: " + totalCount;
        textViewOrders.setText(countText);
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