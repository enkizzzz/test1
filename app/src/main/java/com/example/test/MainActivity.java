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

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Orders> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView); //아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //orders 객체를 담을 어레이 리스트(어댑터쪽으로 날리기위해)

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Orders/2024/3/4"); // 주문이 저장된 경로
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear(); // 기존 데이터 초기화
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    // 각 주문의 고유 ID 아래 있는 WaitNumber 필드를 가져옴
                    Integer waitNumber = orderSnapshot.child("WaitNumber").getValue(Integer.class);
                    if (waitNumber != null) {
                        // 새 Orders 객체를 생성하고 WaitNumber만 설정
                        Orders orders = new Orders();
                        orders.setWaitNumber(waitNumber);
                        arrayList.add(orders); // 리스트에 추가
                    }
                }
                adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 발생 시 처리
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어뎁터 연결
    }
}