package com.awesomecorp.sammy.apogeewallet.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.awesomecorp.sammy.apogeewallet.QrScanActivity;
import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.WalletActivity;
import com.awesomecorp.sammy.apogeewallet.fragments.CartFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.ItemsViewFragment;
import com.awesomecorp.sammy.apogeewallet.fragments.OrderFoodFragment;
import com.awesomecorp.sammy.apogeewallet.listners.BackPressedListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnAddToCartButtonListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnCartViewButtonListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnViewItemsClickedListener;
import com.awesomecorp.sammy.apogeewallet.listners.ShopLoadListener;
import com.awesomecorp.sammy.apogeewallet.models.Item;
import com.awesomecorp.sammy.apogeewallet.models.Shop;
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

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ShopActivity extends AppCompatActivity implements BackPressedListener, OnAddToCartButtonListener,
        OnCartViewButtonListener, OnViewItemsClickedListener {

    Activity activity;
    LinearLayout bottomSheet;
    Context context;
    Typeface montLight;
    Typeface montSemiBoldItalic;
    Typeface montMedium;
    Typeface montSemiBold;
    Typeface montReg;
    Typeface montBold;
    Typeface montMedItalic;
    FirebaseDatabase database;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FrameLayout bottomSheetContents;
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
        setContentView(R.layout.activity_shop);
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
                Intent intent = new Intent(activity,CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        ImageButton profile = findViewById(R.id.profile);
        ImageButton wallet = findViewById(R.id.wallet);

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,WalletActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        boolean shop_qr_scanned = preferences.getBoolean("shop_qr_scanned",false);
        Log.e("Shop QR", "" + shop_qr_scanned);

        if (shop_qr_scanned){
            String qrcode = preferences.getString("qr_code","");
            editor.putBoolean("shop_qr_scanned", false);
            editor.putBoolean("transfer_scanned", false);
            editor.apply();
            Shop shop = new Shop();
            shop.setId(qrcode);
            ItemsViewFragment itemsViewFragment = ItemsViewFragment.newInstance(shop);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, itemsViewFragment,
                    "XXX").commitAllowingStateLoss();
        }else {
            OrderFoodFragment orderFoodFragment = new OrderFoodFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, orderFoodFragment,
                    "Order Food Fragment").commitAllowingStateLoss();
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileOpen();
            }
        });

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
    public void onCartViewButtonListener() {
        Intent intent = new Intent(activity,CartActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }



    @Override
    public void onAddToCartClicked(final Item itemAdded) {
        View add_to_cart = inflater.inflate(R.layout.add_item_to_cart_layout,
                bottomSheetContents, false);
        bottomSheet.getLayoutParams().height = Utils.dpToPx(500);
        bottomSheet.requestLayout();
        final TextView quantity;
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
                        JSONObject x = checkIfItemExists(array,newItem);
                        if (x !=null){
                            newItem = x.getJSONObject("jsonObj");
                            int quantity = newItem.getInt("quantity");
                            newItem.put("price", quantity*cost);
                            int position = x.getInt("position");
                            array.put(position,newItem);
                        }else {
                            array.put(array.length(),newItem);
                        }
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

    JSONObject checkIfItemExists(JSONArray array, JSONObject item) throws JSONException {
        for (int i =0; i< array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            if (obj.getString("id").equals(item.getString("id"))){
                int qty = obj.getInt("quantity") + item.getInt("quantity");
                JSONObject x = new JSONObject();
                obj.put("quantity", qty);
                x.put("jsonObj",obj);
                x.put("position",i);
                return x;
            }
        }

        return null;
    }

    void profileOpen(){
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

        if (sheetBehavior.getState() ==BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onBackButtonFragment() {
        super.onBackPressed();
    }
}
