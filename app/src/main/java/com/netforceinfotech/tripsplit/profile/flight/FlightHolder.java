package com.netforceinfotech.tripsplit.profile.flight;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Gowtham Chandrasekar on 31-07-2015.
 */
public class FlightHolder extends RecyclerView.ViewHolder {


    View view;


    public FlightHolder(View itemView) {
        super(itemView);
        //implementing onClickListener
        view = itemView;

    }
}