package com.awesomecorp.sammy.apogeewallet.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomecorp.sammy.apogeewallet.R;

import java.util.List;

/**
 * Created by sammy on 7/2/18.
 */

public class RecieptItemAdapter extends RecyclerView.Adapter<RecieptItemAdapter.ViewHolder> {

    private List<String> values;
    Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtStall;
        public TextView txtCost;
        public TextView txtDate;

        public ViewHolder(View v) {
            super(v);

            txtStall =  v.findViewById(R.id.stall);
            txtCost =  v.findViewById(R.id.cost);
            txtDate = v.findViewById(R.id.date);
        }
    }

    public RecieptItemAdapter(List<String> data, Activity activity) {
        values=data;
        this.activity = activity;
    }

    @Override
    public RecieptItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        Log.e("dgdf", "oncreateviewholder");
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v =inflater.inflate(R.layout.bill_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.e("dgdf", "onbind");
        final String name = values.get(position);
        holder.txtStall.setText(name);
        holder.txtStall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Position", position+"");
            }
        });

        Typeface montLight = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Light.ttf");
        Typeface montReg = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Regular.ttf");
        holder.txtDate.setText("FEB 22");
        holder.txtStall.setTypeface(montReg);
        holder.txtDate.setTypeface(montLight);
        holder.txtCost.setTypeface(montLight);

        holder.txtCost.setText("Rs. 150");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
