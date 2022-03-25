package com.example.quizsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VictorinsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victorins);

        Toolbar toolbar = findViewById(R.id.victorin_toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CATEGORY");
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//      How to make Quiz App in Android Studio 2020 | Part 4 | Sets Activity 26:04 примерно тут если что поменять для базы данных
        GridView victorins_grid = findViewById(R.id.victorin_gridview);
        List<String> victorinList = new ArrayList<>();
        victorinList.add("Математика");
        victorinList.add("Геометрия");
        victorinList.add("Физика");

        VictorinAdapter adapter = new VictorinAdapter(victorinList);
        victorins_grid.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            VictorinsActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}