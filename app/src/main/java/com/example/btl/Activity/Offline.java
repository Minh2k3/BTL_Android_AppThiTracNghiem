package com.example.btl.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl.R;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Offline extends AppCompatActivity {
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;
    String DATABASE_NAME="lyThuyet.db";
    ImageView imageView;
    TextView textView,tvContent;
    private Handler handler;
    private Runnable runnable;
    private boolean check = true;
    private static final long INTERVAL = 5000; // 5 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offline);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.offline), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView = findViewById(R.id.title);
        tvContent=findViewById(R.id.tvContent);
        imageView = findViewById(R.id.image);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("title");
            int image = extras.getInt("image");

            // Đặt tiêu đề và hình ảnh vào các thành phần giao diện
            textView.setText(title);
            imageView.setImageResource(image);
        }
        vonglaptuthan();

//        Context context = Offline.this;
//
//        context.deleteDatabase(DATABASE_NAME);

        fillTvContent();
    }

    private void fillTvContent() {
        processCopy();
        database = openOrCreateDatabase(DATABASE_NAME ,MODE_PRIVATE,null);
        Cursor c = database.query("lythuyet",null,null,null,null,null,null,null);
        c.moveToFirst();
        while (c.isAfterLast()==false){
            if(textView.getText().equals(c.getString(0))){
                tvContent.setText(c.getString(1));
            }
            c.moveToNext();
        }
        c.close();
    }

    private void vonglaptuthan() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (check) {
                    Log.d("Check 5s", "Có chạy");
                    checkConnectivity();
                    handler.postDelayed(this, INTERVAL);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        check = true;
        handler.postDelayed(runnable, INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        check = false;
        handler.removeCallbacks(runnable);
    }

    private void checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        Log.d("Check bắt mạng", "Có vào chỗ này");

        if (isConnected) {
            Log.d("Check bắt mạng", "Có vào đây");
            // Có kết nối internet
            // Hiển thị dialog và chuyển đổi view nếu cần
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

//        builder.setTitle("Thông báo");
//        builder.setMessage("Co wifi roi dmm! m co muon sang form login ko con cho?");

        View vuto = getLayoutInflater().inflate(R.layout.move_login_dialog, null);

        Button btnCancel = vuto.findViewById(R.id.btnCancel);
        Button btnConfirm = vuto.findViewById(R.id.btnConfirm);

        builder.setView(vuto);

        AlertDialog alertDialog = builder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                check = false;
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                alertDialog.dismiss();

                Intent intent = new Intent(Offline.this, login.class);
                startActivity(intent);
                Offline.this.finish();
            }
        });

//        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void processCopy() {
//private app
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())
        {
            try{CopyDataBaseFromAsset();
                Toast.makeText(this, "Copying sucess from Assets folder",
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }
    public void CopyDataBaseFromAsset() {
// TODO Auto-generated method stub
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
// Path to the just created empty db
            String outFileName = getDatabasePath();
// if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
// Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
// transfer bytes from the inputfile to the outputfile
// Truyền bytes dữ liệu từ input đến output
            int size = myInput.available();
            byte[] buffer = new byte[size];
            myInput.read(buffer);
            myOutput.write(buffer);
// Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}