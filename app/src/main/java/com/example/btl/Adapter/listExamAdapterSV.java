package com.example.btl.Adapter;

import static java.lang.System.exit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.btl.Activity.SVResult;
import com.example.btl.Activity.StartExamActivity;
import com.example.btl.R;
import com.example.btl.models.Exam;
import com.example.btl.models.SV;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class listExamAdapterSV extends BaseAdapter {
    Handler handler;
    boolean f = false;
    Context context;
    ArrayList<Exam> list;
    SV sv;
//    boolean isDone = false;
    AtomicReference<Boolean> isDone = new AtomicReference<>(false);
    public listExamAdapterSV() {
    }

    public listExamAdapterSV(Context context, ArrayList<Exam> list, SV sv) {
        this.context = context;
        this.list = list;
        this.sv = sv;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.listview_exam_sv, viewGroup, false);
        }

        String id = list.get(i).getId();
        String name = list.get(i).getName();
        String status = list.get(i).getStatus();
        long timeLimit = list.get(i).getTimeLimit();

        TextView tvExamId = view.findViewById(R.id.tvExamId);
        tvExamId.setText("Mã đề: " + id);

        TextView tvExamName = view.findViewById(R.id.tvExamName);
        tvExamName.setText(name);

//        TextView tvExamStatus = view.findViewById(R.id.tvExamStatus);
//        tvExamStatus.setText(status);

        TextView tvExamTime = view.findViewById(R.id.tvExamTime);
        tvExamTime.setText("Thời gian: " + timeLimit + " phút");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if user have done this exercise or not
                Log.d("Đã bấm", "Đã bấm");
                check(i, isDone);

//                Log.d("Check isdone", isDone.get().toString());

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (f) {
//                            f = true;
                            Log.d("Đã bấm", "Ở đây");
                            Toast.makeText(context, "Bạn đã làm bài thi này rồi", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, SVResult.class);
                            intent.putExtra("sv", sv);
                            intent.putExtra("exam", list.get(i));
                            context.startActivity(intent);
                        }
                    }
                }, 3000);


//                if (isDone.get()) {

                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!f) {
                                Log.d("Đã bấm", "Vào làm bài");
                                Toast.makeText(context, "Đang vào bài", Toast.LENGTH_LONG).show();
                                showPasswordDialog(i);
                            }
                        }
                    }, 5000);

//                }

                    //                if (isDone(i)) {
                    //                    Toast.makeText(context, "Bạn đã làm bài thi này rồi", Toast.LENGTH_SHORT).show();
                    //                    return;
                    //                }

                    //                Log.d("Kiểm tra sv", sv.getName());
                    //                Log.d("Kiểm tra sv", sv.getMsv());

                    // Handle click event
            }
        });

        return view;
    }
    private void check(int i, AtomicReference<Boolean> isDone) {
        Exam exam = (Exam) this.getItem(i);

        Log.d("Check ", "hàm check");

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(getUserUid())
                .child("exams");
        Log.d("Check", getUserUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Check", "Vào snapshot");
//                if (snapshot.hasChild("exams")) {
                    boolean flag = false;
                    Log.d("Check", "Vào child exams");
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (data.getKey().equals(exam.getId())) {
                            flag = true;
                            f = true;
                            break;
                        }
                    }
                    isDone.set(flag);
//                    f = flag;

                    Log.d("Check isdone trong hàm", isDone.get().toString());
//                } else {
//                    isDone.set(false);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isDone.set(false);
            }
        });
    }

    private String getUserUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void showPasswordDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Nhập mật khẩu");

        final EditText passwordEditText = new EditText(context);
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordEditText);

        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordEditText.getText().toString();

                if (validatePassword(password, i)) {
                    navigateToOtherView(i);
                } else {
                    Toast.makeText(context, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validatePassword(String password, int i) {
        // Kiểm tra mật khẩu tại đây
        // ...
        Exam exam = (Exam) this.getItem(i);
        return password.equals(exam.getPassword());
    }

    private void navigateToOtherView(int i) {
        Exam exam = (Exam) this.getItem(i);

        Intent intent = new Intent(context, StartExamActivity.class);
        intent.putExtra("sv", sv);
        intent.putExtra("exam", exam);
        intent.putExtra("name", sv.getName());

        Log.d("kiểm tra exam", exam.toString());
        Log.d("Kiểm tra sv", sv.toString());

        context.startActivity(intent);
    }
}
