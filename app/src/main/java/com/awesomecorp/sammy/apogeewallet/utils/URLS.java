package com.awesomecorp.sammy.apogeewallet.utils;

/**
 * Created by sammy on 9/2/18.
 */

public class URLS {
    public static String baseIP = "http://192.168.43.14:8000/shop/";
//    public static String baseIP = "http://172.17.38.252:8000/shop/";
    public static String get_profile = baseIP + "get_profile/";
    public static String api_token = baseIP + "api_token/";
    public static String api_token_refresh = baseIP + "api_token_refresh/";
    public static String get_shops = baseIP + "get_stalls/";
    public static String get_products = baseIP + "get_products/";
    public static String checkout = baseIP + "checkout_payment/";
    public static String add_amount = baseIP + "add_money_request/";
    public static String transfer = baseIP + "transfer/";
    public static String generate = baseIP + "generate_code/";
}
