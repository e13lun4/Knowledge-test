package com.example.quizsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private GridView catGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Категории");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        catGridView = findViewById(R.id.categoryGridView);

        List<String> categoryList = new ArrayList<>();
        categoryList.add("Категория 1");
        categoryList.add("Категория 2");
        categoryList.add("Категория 3");
        categoryList.add("Категория 4");
        categoryList.add("Категория 5");
        categoryList.add("Категория 6");

        CategoryGridAdapter adapter = new CategoryGridAdapter(categoryList);
        catGridView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            CategoryActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}