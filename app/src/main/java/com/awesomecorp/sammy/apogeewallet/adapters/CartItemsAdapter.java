package com.awesomecorp.sammy.apogeewallet.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
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
import com.awesomecorp.sammy.apogeewallet.listners.OnAddToCartButtonListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnRemoveFromCartListener;
import com.awesomecorp.sammy.apogeewallet.models.CartItem;
import com.awesomecorp.sammy.apogeewallet.models.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by sammy on 10/2/18.
 */

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ViewHolder>{

    List<CartItem> values;
    Activity activity;
    SharedPreferences preferences;
    OnRemoveFromCartListener onRemoveFromCartListener;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtItemName;
        public TextView txtCost;
        public ImageView removeFromCart;

        public ViewHolder(View v) {
            super(v);
            txtItemName =  v.findViewById(R.id.item_name);
            txtCost =  v.findViewById(R.id.cost);
            removeFromCart = v.findViewById(R.id.remove_from_cart_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v =inflater.inflate(R.layout.remove_from_cart_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public CartItemsAdapter(List<CartItem> data, Activity activity, OnRemoveFromCartListener onRemoveFromCartListener,SharedPreferences preferences) {
        this.preferences = preferences;
        this.values=data;
        this.activity = activity;
        this.onRemoveFromCartListener = onRemoveFromCartListener ;
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = values.get(position).getName() + " X " + values.get(position).getQuantity();
        holder.txtItemName.setText(name);
        Log.e("Bind", name);
        holder.removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Removing item "+name,Toast.LENGTH_SHORT).show();
                removeItem(position);
//                onRemoveFromCartListener.onRemoveFromCart();
            }
        });

        Typeface montLight = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Light.ttf");
        Typeface montReg = Typeface.createFromAsset(activity.getAssets(),"fonts/Montserrat-Regular.ttf");
        holder.txtCost.setText("INR " + values.get(position).getPrice());
        holder.txtItemName.setTypeface(montReg);
        holder.txtCost.setTypeface(montLight);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    void  removeItem(int pos){
        JSONObject object = null;
        try {
            object = new JSONObject(preferences.getString("cart",""));
            JSONArray cart = object.getJSONArray("current_cart");
            JSONArray newCart = new JSONArray();
            int count=0;
            for (int i=0;i<cart.length();i++){
                if(i!=pos){
                    object = cart.getJSONObject(i);
                    newCart.put(count,object);
                    count++;
                }
            }
            object=new JSONObject();
            object.put("current_cart",newCart);
            SharedPreferences.Editor editor =preferences.edit();
            editor.putString("cart",object.toString());
            editor.commit();
            Log.e("Current Cart",object.toString());
            values.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos,values.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
