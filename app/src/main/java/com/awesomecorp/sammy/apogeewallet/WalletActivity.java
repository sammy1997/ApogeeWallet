package com.awesomecorp.sammy.apogeewallet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.awesomecorp.sammy.apogeewallet.fragments.OrderFoodFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.WalletEmptyFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.WalletHomeFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.WalletLoadFragment;
import com.awesomecorp.sammy.apogeewallet.models.Transaction;
import com.awesomecorp.sammy.apogeewallet.utils.Utils;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.List;

import okhttp3.OkHttpClient;

public class WalletActivity extends FragmentActivity implements
        WalletLoadFragment.OnDataLoadedListner, WalletHomeFragment.AddMoneyButtonListener,
        OrderFoodFragment.AddMoneyListener{

    BottomSheetBehavior sheetBehavior;
    Activity activity;
    LinearLayout bottomSheet;
    Context context;
    Typeface montBold;
    Typeface montLight;
    boolean changedToWallet;
    Typeface montSemiBoldItalic;
    Typeface montMedium;
    Typeface montSemiBold;
    Typeface montReg;
    TextView quantity;
    LayoutInflater inflater;
    FrameLayout bottomSheetContents;

    void setTypefaces(){
       montBold = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Bold.ttf");
       montLight = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Light.ttf");
       montReg = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Regular.ttf");
       montSemiBoldItalic = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-SemiBoldItalic.ttf");
       montMedium = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Medium.ttf");
       montSemiBold = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-SemiBold.ttf");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
        context=this;
        activity=this;
        setTypefaces();
        changedToWallet = false;
        bottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior= BottomSheetBehavior.from(bottomSheet);
        final FloatingActionButton floatingActionButton = findViewById(R.id.central);

        inflater = LayoutInflater.from(context);
        bottomSheetContents = findViewById(R.id.bottom_sheet_content);

        WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment,walletLoadFragment).commit();

        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageButton payOrReceive = findViewById(R.id.pay_receive);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderFoodFragment orderFoodFragment = new OrderFoodFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment,orderFoodFragment,
                        "Order Food Fragment").commit();
                floatingActionButton.setImageResource(R.drawable.white_qr);
                payOrReceive.setImageResource(R.drawable.wallet_icon);
                changedToWallet = true;
            }
        });


        payOrReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!changedToWallet) {
                    View pay_receive = inflater.inflate(R.layout.transfer_receive_layout,
                            bottomSheetContents, false);
                    bottomSheet.getLayoutParams().height = Utils.dpToPx(400);
                    bottomSheet.requestLayout();
                    TextView topText = pay_receive.findViewById(R.id.textView8);
                    Button receiveButton = pay_receive.findViewById(R.id.receive_money);
                    Button transferMoney = pay_receive.findViewById(R.id.transfer_money);
                    topText.setTypeface(montSemiBoldItalic);
                    receiveButton.setTypeface(montMedium);
                    transferMoney.setTypeface(montMedium);
                    bottomSheetContents.removeAllViews();
                    bottomSheetContents.addView(pay_receive);
                    if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }

                    transferMoney.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            View transferMoney = inflater.inflate(R.layout.pay_money, bottomSheetContents, false);
                            bottomSheet.getLayoutParams().height =  Utils.dpToPx(400);
                            bottomSheet.requestLayout();
                            TextView moneyHeader = transferMoney.findViewById(R.id.textView6);
                            final EditText amount = transferMoney.findViewById(R.id.editText);
                            Button continueBtn = transferMoney.findViewById(R.id.pay_money_button);
                            TextView rupeesText = transferMoney.findViewById(R.id.textView7);
                            moneyHeader.setTypeface(montSemiBoldItalic);
                            rupeesText.setTypeface(montSemiBoldItalic);
                            amount.setTypeface(montBold);
                            continueBtn.setTypeface(montMedium);
                            bottomSheetContents.removeAllViews();
                            bottomSheetContents.addView(transferMoney);

                            continueBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.e("Add Money", amount.getText().toString());
                                }
                            });

                            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }

                        }
                    });

                    receiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View receiveMoney = inflater.inflate(R.layout.profile_qr_code_layout, bottomSheetContents, false);
                            bottomSheet.getLayoutParams().height =  Utils.dpToPx(420);
                            bottomSheet.requestLayout();
                            bottomSheetContents.removeAllViews();
                            bottomSheetContents.addView(receiveMoney);
                            TextView name = receiveMoney.findViewById(R.id.name);
                            TextView college = receiveMoney.findViewById(R.id.college);
                            TextView userId = receiveMoney.findViewById(R.id.id_user);
                            name.setTypeface(montBold);
                            college.setTypeface(montMedium);
                            userId.setTypeface(montMedium);
                        }
                    });

                }else{
                    changedToWallet = false;
                    floatingActionButton.setImageResource(R.drawable.food_icon);
                    payOrReceive.setImageResource(R.drawable.pay_icon);
                    WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment,walletLoadFragment,"Replace Load").commit();
                }
            }
        });
    }

    @Override
    public void onDataLoaded(List<Transaction> transactions, boolean listEmpty) {
        if (listEmpty){
            WalletEmptyFragment walletEmptyFragment = new WalletEmptyFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletEmptyFragment,
                    "EmptyFragment").commit();
        }else{
            WalletHomeFragment walletHomeFragment = new WalletHomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletHomeFragment,
                                "Loaded Fragment").commit();
        }
    }

    @Override
    public void onAddMoneyButtonClicked() {

        View addMoney = inflater.inflate(R.layout.add_money,bottomSheetContents,false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(500);
        bottomSheet.requestLayout();
        TextView moneyHeader = addMoney.findViewById(R.id.textView6);
        final EditText amount = addMoney.findViewById(R.id.editText);
        Button addMoneyButton = addMoney.findViewById(R.id.add_money_button);
        TextView rupeesText = addMoney.findViewById(R.id.textView7);
        moneyHeader.setTypeface(montSemiBoldItalic);
        rupeesText.setTypeface(montSemiBoldItalic);
        amount.setTypeface(montBold);
        addMoneyButton.setTypeface(montMedium);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(addMoney);

        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Add Money",amount.getText().toString());
            }
        });

        if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onAddMoneyClicked() {
        View addMoney = inflater.inflate(R.layout.add_money,bottomSheetContents,false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(500);
        bottomSheet.requestLayout();
        TextView moneyHeader = addMoney.findViewById(R.id.textView6);
        final EditText amount = addMoney.findViewById(R.id.editText);
        Button addMoneyButton = addMoney.findViewById(R.id.add_money_button);
        TextView rupeesText = addMoney.findViewById(R.id.textView7);
        moneyHeader.setTypeface(montSemiBoldItalic);
        rupeesText.setTypeface(montSemiBoldItalic);
        amount.setTypeface(montBold);
        addMoneyButton.setTypeface(montMedium);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(addMoney);

        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Add Money",amount.getText().toString());
            }
        });

        if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}
