package com.example.btl.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.btl.Adapter.listExamAdapterGV;
import com.example.btl.R;
import com.example.btl.models.Exam;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class main_gv extends AppCompatActivity {
    Button btnCreateExam, btnCreateQuestion, btnLogout;
    ListView lvExam;
    ArrayList listExam;
    listExamAdapterGV adapter;
    TextView tvGvName;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gv);
        btnCreateExam = findViewById(R.id.btnCreateExam);
        btnCreateQuestion = findViewById(R.id.btnCreateQuestion);
        btnLogout = findViewById(R.id.btnLogout);
        tvGvName = findViewById(R.id.tvGvName);
        lvExam = findViewById(R.id.lvExam);
        listExam = new ArrayList<>();

        getExam();

        adapter = new listExamAdapterGV(this, listExam);
        lvExam.setAdapter(adapter);

        intent = getIntent();

        tvGvName.setText(intent.getStringExtra("name"));

        btnCreateExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent = new Intent(main_gv.this, AddTest.class);
                startActivity(myintent);

            }
        });

        btnCreateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent = new Intent(main_gv.this, QuestionManagement.class);
                startActivity(myintent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent = new Intent(main_gv.this, login.class);
                startActivity(myintent);
                finish();
            }
        });
    }

    private void getExam() {

        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("list_exam");
        examsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot examSnapshot : dataSnapshot.getChildren()) {
                    String key = examSnapshot.getKey();
                    String name = examSnapshot.child("name").getValue().toString();
                    String status = examSnapshot.child("status").getValue().toString();
                    String password = examSnapshot.child("pass").getValue().toString();
                    long timeLimit = (long) examSnapshot.child("time").getValue();
                    Exam exam = new Exam(key, name, status, password, timeLimit);
                    boolean isExist = false;
                    for (int i = 0; i < listExam.size(); i++) {
                        Exam e = (Exam) listExam.get(i);
                        if (e.getId().equals(exam.getId())) {
                            listExam.set(i, exam);
                            isExist = true;
                            break;
                        }
                    }
                    if (isExist) {
                        continue;
                    }
                    listExam.add(exam);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        });
    }

}