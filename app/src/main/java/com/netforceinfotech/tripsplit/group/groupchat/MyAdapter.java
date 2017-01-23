package com.netforceinfotech.tripsplit.group.groupchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
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
        if (viewType == YOU) {
            View view = inflater.inflate(R.layout.row_groupchatyou, parent, false);
            viewHolder = new MyHolder(view);
            return viewHolder;
        } else {
            View view = inflater.inflate(R.layout.row_groupchatthem, parent, false);
            viewHolder = new MyHolder(view);
            return viewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.get(position).you) {
            return YOU;
        } else {
            return THEM;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.textViewDate.setText(getFormattedDate(itemList.get(position).timestamp));
        myHolder.textViewTime.setText(getFormetedTime(itemList.get(position).timestamp));
        try {
            Glide.with(context).load(itemList.get(position).image_url).error(R.drawable.ic_error).into(myHolder.imageViewDp);
        }catch (Exception ex){

        }

        myHolder.textViewMessage.setText(itemList.get(position).message);
        myHolder.textViewName.setText(itemList.get(position).name);

    }


    public String getFormattedDate(long timestamp) {
        Date date = new Date(timestamp);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.DATE) == cal.get(Calendar.DATE)) {
            return "Today";
        } else if (now.get(Calendar.DATE) - cal.get(Calendar.DATE) == 1) {
            return "Yesterday ";
        } else {
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
            return sfd.format(new Date(timestamp));
        }


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