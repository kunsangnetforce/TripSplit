package com.netforceinfotech.tripsplit.mysplitz;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.netforceinfotech.tripsplit.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by John on 9/7/2016.
 */
public class MyHolder extends RecyclerView.ViewHolder {
    SwipeRevealLayout swipeRevealLayout;
    FrameLayout frameDelete;
    View view;
    CircleImageView imageView;
    LinearLayout linearLayout;
    TextView textViewSource,textViewDestination,textViewDate,textViewItenerary;

    public MyHolder(View itemView) {
        super(itemView);
        //implementing onClickListener
        view = itemView;
        linearLayout= (LinearLayout) view.findViewById(R.id.linearLayout);

        frameDelete = (FrameLayout) view.findViewById(R.id.frameDelete);
        swipeRevealLayout = (SwipeRevealLayout) view.findViewById(R.id.swipeRevealLayout);
        imageView= (CircleImageView) view.findViewById(R.id.imageView);
        textViewSource= (TextView) view.findViewById(R.id.textViewSource);
        textViewDestination= (TextView) view.findViewById(R.id.textViewDestination);
        textViewDate= (TextView) view.findViewById(R.id.textViewDate);
        textViewItenerary= (TextView) view.findViewById(R.id.textViewItenerary);
      /*  RotateAnimation ranim = (RotateAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
        ranim.setFillAfter(true); //For the textview to remain at the same place after the rotation
        textViewTime.setAnimation(ranim);*/

    }
}
