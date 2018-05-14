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

public class SearchStoreActivity extends AppCompatActivity {

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    String countStatus = ".";
    int countQ = 0;
    ListView listViewStore;
    TextView testData;
    String countUser = ".";
    String [] arr1;
    String [] arr2;
    List<ListSearchStore> list;
    ArrayAdapter<ListSearchStore> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_store);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewStore = (ListView)findViewById(R.id.listViewStore);
        testData = (TextView)findViewById(R.id.testData);

        list = new ArrayList<ListSearchStore>();    // Listview ที่ใช้ในการแสดงร้านค้าและจำนวนคิวที่ต้องรอ

        //เข้าถึงข้อมมูลทางดาต้าเบสในฝั่งของ User
        mRootRef.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear(); //ทำ Listview ให้ว่าง
                countUser = String.valueOf(dataSnapshot.getChildrenCount());
                //สร้าง array ตามขนาดของจำนวนร้านค้าที่มีอยู่ทั้งหมด
                arr1 = new String[Integer.parseInt(countUser)]; //
                arr2 = new String[Integer.parseInt(countUser)];

                int i = 0;
                for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) { //ใช้วน get ค่า ชื่อร้าน คิวที่ต้องรอ
                    String shopName = String.valueOf(shopSnapshot.child("shopData").child("nameShop").getValue()); // ดึงชื่อแต่ละร้านร้านมาใช้
                    arr1[i] = new String(shopName);
                    int k=1;
                    countQ =0;
                    countStatus = ".";
                    while (!countStatus.equals("null")){ // คำนวณหาจำนวนที่ต้องรอคิว
                        countStatus = String.valueOf(shopSnapshot.child("qNumber").child(k+"").child("status").getValue());
                        if(countStatus.equals("q")){
                            countQ++; // เพิ่มเพื่อเมื่อ status เป็น "q"
                        }
                        k++;
                    }
                    arr2[i] = new String(countQ+"");
                    ListSearchStore l_search_store = new ListSearchStore(arr1[i],arr2[i]); // นำชื่อร้าน และจำนวนคิวที่ต้องรอ ใส่ใน ListView
                    list.add(l_search_store);
                    i++;

                }

                adapter = new ListSearchStore_adapter();
                listViewStore.setAdapter(adapter);

                listViewStore.setOnItemClickListener(new AdapterView.OnItemClickListener() { // กดเพื่อเข้าไปดูรายละเอียดร้านค้าที่ต้องการ
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(SearchStoreActivity.this, ReservationActivity.class);
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
            super(SearchStoreActivity.this,R.layout.item_listview_1,list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_listview_1,parent,false);
            ListSearchStore l_search = list.get(position);

            TextView nameStore = (TextView)view.findViewById(R.id.nameStore);
            TextView qStore = (TextView)view.findViewById(R.id.qStore);
            TextView tvSearch1 = (TextView)view.findViewById(R.id.tvSearch1);
            TextView tvSearch2 = (TextView)view.findViewById(R.id.tvSearch2);

            Typeface tf_1 = Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit.ttf");
            Typeface tf_2 = Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit_Bol.ttf");

            nameStore.setTypeface(tf_2);
            qStore.setTypeface(tf_1);

            tvSearch1.setTypeface(tf_1);
            tvSearch2.setTypeface(tf_1);

            nameStore.setText(l_search.getName_shop());
            qStore.setText(l_search.getQ_shop());

            return view;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);  // ลักษณะ Tool bar ที่ใช้
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){ // กลับไปยัง Activity ที่ผ่านมา
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



}
