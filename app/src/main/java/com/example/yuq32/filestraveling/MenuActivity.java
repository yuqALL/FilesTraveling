package com.example.yuq32.filestraveling;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.yuq32.filestraveling.utils.WifiUtils;
import com.example.yuq32.filestraveling.view.AVLoadingIndicatorDialog;
import com.example.yuq32.filestraveling.view.AcceptFile;
import com.example.yuq32.filestraveling.view.ScanFiles;
import com.xys.libzxing.zxing.activity.CaptureActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //发送文件
        findViewById(R.id.send_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openFileExplorerIntent = new Intent(MenuActivity.this, ScanFiles.class);
                startActivityForResult(openFileExplorerIntent, 1);

            }
        });

        //保存文件
        findViewById(R.id.save_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(MenuActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

        //连接我的电脑
        findViewById(R.id.conn_pc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //打开设置
        findViewById(R.id.set_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AVLoadingIndicatorDialog dialog=new AVLoadingIndicatorDialog(MenuActivity.this);
                dialog.setMessage("Loading");
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //dialog.cancel();
                        dialog.cancelDialog();
                    }
                },2000);
            }
        });

    }

    //双击退出
    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Snackbar.make(this.getWindow().getDecorView().getRootView(), "再按一次退出", Snackbar.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            WifiUtils w=new WifiUtils(this);
            if(w.isWifiApEnabled()){
                w.closeWifiAp();
            }
            finish();
        }

    }

    //activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            final String scanResult = bundle.getString("result");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("是否接受文件：" + scanResult);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //这里可以跳转界面连接wifi，接受文件了
                    Intent intent = new Intent(MenuActivity.this, AcceptFile.class);
                    intent.putExtra("info", scanResult);
                    startActivity(intent);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "点击了取消按钮", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog mAlertDialog = builder.create();
            mAlertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //菜单点击处理
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.download:
                Intent intent = new Intent(MenuActivity.this, AcceptFile.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
