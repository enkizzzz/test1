package com.example.test;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Integer> arrayList; //Orders 객체 배열리스트 저장
    private Context context;

    public CustomAdapter(ArrayList<Integer> arrayList, Context context) {
        this.arrayList = arrayList; //생성자에서 두 변수 초기화
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //context가 필요한 이유: LayoutInflater를 이용해서 xml레이아웃 파일을 뷰 객체로 변환하는데 필요
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_waitnumber, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        // 현재 위치의 Orders 객체에서 WaitNumber를 얻음
        int waitNumber = arrayList.get(position);

        // "대기번호" 텍스트와 함께 WaitNumber를 설정
        holder.textViewWaitNumber.setText("대기번호: " + waitNumber);
    }

    @Override
    //arraylist의 크기가 null이 아니면 크기 반환, null이면 0반환
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }


    //이곳에서 list_waitnumber,xml에 정의한 텍스트뷰를 불러옴
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWaitNumber;

        //View 객체: RecyclerView의 개별 아이템 나타냄, inCreateViewHolder 메소드에서 생성, 반환
        public CustomViewHolder(@NonNull View itemView) {
            //super: RecyclerView.ViewHolder 클래스의 생성자 호출하는 역할,
            //super: itemView를 ViewHolder의 내부구조에 저장, 뷰에 접근하게 함
            super(itemView);
            // 텍스트뷰에 저장된 아이디에 해당하는 뷰를 itemView의 레이아웃 계층구조 내에서 찾고 변수에 저장
            textViewWaitNumber = itemView.findViewById(R.id.tv_WaitNumber);

        }
    }
}