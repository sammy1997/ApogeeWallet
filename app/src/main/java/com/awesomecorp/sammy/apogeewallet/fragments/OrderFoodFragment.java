package com.awesomecorp.sammy.apogeewallet.fragments;

import android.content.Context;
import android.graphics.Typeface;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.adapters.ShopsAdapter;
import com.awesomecorp.sammy.apogeewallet.listners.OnCartViewButtonListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnViewItemsClickedListener;
import com.awesomecorp.sammy.apogeewallet.listners.ShopLoadListener;
import com.awesomecorp.sammy.apogeewallet.models.Shop;
import com.awesomecorp.sammy.apogeewallet.utils.URLS;
import com.awesomecorp.sammy.apogeewallet.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.awesomecorp.sammy.apogeewallet.utils.Utils.userObject;

public class OrderFoodFragment extends Fragment {
    List<Shop> shops;
    ProgressBar progressBar;
    TextView tv;
    ImageView refresh;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public OrderFoodFragment() {
    }

    OnCartViewButtonListener cartViewListener;
    OnViewItemsClickedListener viewItemsClickedListener;
    ImageView cartView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View orderFood = inflater.inflate(R.layout.fragment_order_food, container, false);
        recyclerView = orderFood.findViewById(R.id.shop_list);
        shops = new ArrayList<>();
        cartView = orderFood.findViewById(R.id.view_cart);
        progressBar = orderFood.findViewById(R.id.shop_loader);
        tv = orderFood.findViewById(R.id.textView11);
        refresh = orderFood.findViewById(R.id.refresh);
        Typeface montSemiBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat-SemiBold.ttf");
        TextView header = orderFood.findViewById(R.id.textView);
        header.setTypeface(montSemiBold);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        getShops();
        adapter = new ShopsAdapter(shops,this.getActivity(),viewItemsClickedListener);
        recyclerView.setAdapter(adapter);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShops();
            }
        });


        cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartViewListener.onCartViewButtonListener();
            }
        });
        return orderFood;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            cartViewListener = (OnCartViewButtonListener) context;
            viewItemsClickedListener = (OnViewItemsClickedListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement addMoneyListener");
        }
    }

    @Override
    public void onDetach() {
        cartViewListener =null;
        viewItemsClickedListener =null;
        super.onDetach();
    }

    void getShops() {
        shops = new ArrayList<>();
        JSONObject jsonObject = userObject();
        progressBar.setVisibility(View.VISIBLE);
        tv.setVisibility(View.INVISIBLE);
        refresh.setVisibility(View.GONE);
        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(jsonObject).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String token = null;
                        try {
                            token = response.getString("token");
                            Log.e("Token",token);
                            AndroidNetworking.post(URLS.get_shops).addJSONObjectBody(Utils.walletSecret())
                                    .addHeaders("Authorization","JWT " + token).build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    JSONArray stalls=null;
                                    try {
                                         stalls = response.getJSONArray("stalls");
                                         for (int i=0; i< stalls.length(); i++) {
                                             JSONObject object = stalls.getJSONObject(i);
                                             Shop shop = new Shop();
                                             shop.setDescription(object.getString("description"));
                                             shop.setId(object.getString("id"));
                                             shop.setName(object.getString("name"));
                                             shops.add(shop);
                                         }

                                        adapter = new ShopsAdapter(shops,getActivity(),viewItemsClickedListener);
                                        recyclerView.setAdapter(adapter);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    tv.setVisibility(View.VISIBLE);
                                    refresh.setVisibility(View.VISIBLE);
                                }
                            });

                        } catch (JSONException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            tv.setVisibility(View.VISIBLE);
                            refresh.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        tv.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                    }
                });
    }
}
