package com.cookandroid.graduation_project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminReportListActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ArrayList<ReportData> arrayList;
    private String email;
    private ReportAdapter adapter;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        RecyclerView reportListView = findViewById(R.id.report_list);
        adapter = new ReportAdapter();
        reportListView.setAdapter(adapter);
        arrayList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = mDatabase.child("reports");

        //데이터 읽기
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ReportData reportData = postSnapshot.getValue(ReportData.class);
                    String key = postSnapshot.getKey();
                    reportData.setKey(key);
                    Log.d("PRINTARRAY", key);

                    arrayList.add(reportData); //데이터 배열에 넣고 리사이클러뷰로 보낼 준비
                    Log.d("PRINTARRAY", String.valueOf(arrayList));
                }
                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //참조에 액세스 할 수 없을 때 호출
                Toast.makeText(getApplicationContext(), "데이터를 가져오는데 실패했습니다", Toast.LENGTH_LONG).show();
            }
        });

    }




    class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_listitem_report, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.tv_address.setText(arrayList.get(position).getAddress());
            holder.tv_date.setText(arrayList.get(position).getTime());
            if(arrayList.get(position).isState()){
                holder.tv_report_status.setText("처리됨");
                holder.tv_report_status.setTextColor(Color.GREEN);
            }
            else{
                holder.tv_report_status.setText("미처리");
                holder.tv_report_status.setTextColor(Color.RED);
            }

        }

        @Override
        public int getItemCount() {
            return arrayList == null ? 0 : arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_date;
            public TextView tv_address;
            private Button btn_handle;
            private TextView tv_report_status;

            public ViewHolder(View view) {
                super(view);
                tv_date = view.findViewById(R.id.tv_report_date);
                tv_address = view.findViewById(R.id.tv_report_address);
                btn_handle = view.findViewById(R.id.btn_handle);
                tv_report_status = view.findViewById(R.id.tv_report_status);

                btn_handle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                        String key = arrayList.get(getAdapterPosition()).getKey();

                        DatabaseReference databaseReference = mDatabase.child("reports").child(key);
                        HashMap result = new HashMap<>();
                        result.put("state", true);
                        databaseReference.updateChildren(result);


                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AdminReportListActivity.this, MapActivity.class);
                        intent.putExtra("longitude", arrayList.get(getAdapterPosition()).getLongitude().toString());
                        intent.putExtra("latitude", arrayList.get(getAdapterPosition()).getLatitude().toString());

                        startActivity(intent);
                    }
                });

            }
        }
    }

}