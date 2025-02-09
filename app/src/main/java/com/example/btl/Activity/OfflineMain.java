package com.example.btl.Activity;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.btl.Adapter.OfflineAdapter;
import com.example.btl.R;
import com.example.btl.models.Detail;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class OfflineMain extends AppCompatActivity {

    int image[]={R.drawable.back_ly_thuyet,R.drawable.back_ly_thuyet,R.drawable.back_ly_thuyet,R.drawable.back_ly_thuyet,R.drawable.back_ly_thuyet,R.drawable.back_ly_thuyet};
    ArrayList<String> title;
    ArrayList<Detail> Detail;
    OfflineAdapter myadapter;
    ListView lv;
    SQLiteDatabase db;
    Button btnLogin;
    private Handler handler;
    private Runnable runnable;
    private boolean check = true;
    private static final long INTERVAL = 5000; // 5 giây


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offline_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfflineMain.this, login.class);
                startActivity(intent);
                OfflineMain.this.finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        title = new ArrayList<String>();
        title.add("Lý thuyết Android buổi 1");
        title.add("Lý thuyết Android buổi 2");
        title.add("Lý thuyết Android buổi 3");
        title.add("Lý thuyết Android buổi 4");
        title.add("Lý thuyết Android buổi 5");
        title.add("Lý thuyết Android buổi 6");

        lv=findViewById(R.id.lv);
        Detail=new ArrayList<Detail>();
        for (int i=0;i<title.size();i++){
            Detail.add(new Detail(title.get(i), image[i]));
        }
        Log.d("MainActivity", "Check" + Detail.get(0).getTitle());
        myadapter=new OfflineAdapter(OfflineMain.this,Detail);
        lv.setAdapter(myadapter);

        db = openOrCreateDatabase("mydatabase.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Detail (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT)");
        insertDB();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy đối tượng Detail tương ứng với vị trí được bấm
                Detail detail =(Detail) myadapter.getItem(position);

                // Tạo Intent để mở một trang mới và chuyển dữ liệu
                Intent intent = new Intent(OfflineMain.this, Offline.class);
                intent.putExtra("title", detail.getTitle()); // Truyền tiêu đề vào Intent
                intent.putExtra("image", detail.getImage()); // Truyền hình ảnh vào Intent
                startActivity(intent);
            }
        });

        MaterialButton btnOpen = findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị hộp thoại
                showAlertDialog();
            }
        });
        vonglaptuthan();
    }

//    private void showAlertDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//
////        builder.setTitle("Thông báo");
////        builder.setMessage("Co wifi roi dmm! m co muon sang form login ko con cho?");
//
//        View vuto = getLayoutInflater().inflate(R.layout.dialog, null);
//
//        Button btnCancel = vuto.findViewById(R.id.btnCancel);
//        Button btnConfirm = vuto.findViewById(R.id.btnConfirm);
//
//        builder.setView(vuto);
//
//        AlertDialog alertDialog = builder.create();
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//
//        btnConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//
//            public void onClick(View v) {
//
//                alertDialog.dismiss();
//
//                Intent intent = new Intent(MainActivity.this, Login.class);
//                startActivity(intent);
//                MainActivity.this.finish();
//            }
//        });
//
////        AlertDialog alertDialog = builder.create();
//
//    }

    public void insertDB() {
        try {
            for (int i = 0; i < title.size(); i++) {
                db.execSQL("INSERT INTO Detail(title) VALUES('" + title.get(i) + "')");
            }
            Log.d("MainActivity", "Insert success");
        } catch (Exception e) {
            Log.d("MainActivity", "Chưa thêm được dữ liệu vào DB");
            return;
        }
    }

    //    public ArrayList<Detail> getDB() {
//        ArrayList<Detail> list = new ArrayList<Detail>();
//        try {
//            Cursor c = db.rawQuery("SELECT * FROM Detail", null);
//            while (c.moveToNext()) {
//                String title = c.getString(1);
//                list.add(new Detail(title, 0));
//            }
//            c.close();
//        } catch (Exception e) {
//            Log.d("MainActivity", "Không lấy được dữ liệu từ DB");
//        }
//        return list;
//    }
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

        View vinh = getLayoutInflater().inflate(R.layout.move_login_dialog, null);

        Button btnCancel = vinh.findViewById(R.id.btnCancel);
        Button btnConfirm = vinh.findViewById(R.id.btnConfirm);

        builder.setView(vinh);

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

                Intent intent = new Intent(OfflineMain.this, login.class);
                startActivity(intent);
                OfflineMain.this.finish();
            }
        });

//        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}