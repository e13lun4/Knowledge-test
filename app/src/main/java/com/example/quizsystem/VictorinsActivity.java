package com.example.quizsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VictorinsActivity extends AppCompatActivity {

    private GridView victorins_grid;
    private FirebaseFirestore firestore;
    public static int category_id;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victorins);

        Toolbar toolbar = findViewById(R.id.victorin_toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CATEGORY");
        category_id = getIntent().getIntExtra("CATEGORY_ID", 1);
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        GridView victorins_grid = findViewById(R.id.victorin_gridview);
        victorins_grid = findViewById(R.id.victorin_gridview);
        loadingDialog = new Dialog(VictorinsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
//        List<String> victorinList = new ArrayList<>();
//        victorinList.add("Математика");
//        victorinList.add("Геометрия");
//        victorinList.add("Физика");

//        VictorinAdapter adapter = new VictorinAdapter(victorinList);
        firestore = FirebaseFirestore.getInstance();
        loadVictorins();
//        VictorinAdapter adapter = new VictorinAdapter(6);
//        victorins_grid.setAdapter(adapter);
    }

    public void loadVictorins(){
        firestore.collection("QUIZ").document("CAT" + String.valueOf(category_id))
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    long victorins = (long) doc.get("VICTORINS");
                    VictorinAdapter adapter = new VictorinAdapter((int) victorins);
                    victorins_grid.setAdapter(adapter);
                }else{
                    Toast.makeText(VictorinsActivity.this, "Отсутствуют викторины", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }else{
                Toast.makeText(VictorinsActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
            loadingDialog.cancel();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            VictorinsActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}