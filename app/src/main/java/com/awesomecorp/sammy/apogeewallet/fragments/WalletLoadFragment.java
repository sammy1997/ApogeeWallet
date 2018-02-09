package com.awesomecorp.sammy.apogeewallet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.awesomecorp.sammy.apogeewallet.R;
import com.awesomecorp.sammy.apogeewallet.models.Transaction;
import com.awesomecorp.sammy.apogeewallet.utils.URLS;
import com.awesomecorp.sammy.apogeewallet.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.awesomecorp.sammy.apogeewallet.utils.Utils.userObject;

public class WalletLoadFragment extends Fragment {

    OnDataLoadedListner loadedListner;
    List<Transaction> data;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    TextView tv;
    ImageView refresh;

    public WalletLoadFragment() {
    }

    public interface OnDataLoadedListner{
         void onDataLoaded(List<Transaction> transactions, boolean listEmpty);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getApplicationContext().getSharedPreferences("details",Context.MODE_PRIVATE);
        editor = preferences.edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet_load, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        tv = view.findViewById(R.id.textView11);
        refresh = view.findViewById(R.id.refresh);
        loadData();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
           loadedListner = (OnDataLoadedListner) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " didn't implement onDataLoaded");
        }
    }

    @Override
    public void onDetach() {
        loadedListner =null;
        super.onDetach();
    }

    public void loadData(){
        data = new ArrayList<>();
        JSONObject jsonObject = userObject();
        Log.e("Here",jsonObject.toString());

        progressBar.setVisibility(View.VISIBLE);
        tv.setVisibility(View.INVISIBLE);
        refresh.setVisibility(View.INVISIBLE);

        AndroidNetworking.post(URLS.api_token).addJSONObjectBody(jsonObject).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            Log.e("Token",token);

                            AndroidNetworking.post(URLS.get_profile).addJSONObjectBody(Utils.walletSecret())
                                    .addHeaders("Authorization","JWT " + token).build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String name = response.getJSONObject("participant").getString("name");
                                        String college = response.getJSONObject("participant").getString("college_name");
                                        String balance = response.getJSONObject("wallet").getString("curr_balance");
                                        JSONArray sales = response.getJSONObject("cart").getJSONArray("sales");
                                        Log.e("TAG: ",response.toString());
                                        editor.putString("name",name);
                                        editor.putString("college",college);
                                        editor.putString("balance",balance);
                                        editor.commit();
                                        if(sales.length()!=0) {
                                            loadedListner.onDataLoaded(data, false);
                                        }else {
                                            loadedListner.onDataLoaded(data, true);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                        progressBar.setVisibility(View.INVISIBLE);
                                        tv.setVisibility(View.VISIBLE);
                                        refresh.setVisibility(View.VISIBLE);
                                        refresh.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                loadData();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("Error", anError.toString());
                                    progressBar.setVisibility(View.INVISIBLE);
                                    tv.setVisibility(View.VISIBLE);
                                    refresh.setVisibility(View.VISIBLE);
                                    refresh.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            loadData();
                                        }
                                    });
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();

                            progressBar.setVisibility(View.INVISIBLE);
                            tv.setVisibility(View.VISIBLE);
                            refresh.setVisibility(View.VISIBLE);
                            refresh.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadData();
                                }
                            });

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        tv.setVisibility(View.VISIBLE);
                        refresh.setVisibility(View.VISIBLE);
                        refresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadData();
                            }
                        });
                    }
        });
    }

}
