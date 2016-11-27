package com.example.yuq32.filestraveling.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuq32 on 2016/5/25.
 */
public class SplitInfo {

    public SplitInfo() {

    }

    public SplitInfo(String info) {

    }

    public static Map<String, String> mapInfo(String info) {
        if (info != null) {
            String mes = info;
            Map<String, String> mInfo = new HashMap<>();
            String[] mesList = mes.split("\n");
            String[] listName = new String[]{"fileName", "filePath", "fileType", "Ip", "port", "wifiName", "wifiPSW", "deviceName"};
            for (int i = 0; i < mesList.length; i++) {
                mInfo.put(listName[i], mesList[i]);
            }
            return mInfo;
        } else {
            return null;
        }
    }
}
