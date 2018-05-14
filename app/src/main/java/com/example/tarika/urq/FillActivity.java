package com.example.tarika.urq;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FillActivity extends AppCompatActivity {



    private String text;
    private boolean delete = false;
    private static final int CODE_SIZE=4;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    Button btn_fill_save;
    EditText et_num_q;
    EditText et_pin;
    Spinner areaSpinner;
    int temp=0;
    String countNo="0";
    String shopName;
    String noCustomer;
    String codeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        codeId = mRootRef.push().getKey().toString();
        btn_fill_save =(Button)findViewById(R.id.btn_fill_save);
        et_num_q =(EditText)findViewById(R.id.et_num_q);
        et_pin = (EditText)findViewById(R.id.et_pin);
        areaSpinner = (Spinner) findViewById(R.id.sp_location_q);

        // ตัวอักษร
        final Typeface tf_1 =Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit.ttf");
        final Typeface tf_2 = Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit_Bol.ttf");
        btn_fill_save.setTypeface(tf_2);
        et_num_q.setTypeface(tf_2);
        et_pin.setTypeface(tf_2);

        //การเข้าถึงข้อมูลฝั่ง User
        mRootRef.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> shop = new ArrayList<String>(); // สร้าง Listview เพื่อเก็บข้อมูลชื่อร้านค้าแล้วนำไปแสดงผล

                for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) { // วนเช็คว่าร้านที่มีในระบบมีร้านอะไรบ้าง
                    String shopName = String.valueOf(shopSnapshot.child("shopData").child("nameShop").getValue());
                    shop.add(shopName);
                }

                //แสดงชื่อร้านค้าที่มีทั้งหมดลงใน Spinner (areaSpinner)
                ArrayAdapter<String> shopsAdapter = new ArrayAdapter<String>(FillActivity.this, android.R.layout.simple_spinner_item, shop);
                shopsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(shopsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //เมื่อมีการกดเลือก areaSpinner
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                temp = i+1 ;
                final int[] k = {1};
                //เข้าถึงข้อมมูลในฝั่ง User ร้านค้า เพื่อนำค่าจำนวนร้านค้าที่มีมาใช้
                mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot numSnapshot: dataSnapshot.getChildren()) {
                            if(temp == k[0]){ // เเมื่อลำดับร้านที่เลือกใน Spinner ตรงกับข้อมูล จะนำค่าจำนวนคิวที่มีในระบบมาใช้
                                countNo = String.valueOf(numSnapshot.child("qNumber").getChildrenCount());
                                // a2.setText(countNo+"--");
                            }
                            k[0]++;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //เข้าถึงข้อมมูลในฝั่ง User ร้านค้า เพื่อดึงค่าชื่อร้านค้ามาแสดงผล
                mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 1;
                        for (DataSnapshot numSnapshot: dataSnapshot.getChildren()) {
                            if(temp == i){ // เมื่อเช็คว่าลำดับ Spinner ที่เลือกตรงกับร้านค้าที่หาเจอใน Database ในส่วนของ User จะดึงค่าชื่อมาใช้ต่อไป
                                shopName = String.valueOf(numSnapshot.child("shopData").child("nameShop").getValue());
                            }
                            i++;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //validate ค่า Pin code ที่ผู้ใช้ระบุมา โดยจะกำหนดให้เป็นตัวเลข 4 หลัก
        et_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int count, int after) {
                text = et_pin.getText().toString();
                if (count > after){
                    delete = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                StringBuilder sb = new StringBuilder(s.toString());
                int replacePosition = et_pin.getSelectionEnd();
                if (s.length() != CODE_SIZE) {
                    if (!delete) {
                        if (replacePosition < s.length())
                            sb.deleteCharAt(replacePosition);
                    } else {
                        sb.insert(replacePosition, '_');
                    }
                    if (replacePosition < s.length() || delete) {
                        et_pin.setText(sb.toString());
                        et_pin.setSelection(replacePosition);
                    } else {
                        et_pin.setText(text);
                        et_pin.setSelection(replacePosition - 1);
                    }
                }
                delete = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // เมื่อกดปุ่มบันทึกค่า
        btn_fill_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(v == btn_fill_save && et_num_q.getText().toString().equals("")){ // ในกรณีที่ไม่ระบุตัวเลขคิว
                    Toast.makeText(getApplicationContext(), "กรุณาระบุเลขคิว" ,Toast.LENGTH_SHORT).show();
                }
                else if(v == btn_fill_save && (Integer.parseInt(et_num_q.getText().toString()) > Integer.parseInt(countNo) )){ // ในกรณีที่ระบุคิวเกินจำนวนคิวทที่มีในระบบ
                    Toast.makeText(getApplicationContext(), "ไม่มีเลขคิวนี้ในระบบ" ,Toast.LENGTH_SHORT).show();
                }else if(et_num_q.getText().toString().equals("0") ){ // กรณีที่ระบุเลขคิวเป็น 0
                    Toast.makeText(getApplicationContext(), "กรุณาระบุเลขคิว" ,Toast.LENGTH_SHORT).show();
                }
                else if(et_num_q.getText().toString().length()!=0){ // กรณีที่ระบุเลขคิวถูกต้อง

                    // เข้าถึงข้อมูล database ฝั่ง User
                    mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int k = 1;
                            for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) {

                                String getPin = String.valueOf(shopSnapshot.child("qNumber").child(et_num_q.getText().toString()+"").child("pin").getValue());
                                String getUid = String.valueOf(shopSnapshot.getKey().toString());

                                if(k==temp){ // ดูว่าร้านที่เลือกตรงกับลำดับข้อมมูลที่อยู่ในร้านค้าหรือไม่

                                    if (getPin.equals(et_pin.getText().toString())){ // ในกรณีที่ระบุ Pin Code ถูกต้อง

                                        noCustomer = String.valueOf(shopSnapshot.child("qNumber").child(et_num_q.getText().toString()+"").child("noCustomer").getValue());
                                        String getQType = String.valueOf(shopSnapshot.child("shopData").child("qType").getValue());

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        // โดยจะนำค่าที่ได้จากฝั่ง User มาเขียนลงในฝั่ง Customer ข้อมูลทั่วไป
                                        DatabaseReference mCodeCodeId = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noCodeId");
                                        DatabaseReference mCodeShopRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noShop");
                                        DatabaseReference mCodeNoRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noQ");
                                        DatabaseReference mCodeNameRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("nameShop");
                                        DatabaseReference mCodePinRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noPin");
                                        DatabaseReference mCodeNoCustomerRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noCustomer");
                                        DatabaseReference mCodeQType = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("qType");
                                        DatabaseReference mCodeNameUser = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("nameUser");
                                        mCodeCodeId.setValue(codeId+"");                        // UniqueId ของร้านการนั้น
                                        mCodeShopRef.setValue(getUid+"");                       // Uid ของร้านค้า
                                        mCodeNoRef.setValue(et_num_q.getText().toString()+"");  // เลขคิวที่ต้องการระบุลงในการแจ้งเตือน
                                        mCodeNameRef.setValue(shopName);                        // ชื่อร้านค้า
                                        mCodePinRef.setValue(et_pin.getText().toString()+"");   // Pin code ของคิวนั้นๆ
                                        mCodeNoCustomerRef.setValue(noCustomer+"");             // จำนวนลูกค้าที่ระบุไว้
                                        mCodeQType.setValue(getQType+"");                       // รูปแบบการแจ้งเตือน
                                        mCodeNameUser.setValue(user.getDisplayName()+"");       // ชื่อลูกค้า

                                        // โดยจะนำค่าที่ได้จากฝั่ง User มาเขียนลงในฝั่ง Customer ข้อมูลเกี่ยวกับการแจ้งเตือน
                                        DatabaseReference mCodeNotificationSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"")
                                                .child("notification").child("sound");
                                        DatabaseReference mCodeAlarmSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"")
                                                .child("notification").child("alarm");
                                        DatabaseReference mCodeTypeSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"")
                                                .child("notification").child("type");
                                        DatabaseReference mCodeDetailTypeSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"")
                                                .child("notification").child("detailType");
                                        DatabaseReference mCodeDetailTypeSound2 = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"")
                                                .child("notification").child("detailType2");
                                        mCodeNotificationSound.setValue("1");       // เปิดปิดการแจ้งเตือน (0ปิด,1เปิด)
                                        mCodeAlarmSound.setValue("1");              // เปิดปิดเสียงแจ้งเตือน (0ปิด,1เปิด)
                                        mCodeTypeSound.setValue("0");               // รูปแบบการแจ้งเตือน (0คือทุกคิว, 1คือแจ้งก่อนกี่คิวตามจำนวณที่กำหนด , 2คือแจ้งเตือนก่อนเวลาที่กำหนด)
                                        mCodeDetailTypeSound.setValue("22");        // เก็บค่าเลขที่ต้องการให้รอก่อนถึงคิว
                                        mCodeDetailTypeSound2.setValue("5");        // เก็บค่าเวลาทที่ต้องการให้แจ้งเตือนก่อนถึงเวลา

                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class); // ไปยังหน้า MainActivity
                                        startActivity(intent);

                                    }else {

                                        if (et_pin.getText().toString().length()<4){ //ในกรณีที่ระบุ Code Pin ไม่ครบ 4 หลัก
                                            Toast.makeText(getApplicationContext(), "คุณระบุเลขไม่ครบ" ,Toast.LENGTH_SHORT).show();

                                        }else {                                      // ระบุเลขคิวไม่ตรงกับในฐานข้อมมูล
                                            Toast.makeText(getApplicationContext(), "คุณระบุเลข pin ผิด" ,Toast.LENGTH_SHORT).show();

                                        }

                                    }

                                }
                                k++;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }else{ // ในกรณีที่ไม่รุบุเลขคิวแล้วกดบันทึก
                    Toast.makeText(getApplicationContext(), "กรุณาระบุเลขคิว" ,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);     // ลักษณะ Tool bar ที่ใช้
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();    // กลับไปยัง Activity ก่อนหน้า
        }
        return super.onOptionsItemSelected(item);
    }

}
