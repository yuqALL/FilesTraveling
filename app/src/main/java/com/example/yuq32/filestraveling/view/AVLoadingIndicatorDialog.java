package com.example.yuq32.filestraveling.view;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuq32.filestraveling.R;
import com.example.yuq32.filestraveling.utils.SplitInfo;
import com.example.yuq32.filestraveling.utils.WifiUtils;

import java.util.Map;

public class AVLoadingIndicatorDialog extends AlertDialog {

    public static String OPEN_WIFI_AP="open_wifi_ap";
    public static String OPEN_WIFI="open_wifi";
    private TextView mMessageView;
    private WifiUtils w;
    private Map<String,String> infoMap;
private Context mContext;
    public AVLoadingIndicatorDialog(Context context) {
        super(context);
        mContext=context;
        View view=LayoutInflater.from(getContext()).inflate(R.layout.progress_avld,null);
        mMessageView= (TextView) view.findViewById(R.id.message);
        setView(view);
    }

    public AVLoadingIndicatorDialog(Context context,String mes) {
        super(context);
        mContext=context;
        View view=LayoutInflater.from(getContext()).inflate(R.layout.progress_avld,null);
        mMessageView= (TextView) view.findViewById(R.id.message);
        setView(view);
        switchMes(mes);
    }

    @Override
    public void setMessage(CharSequence message) {
        mMessageView.setText(message);
    }

    public void switchMes(String mes){
        if(OPEN_WIFI_AP.equals(mes)){
            w=new WifiUtils(mContext);

        }else if(OPEN_WIFI.equals(mes)){
            w=new WifiUtils(mContext);
            if(w.isWifiApEnabled()){
                w.closeWifiAp();
            }
            if(w.isWifiEnabled()){
                cancel();
            }
            //w.openWifi();

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelDialog();
            }
        },5000);
    }

    public void cancelDialog(){
        this.cancel();
    }

    public  Map<String,String> spitMesInfo(String info){
        Map<String,String> infoMap= SplitInfo.mapInfo(info);
        return infoMap;
    }
}
