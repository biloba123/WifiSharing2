package com.lvqingyang.wifisharing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import frame.tool.MyToast;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private android.widget.Button btninfo;
    private android.widget.Button btnsucc;
    private android.widget.Button btnerror;
    private android.widget.Button btnwarn;
    private android.widget.Button btnloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        this.btnloading = (Button) findViewById(R.id.btn_loading);
        this.btnwarn = (Button) findViewById(R.id.btn_warn);
        this.btnerror = (Button) findViewById(R.id.btn_error);
        this.btnsucc = (Button) findViewById(R.id.btn_succ);
        this.btninfo = (Button) findViewById(R.id.btn_info);

        btninfo.setOnClickListener(this);
        btnsucc.setOnClickListener(this);
        btnerror.setOnClickListener(this);
        btnwarn.setOnClickListener(this);
        btnloading.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_info:
                Toast toast=Toast.makeText(this, "完全不一样", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);

                View v= getLayoutInflater().inflate(R.layout.toast,null);
                ImageView ivIcon=v.findViewById(R.id.iv_icon);

                toast.setView(v);
                toast.show();
                break;
            case R.id.btn_succ:
                MyToast.success(this, "操作成功！");
                break;
            case R.id.btn_error:
                MyToast.error(this, "出错了...");
                break;
            case R.id.btn_warn:
                MyToast.warning(this, "你不能这样子");
                break;
            case R.id.btn_loading:
                MyToast.loading(this, "加载中。。。");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            MyToast.cancel();
                        }
                    }
                }).start();
                break;
        }
    }
}
