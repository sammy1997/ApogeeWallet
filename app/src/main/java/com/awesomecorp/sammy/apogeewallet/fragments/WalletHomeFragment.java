package com.awesomecorp.sammy.apogeewallet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.adapters.RecieptItemAdapter;
import com.awesomecorp.sammy.apogeewallet.listners.AddMoneyButtonClickListener;
import com.awesomecorp.sammy.apogeewallet.listners.BackPressedListener;
import com.awesomecorp.sammy.apogeewallet.listners.OnReceiptItemClickListener;
import com.awesomecorp.sammy.apogeewallet.models.Bitsian;
import com.awesomecorp.sammy.apogeewallet.models.Participant;
import com.awesomecorp.sammy.apogeewallet.models.Sales;
import com.awesomecorp.sammy.apogeewallet.models.Shop;
import com.awesomecorp.sammy.apogeewallet.models.Stall;
import com.awesomecorp.sammy.apogeewallet.models.Transaction;
import com.awesomecorp.sammy.apogeewallet.models.Transfer;
import com.awesomecorp.sammy.apogeewallet.utils.TransactionComparator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class WalletHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    List<String> data;
    List<Transaction> transactions;
    DatabaseReference reference;
    SimpleDateFormat format;
    private RecyclerView.LayoutManager layoutManager;
    SharedPreferences preferences;

    BackPressedListener backPressedListener;
    AddMoneyButtonClickListener moneyButtonListener;
    OnReceiptItemClickListener onReceiptItemClickListener;

    ImageView addMoney;
    FirebaseDatabase database;

    public WalletHomeFragment() {
    }

    public static WalletHomeFragment newInstance(List<Transaction> transactions) {
        WalletHomeFragment fragment = new WalletHomeFragment();
        fragment.transactions = transactions;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        preferences = getActivity().getApplicationContext().getSharedPreferences("details",MODE_PRIVATE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        format = new SimpleDateFormat("dd MMM hh:mm a");
        View view= inflater.inflate(R.layout.fragment_wallet_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        addMoney = view.findViewById(R.id.add_money);
        database = FirebaseDatabase.getInstance();
        data = new ArrayList<>();
        transactions = new ArrayList<>();
        getData();
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecieptItemAdapter(transactions,this.getActivity(),onReceiptItemClickListener);
        recyclerView.setAdapter(adapter);
        Typeface montBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat-Bold.ttf");
        Typeface montSemiBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat-SemiBold.ttf");
        TextView header = view.findViewById(R.id.textView);
        TextView balance = view.findViewById(R.id.textView2);
        balance.setText(preferences.getString("balance","--"));

        TextView spentText = view.findViewById(R.id.textView3);

        header.setTypeface(montSemiBold);
        balance.setTypeface(montBold);
        spentText.setTypeface(montBold);


        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyButtonListener.onAddMoneyButtonClicked();
            }
        });

        ImageView back = view.findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressedListener.onBackButtonFragment();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            backPressedListener = (BackPressedListener)context;
            moneyButtonListener = (AddMoneyButtonClickListener) context;
            onReceiptItemClickListener= (OnReceiptItemClickListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        backPressedListener = null;
        moneyButtonListener = null;
        onReceiptItemClickListener = null;
        super.onDetach();
    }

    public void getData(){

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
             reference =database.getReference().child("wallet")
                    .child(preferences.getString("walletID","-")).child("transactions");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        transactions.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Log.e("Key", data.getKey());
                            Transaction transaction = new Transaction();
                            String transactionType = data.child("t_type").getValue().toString();
                            if (transactionType.equals("buy")){
                                buy(transaction,data);
                            }else if (transactionType.equals("add")){
                                add(transaction,data);
                            }else if (transactionType.equals("transfer")){
                                transfer(transaction,data);
                            }else if (transactionType.equals("recieve")){
                                receive(transaction,data);
                            }
                            transactions.add(transaction);
                        }
                        Collections.sort(transactions, new TransactionComparator());

                        adapter = new RecieptItemAdapter(transactions, getActivity(), onReceiptItemClickListener);
                        recyclerView.setAdapter(adapter);
                    }catch (Exception e){
                        Log.e("Exception","here");
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void buy(Transaction transaction,DataSnapshot dataSnapshot){
        try {
            String dateOfTransaction = dataSnapshot.child("created_at").getValue().toString();
            transaction.setCreated_at(dateOfTransaction);
            transaction.setT_type("buy");
            transaction.setId(Long.valueOf(dataSnapshot.child("id").getValue().toString()));
            transaction.setValue(Long.valueOf(dataSnapshot.child("value").getValue().toString()));
            transaction.setTransfer(null);

            Stall stallGrp = new Stall();
            stallGrp.setCancelled(Boolean.valueOf(dataSnapshot.child("stallgroup").child("cancelled")
                    .getValue().toString()));
            stallGrp.setUID(dataSnapshot.child("stallgroup").child("unique_code").getValue().toString());
            stallGrp.setOrder_ready(Boolean.valueOf(dataSnapshot.child("stallgroup").child("order_ready")
                    .getValue().toString()));
            stallGrp.setStallname(dataSnapshot.child("stallgroup").child("stallname").getValue().toString());
            stallGrp.setOrder_id(dataSnapshot.child("stallgroup").child("orderid").getValue().toString());
            stallGrp.setOrder_complete(Boolean.valueOf(dataSnapshot.child("stallgroup").child("order_complete")
                    .getValue().toString()));

            stallGrp.setSgid(dataSnapshot.child("stallgroup").child("id").getValue().toString());

            stallGrp.setSales(getSales(dataSnapshot));

            transaction.setStallgroup(stallGrp);
            try {
                Log.e("Name - ",dateOfTransaction);
                Date date = format.parse(dateOfTransaction);
                System.out.println(date);
                transaction.setDate(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<Sales> getSales(DataSnapshot dataSnapshot){
        List<Sales> sales = new ArrayList<>();
        try {
            for (DataSnapshot ds: dataSnapshot.child("stallgroup").child("sales").getChildren()){
                Sales sale = ds.getValue(Sales.class);
                sales.add(sale);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sales;
    }

    void add(Transaction transaction, DataSnapshot dataSnapshot){
        try {
            String dateOfTransaction = dataSnapshot.child("created_at").getValue().toString();
            transaction.setCreated_at(dateOfTransaction);
            transaction.setId(Long.valueOf(dataSnapshot.child("id").getValue().toString()));
            transaction.setT_type("add");
            transaction.setValue(Long.valueOf(dataSnapshot.child("value").getValue().toString()));
            transaction.setStallgroup(null);
            transaction.setTransfer(null);

            try {
                Date date = format.parse(dateOfTransaction);
                System.out.println(date);
                transaction.setDate(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    void transfer(Transaction transaction, DataSnapshot dataSnapshot){
        try {
            String dateOfTransaction = dataSnapshot.child("created_at").getValue().toString();
            transaction.setCreated_at(dateOfTransaction);
            transaction.setId(Long.valueOf(dataSnapshot.child("id").getValue().toString()));
            transaction.setT_type("transfer");
            transaction.setValue(-1 * Long.valueOf(dataSnapshot.child("value").getValue().toString()));
            transaction.setStallgroup(null);

            Transfer transfer =new Transfer();
            transfer.setIs_bitsian(Boolean.valueOf(dataSnapshot.child("transfer_to_from").child("is_bitsian").getValue().toString()));
            Bitsian bitsian = null;
            Participant participant = null;
            if (transfer.isIs_bitsian()){
                bitsian = dataSnapshot.child("transfer_to_from").child("bitsian").getValue(Bitsian.class);
            }
            else {
                participant = dataSnapshot.child("transfer_to_from").child("participant").getValue(Participant.class);
            }

            transfer.setBitsian(bitsian);
            transfer.setParticipant(participant);
            transaction.setTransfer(transfer);
            try {
                Date date = format.parse(dateOfTransaction);
                System.out.println(date);
                transaction.setDate(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    void receive(Transaction transaction, DataSnapshot dataSnapshot){
        try {
            String dateOfTransaction = dataSnapshot.child("created_at").getValue().toString();
            transaction.setCreated_at(dateOfTransaction);
            transaction.setId(Long.valueOf(dataSnapshot.child("id").getValue().toString()));
            transaction.setT_type("recieve");
            transaction.setValue( Long.valueOf(dataSnapshot.child("value").getValue().toString()));
            transaction.setStallgroup(null);

            Transfer transfer =new Transfer();
            transfer.setIs_bitsian(Boolean.valueOf(dataSnapshot.child("transfer_to_from").child("is_bitsian").getValue().toString()));
            Bitsian bitsian = null;
            Participant participant = null;
            if (transfer.isIs_bitsian()){
                bitsian = dataSnapshot.child("transfer_to_from").child("bitsian").getValue(Bitsian.class);
            }
            else {
                participant = dataSnapshot.child("transfer_to_from").child("participant").getValue(Participant.class);
            }

            transfer.setBitsian(bitsian);
            transfer.setParticipant(participant);
            transaction.setTransfer(transfer);
            try {
                Date date = format.parse(dateOfTransaction);
                System.out.println(date);
                transaction.setDate(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
