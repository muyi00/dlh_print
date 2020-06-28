package com.dlh.open.print;


import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.dlh.open.print.enums.DefaultWords;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * @desc: 打印助手
 * @author: YJ
 * @time: 2020/6/28
 */
public class PrinterHelper implements GenericLifecycleObserver {

    /***
     * 请求开启蓝牙
     */
    private static final int REQUEST_ENABLE_BLUETOOTH = 668;
    /***
     * 打印纸一行可打印中文字数
     */
    @DefaultWords.Type
    private int oneLineOfWords = DefaultWords.SUM_16;

    private AppCompatActivity activity;
    private Context mContext;
    /***
     * 打印机配置工具类
     */
    public PrinterConfig printerConfig;
    /***
     * 已经配置的设备地址
     */
    private String printerAddress;

    private OnPrintTaskCallback printTaskCallback;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket = null;
    private PrinterAsyncTask asynTask;

    public PrinterHelper(AppCompatActivity activity) {
        this.activity = activity;
        mContext = activity;
        printerConfig = new PrinterConfig(mContext);
        printerAddress = printerConfig.getPrinterAddress();
        activity.getLifecycle().addObserver(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                if (printTaskCallback != null) {
                    printTaskCallback.hint(mContext.getString(R.string.app_bluetooth_is_turned_on));
                }
            }
        }
    }

    private static final String TAG_L = "printer_conn_helper";

    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        switch (event) {
            case ON_CREATE:
                Log.i(TAG_L, "Observer【onCreate】");
                break;
            case ON_START:
                Log.i(TAG_L, "Observer【onStart】");
                break;
            case ON_RESUME:
                Log.i(TAG_L, "Observer【onResume】");
                break;
            case ON_PAUSE:
                Log.i(TAG_L, "Observer【onPause】");
                break;
            case ON_STOP:
                Log.i(TAG_L, "Observer【onStop】");
                cancelConnectTask();
                closeSocket();
                break;
            case ON_DESTROY:
                Log.i(TAG_L, "Observer【onDestroy】");
                break;
            case ON_ANY:
                Log.i(TAG_L, "Observer onAny");
                break;
            default:
                Log.i(TAG_L, "Observer【不存在的事件】");
                break;
        }
    }

    private void closeSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                mSocket = null;
                e.printStackTrace();
            }
        }
    }

    private void cancelConnectTask() {
        if (asynTask != null) {
            asynTask.cancel(true);
            asynTask = null;
        }
    }

    private void configBondedDevice() {
        if (printTaskCallback != null) {
            printTaskCallback.configBondedDevice("没有找到配置的打印机，请配置打印机");
        }
    }

    /**
     * 获取已配置的设备
     *
     * @return
     */
    private BluetoothDevice getConfigBondedDevice() {
        //获得已经绑定的蓝牙设备列表
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals(printerAddress)) {
                    return device;
                }
            }
        }
        return null;
    }





    /***
     * 打印任务
     */
    private void printerTask() {
        asynTask = new PrinterAsyncTask(mContext, asyncPrintCallBack)
                .setMaskContent(mContext.getString(R.string.testing_printer_text))
                .executeTask();
    }

    @Nullable
    private final PrinterAsyncTask.AsyncCallBack asyncPrintCallBack = new PrinterAsyncTask.AsyncCallBack() {

        @Override
        public void postUI(int rsult) {

        }

        @Override
        public int asyncProcess() {
            //读取打印机字数配置
//            PrintDeviceInfo deviceInfo = PrintHelper.getPrintDeviceInfo(mBluetoothDevice.getName());
//            if (deviceInfo != null) {
//                oneLineOfWords = deviceInfo.getWordCount();
//                if (deviceInfo.getDeviceType() == DeviceType.JQ) {
//                    //济强打印机
//                    jqPrintInit();
//                } else {
//                    //其他打印机
//                    universalPrintInit();
//                }
//            } else {
//                oneLineOfWords = printerConfig.getALineWords();
            universalPrintInit();
//            }

            return 0;
        }
    };

    private final Handler myHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(android.os.Message msg) {
            int result = msg.what;
            String msgContent = "";
            switch (result) {
                case -1:
                    msgContent = mContext.getString(R.string.failed_to_connect_the_printer);

                    break;
                case -2:
                    //"打印机纸仓盖未关闭";
                    msgContent = mContext.getString(R.string.printer_paper_compartment_cover_is_not_closed);

                    break;
                case -3:
                    //"打印机缺纸";
                    msgContent = mContext.getString(R.string.printer_is_out_of_paper);

                    break;
                case -4:
                    //"唤醒打印机失败";
                    msgContent = mContext.getString(R.string.wake_up_the_printer_failed);

                    break;
                case -8:
                    //"打印出错";
                    msgContent = mContext.getString(R.string.print_error);

                    break;
                case 1:
                    if (asynTask != null) {
                        asynTask.setMaskContent(mContext.getString(R.string.printed));
                    }
                    return;
                case 8:
                    //"打印完毕";
                    msgContent = mContext.getString(R.string.print_finished);
                    break;
                default:
            }
            if (!TextUtils.isEmpty(msgContent)) {
                //ToastUtils.showShort(msgContent);
            }
        }
    };


    //<editor-fold desc="通用打印">

    private BluetoothSocket connectDevice(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            socket.connect();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException closeException) {
                return null;
            }
            return null;
        }
        return socket;
    }

    private void universalPrintInit() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            mSocket = connectDevice(mBluetoothDevice);
            Print printUtil = new Print(mSocket.getOutputStream(), "GBK");
            if (printTaskCallback != null) {
                printTaskCallback.asyncPrint(printUtil, oneLineOfWords);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //</editor-fold>


    /***
     * 获取打印机mac地址
     * @return
     */
    public String getPrinterAddress() {
        return printerConfig.getPrinterAddress();
    }

    /***
     * 保存打印机mac地址
     * @param address
     * @return
     */
    public boolean setPrinterAddress(String address) {
        return printerConfig.setPrinterAddress(address);
    }


    /***
     * 初始化
     */
    public void init() {
        mBluetoothAdapter = Utils.getDefaultAdapter(mContext);
        if (mBluetoothAdapter == null) {
            if (printTaskCallback != null) {
                printTaskCallback.error("设备不支持蓝牙");
            }
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            //蓝牙没有开启，开启蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }


    /**
     * 获取已经配对的设备列表
     *
     * @return
     */
    public List<BluetoothDevice> getBondedDeviceList() {
        List<BluetoothDevice> list = new ArrayList<>();

        //获得已经绑定的蓝牙设备列表
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals(printerAddress)) {
                    list.add(device);
                }
            }
        }
        return list;
    }


    /***
     *打印
     */
    public void print() {
        if (mBluetoothAdapter == null) {
            if (printTaskCallback != null) {
                printTaskCallback.error("设备不支持蓝牙");
            }
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            //蓝牙没有开启，开启蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            //蓝牙已开启,获取已经配对的打印机设备
            mBluetoothDevice = getConfigBondedDevice();
        }

        printerAddress = printerConfig.getPrinterAddress();
        if (TextUtils.isEmpty(printerAddress)) {
            //没有打印机配对记录，请配对打印机
            configBondedDevice();
            return;
        }


        if (mBluetoothDevice != null) {
            //已经找到了配置的打印机
            printerTask();
        } else {
            //没找到配置的设备
            configBondedDevice();
        }
    }



    /***
     *设置打印任务机回调
     * @param printTaskCallback
     */
    public void setOnPrintTaskCallback(OnPrintTaskCallback printTaskCallback) {
        this.printTaskCallback = printTaskCallback;
    }

    public interface OnPrintTaskCallback {
        /***
         * 没有找到配置的打印机
         */
        void configBondedDevice(String msg);

        /***
         * 提示信息
         */
        void hint(String msg);

        /***
         * 错误信息
         */
        void error(String er);

        /***
         * 异步打印
         * @param printer
         */
        void asyncPrint(Print printer, int oneLineOfWords);
    }
}
