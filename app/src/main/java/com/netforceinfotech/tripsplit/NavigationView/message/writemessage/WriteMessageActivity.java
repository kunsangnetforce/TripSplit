package com.netforceinfotech.tripsplit.NavigationView.message.writemessage;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.netforceinfotech.tripsplit.R;

import java.util.ArrayList;

public class WriteMessageActivity extends AppCompatActivity
{


    private RecyclerView recyclerView;
    Context context;
    private LinearLayoutManager layoutManager;
    private WriteAdapter adapter;
    FrameLayout messageLayout;
    int theme;

    ArrayList<WriteData> highestDatas = new ArrayList<WriteData>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupRecyclerView();
    }

    private void setupRecyclerView()
    {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerMyGroup);

        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new WriteAdapter(getApplicationContext(), highestDatas);
        recyclerView.setAdapter(adapter);
        setupFinsihedDatas();
        adapter.notifyDataSetChanged();

    }

    private void setupFinsihedDatas()
    {
        try
        {
            highestDatas.clear();
        }
        catch (Exception ex) {

        }
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));
        highestDatas.add(new WriteData("Tea", "imageurl"));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
