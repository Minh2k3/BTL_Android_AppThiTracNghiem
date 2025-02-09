package com.example.btl.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExamStatistics extends AppCompatActivity {

    TextView tvExamName, tvCandidates, tvScore;
    Intent intent;
    String examId;
    int number = 0;
    double score = 0.0, tbScore = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_statistics);

        tvExamName = findViewById(R.id.tvExamName);
        tvCandidates = findViewById(R.id.tvCandidates);
        tvScore = findViewById(R.id.tvScore);
        intent = getIntent();

        tvExamName.setText(intent.getStringExtra("examName"));
        examId = intent.getStringExtra("examId");

        showInfo();
    }

    private void showInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("list_exam")
                .child(examId)
                .child("candidates");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    number++;
                    double curScore = Double.parseDouble(dataSnapshot.child("score").getValue().toString());
                    score += curScore;
                    tvCandidates.setText(number + "");
                    tbScore = score/number;
                    tvScore.setText(String.format("%.2f", tbScore));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExamStatistics.this, "Không lấy được dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}