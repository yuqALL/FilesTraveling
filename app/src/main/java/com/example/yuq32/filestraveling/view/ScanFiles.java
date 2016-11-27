package com.example.yuq32.filestraveling.view;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yuq32.filestraveling.R;
import com.example.yuq32.filestraveling.utils.WifiUtils;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class ScanFiles extends AppCompatActivity {

    private ActionBar mActionBar;
    private ListView list_files;
    private WifiUtils mWifiUtils;
    private String wifiSSID = null;
    private String wifiPSW = null;
    // 记录当前的父文件夹
    File currentParent;
    // 记录当前路径下的所有文件的文件数组
    File[] currentFiles;

    ImageView qrImgImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_files);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mWifiUtils = new WifiUtils(this);
        if(wifiSSID==null||wifiPSW==null){
            wifiSSID = "FilesTraveling";
            wifiPSW = "testfilesyq";

            mWifiUtils.openWifiAP(wifiSSID, wifiPSW);
        }

        list_files = (ListView) findViewById(R.id.list_files);
        //获取系统的SD卡的目录
        File root = Environment.getExternalStorageDirectory();
        //如果 SD卡存在
        if (root.exists()) {
            currentParent = root;
            currentFiles = root.listFiles();
            //使用当前目录下的全部文件、文件夹来填充ListView
            inflateListView(currentFiles);
        }

        // 为ListView的列表项的单击事件绑定监听器
        list_files.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 用户单击了文件，直接返回，不做任何处理
                if (currentFiles[position].isFile()) {
                    if(!mWifiUtils.isWifiApEnabled()){
                        mWifiUtils.openWifiAP(wifiSSID,wifiPSW);
                    }
                    //得到文件基本信息，以及wifi信息
                    Map<String, String> mapInfo = ScanFiles.this.fileInfo(currentFiles[position]);
                    //配置二维码信息
                    StringBuilder contentString = new StringBuilder();
                    contentString.append(mapInfo.get("fileName") + "\n");
                    contentString.append(mapInfo.get("filePath") + "\n");
                    contentString.append(mapInfo.get("fileType") + "\n");
                    contentString.append(mapInfo.get("Ip") + "\n");
                    contentString.append(mapInfo.get("port") + "\n");
                    contentString.append(mapInfo.get("wifiName") + "\n");
                    contentString.append(mapInfo.get("wifiPSW") + "\n");
                    contentString.append(mapInfo.get("deviceName") + "\n");
                    String mes = contentString.toString();
                    if (!mes.equals("")) {
                        //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                        Bitmap qrCodeBitmap = EncodingUtils.createQRCode(mes, 350, 350,
                                true ?
                                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) :
                                        null);
                        //创建提示框展示二维码
                        Dialog mDialog = new Dialog(ScanFiles.this);
                        View layout = mDialog.getLayoutInflater().inflate(R.layout.m_pic_dialog, null);
                        mDialog.getWindow().setContentView(layout);

                        mDialog.setTitle("请对方扫描二维码");

                        qrImgImageView = (ImageView) layout.findViewById(R.id.m_pic_dialog);
                        qrImgImageView.setImageBitmap(qrCodeBitmap);

                        mDialog.show();
                    } else {
                        Toast.makeText(ScanFiles.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
                    }
                }

                // 获取用户点击的文件夹下的所有文件
                File[] tmp = currentFiles[position].listFiles();
                if (tmp == null || tmp.length == 0) {

                } else {
                    //获取用户单击的列表项对应的文件夹，设为当前的父文件夹
                    currentParent = currentFiles[position];
                    //保存当前的父文件夹内的全部文件和文件夹
                    currentFiles = tmp;
                    // 再次更新ListView
                    inflateListView(currentFiles);
                }
            }
        });

        // 获取上一级目录的按钮
        LinearLayout parent = (LinearLayout) findViewById(R.id.return_recent_path);

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View source) {

                if (!currentParent.getAbsolutePath().equals("/")) {
                    // 获取上一级目录
                    currentParent = currentParent.getParentFile();
                    // 列出当前目录下所有文件
                    currentFiles = currentParent.listFiles();
                    // 再次更新ListView
                    inflateListView(currentFiles);
                }

            }
        });

        Toast.makeText(this,mWifiUtils.getDhcIP(),Toast.LENGTH_LONG).show();
    }

    //展示文件列表
    private void inflateListView(File[] files) {
        // 创建一个List集合，List集合的元素是Map
        ArrayList<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < files.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            //如果当前File是文件夹，使用folder图标；否则使用file图标
            if (files[i].isDirectory()) {
                listItem.put("img", R.drawable.f);
            } else {
                File f = files[i];
                int img = fileTypeCheck(f);
                listItem.put("img", img);

            }
            listItem.put("title", files[i].getName());
            listItem.put("info", files[i].getAbsolutePath());
            //添加List项
            listItems.add(listItem);
        }

        //适配器
        FilesItemAdapter adapter = new FilesItemAdapter(this, listItems);

        // 为ListView设置Adapter
        list_files.setAdapter(adapter);
        try {
            mActionBar.setTitle(currentParent.getCanonicalPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //文件类型
    public int fileTypeCheck(File f) {
        String name = f.getName();
        if (name.endsWith(".png") ||
                name.endsWith(".ico") || name.endsWith(".gif")) {
            return R.drawable.png;
        } else if (name.endsWith(".jpg")) {
            return R.drawable.jpg;
        } else if (name.endsWith(".mp4") || name.endsWith("rmvb")) {
            return R.drawable.mpg;
        } else if (name.endsWith(".mp3")) {
            return R.drawable.mp3;
        } else if (name.endsWith(".ai")) {
            return R.drawable.ai;
        } else if (name.endsWith(".pdf")) {
            return R.drawable.pdf;
        } else if (name.endsWith(".doc")) {
            return R.drawable.doc;
        } else if (name.endsWith(".ppt") || name.endsWith(".pptx")) {
            return R.drawable.ppt;
        } else if (name.endsWith(".txt") || name.endsWith(".text")) {
            return R.drawable.txt;
        } else if (name.endsWith(".psd")) {
            return R.drawable.psd;
        } else if (name.endsWith(".xls")) {
            return R.drawable.xls;
        } else if (name.endsWith(".zip")) {
            return R.drawable.zip;
        } else if (name.endsWith(".xml")) {
            return R.drawable.xml;
        } else if (name.endsWith(".html")) {
            return R.drawable.html;
        } else {
            return R.drawable.generic_document;
        }
    }


    //基本信息
    public Map<String, String> fileInfo(File f) {

        Map<String, String> mapFileInfo = new HashMap<>();
        mapFileInfo.put("fileName", f.getName());
        mapFileInfo.put("filePath", f.getAbsolutePath());
        if(f.getName().lastIndexOf(".")!=-1){
            mapFileInfo.put("fileType", f.getName().substring(f.getName().lastIndexOf(".")));
        }else{
            mapFileInfo.put("fileType", "null");
        }

        //得到热点ip
        String ip = mWifiUtils.getDhcIP();
        if (ip == null) {
            ip = "127.0.0.1";
        }
        mapFileInfo.put("Ip", ip);
        //端口固定
        mapFileInfo.put("port", "5000");
            mapFileInfo.put("wifiName", wifiSSID);
            mapFileInfo.put("wifiPSW", wifiPSW);

        //得到设备名
        mapFileInfo.put("deviceName", new Build().MODEL);
        return mapFileInfo;
    }

    //生成随机字符串
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    //菜单点击处理
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
