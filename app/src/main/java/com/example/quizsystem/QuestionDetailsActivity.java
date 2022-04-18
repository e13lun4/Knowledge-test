package com.example.quizsystem;

import static com.example.quizsystem.CategoriesCCDActivity.categoriesCCDList;
import static com.example.quizsystem.CategoriesCCDActivity.selectedCategoryIndex;
import static com.example.quizsystem.QuestionsCCDActivity.questionsList;
import static com.example.quizsystem.VictorinsCCDActivity.selectedVictorinIndex;
import static com.example.quizsystem.VictorinsCCDActivity.victorinsIDs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class QuestionDetailsActivity extends AppCompatActivity {

    private EditText question, optionA, optionB, optionC, optionD, answer;
    private Button addQButton;
    private String qStr, aStr, bStr, cStr, dStr, ansStr;
    private Dialog loadingDialog;
    private FirebaseFirestore firestore;
    private String action;
    private int qID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);

        Toolbar toolbar = findViewById(R.id.qDetailsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        question = findViewById(R.id.question);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        answer = findViewById(R.id.answer);
        addQButton = findViewById(R.id.addQButton);

        loadingDialog = new Dialog(QuestionDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        firestore = FirebaseFirestore.getInstance();

        action = getIntent().getStringExtra("ACTION");

        if (action.compareTo("EDIT") == 0){
            qID = getIntent().getIntExtra("Q_ID", 0);
            loadData(qID);
            getSupportActionBar().setTitle("Вопрос " + String.valueOf(qID + 1));
            addQButton.setText("ОБНОВИТЬ");

        }else{
            getSupportActionBar().setTitle("Вопрос " + String.valueOf(questionsList.size() + 1));
            addQButton.setText("ДОБАВИТЬ");

        }

        addQButton.setOnClickListener(view -> {
            qStr = question.getText().toString();
            aStr = optionA.getText().toString();
            bStr = optionB.getText().toString();
            cStr = optionC.getText().toString();
            dStr = optionD.getText().toString();
            ansStr = answer.getText().toString();

            if(qStr.isEmpty()){
                question.setError("Введите вопрос");
                return;
            }
            if(aStr.isEmpty()){
                optionA.setError("Введите вариант 1");
                return;
            }
            if(bStr.isEmpty()){
                optionB.setError("Введите вариант 2");
                return;
            }
            if(cStr.isEmpty()){
                optionC.setError("Введите вариант 3");
                return;
            }
            if(dStr.isEmpty()){
                optionD.setError("Введите вариант 4");
                return;
            }
            if(ansStr.isEmpty()){
                answer.setError("Введите правильный ответ");
                return;
            }

            if(action.compareTo("EDIT") == 0){
                editQuestion();
            }else{
                addNewQuestion();
            }

        });

    }

    private void addNewQuestion(){
        loadingDialog.show();

        Map<String, Object> questionData = new ArrayMap<>();

        questionData.put("QUESTION", qStr);
        questionData.put("A", aStr);
        questionData.put("B", bStr);
        questionData.put("C", cStr);
        questionData.put("D", dStr);
        questionData.put("ANSWER", ansStr);

        String document_id = firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                .collection(victorinsIDs.get(selectedVictorinIndex)).document().getId();

        firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                .collection(victorinsIDs.get(selectedVictorinIndex)).document(document_id)
                .set(questionData)
                .addOnSuccessListener(unused -> {
                    Map<String, Object> questionDocument = new ArrayMap<>();

                    questionDocument.put("Q" + String.valueOf(questionsList.size() + 1) + "_ID", document_id);
                    questionDocument.put("COUNT", String.valueOf(questionsList.size() + 1));

                    firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                            .collection(victorinsIDs.get(selectedVictorinIndex)).document("QUESTIONS_LIST")
                            .update(questionDocument)
                            .addOnSuccessListener(unused1 -> {
                                Toast.makeText(QuestionDetailsActivity.this, "Вопрос добавлен успешно", Toast.LENGTH_SHORT).show();

                                questionsList.add(new QuestionModel(
                                        document_id,
                                        qStr, aStr, bStr, cStr, dStr, Integer.parseInt(ansStr)
                                ));

                                loadingDialog.dismiss();
                                QuestionDetailsActivity.this.finish();

                            }).addOnFailureListener(e -> {
                                Toast.makeText(QuestionDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            });

                }).addOnFailureListener(e -> {
                    Toast.makeText(QuestionDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });

    }

    private void loadData(int id){
        question.setText(questionsList.get(id).getQuestion());
        optionA.setText(questionsList.get(id).getOptionA());
        optionB.setText(questionsList.get(id).getOptionB());
        optionC.setText(questionsList.get(id).getOptionC());
        optionD.setText(questionsList.get(id).getOptionD());
        answer.setText(String.valueOf(questionsList.get(id).getCorrectAnswer()));
    }

    private void editQuestion(){
        loadingDialog.show();

        Map<String, Object> questionData = new ArrayMap<>();

        questionData.put("QUESTION", qStr);
        questionData.put("A", aStr);
        questionData.put("B", bStr);
        questionData.put("C", cStr);
        questionData.put("D", dStr);
        questionData.put("ANSWER", ansStr);

        firestore.collection("QUIZ").document(categoriesCCDList.get(selectedCategoryIndex).getId())
                .collection(victorinsIDs.get(selectedVictorinIndex)).document(questionsList.get(qID).getQuestionID())
                .set(questionData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(QuestionDetailsActivity.this, "Вопрос успешно обновлен", Toast.LENGTH_SHORT).show();

                    questionsList.get(qID).setQuestion(qStr);
                    questionsList.get(qID).setOptionA(aStr);
                    questionsList.get(qID).setOptionB(bStr);
                    questionsList.get(qID).setOptionC(cStr);
                    questionsList.get(qID).setOptionD(dStr);
                    questionsList.get(qID).setCorrectAnswer(Integer.parseInt(ansStr));

                    loadingDialog.dismiss();
                    QuestionDetailsActivity.this.finish();

                }).addOnFailureListener(e -> {
                    Toast.makeText(QuestionDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            QuestionDetailsActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}