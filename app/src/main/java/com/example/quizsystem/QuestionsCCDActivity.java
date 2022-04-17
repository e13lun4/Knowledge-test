package com.example.quizsystem;

import static com.example.quizsystem.CategoriesCCDActivity.categoriesCCDList;
import static com.example.quizsystem.CategoriesCCDActivity.selectedCategoryIndex;
import static com.example.quizsystem.VictorinsCCDActivity.selectedVictorinIndex;
import static com.example.quizsystem.VictorinsCCDActivity.victorinsIDs;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuestionsCCDActivity extends AppCompatActivity {

    private RecyclerView questionsView;
    private Button addQuestionButton;
    public static List<QuestionModel> questionsList = new ArrayList<>();
    private QuestionCCDAdapter adapter;
    private FirebaseFirestore firestore;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_ccdactivity);

        Toolbar toolbar = findViewById(R.id.questionsToolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("VICTORIN");
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        questionsView = findViewById(R.id.questionsCCDRecyclerView);
        addQuestionButton = findViewById(R.id.addQuestionButton);

        loadingDialog = new Dialog(QuestionsCCDActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addQuestionButton.setOnClickListener(view -> {

        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        questionsView.setLayoutManager(layoutManager);

        firestore = FirebaseFirestore.getInstance();

        loadQuestions();

    }

    private void loadQuestions(){
        questionsList.clear();
        loadingDialog.show();

        firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                .collection(victorinsIDs.get(selectedVictorinIndex)).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    Map<String, QueryDocumentSnapshot> documentList = new ArrayMap<>();

                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        documentList.put(document.getId(), document);
                    }

                    QueryDocumentSnapshot questionsListDocument = documentList.get("QUESTIONS_LIST");

                    String count = Objects.requireNonNull(questionsListDocument).getString("COUNT");

                    for(int i = 0; i < Integer.parseInt(Objects.requireNonNull(count)); i++){
                        String questionID = questionsListDocument.getString("Q" + String.valueOf(i+1) + "_ID");

                        QueryDocumentSnapshot questionDocument = documentList.get(questionID);

                        questionsList.add(new QuestionModel(
                                questionID,
                                Objects.requireNonNull(questionDocument).getString("QUESTION"),
                                questionDocument.getString("A"),
                                questionDocument.getString("B"),
                                questionDocument.getString("C"),
                                questionDocument.getString("D"),
                                Integer.parseInt(Objects.requireNonNull(questionDocument.getString("ANSWER")))
                        ));
                    }

                    adapter = new QuestionCCDAdapter(questionsList);
                    questionsView.setAdapter(adapter);

                    loadingDialog.dismiss();

                }).addOnFailureListener(e -> {
                    Toast.makeText(QuestionsCCDActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            QuestionsCCDActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}