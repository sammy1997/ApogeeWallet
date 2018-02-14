package com.awesomecorp.sammy.apogeewallet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.listners.AddMoneyButtonClickListener;

import static android.content.Context.MODE_PRIVATE;

public class WalletEmptyFragment extends Fragment {

    SharedPreferences preferences;
    AddMoneyButtonClickListener addMoneyButtonClickListener;
    ImageView addMoney;

    public WalletEmptyFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet_empty, container, false);
        addMoney = view.findViewById(R.id.add_money);
        Typeface montBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat-Bold.ttf");
        Typeface montSemiBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat-SemiBold.ttf");
        TextView header = view.findViewById(R.id.textView);
        TextView spent = view.findViewById(R.id.textView2);
        spent.setText(preferences.getString("balance","--"));
        TextView spentText = view.findViewById(R.id.textView3);
        TextView balance = view.findViewById(R.id.textView4);
        balance.setText(preferences.getString("balance","--"));
        TextView balanceText = view.findViewById(R.id.textView5);
        header.setTypeface(montSemiBold);
        spent.setTypeface(montBold);
        spentText.setTypeface(montBold);
        balance.setTypeface(montBold);
        balanceText.setTypeface(montSemiBold);
        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMoneyButtonClickListener.onAddMoneyButtonClicked();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            addMoneyButtonClickListener = (AddMoneyButtonClickListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        addMoneyButtonClickListener =null;
        super.onDetach();
    }
}
