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
import com.awesomecorp.sammy.apogeewallet.listners.OnViewItemsClickedListener;
import com.awesomecorp.sammy.apogeewallet.models.Shop;

import java.util.List;

/**
 * Created by sammy on 9/2/18.
 */

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ViewHolder> {

    List<Shop> values;
    Activity activity;
    OnViewItemsClickedListener onViewItemsClickedListener;
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

    public ShopsAdapter(List<Shop> data, Activity activity, OnViewItemsClickedListener onViewItemsClickedListener) {
        values=data;
        this.activity = activity;
        this.onViewItemsClickedListener = onViewItemsClickedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v =inflater.inflate(R.layout.stalls_name_item, parent, false);

        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewItemsClickedListener.onViewItemClicked(values.get(vh.getAdapterPosition()));
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = values.get(position).getName();
        holder.txtStallName.setText(name);
        holder.getItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewItemsClickedListener.onViewItemClicked(values.get(position));
//                Toast.makeText(activity, "Position "+ position,Toast.LENGTH_SHORT).show();
            }
        });

        Typeface montLight = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Light.ttf");
        Typeface montReg = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Regular.ttf");
        holder.txtDesc.setText(values.get(position).getDescription());
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
