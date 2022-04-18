package com.example.quizsystem;

import static com.example.quizsystem.SplashActivity.categoryList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.Objects;

@SuppressWarnings("ALL")
public class CategoriesActivity extends AppCompatActivity {
    private GridView catGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Категории");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        catGridView = findViewById(R.id.categoryGridView);

        CategoryAdapter adapter = new CategoryAdapter(categoryList);
        catGridView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            CategoriesActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}