package com.example.quizsystem;

import static com.example.quizsystem.CategoriesCCDActivity.categoriesCCDList;
import static com.example.quizsystem.CategoriesCCDActivity.selectedCategoryIndex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ALL")
public class VictorinsCCDActivity extends AppCompatActivity {

    private RecyclerView victorinsView;
    private Button addVictorinButton;
    private VictorinCCDAdapter adapter;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;

    public static List<String> victorinsIDs = new ArrayList<>();
    public static int selectedVictorinIndex = 0;

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

        loadingDialog = new Dialog(VictorinsCCDActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addVictorinButton.setOnClickListener(view -> {
            addNewVictorin();
        });

        firestore = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        victorinsView.setLayoutManager(layoutManager);

        loadVictorins();

    }

    private void loadVictorins(){
        victorinsIDs.clear();

        loadingDialog.show();

        firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    long numberOfVictorins = (long) documentSnapshot.get("VICTORINS");

                    for (int i = 1; i <= numberOfVictorins; i ++){
                        victorinsIDs.add(documentSnapshot.getString("VICTORIN" + String.valueOf(i) + "_ID"));
                    }

                    categoriesCCDList.get(selectedCategoryIndex).setVictorinCounter(documentSnapshot.getString("COUNTER"));
                    categoriesCCDList.get(selectedCategoryIndex).setNumberOfVictorins(String.valueOf(numberOfVictorins));

                    adapter = new VictorinCCDAdapter(victorinsIDs);
                    victorinsView.setAdapter(adapter);

                    loadingDialog.dismiss();

                }).addOnFailureListener(e -> {
                    Toast.makeText(VictorinsCCDActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });

    }

    private void addNewVictorin(){
        loadingDialog.show();

        String currentCategoryId = categoriesCCDList.get(selectedCategoryIndex).getId();
        final String currentCounter = categoriesCCDList.get(selectedCategoryIndex).getVictorinCounter();

        Map<String, Object> questionDate = new ArrayMap<>();
        questionDate.put("COUNT", "0");

        firestore.collection("QUIZ").document(currentCategoryId)
                .collection(currentCounter).document("QUESTIONS_LIST")
                .set(questionDate)
                .addOnSuccessListener(unused -> {
                    Map<String, Object> categoryDocument = new ArrayMap<>();
                    categoryDocument.put("COUNTER", String.valueOf(Integer.parseInt(currentCounter) + 1));
                    categoryDocument.put("VICTORIN" + String.valueOf(victorinsIDs.size() + 1) + "_ID", currentCounter);
                    categoryDocument.put("VICTORINS", victorinsIDs.size() + 1);

                    firestore.collection("QUIZ").document(currentCategoryId)
                            .update(categoryDocument)
                            .addOnSuccessListener(unused1 -> {
                                Toast.makeText(VictorinsCCDActivity.this, "Викторина добавлена успешно", Toast.LENGTH_SHORT).show();
                                victorinsIDs.add(currentCounter);
                                categoriesCCDList.get(selectedCategoryIndex).setNumberOfVictorins(String.valueOf(victorinsIDs.size()));
                                categoriesCCDList.get(selectedCategoryIndex).setVictorinCounter(String.valueOf(Integer.parseInt(currentCounter) + 1));

                                adapter.notifyItemInserted(victorinsIDs.size());

                                loadingDialog.dismiss();

                            }).addOnFailureListener(e -> {
                                Toast.makeText(VictorinsCCDActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            });

                }).addOnFailureListener(e -> {
                    Toast.makeText(VictorinsCCDActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            VictorinsCCDActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}