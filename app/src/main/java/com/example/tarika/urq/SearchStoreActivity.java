package com.example.tarika.urq;

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







        list = new ArrayList<ListSearchStore>();

        mRootRef.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                countUser = String.valueOf(dataSnapshot.getChildrenCount());
                arr1 = new String[Integer.parseInt(countUser)];
                arr2 = new String[Integer.parseInt(countUser)];

                int i = 0;

                for (DataSnapshot shopSnapshot: dataSnapshot.getChildren()) {

                    String shopName = String.valueOf(shopSnapshot.child("shopName").child("name").getValue());

                    arr1[i] = new String(shopName);

                    int k=1;
                    countQ =0;
                    countStatus = ".";
                    while (!countStatus.equals("null")){
                        countStatus = String.valueOf(shopSnapshot.child("qNumber").child(k+"").child("status").getValue());
                        if(countStatus.equals("q")){
                            countQ++;
                        }
                        k++;
                    }

                    arr2[i] = new String(countQ+"");

                    ListSearchStore l_search_store = new ListSearchStore(arr1[i],arr2[i]);
                    list.add(l_search_store);

                    i++;
                    // testData.setText(arr2[1]);

                }

                adapter = new ListSearchStore_adapter();
                listViewStore.setAdapter(adapter);

              /*  listViewStore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(SearchStore.this, Reservation.class);
                        intent.putExtra("location", position); // String
                        startActivity(intent);

                    }
                });
              */

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }




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
