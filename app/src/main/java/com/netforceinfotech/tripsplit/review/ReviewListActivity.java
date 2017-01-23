package com.netforceinfotech.tripsplit.review;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.netforceinfotech.tripsplit.R;

import java.util.ArrayList;

public class ReviewListActivity extends AppCompatActivity implements View.OnClickListener
{

    RecyclerView recyclerView;
    MaterialDialog dailog;
    LinearLayoutManager layoutManager;
    ReviewAdapter adapter;
    ArrayList<ReviewData> highestDatas = new ArrayList<ReviewData>();
    Button write_review_button;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupRecyclerView();


    }

    private void setupRecyclerView()
    {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerMyGroup);

        write_review_button = (Button)   findViewById(R.id.write_reviewButton);

        write_review_button.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ReviewAdapter(getApplicationContext(), highestDatas);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        setupFinsihedDatas();
        adapter.notifyDataSetChanged();
    }

    private void setupFinsihedDatas()
    {
        try {
            highestDatas.clear();
        } catch (Exception ex) {

        }
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
        highestDatas.add(new ReviewData("Tea", "imageurl"));
    }



    public void  onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.write_reviewButton:

                showPopUp("hi");
                return;

            default:
                return;



        }

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


    private void showPopUp(String s)
    {
        dailog = new MaterialDialog.Builder(ReviewListActivity.this)

                .customView(R.layout.custom_write_review_dailog, true).build();

        Button b = (Button) dailog.findViewById(R.id.submit);
       // TextView textView = (TextView) dailog.findViewById(R.id.textView1);

        //   mExplosionField.explode(icon,null,0,5000);
        //addListener(dailog.findViewById(R.id.root));

        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                dailog.dismiss();

            }
        });
        dailog.show();

    }


}
