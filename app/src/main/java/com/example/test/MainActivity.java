package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

//mainactivity: 메인화면 구성, 리사이클러뷰에 표시하기 위해 orders 객체 리스트 만들기
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomAdapter adapter; // Adapter 타입을 CustomAdapter로 변경
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Integer> arrayList; // 대기번호만 담을 ArrayList
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener; // ValueEventListener를 멤버 변수로 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        // 현재 날짜에 대한 참조 경로를 설정
        String referencePath = String.format("Orders/2024/3/%d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        databaseReference = database.getReference(referencePath);

        // ValueEventListener 초기화
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear(); // 기존 데이터 초기화
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Orders order = orderSnapshot.getValue(Orders.class);
                    if (order != null && order.getTime() != null) {
                        Orders.Time time = order.getTime();
                        // 현재 시간과 DB의 시간을 비교합니다.
                        Calendar now = Calendar.getInstance();
                        if (time.getHour() == now.get(Calendar.HOUR_OF_DAY) &&
                                time.getMinute() == now.get(Calendar.MINUTE) &&
                                time.getSecond() == now.get(Calendar.SECOND)) {
                            arrayList.add(order.getWaitNumber());
                        }
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        };
        // 리스너를 데이터베이스 참조에 추가합니다.
        databaseReference.addValueEventListener(valueEventListener);

        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); // RecyclerView에 Adapter 연결
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 데이터베이스 참조에서 리스너를 제거합니다.
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}