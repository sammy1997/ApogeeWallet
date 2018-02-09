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
import com.awesomecorp.sammy.apogeewallet.adapters.RecieptItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class WalletHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    List<String> data;
    private RecyclerView.LayoutManager layoutManager;

    AddMoneyButtonListener moneyButtonListener;


    ImageView addMoney;
    public interface AddMoneyButtonListener{
        public void onAddMoneyButtonClicked();
    }

    public WalletHomeFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_wallet_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        addMoney = view.findViewById(R.id.add_money);
        data = new ArrayList<>();
        data.add("Stall #23");
        data.add("Stall #24");
        data.add("Stall #25");
        data.add("Stall #26");
        data.add("Stall #27");
        data.add("Stall #28");
        data.add("Stall #26");
        data.add("Stall #27");
        data.add("Stall #28");
        data.add("Stall #26");
        data.add("Stall #27");
        data.add("Stall #28");
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecieptItemAdapter(data,this.getActivity());
        recyclerView.setAdapter(adapter);
        Typeface montBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat-Bold.ttf");
        Typeface montSemiBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat-SemiBold.ttf");
        TextView header = view.findViewById(R.id.textView);
        TextView spent = view.findViewById(R.id.textView2);
        TextView spentText = view.findViewById(R.id.textView3);
        TextView balance = view.findViewById(R.id.textView4);
        TextView balanceText = view.findViewById(R.id.textView5);
        header.setTypeface(montSemiBold);
        spent.setTypeface(montBold);
        spentText.setTypeface(montBold);
        balance.setTypeface(montBold);
        balanceText.setTypeface(montSemiBold);
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyButtonListener.onAddMoneyButtonClicked();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            moneyButtonListener = (AddMoneyButtonListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        moneyButtonListener = null;
        super.onDetach();
    }

}
