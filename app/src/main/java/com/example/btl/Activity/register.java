package com.example.btl.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl.R;
import com.example.btl.models.SV;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class register extends AppCompatActivity {

    EditText edtMsv, edtPassword, edtEmail, edtRePass, edtName;
    Button btnRegister;
    CheckBox chkPass;
    ProgressBar progressBar;
    TextView tvLogin;
    LinearLayout layout_sign_in;
    Pattern pattern;
    Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUI();

        initListener();
    }

    private void initListener() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msv = edtMsv.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String rePass = edtRePass.getText().toString().trim();
                String name = edtName.getText().toString().trim();

                if (msv.isEmpty()) {
                    edtMsv.setError("Nhập mã sinh viên");
                    edtMsv.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    edtEmail.setError("Nhập email");
                    edtEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    edtPassword.setError("Nhập mật khẩu");
                    edtPassword.requestFocus();
                    return;
                }

                String passPattern = "^[a-zA-Z0-9]{6,}$";
                pattern = Pattern.compile(passPattern);
                matcher = pattern.matcher(password);

                if (!matcher.matches()) {
                    edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự và chỉ chứa chữ cái và số");
                    edtPassword.requestFocus();
                    return;
                }

                if (rePass.isEmpty()) {
                    edtRePass.setError("Nhập lại mật khẩu");
                    edtRePass.requestFocus();
                    return;
                }

                if (!password.equals(rePass)) {
                    edtRePass.setError("Mật khẩu không trùng khớp");
                    edtRePass.requestFocus();
                    return;
                }

                if (!chkPass.isChecked()) {
                    chkPass.setError("Chưa đồng ý điều khoản");
                    chkPass.requestFocus();
                    return;
                }

                SV sv = new SV(msv, name, email);

                onClickSignUp(sv, password);
            }
        });

        layout_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(register.this, login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void onClickSignUp(SV sv, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(sv.getEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {

                            String uid = auth.getCurrentUser().getUid();
                            // Add user information to the Firebase
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ref = database.getReference("users/" + uid);

                            ref.setValue(sv);
                            DatabaseReference ref2 = database.getReference("users/" + uid + "/role");
                            ref2.setValue("student");

                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(register.this, login.class);
                            startActivity(intent);
                            finish();   // Close the all the previous activities before moving to the MainActivity

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initUI() {
        edtMsv = findViewById(R.id.edtMsv);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtRePass = findViewById(R.id.edtRePass);
        btnRegister = findViewById(R.id.btnRegister);
        chkPass = findViewById(R.id.chkPass);
        progressBar = findViewById(R.id.progressBar);
        tvLogin = findViewById(R.id.tvLogin);
        layout_sign_in = findViewById(R.id.layout_sign_in);
    }

}