package com.netforceinfotech.tripsplit.group.mygroup;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.UserSessionManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyGroupFragment extends Fragment {

    Context context;
    LinearLayout linearlayoutNoGroup;
    ArrayList<MyData> myDatas = new ArrayList<>();
    private MyAdapter myAdapter;
    UserSessionManager userSessionManager;
    DatabaseReference _user_group;
    private RecyclerView recyclerView;
    private MaterialDialog progressDialog;

    public MyGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_group, container, false);
        context = getActivity();
        userSessionManager = new UserSessionManager(context);
        setupRecyclerView(view);
        initView(view);
        //setupData();
        return view;
    }

    private void initView(View view) {
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        linearlayoutNoGroup = (LinearLayout) view.findViewById(R.id.linearlayoutNoGroup);

    }

    @Override
    public void onResume() {
        super.onResume();
        setupFirebase();
    }

    private void setupFirebase() {
        progressDialog.show();
        FirebaseDatabase _root = FirebaseDatabase.getInstance();
        _user_group = _root.getReference().child("user_group");

        _user_group.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.hasChild(userSessionManager.getUserId())) {
                    // run some code
                    linearlayoutNoGroup.setVisibility(View.GONE);
                    DatabaseReference _user_id = _user_group.child(userSessionManager.getUserId());
                   /* _user_id.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot.child("timestamp").getValue(Long.class) != null) {
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
                    _user_id.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            appendGroup(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    linearlayoutNoGroup.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void appendGroup(DataSnapshot dataSnapshot1) {

        Log.i("datasnapshot", dataSnapshot1.toString());

        for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
            try {


                String category = dataSnapshot.child("category").getValue(String.class);
                String city = dataSnapshot.child("city").getValue(String.class);
                String country = dataSnapshot.child("country").getValue(String.class);
                String image_url = dataSnapshot.child("image_url").getValue(String.class);
                Long timestamp = dataSnapshot.child("timestamp").getValue(Long.class);
                String user_id = dataSnapshot.child("user_id").getValue(String.class);
                String key = dataSnapshot.getKey();
                String title = dataSnapshot.child("title").getValue(String.class);
                MyData myData = new MyData(category, city, country, image_url, user_id, key, title, timestamp);
                if (!myDatas.contains(myData)) {
                    Log.i("timestmap", timestamp + "");
                    myDatas.add(myData);
                }
                Log.i("imageurl",image_url);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        myAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(myDatas.size());

    }


    private void setupRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerMyGroup);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        myAdapter = new MyAdapter(context, myDatas);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
    }

}
