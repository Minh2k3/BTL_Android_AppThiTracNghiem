package com.example.btl.fragment;

import static com.example.btl.Activity.main_sv.MY_REQUEST_CODE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.btl.R;
import com.example.btl.Activity.main_sv;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MyProfileFragment extends Fragment {

    private View mView;
    private ImageView imgProfile;
    private EditText edtName, edtEmail;
    private Button btnUpdateProfile;
    private ProgressBar progressBar;
    private Uri mUri;
    main_sv svAct;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        initUI();

        svAct = (main_sv) getActivity();

        setUserInformation();

        initListener();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        if (getActivity() != null) {
            getActivity().setTitle("My Profile");
        }
    }

    private void initListener() {
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String name = edtName.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(mUri)
                .build();


        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            svAct.showInfo();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void onClickRequestPermission() {
        if (svAct == null) {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            svAct.openGallery();
        }

        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            svAct.openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permissions, MY_REQUEST_CODE);
        }

    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        edtName.setText(user.getDisplayName());
        edtEmail.setText(user.getEmail());
        if (user.getPhotoUrl() != null) {
            Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.user_clone).into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.user_clone);
        }

    }

    private void initUI() {
        imgProfile = mView.findViewById(R.id.imgProfile);
        edtName = mView.findViewById(R.id.edtName);
        edtEmail = mView.findViewById(R.id.edtEmail);
        btnUpdateProfile = mView.findViewById(R.id.btnUpdateProfile);
        progressBar = mView.findViewById(R.id.progressBar);
    }

    public void setBitmapImageView(Bitmap bitmap) {
        imgProfile.setImageBitmap(bitmap);
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }
}
