package com.example.btl.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.btl.R;
import com.example.btl.models.Exam;
import com.example.btl.models.SV;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicReference;

public class SVResult extends AppCompatActivity {

    TextView tvExamName, tvScore, tvTime, tvNumQues, tvSvName;
    Intent intent;
    AtomicReference<String> scoreA = new AtomicReference<>("0");
    AtomicReference<String> timeA = new AtomicReference<>("0");
    ImageButton btnReturn;
    SV sv;
    Exam exam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svresult);

        initUI();

        exam = (Exam) intent.getParcelableExtra("exam");

        Log.d("Check exam", exam.getName() + " " + exam.getId());

        initListener();

        showInfo();
    }
    private String getUserUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    private void getScore(AtomicReference<String> scoreA) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUserUid())
                .child("exams")
                .child(exam.getId());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String score = "0";
                if (snapshot.exists()) {
                    score = snapshot.child("score").getValue().toString();
                    Log.d("Correct count", score + "");
                }
                scoreA.set(score + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
                scoreA.set("0");
            }
        });
    }

    private void getTime(AtomicReference<String> timeA) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUserUid())
                .child("exams")
                .child(exam.getId());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String time = "0";
                if (snapshot.exists()) {
                    time = snapshot.child("totalTime").getValue().toString();
                    Log.d("Total time", time + "");
                }
                timeA.set(time + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
                scoreA.set("0");
            }
        });
    }

    private void showInfo() {
        tvSvName.setText(getUserName());
        tvExamName.setText(exam.getName());

        getScore(scoreA);
        getTime(timeA);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            showScore();
        }, 2000);

    }

    private void showScore() {
        Log.d("Check score", scoreA.get());
        Log.d("Check time", timeA.get());

        double score = Double.parseDouble(scoreA.get());
        long time = Long.parseLong(timeA.get());
//        for (int i = 0; i < sv.getResults().size(); i++) {
//            if (sv.getResults().get(i).getExamID().equals(exam.getId())) {
//                score = sv.getResults().get(i).getScore();
//                time = sv.getResults().get(i).getTimeMS();
//                break;
//            }
//        }
        tvScore.setText(String.format("%.2f", score));

        long minutes = time / 60000;
        long seconds = (time % 60000) / 1000;

        String min = minutes + "";
        min = min.length() == 1 ? "0" + min : min;

        String sec = seconds + "";
        sec = sec.length() == 1 ? "0" + sec : sec;

        tvTime.setText(min + " phút " + sec + " giây");
    }

    private String getUserName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    private void initListener() {
        btnReturn.setOnClickListener(v -> {
            intent = new Intent(SVResult.this, main_sv.class);
            startActivity(intent);
            finish();
        });
    }

    private void initUI() {
        tvSvName = findViewById(R.id.tvSvName);
        tvExamName = findViewById(R.id.tvExamName);
        tvScore = findViewById(R.id.tvScore);
        tvTime = findViewById(R.id.tvTime);
        tvNumQues = findViewById(R.id.tvNumQues);
        btnReturn = findViewById(R.id.btnReturn);

        intent = getIntent();


    }
}