package com.example.tarika.urq;

import android.content.Intent;
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

    Button btn_fill_save;
    EditText et_num_q;
    EditText et_pin;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    Spinner areaSpinner;
    int temp=0;
    String countNo="0";


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    String shopName;
    String noCustomer;


    private String text;
    private boolean delete = false;
    private static final int CODE_SIZE=4;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btn_fill_save =(Button)findViewById(R.id.btn_fill_save);
        et_num_q =(EditText)findViewById(R.id.et_num_q);
        et_pin = (EditText)findViewById(R.id.et_pin);
        areaSpinner = (Spinner) findViewById(R.id.sp_location_q);



        mRootRef.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> shop = new ArrayList<String>();

                for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) {
                    String shopName = String.valueOf(shopSnapshot.child("shopName").child("name").getValue());
                    shop.add(shopName);
                }


                ArrayAdapter<String> shopsAdapter = new ArrayAdapter<String>(FillActivity.this, android.R.layout.simple_spinner_item, shop);
                shopsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(shopsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                temp = i+1 ;

                // final TextView a2 = (TextView) findViewById(R.id.text_2);
                final int[] k = {1};
                mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot numSnapshot: dataSnapshot.getChildren()) {

                            if(temp == k[0]){
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

                mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 1;
                        for (DataSnapshot numSnapshot: dataSnapshot.getChildren()) {
                            if(temp == i){
                                shopName = String.valueOf(numSnapshot.child("shopName").child("name").getValue());
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





        btn_fill_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {




                if(v == btn_fill_save && et_num_q.getText().toString().equals("")){

                    Toast.makeText(getApplicationContext(), "กรุณาระบุเลขคิว" ,Toast.LENGTH_SHORT).show();

                }
                else if(v == btn_fill_save && (Integer.parseInt(et_num_q.getText().toString()) > Integer.parseInt(countNo) )){

                    Toast.makeText(getApplicationContext(), "ไม่มีเลขคิวนี้ในระบบ" ,Toast.LENGTH_SHORT).show();

                }else if(et_num_q.getText().toString().equals("0") ){

                    Toast.makeText(getApplicationContext(), "กรุณาระบุเลขคิว" ,Toast.LENGTH_SHORT).show();

                }
                else if(et_num_q.getText().toString().length()!=0){


                    mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int k = 1;
                            for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) {

                                String getPin = String.valueOf(shopSnapshot.child("qNumber").child(et_num_q.getText().toString()+"").child("pin").getValue());

                                String getUid = String.valueOf(shopSnapshot.getKey().toString());



                                if(k==temp){

                                    if (getPin.equals(et_pin.getText().toString())){

                                        noCustomer = String.valueOf(shopSnapshot.child("qNumber").child(et_num_q.getText().toString()+"").child("noCustomer").getValue());
                                        String getQType = String.valueOf(shopSnapshot.child("shopName").child("qType").getValue());

                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        DatabaseReference mCodeShop = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"");
                                        DatabaseReference mCodeShopRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("noShop");
                                        DatabaseReference mCodeNoRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("noQ");
                                        DatabaseReference mCodeNameRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("nameShop");
                                        DatabaseReference mCodePinRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("noPin");
                                        DatabaseReference mCodeNoCustomerRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("noCustomer");
                                        DatabaseReference mCodeQType = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUid+"").child("qType");

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


                                        //main detail for Main2Activity
                                        mCodeShopRef.setValue(getUid+"");
                                        mCodeNoRef.setValue(et_num_q.getText().toString()+"");
                                        mCodeNameRef.setValue(shopName);
                                        mCodePinRef.setValue(et_pin.getText().toString()+"");
                                        mCodeNoCustomerRef.setValue(noCustomer+"");
                                        mCodeQType.setValue(getQType+"");

                                        //for notification
                                        mCodeNotificationSound.setValue("1");
                                        mCodeAlarmSound.setValue("1");
                                        mCodeTypeSound.setValue("0");
                                        mCodeDetailTypeSound.setValue("1");
                                        mCodeDetailTypeSound2.setValue("1");


                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        // intent.putExtra("location", temp);
                                        // intent.putExtra("myNumber", et_num_q.getText().toString());
                                        // intent.putExtra("myPin",et_pin.getText().toString());
                                        startActivity(intent);

                                    }else {
                                        if (et_pin.getText().toString().length()<4){
                                            Toast.makeText(getApplicationContext(), "คุณระบุเลขไม่ครบ" ,Toast.LENGTH_SHORT).show();
                                        }else {
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



                }else{

                    Toast.makeText(getApplicationContext(), "กรุณาระบุเลขคิว" ,Toast.LENGTH_SHORT).show();

                }


            }
        });




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
