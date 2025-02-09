package com.example.btl.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.btl.models.Exam;
import com.example.btl.R;
import com.example.btl.Adapter.listExamAdapterSV;
import com.example.btl.models.SV;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    listExamAdapterSV adapter;
    ArrayList listExam;
    SV sv;
    ListView lvExam;
    Button btnSearch;
    EditText edtSearch;

    public Home() {
        // Required empty public constructor
    }

    public Home(SV sv) {
        this.sv = sv;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        btnSearch = view.findViewById(R.id.btnSearch);

        lvExam = view.findViewById(R.id.lvExam);
        listExam = new ArrayList<>();

        getExam();

//        listExam.add(new Exam("HYkto", "Bài thi số 1", "Đang chờ", "123456", 45));
//        listExam.add(new Exam("ashrr", "Bài thi số 2", "Đang thi", "123", 30));
//        listExam.add(new Exam("KHjHI", "Bài thi số 3", "Đã kết thúc", "123456", 15));
//        listExam.add(new Exam("Opojd", "Bài thi số 4", "Đang chờ", "123456", 75));
//        listExam.add(new Exam("IEOdi", "Bài thi số 5", "Đã kết thúc", "123456", 90));
//        listExam.add(new Exam("tjyfd", "Bài thi số 6", "Đang chờ", "123456", 45));
//        listExam.add(new Exam("dfgrd", "Bài thi số 7", "Đang thi", "123456", 30));
//        listExam.add(new Exam("Grhuk", "Bài thi số 8", "Đã kết thúc", "123456", 15));
//        listExam.add(new Exam("REHvr", "Bài thi số 9", "Đang chờ", "123456", 75));
//        listExam.add(new Exam("NladJ", "Bài thi số 10", "Đã kết thúc", "123456", 90));

//        Log.d("Kiểm tra sv", sv.toString());

        adapter = new listExamAdapterSV(getContext(), listExam, sv);
        lvExam.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = edtSearch.getText().toString();
                if (id.isEmpty()) {
                    getExam();
                    adapter.notifyDataSetChanged();
                    return;
                }
                getExam2(id);
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        if (getActivity() != null) {
            getActivity().setTitle("Dashboard");
        }
    }

    private void getExam() {
        listExam.clear();
        // Get exam from server
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

    private void getExam2(String id) {
        listExam.clear();
        // Get exam from server
        DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("list_exam");
        examsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot examSnapshot : dataSnapshot.getChildren()) {
                    String key = examSnapshot.getKey();
                    if (!key.equals(id)) {
                        continue;
                    }
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