package com.orah.meetesh.campusdock.Activities;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.orah.meetesh.campusdock.R;
import com.orah.meetesh.campusdock.Utils.LocalStore;

public class PreviewImage extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.HolyBlack);
        setContentView(R.layout.activity_preview_image);
        imageView = findViewById(R.id.photo_view);
        imageView.setImageDrawable((Drawable)LocalStore.getObject("previewImage"));
    }
}
