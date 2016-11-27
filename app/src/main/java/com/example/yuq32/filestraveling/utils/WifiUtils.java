package com.example.yuq32.filestraveling.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WifiUtils {

    private String TAG="WifiUtils";

    private Context mContext;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> scanResults;
    private List<WifiConfiguration> wifiConfigList;
    private WifiManager.WifiLock mWifiLock;
    //通过WifiLock来锁定wifi网络，使其一直保持连接，
    // 直到这个锁定被释放。如果app需要下载很大的文件，
    // 就需要保持wifi锁，来确保app有足够的时间下载完成。wifi锁不能超越wifi-enabled设置，也没有飞行模式。
    public WifiUtils(Context context){
        mContext = context;

        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();

        Log.v(TAG, "getIpAddress = " + mWifiInfo.getIpAddress());
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public void createWifiLock(){
        // 创建一个wifiLock

        mWifiLock = mWifiManager.createWifiLock("");

    }

    public void acquireWifiLock(){
        // 锁定wifilock

        if(!mWifiLock.isHeld()) {

            mWifiLock.acquire();

        }
    }
    // 解锁wifiLock
public void unAcquireWifiLock(){
    if(mWifiLock.isHeld()) {

        mWifiLock.acquire();

    }

}


    //扫描wifi设备
    public void scanDevices(){
        mWifiManager.startScan();

        try {
            Thread.sleep(5000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //扫描结果
    public List<ScanResult> getScanResults(){
        scanResults= mWifiManager.getScanResults();
        return scanResults;
//        scanResults=clearSameNameDevices(mWifiManager.getScanResults());
//        return scanResults;
    }

    //清除同名项
//    public List<ScanResult> clearSameNameDevices(List<ScanResult> myDevices){
//        List<ScanResult> newDevices=new ArrayList<>();
//        List<ScanResult> temp=new ArrayList<>();
//
//        for(ScanResult wifi:myDevices){
//            String SSID=wifi.SSID.toString();
//            for(ScanResult scanResult:myDevices){
//                if(SSID.equals(scanResult.SSID.toString())){
//                    temp.add(scanResult);
//                }
//            }
//            if(temp.size()>1){
//                ScanResult tempDevice=temp.get(0);
//                //相同名字中找出信号最强的
//                for(int i=0;i<temp.size();i++){
//                    if(tempDevice.level<temp.get(i).level){
//                        tempDevice=temp.get(i);
//                    }
//                }
//                newDevices.add(tempDevice);
//            }else{
//                newDevices.add(wifi);
//            }
//        }
//        return newDevices;
//    }
    //扫描结果SSID
    public ArrayList<String> getScanResultsString(){
        this.getScanResults();
        ArrayList<String> scanResultsString=new ArrayList<>();
        for(ScanResult result:scanResults){
            scanResultsString.add(result.SSID.toString());
            Log.d("wifi_device",result.toString());
        }
        return scanResultsString;
    }

    //通过position得到一个ScanResult
    public ScanResult getScanResult(int position){
        return scanResults.get(position);
    }

    //得到Wifi配置好的信息
    public void getConfiguration(){
        wifiConfigList = mWifiManager.getConfiguredNetworks();//得到配置好的网络信息
        for(int i =0;i<wifiConfigList.size();i++){
            Log.i("getConfiguration",wifiConfigList.get(i).SSID);
            Log.i("getConfiguration",String.valueOf(wifiConfigList.get(i).networkId));
        }
    }

    //判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId
    public int IsConfiguration(String SSID){
        getConfiguration();
        Log.i("IsConfiguration",String.valueOf(wifiConfigList.size()));
        for(int i = 0; i < wifiConfigList.size(); i++){
            Log.i(wifiConfigList.get(i).SSID,String.valueOf( wifiConfigList.get(i).networkId));
            if(wifiConfigList.get(i).SSID.equals(SSID)){
                //地址相同
                return wifiConfigList.get(i).networkId;
            }
        }
        return -1;
    }

    //连接一个指定的新设备
    public boolean ConnectToWifi(String SSID,String PSW){
        this.scanDevices();
        this.getScanResults();
        int netID=this.IsConfiguration(SSID);
        if(netID==-1){
            netID= AddWifiConfig(SSID,PSW);
        }

        if(netID != -1){
            //添加了配置信息，要重新得到配置信息
            wifiConfigList=this.getConfiguredNetworks();
            this.enableNetwork(netID,true);
            return true;
        }
        else{
            //Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //检查指定scanresult的配置
    public int deviceConfiguration(int position){
        ScanResult scanResult=scanResults.get(position);
        wifiConfigList=mWifiManager.getConfiguredNetworks();
        for(WifiConfiguration wifiConf:wifiConfigList){
            String mSSID="\""+scanResult.SSID.toString()+"\"";
            if(mSSID.equals(wifiConf.SSID.toString())){
                return  wifiConf.networkId;
            }
        }
        return  -1;
    }

    //添加指定WIFI的配置信息,原列表不存在此SSID
    public int AddWifiConfig(String ssid,String pwd){
        int wifiId = -1;
        for(int i = 0;i < scanResults.size(); i++){
            ScanResult wifi = scanResults.get(i);
            if(wifi.SSID.equals(ssid)){
                Log.i("AddWifiConfig","equals");
                WifiConfiguration wifiCong = new WifiConfiguration();
                wifiCong.SSID = "\""+wifi.SSID+"\"";//\"转义字符，代表"
                wifiCong.preSharedKey = "\""+pwd+"\"";//WPA-PSK密码
                wifiCong.hiddenSSID = false;
                wifiCong.status = WifiConfiguration.Status.ENABLED;
                wifiId = mWifiManager.addNetwork(wifiCong);
                //将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
                if(wifiId != -1){
                    return wifiId;
                }
            }
        }
        return wifiId;
    }

    //得到wifi配置列表
    public List<WifiConfiguration> getConfiguredNetworks(){
        return mWifiManager.getConfiguredNetworks();
    }

    //链接到指定wifi
    public boolean enableNetwork(int netid,boolean b){

        if(mWifiManager.enableNetwork(netid,b)){
            return true;
        }else{
            return false;
        }
    }

    //得到wifi信息
    public WifiInfo getConnectInfo(){
       return mWifiManager.getConnectionInfo();
    }
    /**判断wifi开启状态*/
    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }
    //打开wifi热点
    public void openWifiAP(String SSID,String PSW){

        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
            Method method1 = null;
            try {
                method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                        WifiConfiguration.class, boolean.class);
                WifiConfiguration netConfig = new WifiConfiguration();

                netConfig.SSID = SSID;
                netConfig.preSharedKey =PSW;

                netConfig.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement
                        .set(WifiConfiguration.KeyMgmt.WPA_PSK);
                netConfig.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                netConfig.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
                netConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.CCMP);
                netConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.TKIP);

                method1.invoke(mWifiManager, netConfig, true);

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MyTimerCheck timerCheck = new MyTimerCheck() {

                @Override
                public void doTimerCheckWork() {
                    // TODO Auto-generated method stub

                    if (isWifiApEnabled(mWifiManager)) {
                        Log.v("test", "Wifi enabled success!");
                        this.exit();
                    } else {
                        Log.v("test", "Wifi enabled failed!");
                    }
                }

                @Override
                public void doTimeOutWork() {
                    // TODO Auto-generated method stub
                    this.exit();
                }
            };
            timerCheck.start(15, 1000);

    }


    public void closeWifiAp() {

        if (isWifiApEnabled(mWifiManager)) {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);

                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);

                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(mWifiManager, config, false);
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private static boolean isWifiApEnabled(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //得到wifi ip地址

    public String intToIp() {
        int i=mWifiManager.getConnectionInfo().getIpAddress();
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    public String intToIp(int ip) {
        int i=ip;
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    public String getDhcIP(){

        DhcpInfo info = mWifiManager.getDhcpInfo();

        //WifiInfo wifiinfo = mWifiManager.getConnectionInfo();
        //String ip = intToIp(wifiinfo.getIpAddress());

        String serverAddress = intToIp(info.serverAddress);

        return serverAddress;
    }

    /**判断热点开启状态*/
    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    private WIFI_AP_STATE getWifiApState(){
        int tmp;
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            tmp = ((Integer) method.invoke(mWifiManager));
// Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    public enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }



}
