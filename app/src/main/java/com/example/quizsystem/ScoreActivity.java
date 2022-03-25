package com.example.quizsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    private TextView score;
    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score = findViewById(R.id.s_score);
        done = findViewById(R.id.s_done);

        String score_str = getIntent().getStringExtra("ОЧКИ");//7 14:30
        score.setText(score_str);

        done.setOnClickListener(view -> {
            Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
            ScoreActivity.this.startActivity(intent);
            ScoreActivity.this.finish();
        });

    }
}