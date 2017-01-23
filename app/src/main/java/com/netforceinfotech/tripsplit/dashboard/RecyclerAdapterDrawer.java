package com.netforceinfotech.tripsplit.dashboard;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netforceinfotech.tripsplit.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by John on 9/14/2016.
 */
public class RecyclerAdapterDrawer extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NORMAL = 1;
    private static final int DUMMY = 0;
    private static final int MESSAGE = 2;
    public static int selected_item = 0;
    private final LayoutInflater inflater;
    List<RowDataDrawer> data = Collections.emptyList();
    Context context;
    boolean newMessage;
    public clickListner click = null;

    public RecyclerAdapterDrawer(Context context, List<RowDataDrawer> data, boolean newMessage) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.newMessage = newMessage;
        this.context = context;


    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).text.equalsIgnoreCase("dummy")) {
            return DUMMY;
        } else if (data.get(position).text.equalsIgnoreCase("messages")) {
            return MESSAGE;
        } else {
            return NORMAL;
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == DUMMY) {
            View view = inflater.inflate(R.layout.row_dummy, parent, false);
            MyViewHolder1 viewHolder = new MyViewHolder1(view);
            return viewHolder;
        } else if (viewType == MESSAGE) {
            View view = inflater.inflate(R.layout.row_navigation_drawer_message, parent, false);
            MyViewHolder2 viewHolder = new MyViewHolder2(view);
            return viewHolder;
        } else {
            View view = inflater.inflate(R.layout.row_navigation_drawer, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        Log.i("testing","refresh "+newMessage);

        if (getItemViewType(position) == NORMAL) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;

            if (position == selected_item) {

                myViewHolder.textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                myViewHolder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.selected_menu_back));
                myViewHolder.imageView.setImageResource(data.get(position).id);

            } else {

                myViewHolder.textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                myViewHolder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                myViewHolder.imageView.setImageResource(data.get(position).id);
            }

            myViewHolder.textView.setText(data.get(position).text);

        }
        if (getItemViewType(position) == MESSAGE) {
            MyViewHolder2 myViewHolder = (MyViewHolder2) holder;

            if (position == selected_item) {

                myViewHolder.textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                myViewHolder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.selected_menu_back));
                myViewHolder.imageView.setImageResource(data.get(position).id);

            } else {

                myViewHolder.textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                myViewHolder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                myViewHolder.imageView.setImageResource(data.get(position).id);
            }
            if (newMessage) {
                myViewHolder.textviewBadge.setText("New");
                myViewHolder.textviewBadge.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.textviewBadge.setVisibility(View.GONE);
            }
            myViewHolder.textView.setText(data.get(position).text);

        }
    }

    private void showMessage(String s) {

        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setClickListner(clickListner click) {
        this.click = click;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        LinearLayout linearLayout;


        public MyViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView1);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.header);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.itemClicked(getAdapterPosition());
                }
            });
        }


    }

    class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView textView, textviewBadge;
        ImageView imageView;
        LinearLayout linearLayout;


        public MyViewHolder2(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView1);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.header);
            textviewBadge = (TextView) itemView.findViewById(R.id.textviewBadge);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    click.itemClicked(getAdapterPosition());

                }
            });
        }


    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {


        public MyViewHolder1(final View itemView) {
            super(itemView);
        }


    }

    public interface clickListner {
        void itemClicked(int position);
    }

}
