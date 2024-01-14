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

    private ArrayList<Orders> arrayList;
    private Context context;

    public CustomAdapter(ArrayList<Orders> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_waitnumber, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        // 현재 위치의 Orders 객체에서 WaitNumber를 가져옴
        int waitNumber = arrayList.get(position).getWaitNumber();

        // "대기번호" 텍스트와 함께 WaitNumber를 설정
        holder.textViewWaitNumber.setText("대기번호: " + waitNumber);
        //holder.tv_WaitNumber.setText(String.valueOf(arrayList.get(position).getWaitNumber()));
    }

    @Override
    public int getItemCount() {
        // if구문과 비슷
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textViewWaitNumber;
        //TextView tv_WaitNumber;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWaitNumber = itemView.findViewById(R.id.tv_WaitNumber); // 실제 사용하는 ID로 변경
            //this.tv_WaitNumber = itemView.findViewById(R.id.tv_WaitNumber);

        }
    }
}
