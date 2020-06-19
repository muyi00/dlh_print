package com.dlh.open.test;

import android.content.Context;
import android.content.Intent;

public class PrintManage {
    private BaseActivity activity;
    private Context mContext;
    private BluetoothPrinterConnectionHelper connectionHelper;

    public PrintManage(BaseActivity activity) {
        this.activity = activity;
        mContext = activity;
        connectionHelper = new BluetoothPrinterConnectionHelper(activity);
    }

    /***
     * 获取蓝牙打印连助手
     * @return
     */
    public BluetoothPrinterConnectionHelper getBluetoothPrinterConnectionHelper() {
        return connectionHelper;
    }


    /***
     * onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        connectionHelper.onActivityResult(requestCode, resultCode, data);
    }


    /***
     * 蓝牙打印测试
     */
    public void bluetoothPrintTest() {
        connectionHelper.printInit(new BluetoothPrinterConnectionHelper.OnPrintTaskCallback() {

            @Override
            public void jqPrint(PrintBill jqPrintBill) {
                jqPrintBill.printTest();
            }
        });
    }




}
