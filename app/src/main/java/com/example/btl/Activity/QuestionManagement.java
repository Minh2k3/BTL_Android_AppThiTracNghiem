package com.example.btl.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.btl.R;
import com.example.btl.models.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class QuestionManagement extends AppCompatActivity {
    EditText edtQuestion, edtAnswer1, edtAnswer2, edtAnswer3, edtAnswer4;
    Button btnAdd;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_management);

        initUI();

        initListener();

    }

    private void initListener() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = edtQuestion.getText().toString();
                String answer1 = edtAnswer1.getText().toString();
                String answer2 = edtAnswer2.getText().toString();
                String answer3 = edtAnswer3.getText().toString();
                String answer4 = edtAnswer4.getText().toString();

                if (question.isEmpty()) {
                    edtQuestion.setError("Please enter question");
                    edtQuestion.requestFocus();
                    return;
                }

                if (answer1.isEmpty()) {
                    edtAnswer1.setError("Please enter answer 1");
                    edtAnswer1.requestFocus();
                    return;
                }

                if (answer2.isEmpty()) {
                    edtAnswer2.setError("Please enter answer 2");
                    edtAnswer2.requestFocus();
                    return;
                }

                if (answer3.isEmpty()) {
                    edtAnswer3.setError("Please enter answer 3");
                    edtAnswer3.requestFocus();
                    return;
                }

                if (answer4.isEmpty()) {
                    edtAnswer4.setError("Please enter answer 4");
                    edtAnswer4.requestFocus();
                    return;
                }

                Question q = new Question("", question, answer1, answer2, answer3, answer4);

                progressBar.setVisibility(View.VISIBLE);
                // Add question to database
                addQuestion(q);
            }
        });
    }

    private void addQuestion(Question q) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        Query query = database.getReference("questions").orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int maxId = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    int id = Integer.parseInt(child.getKey());
                    if (id > maxId) {
                        maxId = id;
                    }
                }
                progressBar.setVisibility(View.GONE);
                q.setId(String.valueOf(maxId + 1));
                database.getReference("questions").child(q.getId()).setValue(q);
                Toast.makeText(QuestionManagement.this, "Add question successfully", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Toast.makeText(QuestionManagement.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        edtQuestion = findViewById(R.id.edtQuestion);
        edtAnswer1 = findViewById(R.id.edtAnswer1);
        edtAnswer2 = findViewById(R.id.edtAnswer2);
        edtAnswer3 = findViewById(R.id.edtAnswer3);
        edtAnswer4 = findViewById(R.id.edtAnswer4);
        btnAdd = findViewById(R.id.btnAdd);
        progressBar = findViewById(R.id.progressBar);
    }
}