package com.example.btl.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.btl.Activity.QuestionsActivity;
import com.example.btl.R;
import com.example.btl.models.StudentAns;

import java.util.HashMap;

public class QuestionGridAdapter extends BaseAdapter {

    private int numOfQuestions;
    private Context context;
    private HashMap<String, StudentAns> selectedOptions;

    public QuestionGridAdapter(Context context, int numOfQuestions, HashMap<String, StudentAns> selectedOptions) {
        this.context = context;
        this.numOfQuestions = numOfQuestions;
        this.selectedOptions = selectedOptions;
    }

    @Override
    public int getCount() {
        return numOfQuestions;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        View mView;

        if (view == null) {
            mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_grid_item, viewGroup, false);
        } else {
            mView = view;
        }

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof QuestionsActivity) {
                    ((QuestionsActivity) context).goToQuestion(i);
                }
            }
        });

        TextView tvQuestionNumber = mView.findViewById(R.id.tvQuestionNumber);
        tvQuestionNumber.setText(String.valueOf(i + 1));

        return mView;
    }
}
