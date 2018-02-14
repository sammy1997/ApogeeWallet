package com.awesomecorp.sammy.apogeewallet.listners;

import com.awesomecorp.sammy.apogeewallet.models.Transaction;

/**
 * Created by sammy on 10/2/18.
 */

public interface OnReceiptItemClickListener {
    void onReceiptItemClicked(Transaction transaction);
}
