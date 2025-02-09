package com.example.btl.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.btl.Activity.ExamStatistics;
import com.example.btl.R;
import com.example.btl.models.Exam;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class listExamAdapterGV extends BaseAdapter {
    Context context;
    ArrayList<Exam> list;
    public listExamAdapterGV() {
    }

    public listExamAdapterGV(Context context, ArrayList<Exam> list) {
        this.context = context;
        this.list = list;
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
            view = inflater.inflate(R.layout.listview_exam_gv, viewGroup, false);
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

        CardView cardView = view.findViewById(R.id.cardDelete);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click event
                deleteExam(i);
            }
        });

        ImageButton btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click event
                deleteExam(i);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click event
                navigateToOtherView(i);
            }
        });

        return view;
    }

    private void deleteExam(int i) {
        Exam exam = (Exam) this.getItem(i);

        Log.d("Check button xóa", "ádf");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa đề thi này không?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Delete exam
                deleteInFirebase(exam.getId());
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, context.getClass());
                context.startActivity(intent);
                if (context instanceof com.example.btl.Activity.main_gv) {
                    ((com.example.btl.Activity.main_gv) context).finish();
                }
            }
        });

        builder.setNegativeButton("Không", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteInFirebase(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("list_exam")
                .child(id);

        ref.removeValue();
    }

    private void navigateToOtherView(int i) {
        Exam exam = (Exam) this.getItem(i);

        Intent intent = new Intent(context, ExamStatistics.class);
        intent.putExtra("examId", exam.getId());
        intent.putExtra("examName", exam.getName());
        context.startActivity(intent);

    }
}
