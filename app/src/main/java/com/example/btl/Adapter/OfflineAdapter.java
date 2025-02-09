package com.example.btl.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.btl.R;
import com.example.btl.models.Detail;

import java.util.ArrayList;

public class OfflineAdapter extends BaseAdapter {
    Activity context;
    int IdLayout;
    ArrayList<Detail> mylist;
    //tạo constructor

    public OfflineAdapter( Activity context, ArrayList<Detail> mylist) {
        this.context = context;

        this.mylist = mylist;
    }
    public int getCount() {
        return mylist.size();
    }

    @Override
    public Object getItem(int position) {
        return mylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    //    @Override
//    public Object getDetail(int position) {
//        return mylist.get(position);
//    }
//
//    @Override
//    public long getDetailId(int position) {
//        return position;
//    }
    //gọi hàm getView để sắp xếp dữ liệu
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null){
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.list_ly_thuyet,parent,false);
        }
        //lấy 1 phần tử trong mảng
        Detail myDetail=mylist.get(position);
        //khai báo tham chiếu id và hiển thị lên imageview
        ImageView img_Detail=convertView.findViewById(R.id.image);
        img_Detail.setImageResource(myDetail.getImage());
        //khai báo
        TextView tv_title1=convertView.findViewById(R.id.title);
        tv_title1.setText(myDetail.getTitle());
        return convertView;
    }
}
