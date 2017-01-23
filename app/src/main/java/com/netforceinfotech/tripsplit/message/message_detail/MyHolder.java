package com.netforceinfotech.tripsplit.message.message_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.netforceinfotech.tripsplit.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by John on 9/7/2016.
 */
public class MyHolder extends RecyclerView.ViewHolder {


    CircleImageView imageViewDp;
    TextView textViewName,textViewDate,textViewTime,textViewMessage;
    View view;

    public MyHolder(View itemView) {
        super(itemView);
        //implementing onClickListener
        view = itemView;
        textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        textViewName= (TextView) view.findViewById(R.id.textViewName);
        textViewMessage= (TextView) view.findViewById(R.id.textViewMessage);
        imageViewDp= (CircleImageView) view.findViewById(R.id.imageViewDp);
      /*  RotateAnimation ranim = (RotateAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
        ranim.setFillAfter(true); //For the textview to remain at the same place after the rotation
        textViewTime.setAnimation(ranim);*/

    }
}
