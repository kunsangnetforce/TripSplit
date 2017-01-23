package com.netforceinfotech.tripsplit.NavigationView.message.writemessage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.netforceinfotech.tripsplit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 8/22/2016.
 */
public class WriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private static final int SIMPLE_TYPE = 0;
    private static final int IMAGE_TYPE = 1;
    private final LayoutInflater inflater;
    private List<WriteData> itemList;
    private Context context;
    ArrayList<Boolean> booleanGames = new ArrayList<>();

    private ItemClickListener clickListener;

    public WriteAdapter(Context context, List<WriteData> itemList)
    {
        this.itemList = itemList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View view = inflater.inflate(R.layout.row_writemessage, parent, false);
        RichestHolder viewHolder = new RichestHolder(view);

        return viewHolder;


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position)
    {

    }

    private void showMessage(String s)
    {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return 12;
//        return itemList.size();
    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class RichestHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {


        TextView textViewTitle, textViewCategory, textViewPros;

        View view;

        public RichestHolder(View itemView) {
            super(itemView);
            //implementing onClickListener
            view = itemView;

            itemView.setOnClickListener(this);

        }


        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }

    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }
}
