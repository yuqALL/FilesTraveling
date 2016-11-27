package com.example.yuq32.filestraveling.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.yuq32.filestraveling.R;
import com.example.yuq32.filestraveling.utils.FileDown;
import com.example.yuq32.filestraveling.utils.SplitInfo;
import com.example.yuq32.filestraveling.utils.WifiUtils;
import com.example.yuq32.filestraveling.utils.saveXML;
import com.example.yuq32.filestraveling.viewLibrary.RecylerListAdapter;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AcceptFile extends AppCompatActivity {

    RecyclerView recyclerView;
    //文件信息---ip 文件名 etc
    Map<String, String> infoMap;
    String info;
    //打开wifi线程
    openWifiThread thread;
    AVLoadingIndicatorDialog dialog;
    boolean mflag = true;  //用来停止线程---好像没用。。。
    WifiUtils mWifiUtils;//调用wifi的一些函数在这个类里
    //适配器
    private RecylerListAdapter recylerListAdapter;

    saveXML save_file_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_file);
        Intent intent = this.getIntent();
        info = intent.getStringExtra("info");  //得到接受的文件的信息及其他连接信息
        init();  //初始化  ---连接指定的wifi热点（热点名及密码从info中获得）
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        getSupportActionBar().setTitle("下载文件");  //设置标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //添加返回键

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);   //布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //setHasFixedSize()方法用来使RecyclerView保持固定的大小
        recyclerView.setHasFixedSize(true);


        /**
         * adapter initialization
         */
        recylerListAdapter = new RecylerListAdapter();
        recyclerView.setAdapter(recylerListAdapter);
        loadDownFileData();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //初始化--连接wifi--保存基本信息
    public void init(){
        infoMap = SplitInfo.mapInfo(info);
        mflag=true;
        if(infoMap!=null) {
            /*save info*/
            //save_file_info=new saveXML(infoMap);
            mWifiUtils = new WifiUtils(this);
            if (mWifiUtils.isWifiApEnabled()) {
                mWifiUtils.closeWifiAp();
            }
            if (!mWifiUtils.isWifiEnabled()) {
                openDialog();
                thread = new openWifiThread();
                thread.start();
            } else {
                Log.d("wifiName", mWifiUtils.getConnectInfo().getSSID().toString());
                Log.d("wifiName2", infoMap.get("wifiName"));
                if (!mWifiUtils.getConnectInfo().getSSID().equals("\"" + infoMap.get("wifiName") + "\"")) {
                    openDialog();
                    thread = new openWifiThread();
                    thread.start();
                }
            }
        }
    }
    //下载列表
    private void loadDownFileData() {
        List<FileDown> fileDowns = new ArrayList<>();
        //解析xml文档
//        InputStream is=null;
//        List<Map<String, String>> files=null;
//        try{
//            //获取读取文件的输入流对象
//            is=getAssets().open("/mnt/sdcard/filestraveling/loadfileslist.xml");
//            //采用dom解析
//            if(save_file_info!=null) {
//               files = save_file_info.parseXML(is);
//            }
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        if(files!=null||files.size()!=0){
//            for(int i=0;i<files.size();i++){
//                fileDowns.add(new FileDown(i+1,files.get(i).get("fileName"),100));//100代表下载所需的秒数
//            }
//        }
//        if(info!=null){
//            if(infoMap!=null){
//                fileDowns.add(new FileDown(1,infoMap.get("fileName"),infoMap.get("deviceName"),100));
//            }
//        }

        fileDowns.add(new FileDown(1, "file 1","owner 1", 81));
        fileDowns.add(new FileDown(2, "file 2","owner 2", 295));
        fileDowns.add(new FileDown(3, "file 3","owner 3", 264));
        fileDowns.add(new FileDown(4, "file 4","owner 4", 23));
        fileDowns.add(new FileDown(5, "file 5","owner 5", 297));

        recylerListAdapter.setFileDownList(fileDowns);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.down_menu, menu);
        return true;
    }

    //菜单点击处理
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.con_wifi:
                //通过二维码获取信息并连接wifi
                Intent openCameraIntent = new Intent(AcceptFile.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;
        }
        return true;
    }

    //打开对话框
    public void openDialog() {
        dialog = new AVLoadingIndicatorDialog(AcceptFile.this);
        dialog.setMessage("正在连接WIFI...");
        dialog.show();
    }

    //处理
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x13:  //连接指定wifi成功
                    if(dialog.isShowing()){
                        dialog.cancel();
                    }
                    mflag = false;
                    break;
                case 0x14:  //连接失败
                    //dialog.cancel();
                    break;
                case 0x15:  //连接wifi失败--找不到对方热点
                    if(dialog.isShowing()){
                        dialog.cancel();
                    }
                    Toast.makeText(AcceptFile.this,"网络连接出错",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //打开wifi并连接至指定热点的线程
    public class openWifiThread extends Thread {

        public void run() {
            if (mflag) {
                mWifiUtils = new WifiUtils(AcceptFile.this);
                //打开wifi
                if (!mWifiUtils.isWifiEnabled()) {

                    mWifiUtils.openWifi();//打开wifi需要一定的时间
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {

                    }
                }

                if (mWifiUtils.isWifiEnabled()) {

                    if (infoMap.get("wifiName") == null || infoMap.get("wifiPSW") == null) {
                        Toast.makeText(AcceptFile.this, "获取WIFI名或密码出错", Toast.LENGTH_SHORT).show();
                    }
                    if(mWifiUtils.ConnectToWifi(infoMap.get("wifiName"), infoMap.get("wifiPSW"))){
                        //通过热点名及密码连接wifi --成功返回true
                    }else{
                        Message m = new Message();
                        m.what = 0x15;
                        handler.sendMessage(m);
                    }
                }
//                while (true) {
                    try {
                        Thread.sleep(5000);
                        Log.d("wifiName", mWifiUtils.getConnectInfo().getSSID().toString());
                        Log.d("wifiName--info:", infoMap.get("wifiName"));
                        //判断是否连接到指定wifi
                        if (mWifiUtils.getConnectInfo().getSSID().toString().equals("\"" + infoMap.get("wifiName") + "\"")) {
                            Message m = new Message();
                            Log.d("wifiName", mWifiUtils.getConnectInfo().getSSID().toString());
                            Log.d("wifiName--info:", infoMap.get("wifiName"));
                            m.what = 0x13;
                            mflag = false;
                            handler.sendMessage(m);
//                            break;
                        } else {
//                            Log.d("wifiName", mWifiUtils.getConnectInfo().getSSID().toString());
//                            Log.d("wifiName--info:", infoMap.get("wifiName"));
//                            if(mWifiUtils.ConnectToWifi(infoMap.get("wifiName"), infoMap.get("wifiPSW"))){
//
//                            }else{
//                                Message m = new Message();
//                                m.what = 0x15;
//                                handler.sendMessage(m);
//                            }
                            Message m = new Message();
                            m.what = 0x14;
                            handler.sendMessage(m);
//                            break;
                        }
                    } catch (Exception e) {

                    }

//                }

            }

        }

    }

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
                   info=scanResult;
                   init();
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

}
