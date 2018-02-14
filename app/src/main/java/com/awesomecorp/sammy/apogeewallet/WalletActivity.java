package com.awesomecorp.sammy.apogeewallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.awesomecorp.sammy.apogeewallet.adapters.BilledItemsAdapter;
import com.awesomecorp.sammy.apogeewallet.fragments.CartFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.ItemsViewFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.OrderFoodFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.WalletEmptyFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.WalletHomeFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.WalletLoadFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.WebViewFragment;
import com.awesomecorp.sammy.apogeewallet.listners.AddMoneyButtonClickListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnAddToCartButtonListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnCartViewButtonListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnDataLoadedListner;
import com.awesomecorp.sammy.apogeewallet.listners.OnGotoShopButtonListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnReceiptItemClickListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnRemoveFromCartListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnViewItemsClickedListener;
import com.awesomecorp.sammy.apogeewallet.listners.ShopLoadListener;
import com.awesomecorp.sammy.apogeewallet.listners.TransactionCompleteListener;
import com.awesomecorp.sammy.apogeewallet.models.BillItem;
import com.awesomecorp.sammy.apogeewallet.models.Item;
import com.awesomecorp.sammy.apogeewallet.models.Sales;
import com.awesomecorp.sammy.apogeewallet.models.Shop;
import com.awesomecorp.sammy.apogeewallet.models.Stall;
import com.awesomecorp.sammy.apogeewallet.models.Transaction;
import com.awesomecorp.sammy.apogeewallet.services.NotificationService;
import com.awesomecorp.sammy.apogeewallet.utils.URLS;
import com.awesomecorp.sammy.apogeewallet.utils.Utils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.awesomecorp.sammy.apogeewallet.utils.Utils.userObject;

public class WalletActivity extends FragmentActivity implements ShopLoadListener,TransactionCompleteListener,
        OnDataLoadedListner, AddMoneyButtonClickListener,OnAddToCartButtonListener,OnCartViewButtonListener,
        OnGotoShopButtonListener,OnReceiptItemClickListener,OnRemoveFromCartListener, OnViewItemsClickedListener{

    public int SHOP_SCAN_CODE=2;
    public int TRANSFER_SCAN_RESULT_CODE= 5;
    boolean clicked;
    BottomSheetBehavior sheetBehavior;
    Activity activity;
    LinearLayout bottomSheet;
    Context context;
    Typeface montBold;
    CartFragment cartFragment=null;
    Typeface montLight;
    Typeface montSemiBoldItalic;
    Typeface montMedium;
    Typeface montSemiBold;
    Typeface montReg;
    Typeface montMedItalic;
    FirebaseDatabase database;
    SharedPreferences preferences;
    SharedPreferences buttonOnClickStatuses;
    SharedPreferences.Editor statusEditor;
    SharedPreferences.Editor editor;
    TextView quantity;
    LayoutInflater inflater;
    FrameLayout bottomSheetContents;
    ImageButton qrScan;
    AlertDialog alert;
    FloatingActionButton floatingActionButton;

    void setTypefaces(){
       montBold = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Bold.ttf");
       montLight = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Light.ttf");
       montReg = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Regular.ttf");
       montSemiBoldItalic = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-SemiBoldItalic.ttf");
       montMedium = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-Medium.ttf");
       montSemiBold = Typeface.createFromAsset(getAssets(),"fonts/Montserrat-SemiBold.ttf");
       montMedItalic = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-MediumItalic.ttf");
    }

    void alertMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
//                                cartFragment.stopProgressBar();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("In onCreate","called");
        setContentView(R.layout.activity_wallet);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
        context=this;
        activity=this;
        Intent i = new Intent(context, NotificationService.class);
        context.startService(i);
        buttonOnClickStatuses = getApplicationContext().getSharedPreferences("status",MODE_PRIVATE);
        statusEditor = buttonOnClickStatuses.edit();
        qrScan = findViewById(R.id.qr_scanner);
        qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,QrScanActivity.class);
                intent.putExtra("scan","shop");
                startActivityForResult(intent, SHOP_SCAN_CODE);
            }
        });
        database = FirebaseDatabase.getInstance();
        preferences = getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        editor = preferences.edit();
        setTypefaces();
        statusEditor.putBoolean("qr_scanner",false);
        statusEditor.putBoolean("changed_to_wallet",false);
        statusEditor.putBoolean("pay",false);
        statusEditor.putBoolean("food",true);
        statusEditor.apply();
        bottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior= BottomSheetBehavior.from(bottomSheet);
        floatingActionButton = findViewById(R.id.central);
        floatingActionButton.setImageResource(R.drawable.food_icon);
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

                boolean isPayBtn= buttonOnClickStatuses.getBoolean("pay",false);
                boolean isFoodBtn = buttonOnClickStatuses.getBoolean("food",true);
                boolean isQrBtn = buttonOnClickStatuses.getBoolean("qr_scanner",false);
                if (isFoodBtn) {
                    OrderFoodFragment orderFoodFragment = new OrderFoodFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, orderFoodFragment,
                            "Order Food Fragment").commit();
                    floatingActionButton.setImageResource(R.drawable.white_qr);
                    payOrReceive.setImageResource(R.drawable.wallet_icon);
                    statusEditor.putBoolean("qr_scanner",true);
                    statusEditor.putBoolean("changed_to_wallet",true);
                    statusEditor.putBoolean("pay",false);
                    statusEditor.putBoolean("food",false);
                    statusEditor.apply();
                }else if (isQrBtn){
                    Intent intent = new Intent(activity,QrScanActivity.class);
                    intent.putExtra("scan","shop");
                    startActivityForResult(intent, SHOP_SCAN_CODE);
                }else if (isPayBtn){
                    alertMessage("Are you sure you want to place this order?");
//                    Toast.makeText(getApplicationContext(),"Pay",Toast.LENGTH_SHORT).show();
                }
            }
        });


        payOrReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean changedToWallet = buttonOnClickStatuses.getBoolean("changed_to_wallet",false);
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
                            loadTransferOptions();
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
                            ImageView qrCode = findViewById(R.id.qr_code);
                            String walletUID = preferences.getString("walletID","xx");
                            QRCodeWriter writer = new QRCodeWriter();
                            try {
                                BitMatrix bitMatrix = writer.encode(walletUID, BarcodeFormat.QR_CODE, 512, 512);
                                int width = bitMatrix.getWidth();
                                int height = bitMatrix.getHeight();
                                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                                for (int x = 0; x < width; x++) {
                                    for (int y = 0; y < height; y++) {
                                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                    }
                                }
                                qrCode.setImageBitmap(bmp);

                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                            TextView name = receiveMoney.findViewById(R.id.name);
                            TextView college = receiveMoney.findViewById(R.id.college);
                            TextView userId = receiveMoney.findViewById(R.id.id_user);
                            name.setTypeface(montBold);
                            name.setText(preferences.getString("name","N.A"));
                            college.setTypeface(montMedium);
                            college.setText(preferences.getString("college","N.A"));
                            userId.setTypeface(montMedium);
                            userId.setText("UID: " + preferences.getString("userid","N.A"));
                        }
                    });

                }
                else{
                    statusEditor.putBoolean("changed_to_wallet",false);
                    statusEditor.apply();
                    floatingActionButton.setImageResource(R.drawable.food_icon);
                    payOrReceive.setImageResource(R.drawable.pay_icon);
                    WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment,walletLoadFragment,"Replace Load").commit();
                }
            }
        });
    }

    void startTransferScan(){
        Intent intent = new Intent(activity,QrScanActivity.class);
        intent.putExtra("scan","transfer");
        startActivityForResult(intent,TRANSFER_SCAN_RESULT_CODE);
    }

    void loadUIDLayout(){
        View loadUID = inflater.inflate(R.layout.enter_uid_layout, bottomSheetContents, false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(450);
        bottomSheet.requestLayout();

        TextView moneyHeader = loadUID.findViewById(R.id.textView6);
        moneyHeader.setTypeface(montSemiBoldItalic);

        final EditText uid = loadUID.findViewById(R.id.uid_text);

        Button btn = loadUID.findViewById(R.id.enter_uid_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uidVal =uid.getText().toString();
                if (uidVal.equals("")){
                    Toast.makeText(activity,"Enter a correct UID",Toast.LENGTH_SHORT).show();
                }else {
                    transferMoney(uidVal);
                }

            }
        });

        uid.setTypeface(montBold);
        btn.setTypeface(montReg);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(loadUID);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    void loadTransferOptions(){
        View transferMoney = inflater.inflate(R.layout.transfer_options_layout, bottomSheetContents, false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(500);
        bottomSheet.requestLayout();

        TextView moneyHeader = transferMoney.findViewById(R.id.textView8);
        moneyHeader.setTypeface(montSemiBoldItalic);

        Button scanQr = transferMoney.findViewById(R.id.via_qr_scan);
        Button uidBtn = transferMoney.findViewById(R.id.enter_uid);
        scanQr.setTypeface(montReg);
        uidBtn.setTypeface(montReg);

        scanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTransferScan();
            }
        });

        uidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUIDLayout();
            }
        });
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(transferMoney);

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }


    }

    void transferMoney(final String walletUID){
        View transferMoney = inflater.inflate(R.layout.pay_money, bottomSheetContents, false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(500);
        bottomSheet.requestLayout();
        TextView moneyHeader = transferMoney.findViewById(R.id.textView6);
        final EditText amount = transferMoney.findViewById(R.id.editText);
        Button continueBtn = transferMoney.findViewById(R.id.pay_money_button);
        TextView rupeesText = transferMoney.findViewById(R.id.textView7);
        moneyHeader.setTypeface(montSemiBoldItalic);
        rupeesText.setTypeface(montSemiBoldItalic);
        amount.setTypeface(montBold);
        final ProgressBar progressBar = transferMoney.findViewById(R.id.progress);
        continueBtn.setTypeface(montMedium);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(transferMoney);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String amountText = amount.getText().toString();
                if (amountText.equals("")){
                    Toast.makeText(activity,"Enter an amount",Toast.LENGTH_SHORT).show();
                }else{
                    forwardTransferRequest(walletUID,amountText,progressBar);
                }

            }
        });

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

    }

    void forwardTransferRequest(final String id, final String amount, ProgressBar progressBar){
        int balance = Integer.valueOf(preferences.getString("balance","0"));
        final int amountTotal = Integer.parseInt(amount);

        if (balance<amountTotal){
            showAlert(amountTotal-balance);
        }else {
            JSONObject user = userObject();
            AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String token = response.getString("token");
                                Log.e("Token",token);
                                JSONObject object = new JSONObject();
                                object.put("uid",id);
                                object.put("WALLET_TOKEN",Utils.wallet_secret);
                                object.put("amount",amount);
                                AndroidNetworking.post(URLS.transfer).addHeaders("Authorization","JWT "+ token).addJSONObjectBody(object).build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String status = response.getString("status");
                                                    if (status.equals("1")){
                                                        WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
                                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletLoadFragment).addToBackStack("Payment").commit();
                                                        showPaymentSuccess(amountTotal);
                                                    }else {
                                                        showPaymentFailure(amountTotal);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    showPaymentFailure(amountTotal);
                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {
                                                Log.e("Error : ", anError.toString());
                                                showPaymentFailure(amountTotal);
                                            }
                                        });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showPaymentFailure(amountTotal);
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("Error : ", anError.toString());
                            showPaymentFailure(amountTotal);
                        }
                    });

        }

    }

    @Override
    public void onDataLoaded(List<Transaction> transactions, boolean listEmpty) {
        floatingActionButton.setImageResource(R.drawable.food_icon);
        statusEditor.putBoolean("qr_scanner",false);
        statusEditor.putBoolean("changed_to_wallet",false);
        statusEditor.putBoolean("pay",false);
        statusEditor.putBoolean("food",true);
        statusEditor.apply();
        if (listEmpty){
            WalletEmptyFragment walletEmptyFragment = new WalletEmptyFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletEmptyFragment,
                    "EmptyFragment").commit();
        }else{
            List<Transaction> transactions1 = new ArrayList<>();
            WalletHomeFragment walletHomeFragment = WalletHomeFragment.newInstance(transactions1);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletHomeFragment,
                                "Loaded Fragment").commit();
        }
    }

    void sendCartRequest(){
        cartFragment.startProgressBar();
        try {
            JSONObject cart = new JSONObject(preferences.getString("cart",""));
            JSONArray cartItems = cart.getJSONArray("current_cart");
            int cartTotal = getCartTotal(cartItems);
            if (preferences.getBoolean("is_boolean",false)){
                forwardCartRequest(cart,cartTotal);
            }else {
                int balance  = Integer.valueOf(preferences.getString("balance","0"));
                if (cartTotal<balance){
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
//                                cartFragment.stopProgressBar();
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
                            reqParams.put("WALLET_TOKEN",Utils.wallet_secret);
                            AndroidNetworking.post(URLS.checkout).addJSONObjectBody(reqParams)
                                    .addHeaders("Authorization","JWT " + token)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    showPaymentSuccess(amount);
                                    preferences.edit().remove("cart").apply();
                                    preferences.edit().putBoolean("newOrder",true).apply();
                                    WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletLoadFragment).addToBackStack("Payment").commit();
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

    void addMoney(int amountToAdd){
        View addMoney = inflater.inflate(R.layout.add_money,bottomSheetContents,false);
        bottomSheet.getLayoutParams().height =  Utils.dpToPx(500);
        bottomSheet.requestLayout();
        TextView moneyHeader = addMoney.findViewById(R.id.textView6);
        final EditText amount = addMoney.findViewById(R.id.editText);
        Button addMoneyButton = addMoney.findViewById(R.id.add_money_button);
        TextView rupeesText = addMoney.findViewById(R.id.textView7);
        final ProgressBar progressBar = addMoney.findViewById(R.id.progressBar3);
        moneyHeader.setTypeface(montSemiBoldItalic);
        rupeesText.setTypeface(montSemiBoldItalic);
        amount.setText("" + amountToAdd);
        amount.setTypeface(montBold);
        addMoneyButton.setTypeface(montMedium);
        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(addMoney);

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
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.e("Add Response", response.getString("url"));
                                        WebViewFragment webViewFragment = WebViewFragment.newInstance(response.getString("url"));
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,webViewFragment).commit();
                                        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                                    WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
//                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletLoadFragment).addToBackStack("Payment").commit();
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
    public void onAddMoneyButtonClicked() {
        addMoney(100);
    }


    @Override
    public void onViewItemClicked(Shop shop) {
        ItemsViewFragment viewFragment = ItemsViewFragment.newInstance(shop);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment,viewFragment);
        ft.addToBackStack("Shops");
        ft.commit();

    }

    @Override
    public void onRemoveFromCart() {
        CartFragment cartFragment = new CartFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,cartFragment).commit();
    }

    @Override
    public void onGotoShopButtonClick() {
        floatingActionButton.setImageResource(R.drawable.white_qr);
        statusEditor = getApplicationContext().getSharedPreferences("status",MODE_PRIVATE).edit();
        statusEditor.putBoolean("qr_scanner",true);
        statusEditor.putBoolean("changed_to_wallet",true);
        statusEditor.putBoolean("pay",false);
        statusEditor.putBoolean("food",false);
        statusEditor.apply();
        OrderFoodFragment orderFoodFragment =  new OrderFoodFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,orderFoodFragment).commit();
    }

    @Override
    public void onCartViewButtonListener() {
        floatingActionButton.setImageResource(R.drawable.pay);
        statusEditor = getApplicationContext().getSharedPreferences("status",MODE_PRIVATE).edit();
        statusEditor.putBoolean("qr_scanner",false);
        statusEditor.putBoolean("changed_to_wallet",true);
        statusEditor.putBoolean("pay",true);
        statusEditor.putBoolean("food",false);
        statusEditor.apply();
        cartFragment = new CartFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,cartFragment).addToBackStack("current").commit();
    }

    @Override
    public void onAddToCartClicked(final Item itemAdded) {
        View add_to_cart = inflater.inflate(R.layout.add_item_to_cart_layout,
                bottomSheetContents, false);
        bottomSheet.getLayoutParams().height = Utils.dpToPx(500);
        bottomSheet.requestLayout();

        TextView item = add_to_cart.findViewById(R.id.itemName);
        TextView desc = add_to_cart.findViewById(R.id.description);
        TextView quantityText = add_to_cart.findViewById(R.id.textView15);
        item.setText(itemAdded.getItemName());
        desc.setText(itemAdded.getSize());
        quantity = add_to_cart.findViewById(R.id.quantity);
        TextView cost = add_to_cart.findViewById(R.id.cost_per_item);
        Button addToCart = add_to_cart.findViewById(R.id.add_to_cart);
        cost.setText("INR " + itemAdded.getCost());
        quantity.setTypeface(montBold);
        addToCart.setTypeface(montMedium);
        cost.setTypeface(montSemiBoldItalic);
        quantityText.setTypeface(montSemiBoldItalic);
        desc.setTypeface(montSemiBoldItalic);
        item.setTypeface(montSemiBoldItalic);
        bottomSheetContents.removeAllViews();
        ImageView plus = add_to_cart.findViewById(R.id.plus);
        ImageView minus = add_to_cart.findViewById(R.id.minus);


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int no_of_items = Integer.valueOf(quantity.getText().toString()) ;
                quantity.setText(""+(no_of_items+1));
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int no_of_items = Integer.valueOf(quantity.getText().toString()) ;
                if (no_of_items>1) {
                    quantity.setText("" + (no_of_items - 1));
                }

            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DatabaseReference reference = database.getReference().child("carts").child();
                int qnt = Integer.valueOf(quantity.getText().toString());
                String itemID = itemAdded.getId();
                int cost = Integer.valueOf(itemAdded.getCost());
                boolean is_new = preferences.getBoolean("newOrder",true);
                JSONObject newItem = new JSONObject();
                JSONArray array = new JSONArray();
                try {
                    newItem.put("name",itemAdded.getItemName());
                    newItem.put("id",itemID);
                    newItem.put("quantity",qnt);
                    newItem.put("price",cost*qnt);

                    if (is_new){
                        array.put(0,newItem);
                        editor.putBoolean("newOrder",false).commit();
                    }else{
                        String jsonString = preferences.getString("cart","");
                        JSONObject object = new JSONObject(jsonString);
                        array = object.getJSONArray("current_cart");
                        array.put(array.length(),newItem);
                    }

                    JSONObject obj = new JSONObject();
                    obj.put("current_cart",array);
                    Log.e("Current Cart",obj.toString());
                    editor.putString("cart",obj.toString()).commit();

                    if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_EXPANDED){
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                    Toast.makeText(getApplicationContext(),"Added Item",Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        bottomSheetContents.removeAllViews();
        bottomSheetContents.addView(add_to_cart);

        if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

    }

    @Override
    public void onReceiptItemClicked(final Transaction transaction) {
        final LinearLayout layout = findViewById(R.id.overlayCard);
        final Button cancelOrder = findViewById(R.id.cancel_order);
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        final Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        TextView heading = findViewById(R.id.heading);
        final TextView name = findViewById(R.id.name);
        TextView date = findViewById(R.id.date);
        TextView total = findViewById(R.id.grand_total);

        heading.setTypeface(montMedium);
        name.setTypeface(montMedItalic);
        date.setTypeface(montMedItalic);
        total.setTypeface(montMedItalic);

        date.setText(transaction.getCreated_at());
        heading.setText("Transaction ID : " + transaction.getId());
        total.setText("INR " + transaction.getValue());



        final String transactionType = transaction.getT_type();
        String nameText;
        if (transactionType.equals("buy")){
            heading.setText("Order ID : " + transaction.getStallgroup().getOrder_id());
            nameText = transaction.getStallgroup().getStallname();
            cancelOrder.setVisibility(View.VISIBLE);
            Stall stall=transaction.getStallgroup();
            if (stall.isOrder_complete() || stall.isCancelled()){
                cancelOrder.setEnabled(false);
                cancelOrder.setVisibility(View.INVISIBLE);
            }else if (stall.isOrder_ready()){
                cancelOrder.setVisibility(View.VISIBLE);
                cancelOrder.setEnabled(true);
            }else {
                cancelOrder.setVisibility(View.INVISIBLE);
                cancelOrder.setEnabled(false);
            }
        }
        else if (transactionType.equals("add")){
            nameText = "ADDED AMOUNT";
            cancelOrder.setVisibility(View.GONE);
        }else if (transactionType.equals("transfer")){
            nameText = "TRANSFERRED";
            cancelOrder.setVisibility(View.GONE);
        }else {
            nameText = "RECEIVED";
            cancelOrder.setVisibility(View.GONE);
        }

        name.setText(nameText);

        ImageView closeOverlay = findViewById(R.id.close_btn);
        cancelOrder.setTypeface(montReg);

        RecyclerView billList = findViewById(R.id.billed_item_list);
        List<BillItem> bill;
        bill = getBillItems(transaction);

        BilledItemsAdapter billedItemsAdapter = new BilledItemsAdapter(bill,activity);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        billList.setLayoutManager(layoutManager);
        billList.setAdapter(billedItemsAdapter);

        if(layout.getVisibility()==View.INVISIBLE){
            layout.startAnimation(slideUp);
            layout.setVisibility(View.VISIBLE);
        }
        cancelOrder.setText("GENERATE OTP");
        cancelOrder.setBackgroundResource(R.drawable.active_btn);

        clicked = false;
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                        findViewById(R.id.progress_uid).setVisibility(View.VISIBLE);
//                    transaction.getStallgroup().setOrder_complete(true);
//                    database.getReference().child("wallet").child(preferences.getString("walletID","-"))
//                            .child("transactions").child("stallgroup").child("order_complete").setValue(true);
////                  cancelOrder.setTextColor(Color.parseColor("#ee517b"));

                    otpRequest(transaction.getStallgroup().getSgid(),cancelOrder,transaction);

                    clicked = true;
                }catch (Exception e){
                   e.printStackTrace();
                }

            }
        });

        closeOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked){
                    WalletLoadFragment loadFragment = new WalletLoadFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment,loadFragment).commit();
                }
                if(layout.getVisibility()==View.VISIBLE){
                    layout.startAnimation(fadeOut);
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            layout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        });
    }

    void otpRequest(final String sgid, final Button cancelOrder, final Transaction transaction){
        JSONObject user = userObject();
        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(user).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String token = response.getString("token");
                            Log.e("Token" , token);
                            final JSONObject reqParams = new JSONObject();
                            reqParams.put("sg_id",sgid);
                            reqParams.put("WALLET_TOKEN",Utils.wallet_secret);
                            AndroidNetworking.post(URLS.generate).addJSONObjectBody(reqParams)
                                    .addHeaders("Authorization","JWT " + token)
                                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    findViewById(R.id.progress_uid).setVisibility(View.INVISIBLE);
                                    cancelOrder.setText("OTP : " + transaction.getStallgroup().getUID());
                                    cancelOrder.setBackgroundResource(R.drawable.inactive_button);
                                    cancelOrder.setEnabled(false);
                                }

                                @Override
                                public void onError(ANError anError) {
                                    findViewById(R.id.progress_uid).setVisibility(View.INVISIBLE);
                                    Log.e("Error", anError.toString());
                                    Toast.makeText(activity,"Some error occured",Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        findViewById(R.id.progress_uid).setVisibility(View.INVISIBLE);
                        Toast.makeText(activity,"Some error occured",Toast.LENGTH_SHORT).show();
                    }
                });
    }


    List<BillItem> getBillItems(Transaction transaction){
        List<BillItem> list = new ArrayList<>();
        if (transaction.getT_type().equals("buy")){
            for (Sales sale: transaction.getStallgroup().getSales()){
                BillItem item= new BillItem();
                item.setItemName(sale.getProduct().getName() +" - " + sale.getProduct().getSize()
                        + " X " + sale.getQuantity());
                item.setCost("" + sale.getProduct().getPrice());
                list.add(item);
            }
        }else if (transaction.getT_type().equals("add")){
            BillItem item = new BillItem();
            item.setItemName("Ref - " + transaction.getId());
            item.setCost("Added INR " + transaction.getValue());
            list.add(item);
        }else {
            BillItem item = new BillItem();
            String from;
            if (transaction.getTransfer().isIs_bitsian()){
                from = transaction.getTransfer().getBitsian().getName();
            }else {
                from = transaction.getTransfer().getParticipant().getName();
            }
            item.setItemName(from);
            item.setCost(String.valueOf(transaction.getValue()));
            list.add(item);
        }
        return list;
    }

    void shopLoad(String id){
        Shop shop = new Shop();
        shop.setId(id);
        ItemsViewFragment fragment = ItemsViewFragment.newInstance(shop);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,fragment).commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SHOP_SCAN_CODE) {
            String id=data.getStringExtra("MESSAGE");
            Log.e("QR Code",id);
            shopLoad(id);
        }else if(requestCode== TRANSFER_SCAN_RESULT_CODE ){
            String id=data.getStringExtra("MESSAGE");
            transferMoney(id);
            Log.e("QR Code",id);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onShopLoad() {
        statusEditor = getApplicationContext().getSharedPreferences("status",MODE_PRIVATE).edit();
        statusEditor.putBoolean("qr_scanner",true);
        statusEditor.putBoolean("changed_to_wallet",true);
        statusEditor.putBoolean("pay",false);
        statusEditor.putBoolean("food",false);
        statusEditor.apply();
        floatingActionButton = findViewById(R.id.central);
        floatingActionButton.setImageResource(R.drawable.white_qr);
    }

    @Override
    public void transactionComplete() {
        WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletLoadFragment).commit();
    }

    public void transactionDone(){
        WalletLoadFragment walletLoadFragment = new WalletLoadFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,walletLoadFragment).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("In onPause","called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("In onStop","called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("In onStart","called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("In onResume","called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("In onDestroy","called");
    }
}
