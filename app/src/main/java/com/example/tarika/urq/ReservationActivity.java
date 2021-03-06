package com.example.tarika.urq;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class ReservationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    Button btn_main_dialog_Logout;
    Button btn_main_dialog_cancel;

    String getName;
    int i=1;

    TextView name_reserve_store;
    Spinner sp_reserve_no_customer;

    TextView text_reserve_1;
    TextView text_reserve_time_open_close;
    TextView text_reserve_2;

    TextView text_reserve_3;
    TextView text_reserve_time_reserve;
    TextView text_reserve_4;

    TextView text_reserve_5;
    TextView text_reserve_6;
    TextView text_reserve_q_wait;

    Button btn_reserve;
    Button btn_reserve_dialog_ok;
    Button btn_reserve_dialog_cancel;

    int countQ =0;
    String countStatus = ".";

    EditText et_reserve_no_customer ;
    private boolean checkNo = true;
    TextView tv_dialog_name_shop;
    TextView tv_dialog_reserve_no_customer;
    String getUid;
    String getTable;
    int temp=0;
    int noRandomPin;

    int countDoing =0;
    int countFinish=0;
    String getNameFromUser;
    String numQnumber;
    String getQType;
    int avgServiceTime;

    TextView tvShow1;
    TextView tvShow2;
    TextView tvShow3;

    TextView tv_rv_1;
    TextView tv_rv_2;

    private boolean checkNameUser = false;
    String codeId;

    //@SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        codeId = mRootRef.push().getKey().toString();
        name_reserve_store = (TextView)findViewById(R.id.name_reserve_store);
        text_reserve_time_open_close = (TextView)findViewById(R.id.text_reserve_time_open_close);

        text_reserve_1 = (TextView)findViewById(R.id.text_reserve_1);
        text_reserve_2 = (TextView)findViewById(R.id.text_reserve_2);
        text_reserve_3 = (TextView)findViewById(R.id.text_reserve_3);
        text_reserve_time_reserve = (TextView)findViewById(R.id.text_reserve_time_reserve);
        text_reserve_4 = (TextView)findViewById(R.id.text_reserve_4);

        text_reserve_5 = (TextView)findViewById(R.id.text_reserve_5);   //  หากจองคิวต้องรอคิว
        text_reserve_q_wait = (TextView)findViewById(R.id.text_reserve_q_wait);
        text_reserve_6 = (TextView)findViewById(R.id.text_reserve_6);

        getName = getIntent().getExtras().getString("shopName"); // ค่าที่รับมา

        et_reserve_no_customer = (EditText)findViewById(R.id.et_reserve_no_customer) ;
        tv_rv_1 =(TextView)findViewById(R.id.tv_rv_1);
        tv_rv_2 =(TextView)findViewById(R.id.tv_rv_2);
        btn_reserve = (Button)findViewById(R.id.btn_reserve);

        //ตัวอักษร
        Typeface tf_1=Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit.ttf");
        Typeface tf_2 = Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit_Bol.ttf");
        name_reserve_store.setTypeface(tf_2);
        text_reserve_time_open_close.setTypeface(tf_2);
        text_reserve_1.setTypeface(tf_2);
        text_reserve_2.setTypeface(tf_2);
        text_reserve_3.setTypeface(tf_2);
        text_reserve_time_reserve.setTypeface(tf_2);
        text_reserve_4.setTypeface(tf_2);
        text_reserve_5.setTypeface(tf_2);
        text_reserve_q_wait.setTypeface(tf_2);
        text_reserve_6.setTypeface(tf_2);
        tv_rv_1.setTypeface(tf_2);
        tv_rv_2.setTypeface(tf_2);
        et_reserve_no_customer.setTypeface(tf_2);
        btn_reserve.setTypeface(tf_2);

        // เข้าถึงดาต้าเบสในส่วน User ฝั่งร้านค้า
        mRootRef.child("user").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) {

                    String shopName = String.valueOf(shopSnapshot.child("shopData").child("nameShop").getValue());
                    String reserveStatus = String.valueOf(shopSnapshot.child("shopData").child("reserve").child("reserveStatus").getValue());
                    if (shopName.equals(getName)){ // ถ้าชื่อร้านตรงกับค้าที่ร้านมา ก็จะดึงข้อมูลต่างๆออกมาแสดงผล
                        name_reserve_store.setText(shopName); // ชื่อร้าน
                        String timeOpen = String.valueOf(shopSnapshot.child("shopData").child("time").child("open").getValue()); //เวลาเปิด
                        String timeClose = String.valueOf(shopSnapshot.child("shopData").child("time").child("close").getValue()); //เวลาปิด
                        String timeReserveOpen = String.valueOf(shopSnapshot.child("shopData").child("reserve").child("reserveOpen").getValue()); //เวลาเปิดจองคิว
                        String timeReserveClose = String.valueOf(shopSnapshot.child("shopData").child("reserve").child("reserveClose").getValue()); //เวลาปิดจองคิว
                        text_reserve_time_open_close.setText(timeOpen + " - " + timeClose);
                        text_reserve_time_reserve.setText(timeReserveOpen + " - " + timeReserveClose);
                        getUid = String.valueOf(shopSnapshot.getKey());
                        getTable = String.valueOf(shopSnapshot.child("shopData").child("numServer").getValue());

                        //ส่วนี้เป็นส่วนที่ใช้นับจำนวนคิวที่มีอยู่ในร้านค้า เพื่อแสดงผลว่าตอนนี้ถ้าจองคิวต้องรอคิวอีกกี่คิว
                        int k=1;
                        countQ =0;
                        countFinish =0;
                        countDoing =0;
                        while (!countStatus.equals("null")){
                            countStatus = String.valueOf(shopSnapshot.child("qNumber").child(k+"").child("status").getValue());

                            if (countStatus.equals("finish")) {
                                countFinish++;

                            } else if (countStatus.equals("doing")) {
                                countDoing++;

                            } else if (countStatus.equals("q")) {
                                countQ++;

                            }

                            k++;
                        }
                            text_reserve_q_wait.setText(countQ+""); // จำนวนคิวที่ต้องรอหากจองคิว

                        //ตรวจสอบสถานะเปิดปิดจองคิว
                        if(reserveStatus.equals("0")){      //เปิดจอง

                            tv_rv_1.setVisibility(View.VISIBLE);
                            et_reserve_no_customer.setVisibility(View.VISIBLE);
                            tv_rv_2.setVisibility(View.VISIBLE);
                            btn_reserve.setVisibility(View.VISIBLE);

                        }else if (reserveStatus.equals("1")){  //ปิดจอง

                            tv_rv_1.setTextColor(Color.parseColor("#cac8ca"));
                            et_reserve_no_customer.setEnabled(false);
                            tv_rv_2.setTextColor(Color.parseColor("#cac8ca"));
                            btn_reserve.setEnabled(false);
                            btn_reserve.setBackgroundColor(R.color.black_grey);
                        }

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        // ส่วนที่เข้าถึง database ในส่วน customer โดยจะระบุตัวต้นจาก Uid (user.getUid())
                        mRootRef.child("customer").child(user.getUid()+"").child("Add").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) { //ใช้ Loop for ในการค้นหาร้านค้าที่มีอยู่ในระบบ

                                    String shopName = String.valueOf(shopSnapshot.child("nameShop").getValue());
                                    String nameUser = String.valueOf(shopSnapshot.child("nameUser").getValue());
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (shopName.equals(getName)&&nameUser.equals(user.getDisplayName()+"")){ //เช็คว่าชื่อผู้ใช้ตรงกับรายการที่ผู้ใช้มีอยู่ในรายการแจ้งหรือไม่ซ้ำจะไม่เปิดให้จอง ถ้าไม่ซ้ำจะเปิดให้จอง

                                        tv_rv_1.setTextColor(Color.parseColor("#cac8ca"));
                                        et_reserve_no_customer.setEnabled(false);
                                        tv_rv_2.setTextColor(Color.parseColor("#cac8ca"));
                                        btn_reserve.setEnabled(false);                      // ปิดปุ่มกดจอง
                                        btn_reserve.setBackgroundColor(R.color.black_grey);
                                    }else {
                                        tv_rv_1.setVisibility(View.VISIBLE);
                                        et_reserve_no_customer.setVisibility(View.VISIBLE);
                                        tv_rv_2.setVisibility(View.VISIBLE);
                                        btn_reserve.setVisibility(View.VISIBLE);            // เปิดปุ่มจดจอง

                                    }

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        // ส่วน EditText ที่รับค่าจำนวนลูกค้าที่ให้ผู้ใช้ระบุ แล้วเช็คว่าค่าที่รับมาถูกต้องหรือไม่
        et_reserve_no_customer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_reserve_no_customer.getText().toString().equals("0")&&i2>=0){
                    Toast.makeText(getApplicationContext(), "จำนวนที่จองจะเป็น0ไม่ได้" ,Toast.LENGTH_SHORT).show();
                    checkNo = false;
                    et_reserve_no_customer.setText("");
                }
                else{
                    checkNo = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });

    }

    public void onClickReserve (View view){ // กดจอง


        if (checkNo){ // ในกรณีที่ระบุเลขคิวถูกต้อง
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReservationActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_reservation_1,null);

            btn_reserve_dialog_ok = (Button)mView.findViewById(R.id.btn_reserve_dialog_ok);
            btn_reserve_dialog_cancel = (Button)mView.findViewById(R.id.btn_reserve_dialog_cancel);
            tv_dialog_name_shop = (TextView)mView.findViewById(R.id.tv_dialog_name_shop);
            tv_dialog_reserve_no_customer =(TextView)mView.findViewById(R.id.tv_dialog_reserve_no_customer);

            tv_dialog_name_shop.setText(getName);
            tv_dialog_reserve_no_customer.setText(et_reserve_no_customer.getText());

            tvShow1 = (TextView)mView.findViewById(R.id.tvShow1);
            tvShow2 = (TextView)mView.findViewById(R.id.tvShow2);
            tvShow3 = (TextView)mView.findViewById(R.id.tvShow3);

            Typeface tf_1=Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit.ttf");
            Typeface tf_2 = Typeface.createFromAsset(getAssets(),"fonts/TEPC_CM-Prasanmit_Bol.ttf");
            btn_reserve_dialog_ok.setTypeface(tf_2);
            btn_reserve_dialog_cancel.setTypeface(tf_2);
            tv_dialog_name_shop.setTypeface(tf_2);
            tv_dialog_reserve_no_customer.setTypeface(tf_2);
            tvShow1.setTypeface(tf_2);
            tvShow2.setTypeface(tf_2);
            tvShow3.setTypeface(tf_2);

            mBuilder.setView(mView);    // สร้าง pop up ขึ้นมา
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            //เข้าถึง database ฝัง User
            mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // get ค่าต่างๆที่จำเป็นออกมา
                    numQnumber = String.valueOf(dataSnapshot.child(getUid+"").child("qNumber").getChildrenCount()); // จำนวนคิวที่มีในร้านทั้งหมด
                    getNameFromUser = String.valueOf(dataSnapshot.child(getUid+"").child("shopData").child("nameShop").getValue()); // ชื่อร้าน
                    getQType = String.valueOf(dataSnapshot.child(getUid+"").child("shopData").child("qType").getValue()); // รูปแบบการเรียกคิว
                    temp = Integer.parseInt(numQnumber)+1; // ลำดับที่มันควรจะได้ (เลขคิว--ที่สร้างขึ้นมาใหม่)
                    avgServiceTime = Integer.parseInt(String.valueOf(dataSnapshot.child(getUid+"").child("shopData").child("avgServiceTime").getValue())); // เวลาที่ให้บริการโดยประมาณ


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            final Random rand = new Random();
            noRandomPin = rand.nextInt(9999-1000) + 1000; // ค่าตัวเลขที่สุ่มขึ้นมาจาก เพื่อเป็นเลข Pin code ให้ User ระบุตัวตนของตัวเอง

            //ปุ่มกดตกลง จองคิว
            btn_reserve_dialog_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    // โดยจะเขียนค่าต่างๆที่จำเป็นลงไป ทั้งฝั่ง database User และ database Customer

                    //ในส่วนของ User (ร้านค้า)
                    DatabaseReference nameCustomer = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("nameCustomer");
                    nameCustomer.setValue(user.getDisplayName());
                    DatabaseReference noCustomer = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("noCustomer");
                    noCustomer.setValue(et_reserve_no_customer.getText().toString());
                    DatabaseReference pin = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("pin");
                    pin.setValue(noRandomPin+"");
                    DatabaseReference addType = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("addType");
                    addType.setValue("1");
                    DatabaseReference repeat = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("repeat");
                    repeat.setValue(0+"");
                    DatabaseReference id = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("id");
                    id.setValue(temp+"");
                    DatabaseReference status = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("status");
                    status.setValue("q");

                    // ในส่วนของ customer (ลูกค้าที่ใช้บริการ)
                    DatabaseReference nameShop = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("nameShop");
                    DatabaseReference noPin = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noPin");
                    DatabaseReference noQ = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noQ");
                    DatabaseReference noCustomerCus = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noCustomer");
                    DatabaseReference noShop = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noShop");
                    DatabaseReference nameUser = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("nameUser");
                    DatabaseReference noCodeId = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("noCodeId");
                    DatabaseReference mCodeQType = mRootRef.child("customer").child(user.getUid()).child("Add").child(codeId+"").child("qType");
                    nameShop.setValue(getNameFromUser+"");
                    noPin.setValue(noRandomPin+"");
                    noQ.setValue(Integer.parseInt(numQnumber)+1);
                    noCustomerCus.setValue(et_reserve_no_customer.getText().toString()+"");
                    noShop.setValue(getUid+"");
                    nameUser.setValue(user.getDisplayName()+"");
                    noCodeId.setValue(codeId+"");
                    mCodeQType.setValue(getQType+"");


                    // set ค่าการแจ้งเตือน
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
                    mCodeNotificationSound.setValue("1");
                    mCodeAlarmSound.setValue("1");
                    mCodeTypeSound.setValue("0");
                    mCodeDetailTypeSound.setValue("22");
                    mCodeDetailTypeSound2.setValue("5");

                    dialog.cancel(); // ปิด pop up
                    Intent intent = new Intent(ReservationActivity.this, MainActivity.class); // กลับไปยัง MainActivity
                    startActivity(intent);

                }
            });
            //ปุ่มกดยกเลิก จองคิว
            btn_reserve_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel(); // ปิด pop up
                }
            });

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
            finish();  // กลับไปยัง Activity ก่อนหน้า
        }
        return super.onOptionsItemSelected(item);
    }

}
