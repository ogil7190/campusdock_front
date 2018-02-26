package com.orah.meetesh.campusdock.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.orah.meetesh.campusdock.Classes.Event;
import com.orah.meetesh.campusdock.R;
import com.orah.meetesh.campusdock.Utils.LocalStore;
import com.robertlevonyan.views.chip.Chip;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class EventActivity extends AppCompatActivity {

    private ImageView cardImage;
    private TextView cardTitle, cardDescription, cardDate, cardCategory, cardOrganizer;
    private Chip createdBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.HolyBlack);
        setContentView(R.layout.activity_event);
        cardImage = findViewById(R.id.card_image);
        cardTitle = findViewById(R.id.card_title);
        cardDescription = findViewById(R.id.card_desc);
        cardDate = findViewById(R.id.card_date);
        cardCategory = findViewById(R.id.card_category);
        cardOrganizer = findViewById(R.id.card_organizer);
        createdBy = findViewById(R.id.created_by);
        final Event event = (Event) getIntent().getSerializableExtra("event");
        createdBy.setChipText(event.getCreated_by());
        cardTitle.setText(event.getEventName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cardDescription.setText(Html.fromHtml(event.getDescription(), Html.FROM_HTML_MODE_LEGACY) );
        }
        else{
            cardDescription.setText(Html.fromHtml(event.getDescription()));
        }

        cardDate.setText(event.getDate());
        cardOrganizer.setText(event.getOrganizer());
        cardCategory.setText(event.getCategory());
        if(event.getUrl()!=null) {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
            File f = new File(folder, event.getUrl());
            Picasso.with(getApplicationContext())
            .load(f)
            .noFade()
            .into(cardImage, new Callback() {
                @Override
                public void onSuccess() {
                    startPostponedEnterTransition();
                }

                @Override
                public void onError() {
                    startPostponedEnterTransition();
                }
            });
        }
        else {
            Picasso.with(getApplicationContext())
            .load("http://www.figr.in/mgiep/public_html/uploads/"+event.getUrl())
            .placeholder(event.getBanner())
            .noFade()
            .into(cardImage, new Callback() {
                @Override
                public void onSuccess() {
                    startPostponedEnterTransition();
                }

                @Override
                public void onError() {
                    startPostponedEnterTransition();
                }
            });
        }

        cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent preview = new Intent(getApplicationContext(), PreviewImage.class);
                LocalStore.putObject("previewImage", cardImage.getDrawable());
                startActivity(preview);
            }
        });
    }
}
