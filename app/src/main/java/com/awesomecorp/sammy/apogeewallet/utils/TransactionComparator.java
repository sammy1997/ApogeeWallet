package com.awesomecorp.sammy.apogeewallet.utils;


import android.util.Log;

import com.awesomecorp.sammy.apogeewallet.models.Transaction;

import java.util.Comparator;
import java.util.Date;

public class TransactionComparator implements Comparator<Transaction> {
    @Override
    public int compare(Transaction o1, Transaction o2) {
        Log.e("Date : ",o1.getDate().toString());
        return o2.getDate().compareTo(o1.getDate());
    }

}
