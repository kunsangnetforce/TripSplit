package com.netforceinfotech.tripsplit.group.mygroup;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.VerticalTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by John on 9/7/2016.
 */
public class MyHolder extends RecyclerView.ViewHolder {


    VerticalTextView textViewTime;
    CircleImageView imageView;
    TextView textViewTitle,textViewCategory,textViewCity,textViewCountry;
    View view;

    public MyHolder(View itemView) {
        super(itemView);
        //implementing onClickListener
        view = itemView;
        textViewTime = (VerticalTextView) view.findViewById(R.id.textViewTime);
        textViewCategory= (TextView) view.findViewById(R.id.textViewCategory);
        textViewCity= (TextView) view.findViewById(R.id.textViewCity);
        textViewCountry= (TextView) view.findViewById(R.id.textViewCountry);
        textViewTitle= (TextView) view.findViewById(R.id.textViewTitle);
        imageView= (CircleImageView) view.findViewById(R.id.imageView);

    }
}
