package com.example.tarika.urq;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReserveOnlineActivity extends AppCompatActivity {

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    String countStatus = ".";
    int countQ ;
    int countOpenReserve = 0 ;
    String [] arr1;
    String [] arr2;
    ListView listViewReserve;
    List<ListSearchStore> list;
    ArrayAdapter<ListSearchStore> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_online);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewReserve = (ListView) findViewById(R.id.listViewReserve);
        list = new ArrayList<ListSearchStore>(); // ListView ที่ใช้ในการแสดงรายชื่อร้านค้าทั้งหมดที่เปิดให้จองคิวได้

        // เข้าถึง database ฝั่ง User (ร้านค้า)
        mRootRef.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear(); // ทำ Listview ให้ว่าง
                for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) {
                    String qType = String.valueOf(shopSnapshot.child("shopData").child("reserve").child("reserveStatus").getValue());
                    if (qType.equals("0")){ // นับดูว่าจำนวนร้านค้าที่เปิดให้จองคิวมีกี่ร้าน
                        countOpenReserve = countOpenReserve +1;
                    }
                }

                arr1 = new String[countOpenReserve]; // ประกาศ array ขนาดเท่ากับร้านที่เปิดให้จองคิว
                arr2 = new String[countOpenReserve]; // ประกาศ array ขนาดเท่ากับร้านที่เปิดให้จองคิว

                int i =0;

                for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) {

                    String statusReserve = String.valueOf(shopSnapshot.child("shopData").child("reserve").child("reserveStatus").getValue());
                    if (statusReserve.equals("0")){ //ในกรณีพบว่าสถานะของร้านเปิดจอง
                        String shopName = String.valueOf(shopSnapshot.child("shopData").child("nameShop").getValue());

                        arr1[i] = new String(shopName); //เก็บชื่อร้านค้า

                        int k=1;
                        countQ =0;
                        countStatus = ".";
                        //ตรวจสอบว่าร้านค้าร้านมีคิวที่รออยู่กี่คิว
                        while (!countStatus.equals("null")){
                            countStatus = String.valueOf(shopSnapshot.child("qNumber").child(k+"").child("status").getValue());
                            if(countStatus.equals("q")){
                                countQ++; //นับคิว
                            }
                            k++;
                        }

                        arr2[i] = new String(countQ+""); //เก็บจำนวนคิวที่ต้องรอ

                        ListSearchStore l_search_store = new ListSearchStore(arr1[i],arr2[i]);
                        list.add(l_search_store); // นำไปใส่ใน ListView

                        i++;
                    }

                }
                adapter = new ReserveOnlineActivity.ListSearchStore_adapter();
                listViewReserve.setAdapter(adapter);

                // เมื่อกดที่ร้านที่นั้นๆ แล้วจะให้ไปยัง ReservationActivity
                listViewReserve.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ReserveOnlineActivity.this, ReservationActivity.class);
                        intent.putExtra("shopName", arr1[position]); // String ----> ส่งชื่อร้านไปยัง ReservationActivity
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    // Class สร้าง adapter เพื่อช่วยสร้าง Listview จากค่าของ array ที่ส่งเข้ามาให้
    class ListSearchStore_adapter extends ArrayAdapter<ListSearchStore>{
        ListSearchStore_adapter(){
            super(ReserveOnlineActivity.this,R.layout.item_listview_3,list);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_listview_3,parent,false);
            ListSearchStore l_search = list.get(position);

            TextView nameStore = (TextView)view.findViewById(R.id.nameReserveStore);
            TextView qStore = (TextView)view.findViewById(R.id.qReserveStore);
            TextView tvListView3_1 = (TextView)view.findViewById(R.id.tvListView3_1);
            TextView tvListView3_2 = (TextView)view.findViewById(R.id.tvListView3_2);

            Typeface tf_1=Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit.ttf");
            Typeface tf_2 = Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit_Bol.ttf");

            nameStore.setTypeface(tf_2);
            qStore.setTypeface(tf_1);
            tvListView3_1.setTypeface(tf_1);
            tvListView3_2.setTypeface(tf_1);

            nameStore.setText(l_search.getName_shop());
            qStore.setText(l_search.getQ_shop());

            return view;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu); // ลักษณะ Tool bar ที่ใช้
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();// กลับไปยัง Activity ก่อนหน้า
        }
        return super.onOptionsItemSelected(item);
    }



}
