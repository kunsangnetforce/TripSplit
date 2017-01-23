package com.netforceinfotech.tripsplit.profile.myprofile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.netforceinfotech.tripsplit.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by John on 8/29/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int YOU = 0;
    private static final int THEM = 1;
    private final LayoutInflater inflater;
    private List<MyData> itemList;
    private Context context;
    ArrayList<Boolean> booleanGames = new ArrayList<>();
    MyHolder viewHolder;


    public MyAdapter(Context context, List<MyData> itemList) {
        this.itemList = itemList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_review, parent, false);
        viewHolder = new MyHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MyHolder myHolder = (MyHolder) holder;
        MyData myData = itemList.get(position);
        myHolder.textViewDate.setText(getFormattedDate(myData.date));
        myHolder.textViewReview.setText(myData.review);
        myHolder.textViewName.setText(myData.name);
        myHolder.ratingbar.setStar(myData.rating);
        Glide.with(context).load(myData.imageUrl).into(myHolder.imageViewDp);

    }


    public String getFormattedDate(String date1) {
        //2017-01-03 14:10:02
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = fmt.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat fmtOut = new SimpleDateFormat("dd EEE yyyy");
        return fmtOut.format(date);


    }

    private void showMessage(String s) {

        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return itemList.size();
//        return itemList.size();
    }

    private String getFormetedTime(Long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("hh:mm a");
        return sfd.format(new Date(timestamp));
    }


}