package com.orah.meetesh.campusdock.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.orah.meetesh.campusdock.Activities.Registration;
import com.orah.meetesh.campusdock.R;
import com.orah.meetesh.campusdock.Utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.orah.meetesh.campusdock.Utils.Config.PREF_USER_IS_LOGGED_IN;
import static com.orah.meetesh.campusdock.Utils.Config.PREF_USER_NAME;
import static com.orah.meetesh.campusdock.Utils.Config.PREF_USER_PHONE;
import static com.orah.meetesh.campusdock.Utils.Config.PREF_USER_ROLL;
import static com.orah.meetesh.campusdock.Utils.Config.PREF_USER_SUBSCRIPTIONS;

/**
 * Created by ogil on 14/01/18.
 */

public class ProfileFragment extends android.support.v4.app.Fragment {

    public static final String ID = "ProfileFragment";
    private TextView name, roll, phone;
    private Button logout;
    private SharedPreferences pref;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile,null);
        pref = getContext().getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);

        roll = v.findViewById(R.id.roll);
        roll.setText(pref.getString(PREF_USER_ROLL, ""));

        name = v.findViewById(R.id.name);
        name.setText(pref.getString(PREF_USER_NAME, ""));

        phone = v.findViewById(R.id.phone);
        phone.setText(pref.getString(PREF_USER_PHONE, ""));

        logout = v.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = new JSONObject(pref.getString(PREF_USER_SUBSCRIPTIONS, ""));
                    JSONArray a = obj.getJSONArray("implicit");
                    for (int i = 0; i < a.length(); i++) {
                        String s = a.getString(i);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(s);
                    }

                    a = obj.getJSONArray("explicit");
                    for (int i = 0; i < a.length(); i++) {
                        String s = a.getString(i);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(s);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

                pref.edit().clear().commit();
                startActivity(new Intent(getActivity(), Registration.class));
                getActivity().finish();
            }
        });
        return v;
    }

    public ProfileFragment() {}
}