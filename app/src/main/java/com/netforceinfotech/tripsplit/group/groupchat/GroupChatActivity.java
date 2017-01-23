package com.netforceinfotech.tripsplit.group.groupchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {

    UserSessionManager userSessionManager;
    String group_id = "", title = "No title", city, country, cateogry, image;
    Context context;
    DatabaseReference _group, _group_child, _user_group, _user_group_user_id, _user_group_group_id;
    TextView textViewNoMessage;
    private boolean child_group_exist = false;
    ArrayList<MyData> myDatas = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    EditText et_message;
    private String tempKey;
    private MaterialDialog progressDialog;
    private boolean user_group_user_id_flag = false;
    private boolean user_group_group_id_flag = false;
    private MaterialDialog custompopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        context = this;
        userSessionManager = new UserSessionManager(context);
        try {
            Bundle bundle = getIntent().getExtras();
            title = bundle.getString("title");
            group_id = bundle.getString("group_id");

            image = bundle.getString("image_url");
            country = bundle.getString("country");
            cateogry = bundle.getString("category");
            city = bundle.getString("city");

        } catch (Exception ex) {
            showMessage("Bundle Error");
        }
        setupToolBar(title);
        initView();
        setupFirebase();
        setupRecyclerView();


    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        myAdapter = new MyAdapter(context, myDatas);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
    }

    private void initView() {
        progressDialog = new MaterialDialog.Builder(context)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        findViewById(R.id.buttonSend).setOnClickListener(this);
        et_message = (EditText) findViewById(R.id.et_message);
        textViewNoMessage = (TextView) findViewById(R.id.textViewNoMessage);
        textViewNoMessage.setVisibility(View.GONE);
    }

    private void setupFirebase() {
        progressDialog.show();
        checkUserGroupExist();
        _group = FirebaseDatabase.getInstance().getReference().child("group");
        _group.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.hasChild(group_id)) {
                    // run some code
                    _group_child = _group.child(group_id);
                    child_group_exist = true;
                    textViewNoMessage.setVisibility(View.GONE);
                    _group_child.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            appendMessage(dataSnapshot);
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
                    });


                } else {
                    child_group_exist = false;
                    textViewNoMessage.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkUserGroupExist() {
        _user_group = FirebaseDatabase.getInstance().getReference().child("user_group");
        _user_group.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(userSessionManager.getUserId())) {
                    user_group_user_id_flag = false;
                } else {
                    user_group_user_id_flag = true;
                    _user_group_user_id = _user_group.child(userSessionManager.getUserId());
                    _user_group_user_id.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(group_id)) {
                                user_group_group_id_flag = false;
                            } else {
                                user_group_group_id_flag = true;
                                _user_group_group_id = _user_group_user_id.child(group_id);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void appendMessage(DataSnapshot dataSnapshot) {

        Log.i("datasnampshot", dataSnapshot.toString());
        String id = dataSnapshot.child("user_id").getValue(String.class);
        String image_url = dataSnapshot.child("image").getValue(String.class);
        String message = dataSnapshot.child("message").getValue(String.class);
        String name = dataSnapshot.child("name").getValue(String.class);
        Long timestamp = dataSnapshot.child("timestamp").getValue(Long.class);
        String key = dataSnapshot.getKey();
        boolean you = false;
        if (userSessionManager.getUserId().equalsIgnoreCase(id)) {
            you = true;
        } else {
            you = false;
        }
        Log.i("message", dataSnapshot.child("message").getValue(String.class) + "");
        MyData myData = new MyData(key, id, image_url, message, name, timestamp, you);
        if (!myDatas.contains(myData)) {
            myDatas.add(myData);
        }
        myAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(myDatas.size());

    }

    private void showMessage(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    private void setupToolBar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String teams = title;
        getSupportActionBar().setTitle(teams);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete:
                showRemoveGroupPopup();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSend:
                if (et_message.length() <= 0) {
                    showMessage("Enter Message");
                    return;
                }
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        if (!child_group_exist) {
            HashMap<String, Object> child_group = new HashMap<String, Object>();
            child_group.put(group_id, "");
            _group.updateChildren(child_group).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    _group_child = _group.child(group_id);
                    tempKey = _group_child.push().getKey();
                    _group_child.updateChildren(map);
                    DatabaseReference message_root = _group_child.child(tempKey);
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("image", userSessionManager.getProfileImage());
                    map1.put("message", et_message.getText().toString());
                    map1.put("name", userSessionManager.getName());
                    map1.put("user_id", userSessionManager.getUserId());
                    map1.put("timestamp", ServerValue.TIMESTAMP);
                    message_root.updateChildren(map1);
                    progressDialog.show();
                    message_root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!user_group_user_id_flag) {
                                HashMap<String, Object> user_group_user_id_map = new HashMap<String, Object>();
                                user_group_user_id_map.put(userSessionManager.getUserId(), "");
                                _user_group.updateChildren(user_group_user_id_map).addOnCompleteListener(GroupChatActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        _user_group_user_id = _user_group.child(userSessionManager.getUserId());
                                        createUseridGroupId();
                                    }
                                });
                            } else if (!user_group_group_id_flag) {
                                createUseridGroupId();
                            }
                            progressDialog.dismiss();
                            et_message.setText("");
                            setupFirebase();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();
                            showMessage("Could not send");

                        }
                    });

                }
            });
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            tempKey = _group_child.push().getKey();
            _group_child.updateChildren(map);
            DatabaseReference message_root = _group_child.child(tempKey);
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("image", userSessionManager.getProfileImage());
            map1.put("message", et_message.getText().toString());
            map1.put("name", userSessionManager.getName());
            map1.put("user_id", userSessionManager.getUserId());
            map1.put("timestamp", ServerValue.TIMESTAMP);
            message_root.updateChildren(map1);
            progressDialog.show();
            message_root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!user_group_user_id_flag) {
                        HashMap<String, Object> user_group_user_id_map = new HashMap<String, Object>();
                        user_group_user_id_map.put(userSessionManager.getUserId(), "");
                        _user_group.updateChildren(user_group_user_id_map).addOnCompleteListener(GroupChatActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                _user_group_user_id = _user_group.child(userSessionManager.getUserId());
                                createUseridGroupId();
                            }
                        });
                    } else if (!user_group_group_id_flag) {
                        createUseridGroupId();
                    }
                    progressDialog.dismiss();
                    et_message.setText("");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                    showMessage("Could not send");
                }
            });
        }
    }

    private void createUseridGroupId() {
        HashMap<String, Object> user_group_user_id_group_id_map = new HashMap<String, Object>();
        user_group_user_id_group_id_map.put(group_id, "");
        _user_group_user_id.updateChildren(user_group_user_id_group_id_map).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                _user_group_group_id = _user_group_user_id.child(group_id);
                Map<String, Object> map1 = new HashMap<String, Object>();
                map1.put("category", cateogry);
                map1.put("city", city);
                map1.put("country", country);
                map1.put("user_id", userSessionManager.getUserId());
                map1.put("timestamp", ServerValue.TIMESTAMP);
                map1.put("image_url", image);
                map1.put("title", title);
                _user_group_group_id.updateChildren(map1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.groupchat, menu);
        return true;
    }


    private void showRemoveGroupPopup() {
        if (user_group_group_id_flag) {
            custompopup = new MaterialDialog.Builder(context)
                    .title(R.string.removegroup)
                    .content(R.string.leavegroup)
                    .positiveText(R.string.ok)
                    .negativeText(getString(R.string.cancel))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            removeGroup();
                            custompopup.dismiss();
                        }
                    })
                    .show();
        } else {
            showMessage(getString(R.string.not_in_group));
        }
    }

    private void removeGroup() {
        _user_group_user_id.child(group_id).removeValue();
        finish();

    }
}
