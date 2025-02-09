package com.example.btl.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordFragment extends Fragment {

    private View mView;

    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;
    private ProgressBar progressBar;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_change_password, container, false);

        initUI();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = edtOldPassword.getText().toString().trim();
                String newPassword = edtNewPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();

                // Validate old password
                if (oldPassword.isEmpty()) {
                    edtOldPassword.setError("Old password is required");
                    edtOldPassword.requestFocus();
                    return;
                }

                // Validate new password
                if (newPassword.isEmpty()) {
                    edtNewPassword.setError("New password is required");
                    edtNewPassword.requestFocus();
                    return;
                }

                String passPattern = "^[a-zA-Z0-9]{6,}$";
                Pattern pattern = Pattern.compile(passPattern);
                Matcher matcher = pattern.matcher(newPassword);

                if (!matcher.matches()) {
                    edtNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự và chỉ chứa chữ cái và số");
                    edtNewPassword.requestFocus();
                    return;
                }

                // Validate confirm password
                if (confirmPassword.isEmpty()) {
                    edtConfirmPassword.setError("Confirm password is required");
                    edtConfirmPassword.requestFocus();
                    return;
                }

                // Validate new password and confirm password
                if (!newPassword.equals(confirmPassword)) {
                    edtConfirmPassword.setError("Password does not match");
                    edtConfirmPassword.requestFocus();
                    return;
                }
                // Show progress bar
                progressBar.setVisibility(View.VISIBLE);

                onClickChangePassword(oldPassword, newPassword);
            }
        });

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        if (getActivity() != null) {
            getActivity().setTitle("Change Password");
        }
    }

    private void onClickChangePassword(String oldPassword, String newPassword) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("ChangePasswordFragment", "User re-authenticated.");
                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failure to change password", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                edtOldPassword.setError("Old password is incorrect");
                edtOldPassword.requestFocus();
            }
        });
    }

    private void initUI() {
        edtOldPassword = mView.findViewById(R.id.edtOldPassword);
        edtNewPassword = mView.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = mView.findViewById(R.id.edtConfirmPassword);
        btnChangePassword = mView.findViewById(R.id.btnChangePassword);
        progressBar = mView.findViewById(R.id.progressBar);
    }
}
