package com.example.quizsystem;

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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ALL")
public class CategoriesCCDActivity extends AppCompatActivity {
    private RecyclerView categoryRecyclerView;
    private Button addCategoryButton;
    public static List<CategoryModel> categoriesCCDList = new ArrayList<>();
    public static int selectedCategoryIndex = 0;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addCategoryDialog;
    private EditText dialogEditCategoryName;
    private Button dialogAddCategoryButton;
    private CategoryCCDAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_ccdactivity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Категории");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryRecyclerView = findViewById(R.id.categoryCCDRecyclerView);
        addCategoryButton = findViewById(R.id.addCategoryButton);

        loadingDialog = new Dialog(CategoriesCCDActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addCategoryDialog = new Dialog(CategoriesCCDActivity.this);
        addCategoryDialog.setContentView(R.layout.add_category_dialog);
        addCategoryDialog.getWindow().setBackgroundDrawableResource(R.drawable.round);
        addCategoryDialog.setCancelable(true);
        addCategoryDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogEditCategoryName = addCategoryDialog.findViewById(R.id.editCategoryName);
        dialogAddCategoryButton = addCategoryDialog.findViewById(R.id.addCategoryDialogButton);

        firestore = FirebaseFirestore.getInstance();

        addCategoryButton.setOnClickListener(view -> {
            dialogEditCategoryName.getText().clear();
            addCategoryDialog.show();
        });

        dialogAddCategoryButton.setOnClickListener(view ->{
            if (dialogEditCategoryName.getText().toString().isEmpty()){
                dialogEditCategoryName.setError("Введите название категории");
                return;
            }
            addNewCategory(dialogEditCategoryName.getText().toString());
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(CategoriesCCDActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(layoutManager);

        loadData();

    }
    private void loadData(){
        loadingDialog.show();
        categoriesCCDList.clear();
        firestore.collection("TestSystem").document("Categories")
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()){
                    long count = (long) doc.get("COUNT");
                    for(int i = 1; i <= count; i++){
                        String categoryName = doc.getString("CAT" + String.valueOf(i) + "_NAME");
                        String categoryId = doc.getString("CAT" + String.valueOf(i) + "_ID");

                        categoriesCCDList.add(new CategoryModel(categoryId, categoryName, "0", "1"));
                    }
                    adapter = new CategoryCCDAdapter(categoriesCCDList);
                    categoryRecyclerView.setAdapter(adapter);
                }else{
                    Toast.makeText(CategoriesCCDActivity.this, "Отсутствуют категории", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }else{
                Toast.makeText(CategoriesCCDActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
            loadingDialog.dismiss();
        });
    }

    private void addNewCategory(String title){
        addCategoryDialog.dismiss();
        loadingDialog.show();

        Map<String, Object> categoryDate = new ArrayMap<>();
        categoryDate.put("NAME", title);
        categoryDate.put("VICTORINS", 0);
        categoryDate.put("COUNTER", "1");

        String documentID = firestore.collection("TestSystem").document().getId();
        firestore.collection("TestSystem").document(documentID)
                .set(categoryDate)
                .addOnSuccessListener(unused -> {

                    Map<String, Object> categoryDocument = new ArrayMap<>();
                    categoryDocument.put("CAT" + String.valueOf(categoriesCCDList.size() + 1) + "_NAME", title);
                    categoryDocument.put("CAT" + String.valueOf(categoriesCCDList.size() + 1) + "_ID", documentID);
                    categoryDocument.put("COUNT", categoriesCCDList.size()+1);

                    firestore.collection("TestSystem").document("Categories")
                            .update(categoryDocument)
                            .addOnSuccessListener(unused1 -> {
                                Toast.makeText(CategoriesCCDActivity.this, "Категория добавлена успешно", Toast.LENGTH_SHORT).show();
                                categoriesCCDList.add(new CategoryModel(documentID, title, "0", "1"));
                                adapter.notifyItemInserted(categoriesCCDList.size());
                                loadingDialog.dismiss();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(CategoriesCCDActivity.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            });

                }).addOnFailureListener(e -> {
                    Toast.makeText(CategoriesCCDActivity.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            CategoriesCCDActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}