package com.example.quizsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VictorinsCCDActivity extends AppCompatActivity {

    private RecyclerView victorinsView;
    private Button addVictorinButton;
    private VictorinCCDAdapter adapter;

    public static List<String> victorinIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victorins_ccdactivity);

        Toolbar toolbar = findViewById(R.id.victorinsToolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CATEGORY");
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        victorinsView = findViewById(R.id.victorinsCCDRecyclerView);
        addVictorinButton = findViewById(R.id.addVictorinButton);

        addVictorinButton.setOnClickListener(view -> {

        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        victorinsView.setLayoutManager(layoutManager);

        loadVictorins();

    }

    private void loadVictorins(){

        victorinIDs.clear();

        victorinIDs.add("A");
        victorinIDs.add("B");
        victorinIDs.add("C");

        adapter = new VictorinCCDAdapter(victorinIDs);
        victorinsView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            VictorinsCCDActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}