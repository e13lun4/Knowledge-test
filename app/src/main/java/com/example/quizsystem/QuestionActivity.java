package com.example.quizsystem;

import static com.example.quizsystem.VictorinsActivity.category_id;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView question, qCount, timer;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    int questionNum;
    private CountDownTimer countDown;
    private int score;
    private FirebaseFirestore firestore;
    private int victorinNumber;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question = findViewById(R.id.question);
        qCount = findViewById(R.id.question_num);
        timer = findViewById(R.id.countdown);

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);

        loadingDialog = new Dialog(QuestionActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        victorinNumber = getIntent().getIntExtra("VICTORIN_NUMBER", 1);
        firestore = FirebaseFirestore.getInstance();

        getQuestionsList();

        score = 0;

    }

    private void getQuestionsList(){
        questionList = new ArrayList<>();
        firestore.collection("QUIZ").document("CAT" + String.valueOf(category_id))
                .collection("VICTORIN" + String.valueOf(victorinNumber))
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot questions = task.getResult();
                for (QueryDocumentSnapshot doc : questions){
                    questionList.add(new Question(doc.getString("QUESTION"),
                            doc.getString("A"),
                            doc.getString("B"),
                            doc.getString("C"),
                            doc.getString("D"),
                            Integer.parseInt(Objects.requireNonNull(doc.getString("ANSWER")))
                            ));
                }
                setQuestion();
            }else{
                Toast.makeText(QuestionActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
            loadingDialog.cancel();
        });
//        questionList.add(new Question("Вопрос 1", "A", "B", "C", "D", 2));
//        questionList.add(new Question("Вопрос 2", "Aa", "Bb", "Cc", "Dd", 3));
//        questionList.add(new Question("Вопрос 3", "Ab", "Ba", "Cd", "Dc", 1));
//        questionList.add(new Question("Вопрос 4", "Ac", "Bd", "Cb", "Da", 4));
//        questionList.add(new Question("Вопрос 5", "Aac", "Bdc", "Ccd", "Dbb", 2));
    }

    private void setQuestion(){
        timer.setText(String.valueOf(10));

        question.setText(questionList.get(0).getQuestion());

        option1.setText(questionList.get(0).getOptionA());
        option2.setText(questionList.get(0).getOptionB());
        option3.setText(questionList.get(0).getOptionC());
        option4.setText(questionList.get(0).getOptionD());

        qCount.setText(String.valueOf(1) + "/" + String.valueOf(questionList.size()));

        startTimer();

        questionNum = 0;

    }

    private void startTimer(){
         countDown = new CountDownTimer(12000, 1000) {
            @Override
            public void onTick(long l) {
                if(l < 10000){
                    timer.setText(String.valueOf(l / 1000));
                }
            }

            @Override
            public void onFinish() {
                changeQuestion();
            }
        };

        countDown.start();

    }

    @Override
    public void onClick(View view) {

        int selectedOption = 0;

        switch (view.getId()){
            case R.id.option1:
                selectedOption = 1;
                break;

            case R.id.option2:
                selectedOption = 2;
                break;

            case R.id.option3:
                selectedOption = 3;
                break;

            case R.id.option4:
                selectedOption = 4;
                break;

            default:
        }

        countDown.cancel();

        checkAnswer(selectedOption, view);

    }

    private void checkAnswer(int selectedOption, View view){
        if(selectedOption == questionList.get(questionNum).getCorrectAnswer()){
            //Правильный ответ
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score++;
        }else{
            //Неправильный ответ
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            switch (questionList.get(questionNum).getCorrectAnswer()){
                case 1:
                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        }, 2000);
    }

    private void changeQuestion(){
        if(questionNum < questionList.size() - 1){

            questionNum++;

            playAnimation(question, 0, 0);
            playAnimation(option1, 0, 1);
            playAnimation(option2, 0, 2);
            playAnimation(option3, 0, 3);
            playAnimation(option4, 0, 4);

            qCount.setText(String.valueOf(questionNum+1) + "/" + String.valueOf(questionList.size()));

            timer.setText(String.valueOf(10));

            startTimer();
        }else{
            //ScoreActivity
            Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);
            intent.putExtra("ОЧКИ", String.valueOf(score) + "/" + String.valueOf(questionList.size()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
//            QuestionActivity.this.finish();
        }
    }

    private void playAnimation(View view, final int value, int viewNum){
        view.animate().alpha(value).scaleX(value).scaleY(value)
                .setDuration(500)
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(value == 0){
                    switch(viewNum){
                        case 0:
                            ((TextView)view).setText(questionList.get(questionNum).getQuestion());
                            break;
                        case 1:
                            ((Button)view).setText(questionList.get(questionNum).getOptionA());
                            break;
                        case 2:
                            ((Button)view).setText(questionList.get(questionNum).getOptionB());
                            break;
                        case 3:
                            ((Button)view).setText(questionList.get(questionNum).getOptionC());
                            break;
                        case 4:
                            ((Button)view).setText(questionList.get(questionNum).getOptionD());
                            break;
                    }

                    if(viewNum != 0){
                        ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#87CEEB")));
                    }

                    playAnimation(view, 1, viewNum);

                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDown.cancel();
    }
}