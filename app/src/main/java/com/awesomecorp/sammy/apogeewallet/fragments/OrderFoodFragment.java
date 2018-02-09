package com.awesomecorp.sammy.apogeewallet.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.adapters.ShopsAdapter;
import com.awesomecorp.sammy.apogeewallet.models.Shop;

import java.util.ArrayList;
import java.util.List;

public class OrderFoodFragment extends Fragment {
    List<Shop> shops;
    List<String> test;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public OrderFoodFragment() {
    }

    AddMoneyListener moneyButtonListener;


    ImageView addMoney;
    public interface AddMoneyListener{
        public void onAddMoneyClicked();
    }


    public static OrderFoodFragment newInstance(List<Shop> shops) {
        OrderFoodFragment fragment = new OrderFoodFragment();
        fragment.shops = shops;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View orderFood = inflater.inflate(R.layout.fragment_order_food, container, false);
        recyclerView = orderFood.findViewById(R.id.shop_list);
        test = new ArrayList<>();
        addMoney = orderFood.findViewById(R.id.add_money);
        test.add("Megh Thakkar");
        test.add("Madhur Wadhwa");
        test.add("Laddha Madarchod");
        Typeface montSemiBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat-SemiBold.ttf");
        TextView header = orderFood.findViewById(R.id.textView);
        header.setTypeface(montSemiBold);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ShopsAdapter(test,this.getActivity());
        recyclerView.setAdapter(adapter);

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyButtonListener.onAddMoneyClicked();
            }
        });
        return orderFood;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            moneyButtonListener = (AddMoneyListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement addMoneyListener");
        }
    }

    @Override
    public void onDetach() {
        moneyButtonListener =null;
        super.onDetach();
    }

}
