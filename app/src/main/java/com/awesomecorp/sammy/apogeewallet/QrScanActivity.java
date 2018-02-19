package com.awesomecorp.sammy.apogeewallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.awesomecorp.sammy.apogeewallet.activities.ShopActivity;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class QrScanActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    boolean flashOn = false;
    public int CAMERA_REQUEST_CODE =200;
    ImageView flash;
    String scanType;
    Activity activity;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity =this;
        setContentView(R.layout.activity_qr_scan);
        scanType = getIntent().getExtras().getString("scan");
        flash =findViewById(R.id.flash);
        qrCodeReaderView = findViewById(R.id.qr_reader);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        preferences = getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        editor = preferences.edit();
        getPerms(this);
    }

    void setUp(){
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flashOn){
                    qrCodeReaderView.setTorchEnabled(false);
                    flashOn = !flashOn;
                    flash.setImageResource(R.drawable.flash_off);
                }else {
                    qrCodeReaderView.setTorchEnabled(true);
                    flashOn = !flashOn;
                    flash.setImageResource(R.drawable.flash_on);
                }
            }
        });
        qrCodeReaderView.startCamera();
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setTorchEnabled(false);
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Intent intent ;
        Log.e("Here : ", scanType);
        qrCodeReaderView.setQRDecodingEnabled(false);
        if (text!=null){
            editor.putString("qr_code",text);
            if (scanType.equals("shop")){
                intent = new Intent(activity, ShopActivity.class);
                editor.putBoolean("shop_qr_scanned", true);
                editor.putBoolean("transfer_scanned", false);
            } else {
                intent = new Intent(activity, WalletActivity.class);
                editor.putBoolean("shop_qr_scanned", false);
                editor.putBoolean("transfer_scanned", true);
            }
        }else {
            intent = new Intent(activity, WalletActivity.class);
            editor.putBoolean("shop_qr_scanned", false);
            editor.putBoolean("transfer_scanned", false);
        }
        editor.apply();
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    void getPerms(Activity activity){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
            else {
                setUp();
            }
        }
        else {
            setUp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==CAMERA_REQUEST_CODE){
            if (grantResults.length>0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permissions granted", Toast.LENGTH_SHORT).show();
                    setUp();
                } else {
                    Toast.makeText(getApplicationContext(), "Permissions not granted", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}
