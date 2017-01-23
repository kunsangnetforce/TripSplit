package com.netforceinfotech.tripsplit.NavigationView.message;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.netforceinfotech.tripsplit.R;


/**1
 * Created by asdf on 7/21/2016.
 */
public class MessageHolder extends RecyclerView.ViewHolder {


    TextView textViewTitle, textViewCategory, textViewPros;

    MaterialRippleLayout materialRippleLayout;
    View view;


    public MessageHolder(View itemView) {
        super(itemView);
        //implementing onClickListener
        view = itemView;

        materialRippleLayout = (MaterialRippleLayout) itemView.findViewById(R.id.ripple);

    }
}