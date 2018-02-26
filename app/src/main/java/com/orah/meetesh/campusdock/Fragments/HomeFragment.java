package com.orah.meetesh.campusdock.Fragments;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orah.meetesh.campusdock.Activities.EventActivity;
import com.orah.meetesh.campusdock.Activities.HomeActivity;
import com.orah.meetesh.campusdock.Adapters.EventAdapter;
import com.orah.meetesh.campusdock.Classes.Event;
import com.orah.meetesh.campusdock.Classes.RecyclerItemClickListener;
import com.orah.meetesh.campusdock.R;
import com.orah.meetesh.campusdock.Utils.Config;
import com.orah.meetesh.campusdock.Utils.EventDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.orah.meetesh.campusdock.Services.CustomFirebaseMessagingService.PREF_EVENT_BASE;
import static com.orah.meetesh.campusdock.Services.CustomFirebaseMessagingService.PREF_EVENT_COUNT;

/**
 * Created by ogil on 14/01/18.
 */

public class HomeFragment extends android.support.v4.app.Fragment {
    public static final String ID = "HomeFragment";
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList;
    private SharedPreferences pref;
    private HomeActivity activity;

    public static HomeFragment newInstance(HomeActivity context) {
        HomeFragment fragment = new HomeFragment();
        fragment.activity = context;
        return fragment;
    }

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,null);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(Config.PUSH_NOTI));

        recyclerView = v.findViewById(R.id.recycler_view);
        pref = getContext().getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);
        eventList = new ArrayList<>();

        eventList = EventDB.getIntsance(getContext()).getEventDao().getAllEvents();

        Collections.reverse(eventList);

        eventList.add(new Event("CS: GO LAN Gaming","Show your <b>Gaming skills</b> in popular CS Go game!<br> Compete with other players <br><br><u>Follow us on Instagram</u> <br> <b>#OGIL7190</b> ","18 Jan - 19 Jan", "DOCK", "Game", null, "@Dock"));
        adapter = new EventAdapter(getContext(), eventList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                       gotoEvent(eventList.get(position), view);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // NEW CODE
                        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationZ", 8f);
                        animation.setDuration(300);
                        animation.start();

                    }
                })
        );
        if(startingEvent!=null){
            gotoEvent(startingEvent, recyclerView);
        }
        return v;
    }

    private Event startingEvent;
    public void setStartingEvent(Event event){
        startingEvent = event;
    }

    private void gotoEvent(Event event, View view){
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("event", event);
        if(startingEvent == null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), view, ViewCompat.getTransitionName(view));
            startActivity(intent, options.toBundle());
        } else {
            startingEvent = null;
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Config.PUSH_NOTI)){
                Bundle extras = intent.getExtras();
                try {
                    activity.setBadge();
                    JSONObject obj = new JSONObject(extras.getString("event"));
                    Event newEvent = new Event(obj.getString("name"), obj.getString("description"), obj.getString("date"), obj.getString("organizer"), obj.getString("category"), obj.getString("url"), obj.getString("created_by"));

                    //adding new event to the top!
                    Collections.reverse(eventList);
                    eventList.add(newEvent);
                    Collections.reverse(eventList);

                    adapter.notifyDataSetChanged();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getContext(),"New Event Added", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}