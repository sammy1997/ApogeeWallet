package com.awesomecorp.sammy.apogeewallet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class QrScanActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    boolean flashOn = false;
    public int CAMERA_REQUEST_CODE =200;
    ImageView flash;
    String scanType;
    public int SHOP_SCAN_RESULT_CODE=2;
    public int TRANSFER_SCAN_RESULT_CODE= 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        scanType = getIntent().getExtras().getString("scan");
        flash =findViewById(R.id.flash);
        qrCodeReaderView = findViewById(R.id.qr_reader);
        qrCodeReaderView.setOnQRCodeReadListener(this);

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
        Intent intent=new Intent();
        intent.putExtra("MESSAGE",text);
        if (scanType.equals("shop")){
            setResult(SHOP_SCAN_RESULT_CODE,intent);
        }else {
            setResult(TRANSFER_SCAN_RESULT_CODE,intent);
        }

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
