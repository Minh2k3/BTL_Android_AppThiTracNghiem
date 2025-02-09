package com.example.btl.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.btl.R;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // Độ trễ của splash screen (ms)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setDecorFitsSystemWindowsCompat(window, false);

        // Hiển thị splash screen trong một khoảng thời gian ngắn trước khi chuyển đến màn hình tiếp theo
        new Handler().postDelayed(() -> {
            if (isNetworkAvailable()) {
                // Chuyển đến form login
                Intent intent = new Intent(SplashScreen.this, login.class);
                startActivity(intent);
                finish();
            } else {
                // Chuyển đến form offline
                Intent intent = new Intent(SplashScreen.this, OfflineMain.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }

    private void setDecorFitsSystemWindowsCompat(Window window, boolean decorFitsSystemWindows) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(decorFitsSystemWindows);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

}