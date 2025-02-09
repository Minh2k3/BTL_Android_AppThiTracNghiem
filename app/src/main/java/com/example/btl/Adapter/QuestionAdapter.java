package com.example.btl.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.models.Question;
import com.example.btl.models.SharedHashMap;
import com.example.btl.models.StudentAns;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>{

    private List<Question> questionsList;
    String examID;
//    private SharedHashMap sharedHashMap;
//    private HashMap<String, StudentAns> selectedOptions;

    public QuestionAdapter(List<Question> questionsList, String examID) {
        this.questionsList = questionsList;
        this.examID = examID;
//        this.selectedOptions = selectedOptions;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AtomicReference<String> studentAnsRef = new AtomicReference<>("");
//        AtomicReference<Integer> correctCnt = new AtomicReference<>(0);
//        AtomicReference<Integer> wrongCnt = new AtomicReference<>(0);
        private TextView tvQuestion;
        private Button btnA, btnB, btnC, btnD, btnPrevSelected;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            btnA = itemView.findViewById(R.id.btnA);
            btnB = itemView.findViewById(R.id.btnB);
            btnC = itemView.findViewById(R.id.btnC);
            btnD = itemView.findViewById(R.id.btnD);

            btnPrevSelected = null;
//            selectedOptions = new HashMap<>();
        }

        private void setData(final int pos) {
            Question question = questionsList.get(pos);
            String ans = question.getAnswer();
            String opt1 = question.getOpt1();
            String opt2 = question.getOpt2();
            String opt3 = question.getOpt3();

            tvQuestion.setText(question.getQuestion());

            List<String> options = new ArrayList<>();
            options.add(ans);
            options.add(opt1);
            options.add(opt2);
            options.add(opt3);

            Collections.shuffle(options);

            btnA.setText(options.get(0));
            btnB.setText(options.get(1));
            btnC.setText(options.get(2));
            btnD.setText(options.get(3));
            
            setOption(btnA, options.get(0), pos, ans);
            setOption(btnB, options.get(1), pos, ans);
            setOption(btnC, options.get(2), pos, ans);
            setOption(btnD, options.get(3), pos, ans);

            btnA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(btnA, options.get(0), pos, ans);
                }
            });

            btnB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(btnB, options.get(1), pos, ans);
                }
            });

            btnC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(btnC, options.get(2), pos, ans);
                }
            });

            btnD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectOption(btnD, options.get(3), pos, ans);
                }
            });
        }

        private void selectOption(Button btn, String option, int quesID, String answer) {
            if (btnPrevSelected == null) {
                btn.setBackgroundResource(R.drawable.selected_btn);
//                selectedOptions.put(quesID + "", new StudentAns(answer, option));
                putSelectedOption(quesID, option);
                if (option.equals(answer)) {
//                    updateCorrectCount(1);
//                    correctCount++;
                } else {
//                    updateWrongCount(1);
//                    wrongCount++;
                }
                btnPrevSelected = btn;
            } else {
                if (btnPrevSelected.getId() == btn.getId()) {
                    btn.setBackgroundResource(R.drawable.unselected_btn);
//                    selectedOptions.put(quesID + "", new StudentAns(answer, ""));
                    putSelectedOption(quesID, "");
                    if (option.equals(answer)) {
//                        updateCorrectCount(-1);
//                        correctCount--;
                    } else {
//                        updateWrongCount(-1);
//                        wrongCount--;
                    }
                    btnPrevSelected = null;
                } else {
                    btnPrevSelected.setBackgroundResource(R.drawable.unselected_btn);
                    btn.setBackgroundResource(R.drawable.selected_btn);
//                    selectedOptions.put(quesID + "", new StudentAns(answer, option));
                    putSelectedOption(quesID, option);
                    if (option.equals(answer)) {
//                        updateCorrectCount(1);
//                        updateWrongCount(-1);
//                        correctCount++;
//                        wrongCount--;
                    } else {
//                        updateCorrectCount(-1);
//                        updateWrongCount(1);
//                        wrongCount++;
//                        correctCount--;
                    }
                    btnPrevSelected = btn;
                }
            }
//            Log.d("selectedOptions", selectedOptions.get(quesID + "").toString());
        }

//        private void updateCorrectCount(int t) {
//            getCorrectCount(correctCnt);
//            int correctCur = correctCnt.get();
//
//            correctCur += t;
//
//            DatabaseReference ref = FirebaseDatabase.getInstance()
//                    .getReference("users")
//                    .child(getUserUid())
//                    .child("exams")
//                    .child(examID);
//            ref.child("correctCount").setValue(correctCur);
//        }

//        private void getCorrectCount(AtomicReference<Integer> correctCnt) {
//            DatabaseReference ref = FirebaseDatabase.getInstance()
//                    .getReference("users")
//                    .child(getUserUid())
//                    .child("exams")
//                    .child(examID);
//
//            ref.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    int correctCount = 0;
//                    if (snapshot.exists()) {
//                        correctCount = snapshot.child("correctCount").getValue(Integer.class);
//                    }
//                    correctCnt.set(correctCount);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.d("Error", error.getMessage());
//                }
//            });
//        }
//        private void updateWrongCount(int t) {
//            getWrongCount(wrongCnt);
//            int wrongCur = wrongCnt.get();
//
//            wrongCur += t;
//
//            DatabaseReference ref = FirebaseDatabase.getInstance()
//                    .getReference("users")
//                    .child(getUserUid())
//                    .child("exams")
//                    .child(examID);
//            ref.child("wrongCount").setValue(wrongCur);
//        }

//        private void getWrongCount(AtomicReference<Integer> wrongCnt) {
//            DatabaseReference ref = FirebaseDatabase.getInstance()
//                    .getReference("users")
//                    .child(getUserUid())
//                    .child("exams")
//                    .child(examID);
//
//            ref.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    int wrongCount = 0;
//                    if (snapshot.exists()) {
//                        wrongCount = snapshot.child("correctCount").getValue(Integer.class);
//                    }
//                    wrongCnt.set(wrongCount);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.d("Error", error.getMessage());
//                }
//            });
//        }

        private void putSelectedOption(int quesID, String option) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(getUserUid()).child("exams").child(examID);
            ref.child(quesID + "").child("studentAns").setValue(option);
        }


        private void getStudentAns(int quesID, AtomicReference<String> studentAnsRef) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(getUserUid())
                    .child("exams")
                    .child(examID)
                    .child(quesID + "");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String studentAnsStr = ""; // Biến tạm thời
                    if (snapshot.exists()) {
//                        StudentAns studentAns = snapshot.getValue(StudentAns.class);
                        studentAnsStr = snapshot.child("studentAns").getValue(String.class);
//                        if (studentAns != null) {
//                            studentAnsStr = studentAns.getStudentAns();
//                        }
                    }
                    studentAnsRef.set(studentAnsStr);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", error.getMessage());
                    studentAnsRef.set(""); // Xử lý khi có lỗi
                }
            });
        }
        private String getUserUid() {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        private void setOption(Button btn, String option, int quesID, String answer) {
//            if (selectedOptions == null) {
//                Log.d("selectedOptions", "null");
//            }
//            if (selectedOptions.get(quesID + "") == null) {
//                Log.d("selectedOptions", "null");
//                selectedOptions.put(quesID + "", new StudentAns(answer, ""));
//            }
//            if (selectedOptions.get(quesID + "").getStudentAns().equals(option)) {
//                btn.setBackgroundResource(R.drawable.selected_btn);
//            } else {
//                btn.setBackgroundResource(R.drawable.unselected_btn);
//            }
            getStudentAns(quesID, studentAnsRef);
            String studentAns = studentAnsRef.get();

            if (studentAns.equals(option)) {
                btn.setBackgroundResource(R.drawable.selected_btn);
            } else {
                btn.setBackgroundResource(R.drawable.unselected_btn);
            }
        }
    }

}
