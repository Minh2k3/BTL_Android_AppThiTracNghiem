package com.example.btl.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl.Adapter.AccountAdapter;
import com.example.btl.MyDatabaseHelper;
import com.example.btl.R;
import com.example.btl.fragment.MyProfileFragment;
import com.example.btl.models.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class login extends AppCompatActivity {

    EditText edtPassword;
    AutoCompleteTextView edtEmail;
    Button btnLogin;
    ProgressBar progressBar;
    TextView tvRegister, tvForgotPassword, tvGuest;
    CheckBox checkPassword, chkGhinho;
    SharedPreferences sharedPreferences;
    LinearLayout layoutSignUp;
    MyDatabaseHelper dbHelper;
    ArrayList<Account> accounts;
    Pattern pattern;
    Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();

        initListener();
    }

    private void initListener() {
        layoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Move to the SignUpActivity
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);
            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
                refreshAutoCompleteTextView();
            }
        });

        edtEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Account selectedText = (Account) parent.getItemAtPosition(position);
                for (Account account : accounts) {
                    if (account.getEmail().equals(selectedText.getEmail())) {
                        edtPassword.setText(account.getPassword().trim());
                        break; // Nếu tìm thấy tài khoản, bạn có thể dừng vòng lặp ngay lập tức
                    }
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if (TextUtils.isEmpty(mail)) {
                    edtEmail.setError("Nhập email");
                    edtEmail.requestFocus();
                    return;
                }

//                String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
//                pattern = Pattern.compile(emailPattern);
//                matcher = pattern.matcher(emailPattern);
//
//                if (!matcher.matches()) {
//                    edtEmail.setError("Email chưa đúng định dạng");
//                    edtEmail.requestFocus();
//                    return;
//                }

                if (TextUtils.isEmpty(password)) {
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

                onClickLogIn(mail, password);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = edtEmail.getText().toString();
                if (mail.isEmpty()) {
                    edtEmail.setError("Vui lòng nhập email đăng ký" );
                    edtEmail.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                onClickForgotPassword(mail);
            }
        });

        checkPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Xử lý sự kiện khi trạng thái của CheckBox thay đổi
                if (isChecked) {
                    // CheckBox đã được chọn
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    // CheckBox không được chọn
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        tvGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, OfflineMain.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void onClickForgotPassword(String emailAddress) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this, "Password must have at least 6 characters and only contains alphabet and numeric", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(login.this, "Failed to send email! Please check the email address", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onClickLogIn(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(chkGhinho.isChecked()) {
                                String email = edtEmail.getText().toString();
                                String password = edtPassword.getText().toString();
                                dbHelper.insertAccount(email, password);
                                Toast.makeText(login.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                            };
                            findUser(email);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void findUser(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = firebaseDatabase.getReference("users");

        Query query = usersRef.orderByKey().equalTo(uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                    String userEmail = userSnapshot.child("email").getValue(String.class);

                    if (userEmail.equals(email)) {
                        String role = userSnapshot.child("role").getValue(String.class);
                        if (role.equals("student")) {
                            Intent intent = new Intent(login.this, main_sv.class);
//                            Toast.makeText(login.this, uid, Toast.LENGTH_SHORT).show();
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                            finish();
                        } else {
                            String name = userSnapshot.child("name").getValue(String.class);
                            Intent intent = new Intent(login.this, main_gv.class);
                            intent.putExtra("name", name);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(login.this, "User with UID not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(login.this, "User with UID not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if necessary
                Toast.makeText(login.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvRegister = findViewById(R.id.tvRegister);
        layoutSignUp = findViewById(R.id.layout_sign_up);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        checkPassword = findViewById(R.id.checkPassword);
        chkGhinho = findViewById(R.id.chkGhinho);
        tvGuest = findViewById(R.id.tvGuest);
        accounts = new ArrayList<>();
    }

    private void refreshAutoCompleteTextView() {
        dbHelper = new MyDatabaseHelper(login.this);
        accounts = dbHelper.getAccounts();
        AccountAdapter accountAdapter = new AccountAdapter(this, R.layout.auto_fill, accounts);
        edtEmail.setAdapter(accountAdapter);
    }

//    public void checkEmail(View v) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//    }
}