package com.awesomecorp.sammy.apogeewallet.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.WalletActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setListeners();
    }

    void setChildListeners(final DatabaseReference ref, final String id){


        ref.child(id).child("stallgroup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    boolean cancelled = Boolean.valueOf(dataSnapshot.child("cancelled").getValue().toString());
                    boolean order_ready = Boolean.valueOf(dataSnapshot.child("order_ready").getValue().toString());
                    if (cancelled ){
                        createNotification("Your recent order got cancelled");
                    }else if (order_ready){
                        createNotification("Your recent order is now ready");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    void setListeners(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("wallet");
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        String walletID = preferences.getString("walletID","-");
        reference = reference.child(walletID);


//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                createNotification("You have a new transaction notification");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });

        final DatabaseReference finalReference = reference;
        reference.child("transactions").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.child("id").getValue().toString();
                setChildListeners(finalReference.child("transactions"),id);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void createNotification(String message){
        Intent intent = new Intent(this, WalletActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Log.e("Notification","Called");
        Notification n  = new Notification.Builder(this)
                .setContentTitle("APOGEE Wallet")
                .setContentText(message)
                .setSmallIcon(R.drawable.pay_icon)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.pay_icon, "View", pIntent).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(0,n);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return Service.START_STICKY;
    }
}
