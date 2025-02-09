package com.example.btl.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.btl.R;
import com.example.btl.fragment.Home;
import com.example.btl.models.StudentAns;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ScoreActivity extends AppCompatActivity {

    private TextView tvScore, tvTime, tvNumQues, tvNumTrue, tvNumFalse, tvNumNotAns;
    ImageButton btnHome;
    private Toolbar toolbar;
    private long totalTime;
    private String examID;
    private int totalQuestions;
    CardView imgWait;
//    AtomicInteger correctQ = new AtomicInteger(0);
//    AtomicInteger notAnsQ = new AtomicInteger(0);
//    AtomicInteger wrongQ = new AtomicInteger(0);
//    AtomicReference<Integer> correctQ = new AtomicReference(0);
//    AtomicReference<Integer> notAnsQ = new AtomicReference<>(0);
//    AtomicReference<Integer> wrongQ = new AtomicReference<>(0);
    AtomicReference<String> correctCnt = new AtomicReference<>("0");
    AtomicReference<String> wrongCnt = new AtomicReference<>("0");
//    CountDownLatch latch;
    Intent intent;
    HashMap<String, StudentAns> selectedOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        initUI();

        loadData();

        initListener();
    }

    private void initListener() {
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ScoreActivity.this, Home.class);
            startActivity(intent);
            finish();
        });
    }


    private void getCorrectCount(AtomicReference<String> correctCnt) {
        examID = intent.getStringExtra("examID");

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUserUid())
                .child("exams")
                .child(examID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int correctCount = 0;
                if (snapshot.exists()) {
                    correctCount = snapshot.child("correctCount").getValue(Integer.class);
                    Log.d("Correct count", correctCount + "");
                }
                correctCnt.set(correctCount + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
                correctCnt.set("0");
            }
        });
    }

    private void getWrongCount(AtomicReference<String> wrongCnt) {
        examID = intent.getStringExtra("examID");

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUserUid())
                .child("exams")
                .child(examID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int wrongCount = 0;
                if (snapshot.exists()) {
                    wrongCount = snapshot.child("wrongCount").getValue(Integer.class);
                    Log.d("Wrong count", wrongCount + "");
                }
                wrongCnt.set(wrongCount + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
                wrongCnt.set("0");
            }
        });
    }

//        correctCount = correctQ.get();
//        notansCount = notAnsQ.get();
//        wrongCount = wrongQ.get();
//        Log.d("1 Check correctQ, wrongQ, notAnsQ", correctQ + " " + wrongQ + " " + notAnsQ);

    private void loadData() {


//        for (String key : selectedOptions.keySet()) {
//            StudentAns studentAns = selectedOptions.get(key);
//            if (studentAns.getStudentAns().equals(studentAns.getCorrectAns())) {
//                correctQ++;
//            } else if (studentAns.getStudentAns().equals("")) {
//                notAnsQ++;
//            } else {
//                wrongQ++;
//            }
//        }

//        getQ(correctQ, notAnsQ, wrongQ);


        getCorrectCount(correctCnt);
        getWrongCount(wrongCnt);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgWait.setVisibility(CardView.GONE);

                Log.d("Check correctAns, wrongAns", correctCnt.get() + " " + wrongCnt.get());
                totalQuestions = intent.getIntExtra("totalQuestions", 0);

                int correctCount = Integer.parseInt(correctCnt.get());
                int wrongCount = Integer.parseInt(wrongCnt.get());
                int notansCount = totalQuestions - correctCount - wrongCount;

                Log.d("Check correctQ, wrongQ, notAnsQ", correctCount + " " + wrongCount + " " + notansCount);

                double score = (double) correctCount / totalQuestions * 10;

                tvScore.setText(String.format("%.2f", score));
                tvNumQues.setText(totalQuestions + "");
                tvNumTrue.setText(correctCount + "");
                tvNumFalse.setText(wrongCount + "");
                tvNumNotAns.setText(notansCount + "");

                totalTime = intent.getLongExtra("totalTime", 0);
                tvTime.setText(totalTime / 60000 + " phút " + (totalTime % 60000) / 1000 + " giây");

                putScoreToDB(score, totalTime);
                }
            }, 5000);
    }

    private void putScoreToDB(double score, long totalTime) {
        examID = intent.getStringExtra("examID");

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUserUid())
                .child("exams")
                .child(examID);

        ref.child("score").setValue(score);
        ref.child("totalTime").setValue(totalTime);

        DatabaseReference refExam = FirebaseDatabase.getInstance()
                .getReference("list_exam")
                .child(examID)
                .child("candidates")
                .child(getUserUid());
        refExam.child("score").setValue(score);
        refExam.child("totalTime").setValue(totalTime);
        refExam.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    private void initUI() {
        tvScore = findViewById(R.id.tvScore);
        tvTime = findViewById(R.id.tvTime);
        tvNumQues = findViewById(R.id.tvNumQues);
        tvNumTrue = findViewById(R.id.tvNumTrue);
        tvNumFalse = findViewById(R.id.tvNumFalse);
        tvNumNotAns = findViewById(R.id.tvNumNotAns);
        toolbar = findViewById(R.id.toolbar);
        imgWait = findViewById(R.id.imgWait);
        btnHome = findViewById(R.id.btnHome);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        intent = getIntent();
        selectedOptions = (HashMap<String, StudentAns>) intent.getSerializableExtra("hashmap");
        Log.d("Hashmap: ", selectedOptions.size() + "");

//        getQ();
    }



    private String getUserUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}