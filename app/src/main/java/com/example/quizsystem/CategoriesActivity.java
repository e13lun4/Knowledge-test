package com.example.quizsystem;

import static com.example.quizsystem.SplashActivity.categoryList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.Objects;

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

//        List<String> categoryList = new ArrayList<>();
//        categoryList.add("Категория 1");
//        categoryList.add("Категория 2");
//        categoryList.add("Категория 3");
//        categoryList.add("Категория 4");
//        categoryList.add("Категория 5");
//        categoryList.add("Категория 6");

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