package com.example.btl.Activity;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.btl.R;
import com.example.btl.fragment.ChangePasswordFragment;
import com.example.btl.fragment.Home;
import com.example.btl.fragment.MyProfileFragment;
import com.example.btl.fragment.Settings;
import com.example.btl.models.Result;
import com.example.btl.models.SV;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class main_sv extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvHello;
    EditText edtSearch;
    Button btnSearch;
    SimpleDateFormat dateFormat;
    BottomNavigationView bottomNav;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView imgAvatar;
    TextView tvName, tvEmail, tvMsv;
    SV sv = new SV();
    Intent intent;
    boolean doubleBackPressed = false;
    public static final int MY_REQUEST_CODE = 10;
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_FAVORITE = 1;
    private static final int FRAGMENT_HISTORY = 2;
    private static final int FRAGMENT_MY_PROFILE = 3;
    private static final int FRAGMENT_CHANGE_PASSWORD = 4;
    private int currentFragment = FRAGMENT_HOME;
    private final MyProfileFragment myProfileFragment = new MyProfileFragment();
    final private ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent == null) {
                    return;
                }

                Uri uri = intent.getData();
                myProfileFragment.setmUri(uri);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    myProfileFragment.setBitmapImageView(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sv);

        init();

//        sv = findSV(new SVCallback() {
//            @Override
//            public SV onSVFound(SV s) {
//                return s;
////                Log.d("Kiểm tra sinh viên", sv.toString());
//            }
//
//            @Override
//            public SV onSVNotFound() {
//                return new SV();
//            }
//        });



        Log.d("Kiểm tra sinh viên chỗ 1", sv.toString());

//        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
//                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f);
//        animation.setDuration(8000); // 5 giây
//        animation.setRepeatCount(Animation.INFINITE); // Lặp vô hạn
//        tvHello.startAnimation(animation);
//        TranslateAnimation animation = new TranslateAnimation(1500.0f, -1500.0f, 0.0f, 0.0f); // new TranslateAnimation (float fromXDelta,float toXDelta, float fromYDelta, float toYDelta)
//
//        animation.setDuration(7500); // animation duration, change accordingly
//        animation.setRepeatCount(1); // animation repeat count
//        animation.setFillAfter(false);
//        tvHello.startAnimation(animation);//your_view for which you need animation


//        timer = new Timer(5, e -> {
//            posX -= 1;
//            if (posX < -tvHello.getWidth())
//                posX = tvHello.getWidth();
//            lbNorth.setLocation(posX, tvHello.getY());
//        });

//        Animation shake = AnimationUtils.loadAnimation(main_sv.this, R.anim.shake);
//        tvHello.startAnimation(shake);

//        tvHello.startAnimation(AnimationUtils.loadAnimation(main_sv.this, R.anim.move));

//        TranslateAnimation animation = new TranslateAnimation(1000.0f, -1000.0f, 0.0f, 0.0f); // new TranslateAnimation (float fromXDelta,float toXDelta, float fromYDelta, float toYDelta)
//        animation.setDuration(3500); // animation duration
//        animation.setRepeatCount(-1); // animation repeat count
//        animation.setRepeatMode(-1); // repeat animation (left to right, right to left)
//
//        animation.setFillAfter(true);
//        tvHello.startAnimation(animation);//your_view for mine is imageView
//
//        ViewPagerAdapter adapter_pager = new ViewPagerAdapter(this);
//        viewPager.setAdapter(adapter_pager);
//
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                if (position == 1) {
//                    bottomNav.getMenu().findItem(R.id.nav_settings).setChecked(true);
//                } else {
//                    bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
//                }
//            }
//        });

//        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                if (item.getItemId() == R.id.nav_home) {
//                    viewPager.setCurrentItem(0);
//                } else {
//                    viewPager.setCurrentItem(1);
//                }
//                return true;
//            }
//        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new Home(sv));

        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        onBackPress();

        showInfo();

    }
    public interface SVCallback {
        SV onSVFound(SV sv);
        SV onSVNotFound();
    }
//    private SV findSV(SVCallback callback) {
//        String uid = "k4ds2Dv1IOTeBdy7g4mxug4BanQ2";
//
//        if (intent != null) {
//            uid = intent.getStringExtra("uid");
//        }
//
//        Log.d("Kiểm tra uid", uid);
//
//        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
//        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    // User with the given UID exists
//                    String uid = dataSnapshot.getKey();
//                    String name = dataSnapshot.child("name").getValue(String.class);
//                    String email = dataSnapshot.child("email").getValue(String.class);
//                    String msv = dataSnapshot.child("msv").getValue().toString();
//                    SV s = new SV(msv, name, email);
//                    Log.d("Kiểm tra sinh viên ở ondatachang findsv", s.toString());
//                    callback.onSVFound(s);
//                } else {
//                    // User with the given UID does not exist
//                    callback.onSVNotFound();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle any errors
//                Toast.makeText(main_sv.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        return new SV();
//    }


    private CompletableFuture<SV> findSV() {
        CompletableFuture<SV> future = new CompletableFuture<>();

        String uid = "k4ds2Dv1IOTeBdy7g4mxug4BanQ2";

        if (intent != null) {
            uid = intent.getStringExtra("uid");
        } else {
            finish();
        }

        Log.d("Kiểm tra uid", uid);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String uid = dataSnapshot.getKey();
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String msv = dataSnapshot.child("msv").getValue().toString();
                    ArrayList<Result> results = new ArrayList<>();
                    for (Object o : dataSnapshot.child("exams").getChildren()) {
                        DataSnapshot data = (DataSnapshot) o;
                        String examId = data.getKey();
                        if (examId.equals("fake")) {
                            continue;
                        }
                        Double score = Double.valueOf(data.child("score").getValue().toString());
                        long time = Long.parseLong(data.child("totalTime").getValue().toString());
                        Result res = new Result(examId, score, time);
                        results.add(res);
                        Log.d("kiểm tra result mainsv", results.size() + "");
                    }
                    SV s = new SV(msv, name, email, results);
                    future.complete(s);
                } else {
                    future.complete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });

        return future;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle the home action
            if (currentFragment != FRAGMENT_HOME) {
                currentFragment = FRAGMENT_HOME;
                if (sv == null) {
                    Toast.makeText(this, "Không tìm thấy thông tin sinh viên", Toast.LENGTH_SHORT).show();
                }
                Log.d("Kiểm tra sinh viên", sv.toString());
                replaceFragment(new Home(sv));
            }
        } else if (id == R.id.nav_fovorite) {
            // Handle the gallery action
            if (currentFragment != FRAGMENT_FAVORITE) {
                currentFragment = FRAGMENT_FAVORITE;
                replaceFragment(new Settings());
            }
        } else if (id == R.id.nav_history) {
            // Handle the slideshow action
            if (currentFragment != FRAGMENT_HISTORY) {
                currentFragment = FRAGMENT_HISTORY;
                replaceFragment(new Home());
            }
        } else if (id == R.id.nav_my_profile) {
            // Handle the tools action
            if (currentFragment != FRAGMENT_MY_PROFILE) {
                currentFragment = FRAGMENT_MY_PROFILE;
                replaceFragment(myProfileFragment);
            }
        } else if (id == R.id.nav_change_password) {
            // Handle the share action
            if (currentFragment != FRAGMENT_CHANGE_PASSWORD) {
                currentFragment = FRAGMENT_CHANGE_PASSWORD;
                replaceFragment(new ChangePasswordFragment());
            }
        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(main_sv.this, login.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // content_frame is the id of the FrameLayout in activity_main.xml
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    public void onBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Handle the back button event
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public void init() {
        navigationView = findViewById(R.id.navigationView);
        tvHello = findViewById(R.id.tvHello);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        drawerLayout = findViewById(R.id.drawerLayout);
        imgAvatar = navigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tvEmail);
        tvMsv = navigationView.getHeaderView(0).findViewById(R.id.tvMsv);
        intent = getIntent();

        CompletableFuture<SV> future = findSV();
        future.thenAccept(result -> {
            // Sử dụng kết quả (đối tượng SV) khi tìm thấy
            if (result != null) {
                sv = result;
                // ...
            } else {
                // Xử lý trường hợp không tìm thấy SV
                // ...
            }
        }).exceptionally(e -> {
            // Xử lý các ngoại lệ xảy ra trong quá trình thực hiện
            // ...
            return null;
        });

//        viewPager = findViewById(R.id.viewPager);
//        bottomNav = findViewById(R.id.bottomNav);
    }

    public void showInfo() {
        // Set the avatar, name, and email of the user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name == null) {
            tvName.setText("Họ và tên:");
        } else {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(name);
        }

        tvEmail.setText(email);
        Glide.with(this).load(photoUrl)
                .error(R.drawable.user_clone)
                .into(imgAvatar);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference().child("users");

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
//                    Log.d("Kiểm tra snapshot", snapshot.getValue());
                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                    String msv = dataSnapshot.child("msv").getValue().toString();
                    String nameSV = dataSnapshot.child("name").getValue().toString();
//                    Log.d("Kiểm tra snapshot", dataSnapshot.getValue().toString());
////                    String msv = snapshot.getValue().child("msv").getValue().toString();
                    tvMsv.setText(msv);
                    tvName.setText(nameSV);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvMsv.setText("Mã sinh viên:");
            }
        });
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}