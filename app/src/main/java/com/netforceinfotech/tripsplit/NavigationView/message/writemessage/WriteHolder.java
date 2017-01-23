package com.netforceinfotech.tripsplit.NavigationView.message.writemessage;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.netforceinfotech.tripsplit.R;

/**
 * Created by John on 8/22/2016.
 */
public class WriteHolder extends RecyclerView.ViewHolder {


    TextView textViewTitle, textViewCategory, textViewPros;

    MaterialRippleLayout materialRippleLayout;
    View view;


    public WriteHolder(View itemView)

    {
        super(itemView);
        //implementing onClickListener
        view = itemView;

        materialRippleLayout = (MaterialRippleLayout) itemView.findViewById(R.id.ripple);

    }
}