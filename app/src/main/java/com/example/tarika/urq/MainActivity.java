
package com.example.tarika.urq;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int SIGN_IN_CODE = 666;
    private Button btnLogout;
    String mCode;

    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
////////////////////

    TextView tv_specify_q;
    Button btn_fill_inform;
    int a;
    String checkListAdded;
    ListView lv_show_added;

    List<ListSearchStore> list;
    ArrayAdapter<ListSearchStore> adapter;
    String getUniqueId;

    String[] arr_1;
    String[] arr_2;
    String[] arr_3;
    String[] arr_4;
    String[] arr_5;
    String[] arr_type;
    String[] arr_type1;
    String[] arr_type2;
    String[] arr_alarm;

    String countAdd;
    String nameShop = ".";
    String noQ = ".";
    String noShop = ".";
    String qWait = ".";
    String countStatus = ".";
    int countFinish = 0;
    int countDoing = 0;
    int countQ = 0;
    int countFinishAndDoing = 0;
    int counterQnumber;
    TextView textShowList2_1;
    TextView textShowList2_2;
    int i = 0;
    int m = 1;

    ImageView imageView;
    TextView nameGoogle;
    TextView textEmail;
    NavigationView navigationView;

    Button btn_main_dialog_Logout;
    Button btn_main_dialog_cancel;

    TextView tv_dialog;
    String timeOut = ".";
    int hr;
    int mi;
    int callNow1;
    int callNow2;
    int p=0;

    String timeBefore;
    int time ;
    String avgServiceTime2;
    String data2;String  alarm ;
    String  detailType ;
    String detailType2;
    String  type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        tv_specify_q = (TextView) findViewById(R.id.tv_specify_q);
        tv_specify_q = (TextView) findViewById(R.id.tv_specify_q);
        btn_fill_inform = (Button) findViewById(R.id.fill_inform);
        lv_show_added = (ListView) findViewById(R.id.lv_show_added);
        list = new ArrayList<ListSearchStore>();
        final Typeface tf_1 = Typeface.createFromAsset(getAssets(), "fonts/TEPC_CM-Prasanmit.ttf");
        final Typeface tf_2 = Typeface.createFromAsset(getAssets(), "fonts/TEPC_CM-Prasanmit_Bol.ttf");
        tv_specify_q.setTypeface(tf_1);
        btn_fill_inform.setTypeface(tf_2);

        // ในส่วนของ Log in ใช้บริการ google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {  // เตรียบสอบดูว่าตอนนี้ Login อยู่หรือไม่
                    list.clear();   // ทำ Listview ให้ว่าง

                    View headerView = navigationView.getHeaderView(0);  // ประกาศ HeaderView (navigation)
                    nameGoogle = (TextView) headerView.findViewById(R.id.nameGoogle); // ดึงค่า ชื่อ จาก Firebase(Gmail) Login เพื่อแสดงผล
                    nameGoogle.setText(user.getDisplayName());
                    textEmail = (TextView) headerView.findViewById(R.id.emailGoogle); // ดึงค่า อีเมล จาก Firebase(Gmail) Login เพื่อแสดงผล
                    textEmail.setText(user.getEmail());

                    imageView = (ImageView) headerView.findViewById(R.id.imageView); // ดึงค่า รูป Profile จาก Firebase(Gmail) Login เพื่อแสดงผล
                    Glide.with(headerView.getContext()).load(user.getPhotoUrl()).into(imageView);

                    nameGoogle.setTypeface(tf_2); // ปรับตัวอักษร
                    textEmail.setTypeface(tf_1);  // ปรับตัวอักษร

                    //เข้าถึงดาต้าเบสในส่วนของ User (ฝั่งร้านค้า---ข้อมูลต่างๆของร้านค้า)
                    mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                                                                     @Override
                                                                     public void onDataChange(DataSnapshot dataSnapshot) {

                                                                        //เข้าถึงดาต้าเบสในส่วนของ Customer (ฝั่งผู้ใช้บริการ) โดยใช้ค่า Uid ระบุตัวตน
                                                                         mRootRef.child("customer").child(user.getUid() + "").child("Add").addValueEventListener(new ValueEventListener() {
                                                                             @Override
                                                                             public void onDataChange(DataSnapshot dataSnapshot) {

                                                                                 checkListAdded = String.valueOf(dataSnapshot.getChildrenCount()); // นับจำนวนร้านค้าที่ผู้ใช้บริการได้เพิ่มไว้ในฐานข้อมูล

                                                                                 list.clear(); // ทำให้ Listview ทั้งหมดเป็นค่าว่าง

                                                                                 if (Integer.parseInt(checkListAdded) == 0) {   //กรณ๊ที่ผู้ใช้บริการไม่มีร้านค้าที่เพิ่มอยู่ในระบบ
                                                                                     tv_specify_q.setVisibility(View.VISIBLE);  // Textview จะแสดงคำว่า "ไม่มีรายการคิวที่ต้องแจ้งเตือน หากต้องการเพิ่มรายการแจ้งเตือน กรุณากดปุ่ม"
                                                                                     lv_show_added.setVisibility(View.GONE);    // listview จะถูกซ่อน

                                                                                 } else {
                                                                                     tv_specify_q.setVisibility(View.GONE);     // จะซ่อนText "ไม่มีรายการคิวที่ต้องแจ้งเตือน หากต้องการเพิ่มรายการแจ้งเตือน กรุณากดปุ่ม"
                                                                                     lv_show_added.setVisibility(View.VISIBLE); // listview จะปรากฏ
                                                                                     list.clear();                              // ทำให้ Listview ทั้งหมดเป็นค่าว่าง
                                                                                     checkListAdded = String.valueOf(dataSnapshot.getChildrenCount()); // นับจำนวนร้านค้าที่ผู้ใช้บริการได้เพิ่มไว้ในฐานข้อมูล

                                                                                     arr_1 = new String[Integer.parseInt(checkListAdded)];      // สร้าง array ที่มีขนาดเท่ากับจำนวณร้านค้าที่มี สำหรับเก็บชื่อร้านค้า
                                                                                     arr_2 = new String[Integer.parseInt(checkListAdded)];      // สร้าง array ที่มีขนาดเท่ากับจำนวณร้านค้าที่มี สำหรับเเก็บ UId ร้านค้า
                                                                                     arr_3 = new String[Integer.parseInt(checkListAdded)];      // สร้าง array ที่มีขนาดเท่ากับจำนวณร้านค้าที่มี สำหรับเลขคิว
                                                                                     arr_4 = new String[Integer.parseInt(checkListAdded)];      // สร้าง array ที่มีขนาดเท่ากับจำนวณร้านค้าที่มี สำหรับเก็บจำนวนที่ต้องรอคิว (ได้จากการคำนวน)
                                                                                     arr_5 = new String[Integer.parseInt(checkListAdded)];      // สร้าง array ที่มีขนาดเท่ากับจำนวณร้านค้าที่มี สำหรับเก็บค่า UniqueId
                                                                                     arr_type = new String[Integer.parseInt(checkListAdded)];   // สร้าง array ที่มีขนาดเท่ากับจำนวณร้านค้าที่มี สำหรับระบุรูปแบบการแจ้งเตือน
                                                                                     arr_type1 = new String[Integer.parseInt(checkListAdded)];  // สร้าง array ที่มีขนาดเท่ากับจำนวณร้านค้าที่มี สำหรับเก็บค่าเพื่อระบุปิด-ปิดแจ้งเตือน
                                                                                     arr_type2 = new String[Integer.parseInt(checkListAdded)];  // สร้าง array ที่มีขนาดเท่ากับจำนวณร้านค้าที่มี สำหรับเก็บค่าตัวเลข
                                                                                     arr_alarm = new String[Integer.parseInt(checkListAdded)];  // สร้าง array ที่มีขนาดเท่ากับจำนวณร้านค้าที่มี สำหรับเก็บค่าเพื่อระบุปิด-ปิดเสียงการแจ้งเตือน

                                                                                     Log.d("customer", checkListAdded + "//");

                                                                                     m = 0;
                                                                                     for (DataSnapshot shopSnapshot : dataSnapshot.getChildren()) {

                                                                                         //ดึงค่าต่างๆที่ต้องใช้มาจาก Database
                                                                                         nameShop = String.valueOf(shopSnapshot.child("nameShop").getValue());
                                                                                         noShop = String.valueOf(shopSnapshot.child("noShop").getValue());
                                                                                         noQ = String.valueOf(shopSnapshot.child("noQ").getValue());
                                                                                         getUniqueId = String.valueOf(shopSnapshot.child("noCodeId").getValue());
                                                                                         alarm = String.valueOf(shopSnapshot.child("notification").child("alarm").getValue());
                                                                                         detailType = String.valueOf(shopSnapshot.child("notification").child("detailType").getValue());
                                                                                         detailType2 = String.valueOf(shopSnapshot.child("notification").child("detailType2").getValue()); // นาทีที่เก็บ
                                                                                         type = String.valueOf(shopSnapshot.child("notification").child("type").getValue()); //0,1/2

                                                                                         arr_1[m] = new String(nameShop);           // นำชื่อร้านมาใส่ใน array
                                                                                         arr_2[m] = new String(noShop);             // นำชื่อ Uid ร้านค้ามาใส่ใน array
                                                                                         arr_3[m] = new String(noQ);                // นำชื่อ เลขคิว ร้านค้ามาใส่ใน array
                                                                                         arr_5[m] = new String(getUniqueId);        // นำชื่อ UniqueId ร้านค้ามาใส่ใน array
                                                                                         arr_4[m] = new String("");                 // ""--จะนำค่าที่ได้มาใส่ในภายหลัง
                                                                                         arr_type[m] = new String(type);            // นำสถานะรูปแบบของการแจ้งตือนมาใส่ใน array
                                                                                         arr_type1[m] = new String(detailType);     // นำค่าจำนวนคิวที่ต้องการให้แจ้งเตือนก่อนมาใส่ใน array
                                                                                         arr_type2[m] = new String(detailType2);    // นำค่าจำนวนเวลาที่ให้แจ้งเตือนก่อนหน้ามาใส่ใน array
                                                                                         arr_alarm[m] = new String(alarm);          // นำค่าที่ใช้เช็คเปิดปิดเสียงมาใส่ใน array

                                                                                         p = 0;
                                                                                         //เข้าถึงข้อมูล Database ฝั่ง user
                                                                                         mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                                                                                             @Override
                                                                                             public void onDataChange(DataSnapshot dataSnapshot) {


                                                                                                 if (p >= arr_1.length) {
                                                                                                     return;
                                                                                                 }
                                                                                                 if (p >= arr_2.length) {
                                                                                                     return;
                                                                                                 }
                                                                                                 if (p >= arr_3.length) {
                                                                                                     return;
                                                                                                 }

                                                                                                 String repeat = String.valueOf(dataSnapshot.child(arr_2[p] + "").child("qNumber").child(arr_3[p] + "").child("repeat").getValue());
                                                                                                 if (repeat.equals("1")) { // เช็คดูว่ามีการเรียกคิวของเราที่มีอยู่หรือไม่
                                                                                                     noti(arr_1[p]);        // เรียก function
                                                                                                     DatabaseReference repeatShopRef = mRootRef.child("user").child(arr_2[p] + "").child("qNumber").child(arr_3[p] + "").child("repeat");
                                                                                                     repeatShopRef.setValue("0");   // กลับไป set ค่า เพื่อให้ระบบเข้าใจว่า ยังไม่มีการเรียกคิว
                                                                                                 }

                                                                                                 //ในส่วนของการคำนวณเวลา
                                                                                                 ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                                                                 String qType = String.valueOf(dataSnapshot.child(arr_2[p] + "").child("shopData").child("qType").getValue());
                                                                                                 String avgServiceTime = String.valueOf(dataSnapshot.child(arr_2[p] + "").child("shopData").child("avgServiceTime").getValue());

                                                                                                 /*
                                                                                                 if (qType.equals("0")) { //การกรณีที่ qType เท่ากับ 0 จะแปลว่าร้านนั้นสามารถคำนวนเวลาได้


                                                                                                     counterQnumber = Integer.valueOf(String.valueOf(dataSnapshot.child(arr_2[p] + "").child("qNumber").getChildrenCount())); // จำนวน node ที่อยู่ข้างใน
                                                                                                     int numServer = Integer.parseInt(String.valueOf(dataSnapshot.child(arr_2[p] + "").child("shopData").child("numServer").getValue()));

                                                                                                     String[] arr_temp = new String[counterQnumber+2];
                                                                                                     avgServiceTime2 = String.valueOf(dataSnapshot.child(arr_2[p] + "").child("shopData").child(avgServiceTime + "").getValue()); // เวลาประมาณในการบริการของร้านค้า

                                                                                                     for (int i = 1; i <= counterQnumber; i++) {   // โดยจะเริ่มจากคำนวนคิวของคิวแรก จนถึงคิวสุดท้ายที่มีในร้านนั้น

                                                                                                         timeOut = String.valueOf(dataSnapshot.child(arr_2[p] + "")
                                                                                                         .child("qNumber").child(i + "").child("time").child("timeOut").getValue()); // เวลารอประมาณที่คำนวณได้ของแต่ละคิว
                                                                                                         countStatus = String.valueOf(dataSnapshot.child(arr_2[p] + "")
                                                                                                         .child("qNumber").child(i + "").child("status").getValue()); // สถานะของคิวๆนั้นๆ



                                                                                                         if (countStatus.equals("finish")) { // ถ้าสถานะเป็น finish จะระบุเวลารอเป็น 0
                                                                                                             DatabaseReference timeWaitShopRef = mRootRef.child("user").child(arr_2[p] + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                                                             timeWaitShopRef.setValue("0");
                                                                                                             arr_temp[i] = new String ("0");

                                                                                                         } else if (countStatus.equals("doing")) { // ถ้าสถานะเป็น doing

                                                                                                             if (!timeOut.equals("null")) { //ตรวจสอบว่าเวลรอเป็นค่าว่าง

                                                                                                                 DateFormat timeNow = new SimpleDateFormat("HH:mm");
                                                                                                                 String getTimeNow = timeNow.format(Calendar.getInstance().getTime());  //เวลาปัจจุบัน
                                                                                                                 String[] timeOutSplit = timeOut.split(":"); /// ออก
                                                                                                                 String[] currentTimeSplit = getTimeNow.split(":"); // ปัจจุบัน

                                                                                                                   // ตรวจสอบดูว่าเวลาปัจจุบันมากกว่าเวลาที่คาดลูกค้าจะออกจากร้านหรือไม่ ถ้าน้อยให้นำเวลาปัจจุบันมาลบกับเวลาที่คาดว่าลูกค้าจะออกจากร้านแล้วนำผลต่างแปลงออกมาเป็นนาที ถ้ามากกว่าให้กำหนดเวลาเป็น0
                                                                                                                 if (Integer.parseInt(currentTimeSplit[0]) > Integer.parseInt(timeOutSplit[0])) {

                                                                                                                   //  DatabaseReference timeWaitShopRef = mRootRef.child("user").child(arr_2[p] + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                                                                   //  timeWaitShopRef.setValue("0");
                                                                                                                     arr_temp[i] = new String ("0");

                                                                                                                 } else if ((Integer.parseInt(timeOutSplit[0]) == Integer.parseInt(currentTimeSplit[0])) &&
                                                                                                                         (Integer.parseInt(timeOutSplit[1]) >= Integer.parseInt(currentTimeSplit[1]))) {

                                                                                                                     mi = Integer.parseInt(timeOutSplit[1]) - Integer.parseInt(currentTimeSplit[1]);
                                                                                                                     hr = 0;

                                                                                                                 } else {

                                                                                                                     hr = Integer.parseInt(timeOutSplit[0]) - 1;
                                                                                                                     mi = Integer.parseInt(timeOutSplit[1]) + 60 - Integer.parseInt(currentTimeSplit[1]);
                                                                                                                     hr = hr - Integer.parseInt(currentTimeSplit[0]);
                                                                                                                     hr = hr * 60;
                                                                                                                     int HrMi = hr + mi;

                                                                                                                   //  DatabaseReference timeWaitShopRef = mRootRef.child("user").child(arr_2[p] + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                                                                   //  timeWaitShopRef.setValue(HrMi);
                                                                                                                     arr_temp[i] = new String (HrMi+"");
                                                                                                                 }

                                                                                                             } else if (timeOut.equals("null")) { //ตรวจสอบว่าเวลรอเป็นค่าว่าง
                                                                                                               //  DatabaseReference timeWaitShopRef = mRootRef.child("user").child(arr_2[p] + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                                                               //  timeWaitShopRef.setValue(avgServiceTime + "");
                                                                                                                 arr_temp[i] = new String (avgServiceTime+""); // นำค่าของเวลารอโดยประมาณที่ร้านค้าระบุให้มาแสดงผลเป็นเวลารอ


                                                                                                             }


                                                                                                         } else if (countStatus.equals("q")) {// ถ้าสถานะเป็น q

                                                                                                             if (i <= numServer) { //ถ้าจำนวนคิวที่คำนวณเวลา น้อยกว่าจำนวนโต๊ะที่ร้านรองรับให้เซ็ตค่าเวลาเป็น0
                                                                                                                 DatabaseReference timeWaitShopRef = mRootRef.child("user").child(arr_2[p] + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                                                                 timeWaitShopRef.setValue("0");
                                                                                                                 arr_temp[i] = new String ("0");

                                                                                                             } else { //ถ้าจำนวนคิวที่คำนวณเวลา มากกว่าจำนวนโต๊ะที่ร้านร้องรับจะนำเวลาก่อนหน้าของคิวปัจจุบ-จำนวนโต๊ะที่ร้านมี แล้วนำค่าที่ได้ไปบวกกับเวลาเฉลี่ยนที่ต้องรอของแต่ละคิว(avgServiceTime) จะได้เป็นเวลาโดยประมาณ
                                                                                                                        int tempTime = i - numServer;
                                                                                                                        data2 = String.valueOf(dataSnapshot.child(arr_2[p] + "").child("shopData").child("avgServiceTime").getValue());
                                                                                                                        //timeBefore = String.valueOf(dataSnapshot.child(arr_2[p] + "").child("qNumber").child(tempTime+"").child("time").child("timeWait").getValue());

                                                                                                                        int timeAll ;
                                                                                                                        timeAll = Integer.parseInt(data2)+Integer.parseInt(arr_temp[tempTime]);
                                                                                                                        arr_temp[i] = new String (timeAll+"");

                                                                                                                        DatabaseReference timeWaitShopRef = mRootRef.child("user").child(arr_2[p] + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                                                                                         timeWaitShopRef.setValue(Integer.parseInt(data2)+Integer.parseInt(timeBefore)+"");
                                                                                                                        arr_temp[i] = new String (timeAll+"");


                                                                                                             }


                                                                                                         }

                                                                                                     }

                                                                                                     for (int i = 1; i <= counterQnumber; i++){

                                                                                                         DatabaseReference timeWaitShopRef = mRootRef.child("user").child(arr_2[p] + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                                                         timeWaitShopRef.setValue(arr_temp[i]);

                                                                                                     }

                                                                                                 }
                                                                                                 */

                                                                                                 counterQnumber = Integer.parseInt(String.valueOf(dataSnapshot.child(arr_2[p] + "").child("qNumber").getChildrenCount())); // นับจำนวณคิวที่ต้องมีทั้งหมด
                                                                                                 countFinish = 0;
                                                                                                 countDoing = 0;
                                                                                                 countQ = 0;

                                                                                                 //ตรวจสอบสถานะคิวทั้งหมดของคิวที่มี
                                                                                                 for (int i = 1; i <= counterQnumber; i++) {

                                                                                                     countStatus = String.valueOf(dataSnapshot.child(arr_2[p] + "").child("qNumber").child(i + "").child("status").getValue()); // สถานะคิวปัจจุบันที่กำลังตรวจสอบ

                                                                                                     if (countStatus.equals("finish")) {
                                                                                                         countFinish++; // นับจำนวณคิวที่ให้บริการไปแล้ว
                                                                                                     } else if (countStatus.equals("doing")) {
                                                                                                         countDoing++;  // นับจำนวณคิวที่กำลังได้รับบริการ
                                                                                                     } else if (countStatus.equals("q")) {
                                                                                                         countQ++;      // นับจำนวณคิวที่กำลังรอ
                                                                                                     }

                                                                                                 }
                                                                                                 //เช็คสถานะปัจจุบันของคิวที่ตรวจสอบ
                                                                                                 countStatus = String.valueOf(dataSnapshot.child(arr_2[p] + "").child("qNumber").child(arr_3[p] + "").child("status").getValue()); // get ค่าคิวปัจจุบัน
                                                                                                 if (countStatus.equals("q")) {
                                                                                                     countQ--;
                                                                                                 }
                                                                                                 if (countStatus.equals("doing")) {     // แปลว่าไม่มีคิวที่ต้องรอ คิวที่ต้องรอจะเป็น 0
                                                                                                     countFinish =0 ;
                                                                                                     countDoing =0;
                                                                                                 }
                                                                                                 if (countStatus.equals("finish")) {    // แปลว่าได้ให้บริการแล้ว คิวที่ต้องรอจะเป็น 0
                                                                                                     countFinish =0 ;
                                                                                                     countDoing =0;
                                                                                                 }


                                                                                                 arr_4[p] = new String(countDoing+countFinish + ""); // เก็บค่าคิวที่ต้องรอไปไว้ใน arr_4

                                                                                                 test(p, p);

                                                                                                 if (arr_type[p].equals("0")) { // ถ้าการสถานะแจ้งเตือนเป็น 0 ให้เรียกใช้ทุกคั้งเมื่อมีการเปลี่ยนแปลง

                                                                                                     mRootRef.child("user").child(arr_2[p]+"").child("qNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                                                                        //notiEvery(arr_1[p],3);
                                                                                                    }
                                                                                                    @Override
                                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                                    }});

                                                                                                 } else if (arr_type[p].equals("1")) { // ถ้าการสถานะแจ้งเตือนเป็น 1 ให้แจ้งเตือนก่อนถึงคิวตามจำนวนที่ต้องการ
                                                                                                     // ตามจำนวนที่กำหนด
                                                                                                     if (Integer.parseInt(arr_type1[p]) == countQ) {
                                                                                                         notiNumber(arr_1[p], countQ);
                                                                                                     }
                                                                                                 } else if (arr_type[p].equals("2")) { // ถ้าการสถานะแจ้งเตือนเป็น 2 ให้แจ้งเตือนก่อนเวลาที่กำหนดไว้

                                                                                                    int getTime = Integer.parseInt(String.valueOf(dataSnapshot.child(arr_2[p] + "")
                                                                                                            .child("qNumber").child(arr_3[p] + "").child("time").child("timeWait").getValue())); // ดึงค่าเวลาที่ต้องรอ
                                                                                                    if (getTime <= Integer.parseInt(arr_type2[m])) {
                                                                                                        notiTime(arr_1[p], getTime);
                                                                                                    }

                                                                                                 }
                                                                                                 p++;
                                                                                             }

                                                                                             @Override
                                                                                             public void onCancelled(DatabaseError databaseError) {

                                                                                             }
                                                                                         });
                                                                                         m++;
                                                                                     }

                                                                                 }
                                                                                 lv_show_added.setOnItemClickListener(new AdapterView.OnItemClickListener() {       // เมื่อกด list view แล้วให้ส่งค่าต่างๆไปยังหน้า Activity ต่อไป
                                                                                     @Override
                                                                                     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                                         Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                                                                         intent.putExtra("location", arr_2[position]); // String ---- ส่งชื่อร้านที่ผู้ใช้เลือกไปยัง Main2Activity
                                                                                         intent.putExtra("myNumber", arr_3[position]); // String ---- ส่งเลขคิวปัจจุบันของผู้ใช้ไปยัง Main2Activity
                                                                                         intent.putExtra("uniqueId", arr_5[position]); // String ---- ส่ง UniqueId ปัจจุบันของผู้ใช้ไปยัง Main2Activity
                                                                                         startActivity(intent);
                                                                                     }
                                                                                 });
                                                                             }

                                                                             @Override
                                                                             public void onCancelled(DatabaseError error) {

                                                                             }
                                                                         });

                                                                     }

                                                                     @Override
                                                                     public void onCancelled(DatabaseError databaseError) {

                                                                     }
                                                                 });

                } else {
                    GoLogInScrean(); // เรียกฟังค์ชั่น GoLogInScrean() เพื่อไปยังหน้า LogInActivity
                }
            }



        };

        // ส่วนของ Navigation (แถบข้างๆ)
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_camera) { // กดเพื่อไปยัง Activity ค้นหา
                    Intent intent = new Intent(getApplicationContext(), SearchStoreActivity.class);
                    startActivity(intent);

                } else if (id == R.id.nav_reservation) { // กดเพื่อไปยัง Activity จองคิวออนไลน์

                    Intent intent = new Intent(getApplicationContext(), ReserveOnlineActivity.class);
                    startActivity(intent);

                } else if (id == R.id.nav_logout) {     // กดเพื่อ ออกจากระบบ
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_main_logout, null);

                    TextView tv_dialog = (TextView) mView.findViewById(R.id.tv_dialog);


                    btn_main_dialog_Logout = (Button) mView.findViewById(R.id.btn_main_dialog_Logout);
                    btn_main_dialog_cancel = (Button) mView.findViewById(R.id.btn_main_dialog_cancel);

                    tv_dialog.setTypeface(tf_2);
                    btn_main_dialog_Logout.setTypeface(tf_2);
                    btn_main_dialog_cancel.setTypeface(tf_2);

                    mBuilder.setView(mView); // สร้าง pop up ขึ้นมา
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    ;

                    btn_main_dialog_Logout.setOnClickListener(new View.OnClickListener() { //ปิด pop up ที่ใช้ถาม
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                            logOut(view);
                        }
                    });

                    btn_main_dialog_cancel.setOnClickListener(new View.OnClickListener() { //ออกจากระบบ
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                // return true;
                return false;

            }

        });

    }

    public void test(int ar1 , int ar2){ // ฟังค์ชันที่ใช้ในการสร้าง Listview เพื่อแสดงผลรายการที่ทั้งหมด

        ListSearchStore l_search_store = new ListSearchStore(arr_1[ar1], arr_4[ar2]); // โดยรับค่า ชื่อร้านและจำนวนคิวที่ต้องรอ
        list.add(l_search_store);

        adapter = new ListSearchStore_adapter();
        lv_show_added.setAdapter(adapter);
    }

    public void clickButtonEnter(View v) { //กดเพื่อไปยังหน้าเพิ่มข้อมูล
        if (v == btn_fill_inform) {
            Intent intent = new Intent(getApplicationContext(), FillActivity.class);
            startActivity(intent);
        }

    }

    public void logOut(View view) { // log out สำหรับ firebase
        firebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    GoLogInScrean();
                } else {
                    Toast.makeText(getApplicationContext(), "ไม่สามารถออกจากระบบได้ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Class สร้าง adapter เพื่อช่วยสร้าง Listview จากค่าของ array ที่ส่งเข้ามาให้
    class ListSearchStore_adapter extends ArrayAdapter<ListSearchStore> {
        ListSearchStore_adapter() {
            super(MainActivity.this, R.layout.item_listview_2, list);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item_listview_2, parent, false);
            ListSearchStore l_search = list.get(position);

            TextView nameStore = (TextView) view.findViewById(R.id.nameShop);
            TextView qStore = (TextView) view.findViewById(R.id.qShop);
            TextView textShowList2_1 = (TextView) view.findViewById(R.id.textShowList2_1);
            TextView textShowList2_2 = (TextView) view.findViewById(R.id.textShowList2_2);

            nameStore.setText(l_search.getName_shop());
            qStore.setText(l_search.getQ_shop());

            Typeface tf_1 = Typeface.createFromAsset(getAssets(), "fonts/TEPC_CM-Prasanmit.ttf");
            Typeface tf_2 = Typeface.createFromAsset(getAssets(), "fonts/TEPC_CM-Prasanmit_Bol.ttf");

            nameStore.setTypeface(tf_2);
            qStore.setTypeface(tf_1);

            textShowList2_1.setTypeface(tf_1);
            textShowList2_2.setTypeface(tf_1);

            return view;
        }

    }

    private void GoLogInScrean() { // ฟังค์ชันที่ให้เรียกในกรณีที่ยังไม่ได้ Log in
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent); // ไปยังหน้า log in
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blank, menu); // รูปแบบที่ใช้ใน Tool bar
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // function ที่ใช้เมื่อต้องการให้แจ้งเตือน เมื่อถึงคิว โดยรับค่า เพื่อนำมาแสดงผล
    public void noti(String nameShop) {
        String channelId = "defaultChannel_1";
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.urq_notication))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ร้าน " + nameShop)
                .setContentText("ถึงคิวของคุณแล้ว")
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1000, notificationBuilder.build());
    }

    // function ที่ใช้เมื่อต้องการให้แจ้งเตือน เมื่อถึงคิว ต้องการให้แจ้งเตือนก่อนถึงคิวตามจำนวนที่กำหนด โดยรับค่า ชื่อร้าน และ เลขคิวที่ต้องรอก่อนหน้า
    public void notiNumber(String nameShop, int num) {
        String channelId = "defaultChannel_2";
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.urq_notication))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ร้าน " + nameShop)
                .setContentText("อีก " + num + " คิว ถึงคิวของคุณ")
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1000, notificationBuilder.build());
    }

    // function ที่ใช้เมื่อต้องการให้แจ้งเตือน เมื่อมีการเปลี่ยนแปลงคิว โดย รับค่าชื่อร้าน และคิวปัจจุบันที่ร้านกำลังเรียกให้รับบริการ
    public void notiEvery(String nameShop, int q_now) {
        String channelId = "defaultChannel_3";
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.urq_notication))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ร้าน " + nameShop)
                .setContentText("ตอนนี้ถึงคิวที่ "+ q_now )
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1000, notificationBuilder.build());
    }

    // function ที่ใช้เมื่อต้องการให้แจ้งเตือน เมื่อถึงเวลาที่ให้แจ้งเตือนก่อนหน้า โดย รับค่าชื่อร้าน และเวลาที่เหลือในการรอของคิวนั้นๆ
    public void notiTime(String nameShop, int time) {
        String channelId = "defaultChannel_3";
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.urq_notication))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ร้าน " + nameShop)
                .setContentText("อีกประมาณ " + time + "นาที ถึงคิวของคุณแล้ว")
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1000, notificationBuilder.build());
    }


}