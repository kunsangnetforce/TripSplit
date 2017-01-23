package com.netforceinfotech.tripsplit.profile.myprofile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hedgehog.ratingbar.RatingBar;
import com.netforceinfotech.tripsplit.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by John on 9/7/2016.
 */
public class MyHolder extends RecyclerView.ViewHolder {


    CircleImageView imageViewDp;
    TextView textViewName,textViewDate,textViewReview;
    RatingBar ratingbar;
    View view;

    public MyHolder(View itemView) {
        super(itemView);
        //implementing onClickListener
        view = itemView;
        ratingbar= (RatingBar) view.findViewById(R.id.ratingbar);
        textViewDate = (TextView) view.findViewById(R.id.textViewReviewDate);
        textViewName= (TextView) view.findViewById(R.id.textViewName);
        textViewReview= (TextView) view.findViewById(R.id.textViewReview);
        imageViewDp= (CircleImageView) view.findViewById(R.id.imageViewDp);
      /*  RotateAnimation ranim = (RotateAnimation) AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
        ranim.setFillAfter(true); //For the textview to remain at the same place after the rotation
        textViewTime.setAnimation(ranim);*/

    }
}
