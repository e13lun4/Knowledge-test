package com.example.quizsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView title;
    private Button start;
    private Button create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.main_title);
        start = findViewById(R.id.ma_startB);
        create = findViewById(R.id.ma_createB);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.days2);
        title.setTypeface(typeface);

        start.setOnClickListener(v -> {
            Intent startIntent = new Intent(MainActivity.this, CategoriesActivity.class);
            startActivity(startIntent);
        });

        create.setOnClickListener(v ->{
            Intent createIntent = new Intent(MainActivity.this, CategoriesCCDActivity.class);
            startActivity(createIntent);
        });
    }

}