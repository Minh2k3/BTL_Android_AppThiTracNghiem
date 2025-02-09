package com.example.btl.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl.R;
import com.example.btl.models.Exam;
import com.example.btl.models.Question;
import com.example.btl.models.SV;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StartExamActivity extends AppCompatActivity {

    TextView tvSvName, tvExamName, tvQuestions, tvScore, tvTime;
    ImageButton btnReturn;
    Button btnStart;
    Exam exam;
    Intent intent;
    SV sv;
    private String name;
//    ArrayList<Question> questionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exam);

        initUI();

        Log.d("Số câu hỏi: ", exam.getQuestions().size() + "");
        for (Question q : exam.getQuestions()) {
            Log.d("Câu hỏi: ", q.toString());
        }

        initListener();

        // Get data from intent
        showInfo();

    }

    private void showInfo() {

        // Show data
        tvSvName.setText(name);
        tvExamName.setText(exam.getName());
        tvQuestions.setText(exam.getQuestions().size() + "");
        tvScore.setText("0");
        tvTime.setText(exam.getTimeLimit() + " phút");
    }

    private void initListener() {
        btnReturn.setOnClickListener(v -> {
            finish();
        });

        btnStart.setOnClickListener(v -> {
            // Start exam
            startExam();
        });
    }

    private void startExam() {

        exam.shuffleQuestions();

        // Start exam
        Toast.makeText(this, "Start exam", Toast.LENGTH_SHORT).show();
        Intent intentSend = new Intent(StartExamActivity.this, QuestionsActivity.class);
        intentSend.putExtra("exam", exam);
        intentSend.putExtra("questions", exam.getQuestions());
        startActivity(intentSend);
        finish();
    }

    private void initUI() {
        tvSvName = findViewById(R.id.tvSvName);
        tvExamName = findViewById(R.id.tvExamName);
        tvQuestions = findViewById(R.id.tvQuestions);
        tvScore = findViewById(R.id.tvScore);
        tvTime = findViewById(R.id.tvTime);
        btnReturn = findViewById(R.id.btnReturn);
        btnStart = findViewById(R.id.btnStart);

        exam = new Exam();

        intent = getIntent();
        exam = (Exam) intent.getParcelableExtra("exam");
        sv = intent.getParcelableExtra("sv");
        name = intent.getStringExtra("name");

        if (exam == null) {
            Toast.makeText(this, "Không tìm thấy thông tin đề thi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (sv == null) {
            Toast.makeText(this, "Không tìm thấy thông tin sinh viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("Kiểm tra exam tồn tại: ", exam.toString());

        exam.setQuestions(new ArrayList<>());

//        questionsList = new ArrayList<>();
        getQuestions(exam.getId());

//        exam.setQuestions(questionsList);
    }

    private void getQuestions(String key) {
        // Get questions from database
        ArrayList<Question> questions = new ArrayList<>();
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("list_exam");

        Query query = examsRef.child(key).child("questions");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String answer = dataSnapshot.child("answer").getValue().toString();
                    String opt1 = dataSnapshot.child("opt1").getValue().toString();
                    String opt2 = dataSnapshot.child("opt2").getValue().toString();
                    String opt3 = dataSnapshot.child("opt3").getValue().toString();
                    String id = dataSnapshot.child("id").getValue().toString();
                    String question = dataSnapshot.child("question").getValue(String.class);
                    Question ques = new Question(id, question, answer, opt1, opt2, opt3);
                    Log.d("Câu hỏi: ", ques.toString());
//                    questionsList.add(ques);
                    exam.addQuestion(ques);
                    Log.d("Số câu hỏi: ", exam.getQuestions().size() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StartExamActivity.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}