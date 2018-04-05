package com.example.tarika.urq;

import android.content.Intent;
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

    //@SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        name_reserve_store = (TextView)findViewById(R.id.name_reserve_store);
        text_reserve_time_open_close = (TextView)findViewById(R.id.text_reserve_time_open_close);

        text_reserve_3 = (TextView)findViewById(R.id.text_reserve_3);
        text_reserve_time_reserve = (TextView)findViewById(R.id.text_reserve_time_reserve);
        text_reserve_4 = (TextView)findViewById(R.id.text_reserve_4);

        text_reserve_5 = (TextView)findViewById(R.id.text_reserve_5);   //  หากจองคิวต้องรอคิว
        text_reserve_q_wait = (TextView)findViewById(R.id.text_reserve_q_wait);
        text_reserve_6 = (TextView)findViewById(R.id.text_reserve_6);

        getName = getIntent().getExtras().getString("shopName");

        et_reserve_no_customer = (EditText)findViewById(R.id.et_reserve_no_customer) ;



        mRootRef.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) {

                    String shopName = String.valueOf(shopSnapshot.child("shopName").child("name").getValue());
                    if (shopName.equals(getName)){
                        name_reserve_store.setText(shopName);
                        String timeOpen = String.valueOf(shopSnapshot.child("shopName").child("time").child("open").getValue());
                        String timeClose = String.valueOf(shopSnapshot.child("shopName").child("time").child("close").getValue());
                        String timeReserve = String.valueOf(shopSnapshot.child("shopName").child("time").child("reserve").getValue());
                        text_reserve_time_open_close.setText(timeOpen +" - " +timeClose);
                        text_reserve_time_reserve.setText(timeReserve);
                        getUid = String.valueOf(shopSnapshot.getKey());
                        getTable = String.valueOf(shopSnapshot.child("shopName").child("numServer").getValue());

                        int k=1;
                        countQ =0;
                        countFinish =0;
                        countDoing =0;
                        countStatus = ".";

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


                    }


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        et_reserve_no_customer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(et_reserve_no_customer.getText().toString().equals("0")&&i2>=0){
                    Toast.makeText(getApplicationContext(), "จำนวนลูกค้าจะเป็น0ไม่ได้" ,Toast.LENGTH_SHORT).show();
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

    public void onClickReserve (View view){

        if (checkNo){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ReservationActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_reservation_1,null);
            btn_reserve_dialog_ok = (Button)mView.findViewById(R.id.btn_reserve_dialog_ok);
            btn_reserve_dialog_cancel = (Button)mView.findViewById(R.id.btn_reserve_dialog_cancel);
            tv_dialog_name_shop = (TextView)mView.findViewById(R.id.tv_dialog_name_shop);
            tv_dialog_name_shop.setText(getName);
            tv_dialog_reserve_no_customer =(TextView)mView.findViewById(R.id.tv_dialog_reserve_no_customer);
            tv_dialog_reserve_no_customer.setText(et_reserve_no_customer.getText());
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    numQnumber = String.valueOf(dataSnapshot.child(getUid+"").child("qNumber").getChildrenCount());
                    getNameFromUser = String.valueOf(dataSnapshot.child(getUid+"").child("shopName").child("name").getValue());
                    getQType = String.valueOf(dataSnapshot.child(getUid+"").child("shopName").child("qType").getValue());
                    temp = Integer.parseInt(numQnumber)+1; // ลำดับที่มันควรจะได้ (ที่สร้างขึ้นมาใหม่)


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            final Random rand = new Random();
            noRandomPin = rand.nextInt(9999-1000) + 1000;
            btn_reserve_dialog_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    DatabaseReference nameCustomer = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("nameCustomer");
                    nameCustomer.setValue(user.getDisplayName());

                    DatabaseReference noCustomer = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("noCustomer");
                    noCustomer.setValue(et_reserve_no_customer.getText().toString());

                    DatabaseReference pin = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("pin");
                    pin.setValue(noRandomPin+"");

                    DatabaseReference repeat = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("repeat");
                    repeat.setValue(0+"");

                    DatabaseReference status = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("status");
                    if (countDoing<Integer.parseInt(getTable)){
                        status.setValue("doing");
                    }else {
                        status.setValue("q");
                    }
                    DatabaseReference mCodeQType = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("qType");
                    mCodeQType.setValue(getQType+"");

                    DatabaseReference timeIn = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("time").child("timeIn");
                    timeIn.setValue("xx.xx");
                    DatabaseReference timeOut = mRootRef.child("user").child(getUid+"").child("qNumber").child(temp+"").child("time").child("timeOut");
                    timeOut.setValue("xx.xx");

                    // ในส่วนของ customer
                    DatabaseReference nameShop = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("nameShop");
                    DatabaseReference noPin = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("noPin");
                    DatabaseReference noQ = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("noQ");
                    DatabaseReference noCustomerCus = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("noCustomer");
                    DatabaseReference noShop = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("noShop");

                    nameShop.setValue(getNameFromUser+"");
                    noPin.setValue(noRandomPin+"");
                    noQ.setValue(Integer.parseInt(numQnumber)+1);
                    noCustomerCus.setValue(et_reserve_no_customer.getText().toString()+"");
                    noShop.setValue(getUid+"");

                    DatabaseReference mCodeNotificationSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"")
                            .child("notification").child("sound");
                    DatabaseReference mCodeAlarmSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"")
                            .child("notification").child("alarm");
                    DatabaseReference mCodeTypeSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"")
                            .child("notification").child("type");
                    DatabaseReference mCodeDetailTypeSound = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"")
                            .child("notification").child("detailType");
                    DatabaseReference mCodeDetailTypeSound2 = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"")
                            .child("notification").child("detailType2");

                    //for notification
                    mCodeNotificationSound.setValue("1");
                    mCodeAlarmSound.setValue("1");
                    mCodeTypeSound.setValue("0");
                    mCodeDetailTypeSound.setValue("1");
                    mCodeDetailTypeSound2.setValue("1");

                    dialog.cancel();

                    Intent intent = new Intent(ReservationActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            });

            btn_reserve_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
