package com.netforceinfotech.tripsplit.profile.myprofile.triplist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.netforceinfotech.tripsplit.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by John on 9/7/2016.
 */
public class MyHolder extends RecyclerView.ViewHolder {

    View view;
    CircleImageView imageView;
    TextView textViewSource, textViewDestination, textViewDate, textViewItenerary;

    public MyHolder(View itemView) {
        super(itemView);
        //implementing onClickListener
        view = itemView;
        imageView = (CircleImageView) view.findViewById(R.id.imageView);
        textViewSource = (TextView) view.findViewById(R.id.textViewSource);
        textViewDestination = (TextView) view.findViewById(R.id.textViewDestination);
        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        textViewItenerary = (TextView) view.findViewById(R.id.textViewItenerary);
      /*  RotateAnimation ranim = (RotateAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
        ranim.setFillAfter(true); //For the textview to remain at the same place after the rotation
        textViewTime.setAnimation(ranim);*/

    }
}
