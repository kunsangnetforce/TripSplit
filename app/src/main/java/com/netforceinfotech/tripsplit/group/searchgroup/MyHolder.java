package com.netforceinfotech.tripsplit.group.searchgroup;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.general.VerticalTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by John on 9/7/2016.
 */
public class MyHolder extends RecyclerView.ViewHolder {


    CircleImageView imageView;
    TextView textViewGroupName,textViewCity,textViewCategory,textViewCountry;
    View view;

    public MyHolder(View itemView) {
        super(itemView);
        //implementing onClickListener
        view = itemView;
        imageView= (CircleImageView) view.findViewById(R.id.imageView);
        textViewGroupName = (TextView) view.findViewById(R.id.textViewGroupName);
        textViewCategory= (TextView) view.findViewById(R.id.textViewCategory);
        textViewCity= (TextView) view.findViewById(R.id.textViewCity);
        textViewCountry= (TextView) view.findViewById(R.id.textViewCountry);
      /*  RotateAnimation ranim = (RotateAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
        ranim.setFillAfter(true); //For the textview to remain at the same place after the rotation
        textViewTime.setAnimation(ranim);*/

    }
}
