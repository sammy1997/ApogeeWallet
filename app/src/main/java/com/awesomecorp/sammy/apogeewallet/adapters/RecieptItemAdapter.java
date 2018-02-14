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
import com.awesomecorp.sammy.apogeewallet.listners.OnReceiptItemClickListener;
import com.awesomecorp.sammy.apogeewallet.models.Stall;
import com.awesomecorp.sammy.apogeewallet.models.Transaction;

import java.util.Date;
import java.util.List;

/**
 * Created by sammy on 7/2/18.
 */

public class RecieptItemAdapter extends RecyclerView.Adapter<RecieptItemAdapter.ViewHolder> {

    private List<Transaction> values;
    OnReceiptItemClickListener receiptItemClickListener;
    Activity activity;

    String months[] = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV","DEC"};

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtStall;
        public TextView txtCost;
        public TextView txtStatus;

        public TextView status;
        public TextView txtDate;

        public ViewHolder(View v) {
            super(v);

            txtStatus = v.findViewById(R.id.status_text);
            status = v.findViewById(R.id.status);
            txtStall =  v.findViewById(R.id.stall);
            txtCost =  v.findViewById(R.id.cost);
            txtDate = v.findViewById(R.id.date);
        }
    }

    public RecieptItemAdapter(List<Transaction> data, Activity activity, OnReceiptItemClickListener receiptItemClickListener) {
        values=data;
        this.receiptItemClickListener = receiptItemClickListener;
        this.activity = activity;
    }

    @Override
    public RecieptItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        Log.e("dgdf", "oncreateviewholder");
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v =inflater.inflate(R.layout.bill_item, parent, false);

        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiptItemClickListener.onReceiptItemClicked(values.get(vh.getAdapterPosition()));
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.e("dgdf", values.toString());

        holder.txtStall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiptItemClickListener.onReceiptItemClicked(values.get(position));
            }
        });

        setItem(holder, values.get(position));

        Typeface montLight = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Light.ttf");
        Typeface montReg = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Regular.ttf");
        holder.txtStall.setTypeface(montReg);
        holder.txtDate.setTypeface(montLight);
        holder.txtCost.setTypeface(montLight);
        holder.txtStatus.setTypeface(montReg);
    }

    void setItem(ViewHolder holder, Transaction transaction){
        String type = transaction.getT_type();
        if (type.equals("buy")){
            Stall stall = transaction.getStallgroup();
            if (stall.isOrder_ready() && !stall.isOrder_complete() && !stall.isCancelled()){
                holder.status.setBackgroundResource(R.drawable.circle_green);
                holder.txtStatus.setText("ready");
            }
            else if(stall.isOrder_complete()){
                holder.status.setBackgroundResource(R.drawable.circle_red);
                holder.txtStatus.setText("completed");
            }else if(!stall.isOrder_complete() && !stall.isCancelled()){
                holder.status.setBackgroundResource(R.drawable.circle_yellow);
                holder.txtStatus.setText("pending");
            }else if (stall.isCancelled()){
                holder.status.setBackgroundResource(R.drawable.circle_red);
                holder.txtStatus.setText("cancelled");
            }
            holder.txtStall.setText(transaction.getStallgroup().getStallname());
            holder.txtCost.setText("INR " + transaction.getValue());
            holder.txtDate.setText( transaction.getCreated_at());
        }else if (type.equals("add")){
            holder.status.setBackgroundResource(R.drawable.circle_blue);
            holder.txtStall.setText("Added");
            holder.txtStatus.setText("transaction");
            holder.txtCost.setText("INR " + transaction.getValue());
            holder.txtDate.setText( transaction.getCreated_at());
        }else if (type.equals("transfer")){
            holder.status.setBackgroundResource(R.drawable.circle_blue);
            holder.txtStatus.setText("transaction");
            holder.txtStall.setText("Transferred");
            holder.txtCost.setText("INR " + transaction.getValue());
            holder.txtDate.setText( transaction.getCreated_at());
        }else if (type.equals("recieve")){
            holder.status.setBackgroundResource(R.drawable.circle_blue);
            holder.txtStatus.setText("transaction");
            holder.txtStall.setText("Received");
            holder.txtCost.setText("INR " + transaction.getValue());
            holder.txtDate.setText( transaction.getCreated_at());
        }
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
