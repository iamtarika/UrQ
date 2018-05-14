package com.example.tarika.urq;

import android.graphics.Color;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationActivity extends AppCompatActivity {

    Switch sw_alarm , sw_notification;
    TextView tv_notification_setting;

    Spinner sp;
    String format[] = {"ทุกคิว", "จำนวนที่กำหนด", "ก่อนระยะเวลาที่กำหนด"}; // เลือกรูปแบบในการตั้งค่าการแจ้งเตือนแบบมีการคำนวณเวลา
    ArrayAdapter<String> adapter;

    Spinner sp_2;
    String format_2[] = {"5 นาที", "10 นาที" , "20 นาที", "30 นาที" ,"60 นาที"};    // เวลาที่ต้องการจะรอ
    ArrayAdapter<String> adapter_time;

    Spinner sp_3;
    String format_3[] = {"ทุกคิว", "จำนวนที่กำหนด" };   // รูปแบบการเลือกแบบไม่มีการคำนวนเวลา
    ArrayAdapter<String> adapter_3;

    EditText ed_add_num;
    TextView tv_detail_1 , tv_detail_2;
    String location;
    String num_text;
    String getUniqueId;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    boolean check = false;
    String notificationTypeBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sw_alarm = (Switch) findViewById(R.id.sw_alarm);
        sw_notification = (Switch) findViewById(R.id.sw_notification);

        tv_notification_setting = (TextView) findViewById(R.id.tv_notification_setting);
        tv_notification_setting.setTextColor(Color.parseColor("#cac8ca"));

        sp = (Spinner) findViewById(R.id.sp_setting);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, format);
        sp.setAdapter(adapter);
        sp.setEnabled(false);

        sp_2 = (Spinner) findViewById(R.id.sp_time);
        adapter_time = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, format_2);
        sp_2.setAdapter(adapter_time);
        sp_2.setVisibility(View.GONE);

        sp_3 = (Spinner) findViewById(R.id.sp_setting_3);
        adapter_3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, format_3);
        sp_3.setAdapter(adapter_3);
        sp_3.setVisibility(View.GONE);

        ed_add_num = (EditText) findViewById(R.id.ed_add_num);
        ed_add_num.setVisibility(View.GONE);

        tv_detail_1 = (TextView) findViewById(R.id.tv_detail_1);
        tv_detail_1.setVisibility(View.GONE);

        tv_detail_2 = (TextView) findViewById(R.id.tv_detail_2);
        tv_detail_2.setVisibility(View.GONE);

        //ค่าที่รับมา
        location = getIntent().getExtras().getString("location");
        num_text = getIntent().getExtras().getString("myNumber");
        getUniqueId =getIntent().getExtras().getString("uniqueId");


        //อ่านค่าในส่วนของ customer
        mRootRef.child("customer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // ประกาศ user เพ่อจะนำค่า Uid มาใช้เพื่อระบุตัวต้น

                //อ่านค่าต่างๆ ที่จำเป็นต้องนำมาแสดงผลหน้าของการแจ้งเตือน
                String qType = String.valueOf(dataSnapshot.child(user.getUid() + "").child("Add").
                        child(getUniqueId + "").child("qType").getValue());
                String notificationSound = String.valueOf(dataSnapshot.child(user.getUid() + "").child("Add").
                        child(getUniqueId + "").child("notification").child("sound").getValue());
                String notificationAlarm = String.valueOf(dataSnapshot.child(user.getUid() + "").child("Add").
                        child(getUniqueId + "").child("notification").child("alarm").getValue());
                String notificationType = String.valueOf(dataSnapshot.child(user.getUid() + "").child("Add").
                        child(getUniqueId + "").child("notification").child("type").getValue());
                String notificationDetailType = String.valueOf(dataSnapshot.child(user.getUid() + "").child("Add").
                        child(getUniqueId + "").child("notification").child("detailType").getValue());
                String notificationDetailType2 = String.valueOf(dataSnapshot.child(user.getUid() + "").child("Add").
                        child(getUniqueId + "").child("notification").child("detailType2").getValue());

                ed_add_num.setText(notificationDetailType + "");

                if (notificationSound.equals("1")) {    //ส่วนของเปิด-ปิดเสียงการแจ้งเตือน 1 เปิด 0 ปิด
                    sw_alarm.setChecked(true);
                } else if (notificationSound.equals("0")) {
                    sw_alarm.setChecked(false);
                }

                if (qType.equals("1")) {  //ในกรณีที่ร้านไม่มีการคำนวนเวลา
                    sp.setVisibility(View.GONE);

                    if (notificationType.equals("0")) {     //แจ้งเตือนทุกคิว

                        tv_notification_setting.setEnabled(true);   // รูปแบบการแจ้งเตือน
                        sp_3.setEnabled(true);
                        sp_3.setVisibility(View.VISIBLE);

                        tv_detail_1.setEnabled(false);
                        ed_add_num.setEnabled(false);
                        tv_detail_1.setVisibility(View.GONE);
                        ed_add_num.setVisibility(View.GONE);

                        String myString = "ทุกคิว"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp_3.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp_3.setSelection(spinnerPosition);

                    } else if (notificationType.equals("1")) {      //แจ้งเตือนก่อนจำนวนคิวที่ต้องการ

                        tv_notification_setting.setEnabled(true);   // รูปแบบการแจ้งเตือน
                        sp_3.setEnabled(true);
                        sp_3.setVisibility(View.VISIBLE);

                        tv_detail_1.setEnabled(true);
                        ed_add_num.setEnabled(true);
                        tv_detail_1.setVisibility(View.VISIBLE);
                        ed_add_num.setVisibility(View.VISIBLE);

                        String myString = "จำนวนที่กำหนด"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp_3.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp_3.setSelection(spinnerPosition);
                    }

                    if (notificationAlarm.equals("1")) {
                        sw_notification.setChecked(true);
                        tv_notification_setting.setTextColor(Color.parseColor("#000000"));
                        tv_detail_2.setTextColor(Color.parseColor("#000000"));
                        tv_detail_1.setEnabled(true);
                        sp_3.setEnabled(true);
                        ed_add_num.setEnabled(true);
                    } else if (notificationAlarm.equals("0")) {
                        sw_notification.setChecked(false);
                        tv_notification_setting.setTextColor(Color.parseColor("#cac8ca"));
                        tv_detail_1.setEnabled(false);
                        tv_detail_2.setTextColor(Color.parseColor("#cac8ca"));
                        sp_3.setEnabled(false);
                        ed_add_num.setEnabled(false);
                    }


                } else if (qType.equals("0")) { //ในกรณีที่ร้านมีการคำนวนเวลา
                    //หมูกระทะ

                    if (notificationType.equals("0")) {             //แจ้งเตือนทุกคิว
                        tv_notification_setting.setEnabled(true);   // รูปแบบการแจ้งเตือน
                        sp.setEnabled(true);                        // spinner เลือกรูปแบบ

                        tv_detail_1.setEnabled(false);
                        ed_add_num.setEnabled(false);
                        tv_detail_1.setVisibility(View.GONE);
                        ed_add_num.setVisibility(View.GONE);

                        tv_detail_2.setEnabled(false);
                        sp_2.setEnabled(false);
                        tv_detail_2.setVisibility(View.GONE);
                        sp_2.setVisibility(View.GONE);

                        String myString = "ทุกคิว"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp.setSelection(spinnerPosition);

                    } else if (notificationType.equals("1")) {      //แจ้งเตือนก่อนจำนวนคิวที่ต้องการ
                        tv_notification_setting.setEnabled(true);   // รูปแบบการแจ้งเตือน
                        sp.setEnabled(true);                        // spinner เลือกรูปแบบ

                        tv_detail_1.setEnabled(true);
                        ed_add_num.setEnabled(true);
                        tv_detail_1.setVisibility(View.VISIBLE);
                        ed_add_num.setVisibility(View.VISIBLE);

                        tv_detail_2.setEnabled(false);              // เวลาที่ต้องการ
                        sp_2.setEnabled(false);
                        tv_detail_2.setVisibility(View.GONE);
                        sp_2.setVisibility(View.GONE);

                        String myString = "จำนวนที่กำหนด"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp.setSelection(spinnerPosition);


                    } else if (notificationType.equals("2")) {      //แจ้งเตือนก่อนเวลาที่กำหนด
                        tv_notification_setting.setEnabled(true);   // รูปแบบการแจ้งเตือน
                        sp.setEnabled(true);                        // spinner เลือกรูปแบบ

                        tv_detail_1.setEnabled(false);
                        ed_add_num.setEnabled(false);
                        tv_detail_1.setVisibility(View.GONE);
                        ed_add_num.setVisibility(View.GONE);

                        tv_detail_2.setEnabled(true);              // เวลาที่ต้องการ
                        sp_2.setEnabled(true);
                        tv_detail_2.setVisibility(View.VISIBLE);
                        sp_2.setVisibility(View.VISIBLE);

                        String myString = "ก่อนระยะเวลาที่กำหนด"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp.setSelection(spinnerPosition);

                    }

                    //ในส่วนของการกำหนดเวลาที่ต้องการให้แจ้งเตือนก่อนเวลา
                    if (notificationDetailType2.equals("5")) {
                        // "5 นาที"
                        String myString = "5 นาที"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp_2.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp_2.setSelection(spinnerPosition);
                    } else if (notificationDetailType2.equals("10")) {
                        // "10 นาที"
                        String myString = "10 นาที"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp_2.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp_2.setSelection(spinnerPosition);
                    } else if (notificationDetailType2.equals("20")) {
                        // "20 นาที",
                        String myString = "20 นาที"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp_2.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp_2.setSelection(spinnerPosition);
                    } else if (notificationDetailType2.equals("3")) {
                        // "30 นาที"
                        String myString = "30 นาที"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp_2.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp_2.setSelection(spinnerPosition);
                    } else if (notificationDetailType2.equals("60")) {
                        // "60 นาที"
                        String myString = "60 นาที"; //the value you want the position for
                        ArrayAdapter myAdap = (ArrayAdapter) sp_2.getAdapter(); //cast to an ArrayAdapter
                        int spinnerPosition = myAdap.getPosition(myString);
                        sp_2.setSelection(spinnerPosition);
                    }

                    //ในส่วนของการเปิดปิดแจ้งเตือน
                    if (notificationAlarm.equals("1")) {        //เปิดการแจ้งเตือน
                        sw_notification.setChecked(true);
                        tv_notification_setting.setTextColor(Color.parseColor("#000000"));
                        tv_detail_2.setTextColor(Color.parseColor("#000000"));
                        sp.setEnabled(true);
                        sp.setVisibility(View.VISIBLE);
                        tv_detail_1.setEnabled(true);
                        ed_add_num.setEnabled(true);
                        tv_detail_2.setEnabled(true);
                        sp_2.setEnabled(true);

                    } else if (notificationAlarm.equals("0")) { //ปิดการแจ้งเตือน
                        sw_notification.setChecked(false);
                        tv_notification_setting.setTextColor(Color.parseColor("#cac8ca"));
                        tv_detail_2.setTextColor(Color.parseColor("#cac8ca"));
                        sp.setEnabled(false);
                        tv_detail_1.setEnabled(false);
                        ed_add_num.setEnabled(false);
                        tv_detail_2.setEnabled(false);
                        sp_2.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //เปิด-ปิดเสียงการแจ้งเตือน
        sw_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_alarm.isChecked()) { //สวิสซ์เปิด
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference mCodeNotificationSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "")
                            .child("notification").child("sound");
                    mCodeNotificationSound.setValue("1");   // เขียนค่า sound ใน database เป็น "1" == เปิดเสียง
                } else {  //สวิสซ์ปิด
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference mCodeNotificationSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "")
                            .child("notification").child("sound");
                    mCodeNotificationSound.setValue("0");   // เขียนค่า sound ใน database เป็น "0" == ปิดเสียง
                }
            }
        });

        //เปิด-ปิดการแจ้งเตือน
        sw_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_notification.isChecked()) { //สวิสซ์เปิด
                    sp.setEnabled(true);

                    tv_detail_1.setEnabled(true);
                    tv_detail_1.setTextColor(Color.parseColor("#000000"));

                    ed_add_num.setEnabled(true);

                    tv_detail_2.setEnabled(true);
                    tv_detail_2.setTextColor(Color.parseColor("#000000"));

                    sp_2.setEnabled(true);
                    tv_notification_setting.setTextColor(Color.parseColor("#000000"));

                    sp_3.setEnabled(true);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference mCodeNotificationSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "")
                            .child("notification").child("alarm");
                    mCodeNotificationSound.setValue("1");  // เขียนค่า alarm ใน database เป็น "1" == เปิดการแจ้งเตือน

                } else if (!sw_notification.isChecked()) { //สวิสซ์ปิด

                    sp.setEnabled(false);

                    tv_detail_1.setEnabled(false);
                    tv_detail_1.setTextColor(Color.parseColor("#cac8ca"));

                    ed_add_num.setEnabled(false);

                    tv_detail_2.setEnabled(true);
                    tv_detail_2.setTextColor(Color.parseColor("#cac8ca"));

                    sp_2.setEnabled(false);
                    tv_notification_setting.setTextColor(Color.parseColor("#cac8ca"));

                    sp_2.setEnabled(false);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference mCodeNotificationSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "")
                            .child("notification").child("alarm");
                    mCodeNotificationSound.setValue("0"); // เขียนค่า alarm ใน database เป็น "1" == ปิดการแจ้งเตือน
                }

            }
        });

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                         @Override
                                         public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                             FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                             DatabaseReference mCodeNotificationType = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "")
                                                     .child("notification").child("type");
                                             switch (i) {
                                                 case 0:
                                                     mCodeNotificationType.setValue("0");
                                                     break; //ทุกคิว
                                                 case 1:
                                                     mCodeNotificationType.setValue("1");
                                                     break; //จำนวนที่กำหนด
                                                 case 2:
                                                     mCodeNotificationType.setValue("2");
                                                     break; //ก่อนเวลาที่กำหนด
                                             }
                                         }

                                         @Override
                                         public void onNothingSelected(AdapterView<?> adapterView) {
                                         }
                                     }
        );
        // เช็คค่าเลขที่ผู้ใช้ระบุมาในในกรณีที่เลือกระบุจำนวนคิวต้องการให้แจ้งเตือนล่วงหน้า
        ed_add_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ed_add_num.getText().toString().equals("0") && (i2 >= 1)) {
                    Toast.makeText(getApplicationContext(), "ไม่ควรจะเป็น0", Toast.LENGTH_SHORT).show();
                    ed_add_num.setText("1");
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ed_add_num.getText().toString().equals("0") && (i2 >= 1)) {
                    Toast.makeText(getApplicationContext(), "ไม่ควรจะเป็น0", Toast.LENGTH_SHORT).show();
                    ed_add_num.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (ed_add_num.getText().toString().equals("")||ed_add_num.getText().toString().equals("0")  ) {

                }else {
                    mRootRef.child("customer").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference mCodeNotificationBtnSound = mRootRef.child("customer").child(user.getUid())
                                    .child("Add").child(getUniqueId + "").child("notification").child("detailType");
                            mCodeNotificationBtnSound.setValue(ed_add_num.getText().toString() + "");
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }





            }
        });

        // spinner ในกรณีที่เลือกการแจ้งเตือนแบบก่อนเวลา โดยจะเขียนค่าลงไปใน database ตามเวลาของ spinner ที่ผู้ใช้ได้กดเลือก
        sp_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mCodeNotificationDetailType2 = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "")
                        .child("notification").child("detailType2");
                switch (i) {
                    case 0:
                        mCodeNotificationDetailType2.setValue("5");  // 5นาที
                        break;
                    case 1:
                        mCodeNotificationDetailType2.setValue("10"); // 10นาที
                        break;
                    case 2:
                        mCodeNotificationDetailType2.setValue("20"); // 20นาที
                        break;
                    case 3:
                        mCodeNotificationDetailType2.setValue("30"); // 30นาที
                        break;
                    case 4:
                        mCodeNotificationDetailType2.setValue("60"); // 60นาที
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //ในกรณีที่รูปแบบของร้านค้าไม่สามารถคำนวณเวลาได้ ผู้ใช้จะมี spinner ที่มีรูปแบบการการแจ้งเตือน 2 แบบ คือแบบแจ้งเตือนทุกคิว และแบบแจ้งเตือนก่อนคิวที่ต้องการตามจำนวนที่ระบุ
        sp_3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mCodeNotificationType = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "")
                        .child("notification").child("type");

                switch (i) {
                    case 0:
                        mCodeNotificationType.setValue("0");  //ทุกคิว
                        break;

                    case 1:
                        mCodeNotificationType.setValue("1"); //จำนวนที่กำหนด
                        break;

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.blank,menu); // ลักษณะ Tool bar ที่ใช้
        return super.onCreateOptionsMenu(menu);
        //  return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
            if(id == android.R.id.home){ //กลับไปยัง Activity ก่อนหน้า
                finish();
            }
        return super.onOptionsItemSelected(item);
    }

}
