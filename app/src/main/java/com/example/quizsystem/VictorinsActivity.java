package com.example.quizsystem;

import static com.example.quizsystem.SplashActivity.categoryList;
import static com.example.quizsystem.SplashActivity.selectedCatIndex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
public class VictorinsActivity extends AppCompatActivity {

    private GridView victorins_grid;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;
    public static List<String> victorinsIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victorins);

        Toolbar toolbar = findViewById(R.id.victorin_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle(categoryList.get(selectedCatIndex).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        victorins_grid = findViewById(R.id.victorin_gridview);
        loadingDialog = new Dialog(VictorinsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        firestore = FirebaseFirestore.getInstance();
        loadVictorins();

    }

    public void loadVictorins(){

        victorinsIDs.clear();

        firestore.collection("QUIZ").document(categoryList.get(selectedCatIndex).getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    long numberOfVictorins = (long) documentSnapshot.get("VICTORINS");

                    for (int i = 1; i <= numberOfVictorins; i ++){
                        victorinsIDs.add(documentSnapshot.getString("VICTORIN" + String.valueOf(i) + "_ID"));
                    }

                    VictorinAdapter adapter = new VictorinAdapter(victorinsIDs.size());
                    victorins_grid.setAdapter(adapter);

                    loadingDialog.dismiss();

                }).addOnFailureListener(e -> {
                    Toast.makeText(VictorinsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
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