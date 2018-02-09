package com.awesomecorp.sammy.apogeewallet.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awesomecorp.sammy.apogeewallet.R;

import java.util.List;

/**
 * Created by sammy on 9/2/18.
 */

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ViewHolder> {

    List<String> values;
    Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtStallName;
        public TextView txtDesc;
        public ImageView getItems;

        public ViewHolder(View v) {
            super(v);
            txtStallName =  v.findViewById(R.id.stall_name);
            txtDesc =  v.findViewById(R.id.desc);
            getItems = v.findViewById(R.id.load_items);
        }
    }

    public ShopsAdapter(List<String> data, Activity activity) {
        values=data;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v =inflater.inflate(R.layout.stalls_name_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = values.get(position);
        holder.txtStallName.setText(name);
        holder.getItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Position "+ position,Toast.LENGTH_SHORT).show();
            }
        });

        Typeface montLight = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Light.ttf");
        Typeface montReg = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Regular.ttf");
        holder.txtDesc.setText("Pizzas and Stuff");
        holder.txtStallName.setTypeface(montReg);
        holder.txtDesc.setTypeface(montLight);
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
