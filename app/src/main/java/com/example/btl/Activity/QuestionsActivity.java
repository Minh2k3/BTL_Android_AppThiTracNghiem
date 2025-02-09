package com.example.btl.Activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl.Adapter.QuestionAdapter;
import com.example.btl.Adapter.QuestionGridAdapter;
import com.example.btl.MyApplication;
import com.example.btl.R;
import com.example.btl.models.Exam;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class QuestionsActivity extends AppCompatActivity {
    private RecyclerView listQuestions;
    private TextView tvQuestionID, tvTimeLeft, tvExamName;
    private Button btnSubmit, btnClearSelection, btnSave;
    private ImageButton btnNext, btnPrev, btnClose;
    private ImageView imgSound, imgAllQuestions;
    private int questionID;
    CountDownTimer timer;
    private DrawerLayout drawerLayout;
    private GridView grvQuestionList;
    QuestionAdapter adapter;
    QuestionGridAdapter gridAdapter;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private int volumn;
    public static ArrayList<Question> questionsList;
    Intent intent;
    Exam exam;
    private long timeLeft;
    AtomicReference<Integer> correctCnt = new AtomicReference<>(0);
    HashMap<String, StudentAns> selectedOptions;
//    private SharedHashMap sharedHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_list_layout);

        initUI();

        initFirebase(); // Tạo ra cơ sở dữ liệu trên firebase

        adapter = new QuestionAdapter(questionsList, exam.getId());
        listQuestions.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        listQuestions.setLayoutManager(layoutManager);

        gridAdapter = new QuestionGridAdapter(this, questionsList.size(), selectedOptions);
        grvQuestionList.setAdapter(gridAdapter);

        setSnapHelper();

        initListener();

        startTimer();

    }

    private void initFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(getUserUid()).child("exams").child(exam.getId());
        int i = 0;
        for (Question question : questionsList) {
            String id = i++ + "";
            String correctAns = question.getAnswer();
            String studentAns = "";
            Map<String, String> map = new HashMap<>();
            map.put("correctAns", correctAns);
            map.put("studentAns", studentAns);
            ref.child(id).setValue(map);
        }
    }

    private String getUserUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    private void getCorrectCount(AtomicReference<Integer> correctCnt) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUserUid())
                .child("exams")
                .child(exam.getId());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int correctCount = 0;
                if (snapshot.exists()) {
                    correctCount = snapshot.child("correctCount").getValue(Integer.class);
                }
                correctCnt.set(correctCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
            }
        });
    }
    private void startTimer() {
        long time = exam.getTimeLimit() * 60 * 1000;

        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timeLeft = millisUntilFinished;

                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;

                String min = minutes + "";
                min = min.length() == 1 ? "0" + min : min;

                String sec = seconds + "";
                sec = sec.length() == 1 ? "0" + sec : sec;

                tvTimeLeft.setText(min + ":" + sec);
            }

            @Override
            public void onFinish() {

                getQ();

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                        long totalTime = exam.getTimeLimit() * 60 * 1000 - timeLeft;
                        intent.putExtra("totalTime", totalTime);
                        intent.putExtra("examID", exam.getId());
                        intent.putExtra("totalQuestions", questionsList.size());
                        intent.putExtra("hashmap", selectedOptions);
                        Log.d("Check gửi putextra: ", selectedOptions.size() + "");
                        for (String key : selectedOptions.keySet()) {
                            Log.d("Key: ", key);
                            Log.d("Value: ", selectedOptions.get(key).toString());
                        }
                        startActivity(intent);
                        QuestionsActivity.this.finish();
                    }
                }, 5000);

//                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
//                long totalTime = exam.getTimeLimit() * 60 * 1000 - timeLeft;
//                intent.putExtra("totalTime", totalTime);
//                intent.putExtra("hashmap", selectedOptions);
//                intent.putExtra("examID", exam.getId());
//                intent.putExtra("totalQuestions", questionsList.size());
//                Log.d("Check gửi putextra: ", selectedOptions.size() + "");
//                for (String key : selectedOptions.keySet()) {
//                    Log.d("Key: ", key);
//                    Log.d("Value: ", selectedOptions.get(key).toString());
//                }
//                startActivity(intent);
//                QuestionsActivity.this.finish();
            }
        };

        timer.start();
    }

    public void goToQuestion(int questionID) {
        listQuestions.smoothScrollToPosition(questionID);

        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }
    }

    public void initListener() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionID < questionsList.size() - 1) {
                    listQuestions.smoothScrollToPosition(questionID + 1);
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionID > 0) {
                    listQuestions.smoothScrollToPosition(questionID - 1);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitExam();
            }
        });

        btnClearSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear selection
//                selectedOptions.put(questionID + "", "");
//
//                adapter.notifyDataSetChanged();

            }
        });

        imgAllQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(QuestionsActivity.this, "Show all questions", Toast.LENGTH_SHORT).show();
                // Show all questions
                if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(QuestionsActivity.this, "Close drawer", Toast.LENGTH_SHORT).show();
                // Close drawer
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
            }
        });

        imgSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = getDrawable(R.drawable.mute);
                if (imgSound.getDrawable().getConstantState().equals(drawable.getConstantState())) {
                    if (volumn == 0) {
                        imgSound.setImageResource(R.drawable.not_mute);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    } else {
                        imgSound.setImageResource(R.drawable.not_mute);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumn, 0);
                    }
                } else {
                    imgSound.setImageResource(R.drawable.mute);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                }
            }
        });
    }

    private void submitExam() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.alert_dialog_layout, null);

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                timer.cancel();
                alertDialog.dismiss();

                getQ();

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                        long totalTime = exam.getTimeLimit() * 60 * 1000 - timeLeft;
                        intent.putExtra("totalTime", totalTime);
                        intent.putExtra("examID", exam.getId());
                        intent.putExtra("totalQuestions", questionsList.size());
                        intent.putExtra("hashmap", selectedOptions);
                        Log.d("Check gửi putextra: ", selectedOptions.size() + "");
                        for (String key : selectedOptions.keySet()) {
                            Log.d("Key: ", key);
                            Log.d("Value: ", selectedOptions.get(key).toString());
                        }
                        startActivity(intent);
                        QuestionsActivity.this.finish();
                    }
                }, 5000);

//                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
//                long totalTime = exam.getTimeLimit() * 60 * 1000 - timeLeft;
//                intent.putExtra("totalTime", totalTime);
//                intent.putExtra("examID", exam.getId());
//                intent.putExtra("totalQuestions", questionsList.size());
//                intent.putExtra("hashmap", selectedOptions);
//                Log.d("Check gửi putextra: ", selectedOptions.size() + "");
//                for (String key : selectedOptions.keySet()) {
//                    Log.d("Key: ", key);
//                    Log.d("Value: ", selectedOptions.get(key).toString());
//                }
//                startActivity(intent);
//                QuestionsActivity.this.finish();
            }
        });

        alertDialog.show();
    }

    private void setSnapHelper() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(listQuestions);

        listQuestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                questionID = recyclerView.getLayoutManager().getPosition(view);

                tvQuestionID.setText((questionID + 1) + "/" + questionsList.size());
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initUI() {
        listQuestions = findViewById(R.id.listQuestions);
        tvQuestionID = findViewById(R.id.tvQuestionID);
        tvTimeLeft = findViewById(R.id.tvTimeLeft);
        tvExamName = findViewById(R.id.tvExamName);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnClearSelection = findViewById(R.id.btnClearSelection);
        btnSave = findViewById(R.id.btnSave);
        imgSound = findViewById(R.id.imgSound);
        imgAllQuestions = findViewById(R.id.imgAllQuestions);
        drawerLayout = findViewById(R.id.drawerLayout);
        btnClose = findViewById(R.id.btnClose);
        selectedOptions = new HashMap<>();
        grvQuestionList = findViewById(R.id.grvQuestionList);

        mediaPlayer = MediaPlayer.create(this, R.raw.test);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        volumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (volumn == 0) {
            imgSound.setImageResource(R.drawable.mute);
        }

        mediaPlayer.setLooping(true);
        mediaPlayer.start();

//        sharedHashMap =  ((MyApplication) getApplicationContext()).getSharedHashMap();

        exam = new Exam();
        questionsList = new ArrayList<>();

        intent = getIntent();
        exam = intent.getParcelableExtra("exam");
        Log.d("Nhận exam từ start", exam.toString());
        questionsList = intent.getParcelableArrayListExtra("questions");

        questionID = 0;

        tvQuestionID.setText("1/" + String.valueOf(questionsList.size()));
        tvExamName.setText(exam.getName());  // NOTE: This should be dynamic
    }

    private void getQ() {
//        totalQuestions = intent.getIntExtra("totalQuestions", 0);
//        examID = intent.getStringExtra("examID");

//        Log.d("Check totalQuestions and examID ", totalQuestions + " " + );

//        latch = new CountDownLatch(totalQuestions);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUserUid())
                .child("exams")
                .child(exam.getId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cQ = 0, nQ = 0, wQ = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
//                    String questionID = ds.getKey();
                    if (!ds.getKey().matches("-?\\d+")) {
                        continue;
                    }

                    String correctAns = ds.child("correctAns").getValue().toString();
                    String studentAns = ds.child("studentAns").getValue().toString();
                    if (studentAns.equals(correctAns)) {
                        cQ++;
                    } else if (studentAns.equals("")) {
                        nQ++;
                    } else {
                        wQ++;
                    }
                    Log.d("trong for Check correctQ, wrongQ, notAnsQ", cQ + " " + wQ + " " + nQ);
                }

                updateCount(cQ, wQ);

                Log.d("ngoài for Check correctQ, wrongQ, notAnsQ", cQ + " " + wQ + " " + nQ);

//                correctQ.set(cQ);
//                notAnsQ.set(nQ);
//                wrongQ.set(wQ);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateCount(int c, int w) {
//        examID = intent.getStringExtra("examID");

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUserUid())
                .child("exams")
                .child(exam.getId());
        ref.child("correctCount").setValue(c);
        ref.child("wrongCount").setValue(w);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volumn == 1) {
                imgSound.setImageResource(R.drawable.mute);
            }
            Log.d("volumn", volumn + "");
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            imgSound.setImageResource(R.drawable.not_mute);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(QuestionsActivity.this, "Không được phép trở lại", Toast.LENGTH_SHORT).show();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}