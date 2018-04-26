
package com.example.tarika.urq;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
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

    TextView ttt;
    int callNow1;
    int callNow2;

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

        ttt = (TextView) findViewById(R.id.ttt);

        tv_specify_q = (TextView) findViewById(R.id.tv_specify_q);
        btn_fill_inform = (Button) findViewById(R.id.fill_inform);

        lv_show_added = (ListView) findViewById(R.id.lv_show_added);
        list = new ArrayList<ListSearchStore>();


        final Typeface tf_1 = Typeface.createFromAsset(getAssets(), "fonts/TEPC_CM-Prasanmit.ttf");
        final Typeface tf_2 = Typeface.createFromAsset(getAssets(), "fonts/TEPC_CM-Prasanmit_Bol.ttf");
        tv_specify_q.setTypeface(tf_1);
        btn_fill_inform.setTypeface(tf_2);
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
                if (user != null) {
                    list.clear();
                    View headerView = navigationView.getHeaderView(0);
                    nameGoogle = (TextView) headerView.findViewById(R.id.nameGoogle);
                    textEmail = (TextView) headerView.findViewById(R.id.emailGoogle);
                    nameGoogle.setText(user.getDisplayName());
                    textEmail.setText(user.getEmail());
                    imageView = (ImageView) headerView.findViewById(R.id.imageView);
                    Glide.with(headerView.getContext()).load(user.getPhotoUrl()).into(imageView);

                    nameGoogle.setTypeface(tf_2);
                    textEmail.setTypeface(tf_1);


                    mRootRef.child("customer").child(user.getUid() + "").child("Add").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            checkListAdded = String.valueOf(dataSnapshot.getChildrenCount());

                            list.clear();

                            if (Integer.parseInt(checkListAdded) == 0) {
                                tv_specify_q.setVisibility(View.VISIBLE);
                                lv_show_added.setVisibility(View.GONE);


                            } else {
                                tv_specify_q.setVisibility(View.GONE);
                                lv_show_added.setVisibility(View.VISIBLE);
                                list.clear();
                                checkListAdded = String.valueOf(dataSnapshot.getChildrenCount());

                                arr_1 = new String[Integer.parseInt(checkListAdded)];
                                arr_2 = new String[Integer.parseInt(checkListAdded)];
                                arr_3 = new String[Integer.parseInt(checkListAdded)];
                                arr_4 = new String[Integer.parseInt(checkListAdded)];
                                arr_5 = new String[Integer.parseInt(checkListAdded)];

                                m = 0;
                                for (DataSnapshot shopSnapshot : dataSnapshot.getChildren()) {

                                    nameShop = String.valueOf(shopSnapshot.child("nameShop").getValue());
                                    noShop = String.valueOf(shopSnapshot.child("noShop").getValue());
                                    noQ = String.valueOf(shopSnapshot.child("noQ").getValue());
                                    getUniqueId = String.valueOf(shopSnapshot.child("noCodeId").getValue());
                                    arr_1[m] = new String(nameShop);
                                    arr_2[m] = new String(noShop); // Uid
                                    arr_3[m] = new String(noQ);
                                    arr_5[m] = new String(getUniqueId);

                                    /////////////////////////////////////////////การแจ้งเตือน
                                    final String qType = String.valueOf(shopSnapshot.child("qType").getValue()); // เช็คว่าคำนวณเวลามั้ย? 0คำนวณ 1 ไม่คำนวณ
                                 //   String sound = String.valueOf(shopSnapshot.child("notification").child("sound").getValue());
                                    final String alarm = String.valueOf(shopSnapshot.child("notification").child("alarm").getValue());
                                    final String type = String.valueOf(shopSnapshot.child("notification").child("type").getValue()); //0,1/2
                                    final String detailType = String.valueOf(shopSnapshot.child("notification").child("detailType").getValue()); // จำนวณที่ให้แจ้งก่อนกี่คิว
                                    final String detailType2 = String.valueOf(shopSnapshot.child("notification").child("detailType2").getValue()); // นาทีที่เก็บ
/////////////บัค///////////////////////////////////////////////////

                                    mRootRef.child("user").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String counter = String.valueOf(dataSnapshot.child(noShop + "").child("qNumber").child(noQ + "").child("status").getValue());
                                            String repeat = String.valueOf(dataSnapshot.child(noShop + "").child("qNumber").child(noQ + "").child("repeat").getValue());
                                            String avgServiceTime = String.valueOf(dataSnapshot.child(noShop + "").child("shopData").child("avgServiceTime").getValue());


                                            if (repeat.equals("1")) {
                                                noti(nameShop);
                                                DatabaseReference repeatShopRef = mRootRef.child("user").child(noShop + "").child("qNumber").child(noQ + "").child("repeat");
                                                repeatShopRef.setValue("0");
                                            }

                                            if (counter.equals("finish")) {
                                                DatabaseReference qWaitShopRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "").child("qWait");
                                                qWaitShopRef.setValue("0");

                                            }else if (counter.equals("doing")) {
                                                noti(nameShop);
                                                DatabaseReference qWaitShopRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "").child("qWait");
                                                qWaitShopRef.setValue("0");

                                            } else if (counter.equals("q")) {
                                                int k = 1;
                                                countStatus = String.valueOf(dataSnapshot.child(noShop + "").child("qNumber").child(k + "").child("status").getValue());
                                                counterQnumber = Integer.valueOf(String.valueOf(dataSnapshot.child(noShop + "").child("qNumber").getChildrenCount()));
                                                countFinish = 0;
                                                countDoing = 0;
                                                countQ = 0;


                                                for (int i = 1; i <= counterQnumber; i++) {

                                                    countStatus = String.valueOf(dataSnapshot.child(noShop+"").child("qNumber").child(i + "").child("status").getValue());

                                                    if (countStatus.equals("finish")) {
                                                        countFinish++;
                                                    } else if (countStatus.equals("doing")) {
                                                        countDoing++;
                                                    } else if (countStatus.equals("q")) {
                                                        countQ++;
                                                    }

                                                }
                                                countFinishAndDoing = countFinish + countDoing;

                                                DatabaseReference qWaitShopRef = mRootRef.child("customer").child(user.getUid()).child("Add").child(getUniqueId + "").child("qWait");
                                                qWaitShopRef.setValue(counterQnumber-countFinish-countDoing+"");

                                            }



                                            if (qType.equals("0")) {
                                                //คำนวนเวลา

                                                counterQnumber = Integer.valueOf(String.valueOf(dataSnapshot.child(noShop + "").child("qNumber").getChildrenCount())); // จำนวน node ที่อยู่ข้างใน
                                                int numServer = Integer.parseInt(String.valueOf(dataSnapshot.child(noShop + "").child("shopData").child("numServer").getValue()));

                                                for (int i = 1; i <= counterQnumber; i++) {
                                                    timeOut = String.valueOf(dataSnapshot.child(noShop + "").child("qNumber").child(i + "").child("time").child("timeOut").getValue());
                                                    countStatus = String.valueOf(dataSnapshot.child(noShop + "").child("qNumber").child(i + "").child("status").getValue());


                                                    if (countStatus.equals("finish")) {
                                                        DatabaseReference timeWaitShopRef = mRootRef.child("user").child(noShop + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                        timeWaitShopRef.setValue("0");

                                                    } else if (countStatus.equals("doing")) {
                                                        if (!timeOut.equals("null")) {

                                                            DateFormat timeNow = new SimpleDateFormat("HH:mm");
                                                            String getTimeNow = timeNow.format(Calendar.getInstance().getTime());
                                                            String[] timeOutSplit = timeOut.split(":"); /// ออก
                                                            String[] currentTimeSplit = getTimeNow.split(":"); // ปัจจุบัน

                                                            if (Integer.parseInt(currentTimeSplit[0]) > Integer.parseInt(timeOutSplit[0])) {

                                                                DatabaseReference timeWaitShopRef = mRootRef.child("user").child(noShop + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                timeWaitShopRef.setValue("0");

                                                            } else if ((Integer.parseInt(timeOutSplit[0]) == Integer.parseInt(currentTimeSplit[0])) &&
                                                                    (Integer.parseInt(timeOutSplit[1]) >= Integer.parseInt(currentTimeSplit[1]))) {

                                                                mi = Integer.parseInt(timeOutSplit[1]) - Integer.parseInt(currentTimeSplit[1]);
                                                                hr = 0;

                                                            } else {

                                                                hr = Integer.parseInt(timeOutSplit[0]) - 1;
                                                                mi = Integer.parseInt(timeOutSplit[1]) + 60 - Integer.parseInt(currentTimeSplit[1]);
                                                                hr = hr - Integer.parseInt(currentTimeSplit[0]);
                                                                // hr = hr/(hr%12);
                                                                hr = hr * 60;
                                                                int HrMi = hr + mi;

                                                                DatabaseReference timeWaitShopRef = mRootRef.child("user").child(noShop + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                timeWaitShopRef.setValue(HrMi);
                                                            }

                                                        } else if (timeOut.equals("null")) {
                                                            DatabaseReference timeWaitShopRef = mRootRef.child("user").child(noShop + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                            timeWaitShopRef.setValue(avgServiceTime + "");


                                                        }


                                                    } else if (countStatus.equals("q")) {


                                                        if (countStatus.equals("q")) {


                                                            if (i <= numServer) {
                                                                DatabaseReference timeWaitShopRef = mRootRef.child("user").child(noShop + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                timeWaitShopRef.setValue("0");

                                                            }else {

                                                                String timeBefore = String.valueOf(dataSnapshot.child(noShop + "").child("qNumber").child(i-numServer+"").child("time").child("timeWait").getValue());
                                                                String avgServiceTime2 = String.valueOf(dataSnapshot.child(noShop + "").child("shopData").child("avgServiceTime").getValue());
                                                                int time ;
                                                                time = Integer.parseInt(timeBefore)+Integer.parseInt(avgServiceTime2);

                                                                DatabaseReference timeWaitShopRef = mRootRef.child("user").child(noShop + "").child("qNumber").child(i + "").child("time").child("timeWait");
                                                                timeWaitShopRef.setValue(time+"");;



                                                            }
                                                        }



                                                    }


                                                }
                                            }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                            if(type.equals("0")){


                                            }else if(type.equals("1")){
                                                // ตามจำนวนที่กำหนด
                                                if (Integer.parseInt(detailType)==Integer.parseInt(qWait)){
                                                    notiNumber(nameShop,Integer.parseInt(qWait));
                                                }

                                            }else if(type.equals("2")){

                                                int getTime = Integer.parseInt(String.valueOf(dataSnapshot.child(noShop + "").child("qNumber").child(noQ+"").child("time").child("timeWait").getValue()));

                                                if (getTime<=Integer.parseInt(detailType2)){

                                                    notiTime(nameShop, getTime);

                                                }

                                            }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

/////////////////////////////////////////////////////////////////


///////////////บัค///////////////////////////////////////////////////
                                    qWait = String.valueOf(shopSnapshot.child("qWait").getValue());
                                    arr_4[m] = new String(qWait + "");


                                    ListSearchStore l_search_store = new ListSearchStore(arr_1[m], arr_4[m]);
                                    list.add(l_search_store);

                                    m++;

                                }


                            }

                            adapter = new ListSearchStore_adapter();
                            lv_show_added.setAdapter(adapter);

                            lv_show_added.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                    intent.putExtra("location", arr_2[position]); // String
                                    intent.putExtra("myNumber", arr_3[position]); // String
                                    intent.putExtra("uniqueId", arr_5[position]); // String
                                    startActivity(intent);


                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });


                } else {
                    GoLogInScrean();
                }
            }
        };


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_camera) {
                    Intent intent = new Intent(getApplicationContext(), SearchStoreActivity.class);
                    startActivity(intent);

                } else if (id == R.id.nav_reservation) {

                    Intent intent = new Intent(getApplicationContext(), ReserveOnlineActivity.class);
                    startActivity(intent);

                } else if (id == R.id.nav_logout) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_main_logout, null);

                    TextView tv_dialog = (TextView) mView.findViewById(R.id.tv_dialog);
                    tv_dialog.setTypeface(tf_2);

                    btn_main_dialog_Logout = (Button) mView.findViewById(R.id.btn_main_dialog_Logout);
                    btn_main_dialog_cancel = (Button) mView.findViewById(R.id.btn_main_dialog_cancel);

                    btn_main_dialog_Logout.setTypeface(tf_2);
                    btn_main_dialog_cancel.setTypeface(tf_2);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    ;

                    btn_main_dialog_Logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                            logOut(view);
                        }
                    });

                    btn_main_dialog_cancel.setOnClickListener(new View.OnClickListener() {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "requestCode = " + requestCode);
    }


    public void clickButtonEnter(View v) {
        if (v == btn_fill_inform) {
            Intent intent = new Intent(getApplicationContext(), FillActivity.class);
            startActivity(intent);
        }

    }

    public void logOut(View view) {
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

    private void GoLogInScrean() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.blank, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    public void noti(String nameShop) {


        String channelId = "defaultChannel";
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


    public void notiNumber(String nameShop, int num) {


        String channelId = "defaultChannel";
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

    public void notiTime(String nameShop, int time) {


        String channelId = "defaultChannel";
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