package com.netforceinfotech.tripsplit.search.searchfragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netforceinfotech.tripsplit.R;

/**
 * Created by John on 9/7/2016.
 */
public class CarHolder extends RecyclerView.ViewHolder {


   /*
   * textViewPax
textCarType
textViewJourneyTime
textViewETA
textViewDestination
textViewSource
textviewETD_time
textViewPrice
   * */

    View view;
    TextView textViewETD_date, textViewETD_time, textViewSource, textViewDestination, textViewETA, textViewJourneyTime,
            textViewCarType, textViewPax, textViewPrice, textViewETD_date_return, textViewETD_time_return,
            textViewSource_return, textViewDestination_return, textViewETA_return, textViewJourneyTime_return;
    LinearLayout linearLayoutReturn ;

    public CarHolder(View itemView) {
        super(itemView);
        //implementing onClickListener
        view = itemView;
        linearLayoutReturn = (LinearLayout) view.findViewById(R.id.linearLayoutReturn);
        textViewETD_date_return = (TextView) view.findViewById(R.id.textviewETD_dateReturn);
        textViewETD_time_return = (TextView) view.findViewById(R.id.textviewETD_timeReturn);
        textViewSource_return = (TextView) view.findViewById(R.id.textViewSourceReturn);
        textViewDestination_return = (TextView) view.findViewById(R.id.textViewDestinationReturn);
        textViewETA_return = (TextView) view.findViewById(R.id.textViewETAReturn);
        textViewJourneyTime_return = (TextView) view.findViewById(R.id.textViewJourneyTimeReturn);
        textViewETD_date = (TextView) view.findViewById(R.id.textviewETD_date);
        textViewETD_time = (TextView) view.findViewById(R.id.textviewETD_time);
        textViewSource = (TextView) view.findViewById(R.id.textViewSource);
        textViewDestination = (TextView) view.findViewById(R.id.textViewDestination);
        textViewETA = (TextView) view.findViewById(R.id.textViewETA);
        textViewJourneyTime = (TextView) view.findViewById(R.id.textViewJourneyTime);
        textViewCarType = (TextView) view.findViewById(R.id.textCarType);
        textViewPax = (TextView) view.findViewById(R.id.textViewPax);
        textViewPrice = (TextView) view.findViewById(R.id.textViewPrice);
    }
}
