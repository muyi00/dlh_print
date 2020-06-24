package com.dlh.open.print;

import android.text.TextUtils;

import com.dlh.open.print.enums.DefaultWords;
import com.dlh.open.print.enums.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class PrintHelper {

    //设备类型 ： 蓝牙打印机，打印机一体机
    //打印纸宽度：48mm（16汉字/行 32英文字符/行），72mm（24汉字/行 48英文字符/行）

    /***
     * 是否启用蓝牙打印
     * @return
     */
    public static boolean isEnableBluetoothPrint() {
        return true;
    }

    /***
     * 默认已适配的打印机
     */
    public static List<PrintDeviceInfo> defaultPrintDevice = new ArrayList<PrintDeviceInfo>() {
        {
            add(new PrintDeviceInfo(DeviceType.JQ, "VMP", DefaultWords.SUM_16));
            add(new PrintDeviceInfo(DeviceType.JQ, "ULT", DefaultWords.SUM_24));
            add(new PrintDeviceInfo(DeviceType.JQ, "JLP", DefaultWords.SUM_24));

            add(new PrintDeviceInfo(DeviceType.COMM, "InnerPrinter", DefaultWords.SUM_16));
            add(new PrintDeviceInfo(DeviceType.COMM, "Qsprinter", DefaultWords.SUM_16));
        }
    };

    /***
     * 获取已经配置的设备配置
     * @param deviceName
     * @return
     */
    public static PrintDeviceInfo getPrintDeviceInfo(String deviceName) {
        if (TextUtils.isEmpty(deviceName)) {
            return null;
        }
        for (PrintDeviceInfo info : defaultPrintDevice) {
            if (deviceName.startsWith(info.getDevicePrefix())) {
                return info;
            }
        }
        return null;
    }


}
