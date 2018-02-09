package com.awesomecorp.sammy.apogeewallet.utils;

import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sammy on 9/2/18.
 */

public class Utils {

    public static String wallet_secret = "4b6e39873f40492aadee397b03316b62";


    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public static String username = "tushar004";
    public static String password = "qwertyuiop";

    public static JSONObject userObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject walletSecret(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("WALLET_TOKEN", wallet_secret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
