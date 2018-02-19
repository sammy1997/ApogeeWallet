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
import com.awesomecorp.sammy.apogeewallet.utils.URLS;
import com.awesomecorp.sammy.apogeewallet.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.awesomecorp.sammy.apogeewallet.utils.Utils.userObject;

public class OrderPlaceIntermediateActivity extends AppCompatActivity {

    Activity activity;
    LinearLayout bottomSheet;
    Context context;
    Typeface montLight;
    Typeface montSemiBoldItalic;
    Typeface montMedium;
    Typeface montSemiBold;
    Typeface montReg;
    ImageView home;
    ImageView cart;
    ImageView refresh;
    TextView tv;
    TextView notice1;
    TextView notice2;
    Typeface montBold;
    Typeface montMedItalic;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FrameLayout bottomSheetContents;
    AlertDialog alert;
    ProgressBar progressBar;
    LayoutInflater inflater;
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
        setContentView(R.layout.activity_order_place_intermediate);
        preferences = getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        editor = preferences.edit();
        progressBar = findViewById(R.id.progressBar5);
        context=this;
        activity=this;
        setTypefaces();
        home = findViewById(R.id.home);
        cart = findViewById(R.id.cart);
        tv = findViewById(R.id.textView22);
        notice1 = findViewById(R.id.textView21);
        notice2 = findViewById(R.id.textView20);
        refresh = findViewById(R.id.refresh);
        bottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior= BottomSheetBehavior.from(bottomSheet);
        inflater = LayoutInflater.from(context);
        bottomSheetContents = findViewById(R.id.bottom_sheet_content);

        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        fetchData();
    }

    void sendCartRequest(){
        progressBar.setVisibility(View.VISIBLE);
        notice1.setVisibility(View.VISIBLE);
        notice2.setVisibility(View.VISIBLE);
        tv.setVisibility(View.INVISIBLE);
        refresh.setVisibility(View.INVISIBLE);
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
            progressBar.setVisibility(View.INVISIBLE);
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
                                    progressBar.setVisibility(View.INVISIBLE);
                                    showPaymentFailure(amount);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.INVISIBLE);
                            showPaymentFailure(amount);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        progressBar.setVisibility(View.INVISIBLE);
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
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        },1800);



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
                                    progressBar.setVisibility(View.VISIBLE);
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


    void fetchData(){

        JSONObject jsonObject = userObject();
        Log.e("Here",jsonObject.toString());

        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(jsonObject).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            Log.e("Token",token);

                            AndroidNetworking.post(URLS.get_profile).addJSONObjectBody(Utils.walletSecret())
                                    .addHeaders("Authorization","JWT " + token).build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    successFetch(response);
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("Error", anError.toString());
                                    reload();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();

                            reload();

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        reload();
                    }
                });
    }

    void successFetch(JSONObject response){
        try {
            String balance = response.getJSONObject("wallet").getString("curr_balance");
            boolean isBitsian = response.getJSONObject("wallet").getBoolean("is_bitsian");

            Log.e("TAG: ",response.toString());
            editor.putString("balance",balance);
            editor.putBoolean("is_bitsian",isBitsian);
            editor.commit();
            sendCartRequest();
        } catch (Exception e) {
            e.printStackTrace();
            reload();
        }
    }

    void reload(){
        notice2.setVisibility(View.INVISIBLE);
        notice1.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });
    }
}
