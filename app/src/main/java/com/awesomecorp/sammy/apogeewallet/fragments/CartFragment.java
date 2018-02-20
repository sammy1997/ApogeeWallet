package com.awesomecorp.sammy.apogeewallet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.adapters.CartItemsAdapter;
import com.awesomecorp.sammy.apogeewallet.listners.BackPressedListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnGotoShopButtonListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnRemoveFromCartListener;
import com.awesomecorp.sammy.apogeewallet.models.CartItem;
import com.awesomecorp.sammy.apogeewallet.models.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class CartFragment extends Fragment{

    OnRemoveFromCartListener removeFromCartListener;
    OnGotoShopButtonListener gotoShopButtonListener;
    BackPressedListener backPressedListener;
    SharedPreferences preferences;
    TextView textView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    List<CartItem> items;
    ProgressBar progressBar;

    public CartFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = v.findViewById(R.id.cart_list);
        preferences = getActivity().getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        progressBar = v.findViewById(R.id.progressBar2);
        items = new ArrayList<>();
        ImageView shopMore = v.findViewById(R.id.shop_more);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        textView = v.findViewById(R.id.textView11);
        getCartItems();
        Log.e("In Cart",items.toString());
        adapter = new CartItemsAdapter(items,this.getActivity(), removeFromCartListener,preferences);
        recyclerView.setAdapter(adapter);

        shopMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoShopButtonListener.onGotoShopButtonClick();
            }
        });

        ImageView back = v.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressedListener.onBackButtonFragment();
            }
        });

        return v;
    }

    public void startProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
           backPressedListener = (BackPressedListener)context;
           removeFromCartListener = (OnRemoveFromCartListener)context;
           gotoShopButtonListener = (OnGotoShopButtonListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement onRemoveFromCart");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        backPressedListener = null;
        gotoShopButtonListener =null;
        removeFromCartListener = null;
    }
    void getCartItems(){
        try {
            JSONObject object = new JSONObject(preferences.getString("cart",""));
            JSONArray cart = object.getJSONArray("current_cart");
            for(int i=0;i<cart.length();i++){
               object = cart.getJSONObject(i);
               CartItem item = new CartItem();
               item.setId(object.getString("id"));
               item.setPrice(object.getString("price"));
               item.setName(object.getString("name"));
               item.setQuantity(object.getString("quantity"));
               items.add(item);
            }
            Log.e("In fragment",items.toString());
            adapter = new CartItemsAdapter(items,getActivity(), removeFromCartListener, preferences );
            recyclerView.setAdapter(adapter);
            if(items.size() == 0){
                recyclerView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
            }else {
                textView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}