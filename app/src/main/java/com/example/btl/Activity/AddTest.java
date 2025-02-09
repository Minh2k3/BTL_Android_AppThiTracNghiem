package com.example.btl.Activity;

import static java.lang.Math.min;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl.R;
import com.example.btl.models.Exam;
import com.example.btl.models.Question;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

public class AddTest extends AppCompatActivity {
    EditText edtTende, edtMatkhau, edtSoluongcau, edtThoigian;
    Button btnAdd, btnCancel;
    ArrayList<Question> listQuestions, examQuestions;
    Exam exam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();

        getAllQuestions();

//        CompletableFuture<Void> future = CompletableFuture.runAsync(this::getAllQuestions);
//        future.join();

//        Log.d("Check list question", listQuestions.size() + "");

        initListener();
    }

    private void initListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReturn = new Intent(AddTest.this, main_gv.class);
                startActivity(intentReturn);
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExam();

            }
        });
    }

    private void createExam() {
        int soCau = Integer.parseInt(edtSoluongcau.getText().toString());
        String examId = generateExamId();
//        Collections.shuffle(listQuestions);
        Random random = new Random();
        TreeSet<Integer> st = new TreeSet<>();
        while (st.size() != soCau) {
            st.add(random.nextInt(listQuestions.size()));
        }
        Iterator<Integer> iterator = st.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            int idx = iterator.next();
            examQuestions.add(listQuestions.get(idx));
            examQuestions.get(i).setId(i + "");
            i++;
        }
//        examQuestions = listQuestions.subList(0, min(Integer.parseInt(edtSoluongcau.getText().toString()), listQuestions.size()));
        String examName = edtTende.getText().toString();
        String examPassword = edtMatkhau.getText().toString();
        long examTime = Long.parseLong(edtThoigian.getText().toString());
        exam = new Exam(examId, examName, "Chưa mở", examPassword, examTime, (ArrayList<Question>) examQuestions);
        Toast.makeText(AddTest.this, "Đề thi " + examId + " đã được tạo", Toast.LENGTH_LONG).show();
        addExam(exam);
    }

    private void initUI() {
        edtTende = findViewById(R.id.edtTende);
        edtMatkhau = findViewById(R.id.edtMatkhau);
        edtSoluongcau = findViewById(R.id.edtSoluongcau);
        edtThoigian = findViewById(R.id.edtThoigian);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        listQuestions = new ArrayList<>();
        examQuestions = new ArrayList<>();
        exam = new Exam();
    }

    private String generateExamId() {
        String examId = "";
        boolean check = true;
        ArrayList<String> listKey = getAllKey();
        while(check) {
            examId = genString();
            if (!listKey.contains(examId)) {
                check = false;
            }
        }
        return examId;
    }

    private String genString() {
        StringBuilder sb = new StringBuilder(5);
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            int offset = (random.nextInt() % 2 == 1) ? 65 : 97;
            char randomChar = (char) (random.nextInt(26) + offset);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private ArrayList<String> getAllKey() {
        ArrayList<String> listKey = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("list_exam");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot examSnapshot : dataSnapshot.getChildren()) {
                    String examCode = examSnapshot.getKey();
                    listKey.add(examCode);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        };

        DatabaseReference examsRef = ref.child("list_exam");
        examsRef.addListenerForSingleValueEvent(valueEventListener);
        return listKey;
    }

    public void getAllQuestions() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("questions");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
//                    Log.d("Check question", questionSnapshot.toString());
                    String id = questionSnapshot.getKey().toString();
                    String question = questionSnapshot.child("question").getValue().toString();
                    String answer = questionSnapshot.child("answer").getValue().toString();
                    String opt1 = questionSnapshot.child("opt1").getValue().toString();
                    String opt2 = questionSnapshot.child("opt2").getValue().toString();
                    String opt3 = questionSnapshot.child("opt3").getValue().toString();

                    Question questionT = new Question(id, question, answer, opt1, opt2, opt3);
                    listQuestions.add(questionT);
//                    Log.d("Check list question", list.size() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Check question", "Error");
            }
        });


    }

    public void addExam(Exam e) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("list_exam");
        ref.child(e.getId()).child("name").setValue(e.getName());
        ref.child(e.getId()).child("status").setValue(e.getStatus());
        ref.child(e.getId()).child("pass").setValue(e.getPassword());
        ref.child(e.getId()).child("time").setValue(e.getTimeLimit());
        ref.child(e.getId()).child("questions").setValue(e.getQuestions());
    }
}