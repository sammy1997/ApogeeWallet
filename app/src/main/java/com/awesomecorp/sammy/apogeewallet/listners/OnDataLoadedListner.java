package com.awesomecorp.sammy.apogeewallet.listners;

import com.awesomecorp.sammy.apogeewallet.models.Transaction;

import java.util.List;

/**
 * Created by sammy on 10/2/18.
 */

public interface OnDataLoadedListner{
    void onDataLoaded(List<Transaction> transactions, boolean listEmpty);
}
