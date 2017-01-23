package com.netforceinfotech.tripsplit.review;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.review.ReviewDetails.ReviewDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 8/26/2016.
 */
public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private static final int SIMPLE_TYPE = 0;
    private static final int IMAGE_TYPE = 1;
    private final LayoutInflater inflater;
    private List<ReviewData> itemList;
    private Context context;
    ArrayList<Boolean> booleanGames = new ArrayList<>();
    private ItemClickListener clickListener;



    public ReviewAdapter(Context context, List<ReviewData> itemList)
    {
        this.itemList = itemList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View view = inflater.inflate(R.layout.row_review, parent, false);
        ReviewHolder viewHolder = new ReviewHolder(view);
        for (int i = 0; i < itemList.size(); i++)
        {
            if (i == 0)
            {
                booleanGames.add(true);
            }
            else
            {
                booleanGames.add(false);
            }
            Log.i("looppp", "" + i);
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
    {

    }

    private void showMessage(String s)
    {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }



    @Override
    public int getItemCount() {
        return 12;
//        return itemList.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ReviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView textViewTitle, textViewCategory, textViewPros;

        View view;

        public ReviewHolder(View itemView)
        {
            super(itemView);
            //implementing onClickListener
            view = itemView;
            itemView.setOnClickListener(this);
        }

        public void onClick(View view)
        {
            Intent intent =  new Intent(context, ReviewDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

}