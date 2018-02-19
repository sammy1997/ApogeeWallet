package com.awesomecorp.sammy.apogeewallet.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.WalletActivity;
import com.awesomecorp.sammy.apogeewallet.fragments.CartFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.OrderFoodFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.WalletLoadFragment;
import com.awesomecorp.sammy.apogeewallet.listners.OnGotoShopButtonListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnRemoveFromCartListener;
import com.awesomecorp.sammy.apogeewallet.utils.URLS;
import com.awesomecorp.sammy.apogeewallet.utils.Utils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.awesomecorp.sammy.apogeewallet.utils.Utils.userObject;

public class CartActivity extends AppCompatActivity implements OnGotoShopButtonListener,OnRemoveFromCartListener {

    Activity activity;
    LinearLayout bottomSheet;
    Context context;
    Typeface montLight;
    Typeface montSemiBoldItalic;
    Typeface montMedium;
    Typeface montSemiBold;
    Typeface montReg;
    CartFragment cartFragment;
    Typeface montBold;
    Typeface montMedItalic;
    FirebaseDatabase database;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FrameLayout bottomSheetContents;
    AlertDialog alert;
    LayoutInflater inflater;
    FloatingActionButton floatingActionButton;
    BottomSheetBehavior sheetBehavior;

    void setTypefaces(){
        montBold = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Bold.ttf");
        montLight = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Light.ttf");
        montReg = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Regular.ttf");
        montSemiBoldItalic = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-SemiBoldItalic.ttf");
        montMedium = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Medium.ttf");
        montSemiBold = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-SemiBold.ttf");
        montMedItalic = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-MediumItalic.ttf");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);

        context=this;
        activity=this;

        database = FirebaseDatabase.getInstance();
        preferences = getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        editor = preferences.edit();
        setTypefaces();

        bottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior= BottomSheetBehavior.from(bottomSheet);
        floatingActionButton = findViewById(R.id.central);
        inflater = LayoutInflater.from(context);
        bottomSheetContents = findViewById(R.id.bottom_sheet_content);

        ImageButton shops = findViewById(R.id.shops);
        ImageButton wallet = findViewById(R.id.wallet);

        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMessage("Are you sure you want to place this order?");
            }
        });

        shops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,ShopActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,WalletActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        cartFragment = new CartFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,cartFragment,
                "Order Food Fragment").commit();
    }


    void alertMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        cartFragment.startProgressBar();
                        sendCartRequest();
                    }
                });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });

        alert = alertDialogBuilder.create();
        alert.show();
    }

    void sendCartRequest(){
        try {
            JSONObject cart = new JSONObject(preferences.getString("cart",""));
            JSONArray cartItems = cart.getJSONArray("current_cart");
            int cartTotal = getCartTotal(cartItems);
            if (preferences.getBoolean("is_boolean",false)){
                forwardCartRequest(cart,cartTotal);
            }else {
                int balance  = Integer.valueOf(preferences.getString("balance","0"));
                if (cartTotal<=balance){
                    forwardCartRequest(cart,cartTotal);
                }else{
                    showAlert(cartTotal - balance);
                }
            }
        } catch (JSONException e) {
            cartFragment.stopProgressBar();
            e.printStackTrace();
        }
    }
    int getCartTotal(JSONArray cartItems){
        int cartTotal=0;
        for (int i=0;i<cartItems.length();i++){
            JSONObject obj = null;
            try {
                obj = cartItems.getJSONObject(i);
                cartTotal += Integer.valueOf(obj.getString("price"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return cartTotal;
    }
    void showAlert(final int amount){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You are short by Rs."+amount+". Please add to continue.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        addMoney(amount);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void forwardCartRequest(final JSONObject cart, final int amount){
        JSONObject user = userObject();
        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            Log.e("Token" , token);
                            Log.e("Cart", cart.toString());
                            JSONObject reqParams = new JSONObject();
                            reqParams.put("current_cart",cart.getJSONArray("current_cart"));
                            reqParams.put("WALLET_TOKEN", Utils.wallet_secret);
                            AndroidNetworking.post(URLS.checkout).addJSONObjectBody(reqParams)
                                    .addHeaders("Authorization","JWT " + token)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    showPaymentSuccess(amount);
                                    preferences.edit().remove("cart").apply();
                                    preferences.edit().putBoolean("newOrder",true).apply();
                                    editor.putBoolean("to_be_ordered",false).apply();
                                    Intent i = new Intent(activity,WalletActivity.class);
                                    startActivity(i);
                                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("Error", anError.toString());
                                    cartFragment.stopProgressBar();
                                    showPaymentFailure(amount);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cartFragment.stopProgressBar();
                            showPaymentFailure(amount);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        cartFragment.stopProgressBar();
                        showPaymentFailure(amount);
                    }
                });
    }

    void showPaymentFailure(int amount){
        View failed = inflater.inflate(R.layout.payment_failed_layout,bottomSheetContents,false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(450);
        bottomSheet.requestLayout();
        TextView tv1 = failed.findViewById(R.id.textView16);
        TextView tv2 = failed.findViewById(R.id.textView17);
        tv2.setText("" + amount);
        TextView tv3 = failed.findViewById(R.id.textView18);
        tv1.setTypeface(montMedItalic);
        tv3.setTypeface(montMedItalic);
        tv2.setTypeface(montBold);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(failed);

        if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    void showPaymentSuccess(int amount){
        View success = inflater.inflate(R.layout.payment_success_layout,bottomSheetContents,false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(450);
        bottomSheet.requestLayout();
        TextView tv1 = success.findViewById(R.id.textView16);
        TextView tv2 = success.findViewById(R.id.textView17);
        tv2.setText("" + amount);
        TextView tv3 = success.findViewById(R.id.textView18);
        tv1.setTypeface(montMedItalic);
        tv3.setTypeface(montMedItalic);
        tv2.setTypeface(montBold);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(success);

        if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    void addMoney(int amountToAdd){
        View addMoney = inflater.inflate(R.layout.add_money,bottomSheetContents,false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(500);
        bottomSheet.requestLayout();
        TextView moneyHeader = addMoney.findViewById(R.id.textView6);
        final EditText amount = addMoney.findViewById(R.id.editText);
        Button addMoneyButton = addMoney.findViewById(R.id.add_money_button);
        Button addMoneyBitsian = addMoney.findViewById(R.id.add_money_bitsian);
        TextView rupeesText = addMoney.findViewById(R.id.textView7);
        final ProgressBar progressBar = addMoney.findViewById(R.id.progressBar3);
        moneyHeader.setTypeface(montSemiBoldItalic);
        rupeesText.setTypeface(montSemiBoldItalic);
        amount.setText("" + amountToAdd);
        amount.setTypeface(montBold);
        addMoneyButton.setTypeface(montMedium);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(addMoney);


        addMoneyBitsian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                addAmountRequest1(Integer.valueOf(amount.getText().toString()),progressBar);
                Log.e("Add Money",amount.getText().toString());
            }
        });


        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                addAmountRequest(Integer.valueOf(amount.getText().toString()));
                Log.e("Add Money",amount.getText().toString());
            }
        });

        if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    void addAmountRequest(final int amount){
        JSONObject user = userObject();
        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            Log.e("Token" , token);
                            final JSONObject reqParams = new JSONObject();
                            reqParams.put("amount",amount);
                            reqParams.put("WALLET_TOKEN",Utils.wallet_secret);
                            AndroidNetworking.post(URLS.add_amount).addJSONObjectBody(reqParams)
                                    .addHeaders("Authorization","JWT " + token)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(final JSONObject response) {
                                    try {
                                        Log.e("Add Response", response.getString("url"));

                                        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        }

                                        editor.putBoolean("to_be_ordered",true);
                                        editor.commit();

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent i = new Intent(activity, WebViewActivity.class);
                                                try {
                                                    i.putExtra("url",response.getString("url"));
                                                    startActivity(i);
                                                    finish();
                                                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        },1500);



                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("Error", anError.toString());
                                    showPaymentFailure(amount);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showPaymentFailure(amount);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        showPaymentFailure(amount);
                    }
                });
    }

    @Override
    public void onRemoveFromCart() {
        CartFragment cartFragment = new CartFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,cartFragment).commit();
    }

    @Override
    public void onGotoShopButtonClick() {
        Intent i = new Intent(activity,ShopActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    void addAmountRequest1(final int amount, final ProgressBar progressBar){
        JSONObject user = userObject();
        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            Log.e("Token" , token);
                            final JSONObject reqParams = new JSONObject();
                            reqParams.put("amount",amount);
                            reqParams.put("WALLET_TOKEN",Utils.wallet_secret);
                            AndroidNetworking.post(URLS.addMoneyBitsian).addJSONObjectBody(reqParams)
                                    .addHeaders("Authorization","JWT " + token)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    try {
                                        if (response.getInt("status") == 1) {
                                            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                            }
                                            sendCartRequest();
                                        }else {

                                            Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("Error", anError.toString());
                                    showPaymentFailure(amount);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showPaymentFailure(amount);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        showPaymentFailure(amount);
                    }
                });
    }


}
