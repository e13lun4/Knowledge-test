package com.example.quizsystem;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ALL")
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private TextView appName;
    public static List<CategoryFModel> categoryList = new ArrayList<>();
    public static int selectedCatIndex = 0;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appName = findViewById(R.id.appName);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.days2);
        appName.setTypeface(typeface);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.myanim);
        appName.setAnimation(anim);
        firestore = FirebaseFirestore.getInstance();
       new Thread(() -> {
           try{
               sleep(3000);
               loadData();
           }catch (InterruptedException e){
               e.printStackTrace();
           }
       }).start();
    }
    private void loadData(){
        categoryList.clear();
        firestore.collection("TestSystem").document("Categories")
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()){
                            long count = (long) doc.get("COUNT");
                            for(int i = 1; i <= count; i++){
                                String categoryName = doc.getString("CAT" + String.valueOf(i) + "_NAME");
                                String categoryID = doc.getString("CAT" + String.valueOf(i) + "_ID");

                                categoryList.add(new CategoryFModel(categoryID, categoryName));
                            }
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }else{
                            Toast.makeText(SplashActivity.this, "Отсутствуют категории", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }else{
                        Toast.makeText(SplashActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        }
}