package com.dlh.open.print;


import androidx.lifecycle.GenericLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;

import com.dlh.open.print.enums.DefaultWords;
import com.dlh.open.print.enums.PaperWidthType;

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
    private int oneLineOfWords;

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
    private OnBluetoothCallback bluetoothCallback;
    private OnPrintTaskCallback printTaskCallback;

    private BluetoothDevice mBluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket = null;
    private PrinterAsyncTask asynTask;

    /***
     * 构造
     * @param activity
     */
    public PrinterHelper(AppCompatActivity activity) {
        this.activity = activity;
        mContext = activity;
        printerConfig = new PrinterConfig(mContext);
        printerAddress = getPrinterAddress();
        oneLineOfWords = PaperWidthType.getWords(getPaperWidthType());
        activity.getLifecycle().addObserver(this);
    }

    /***
     * Activit 中 onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                if (bluetoothCallback != null) {
                    bluetoothCallback.bluetoothEnabled(mContext.getString(R.string.app_bluetooth_is_turned_on));
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
            printTaskCallback.configBondedDevice("没有找到配置的打印机，请配置后再打印");
        }
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
            universalPrintInit();
            return 0;
        }
    };

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(android.os.Message msg) {
            int result = msg.what;
            switch (result) {
                case 100:
                    if (printTaskCallback != null) {
                        printTaskCallback.printComplete("打印完成");
                    }
                    break;
                case -1:
                    if (printTaskCallback != null) {
                        printTaskCallback.error("打印机连接失败");
                    }
                    break;
                default:
                    break;
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
            handler.obtainMessage(-1).sendToTarget();
            try {
                socket.close();
            } catch (IOException closeException) {
                handler.obtainMessage(-1).sendToTarget();
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
                printTaskCallback.asyncPrint(printerAddress, printUtil, oneLineOfWords);
            }
            handler.obtainMessage(100).sendToTarget();
        } catch (Exception e) {
            handler.obtainMessage(-1).sendToTarget();
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
     * 获取打印纸宽度类型
     * @return
     */
    public int getPaperWidthType() {
        return printerConfig.getPaperWidthType();
    }

    /***
     * 设置打印纸宽度类型
     * @param type
     * @return
     */
    public boolean setPaperWidthType(@PaperWidthType.Type int type) {
        return printerConfig.setPaperWidthType(type);
    }

    /***
     * 初始化
     */
    public void init(OnBluetoothCallback bluetoothCallback) {
        this.bluetoothCallback = bluetoothCallback;
        mBluetoothAdapter = Utils.getDefaultAdapter(mContext);
        if (mBluetoothAdapter == null) {
            if (bluetoothCallback != null) {
                bluetoothCallback.nonsupport("设备不支持蓝牙");
            }
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            //蓝牙没有开启，开启蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            if (bluetoothCallback != null) {
                bluetoothCallback.bluetoothEnabled(mContext.getString(R.string.app_bluetooth_is_turned_on));
            }
        }
    }

    /***
     * 是否开启蓝牙
     * @return
     */
    public boolean isEnabledBluetooth() {
        if (mBluetoothAdapter == null) {
            if (bluetoothCallback != null) {
                bluetoothCallback.nonsupport("设备不支持蓝牙");
            }
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }


    /***
     * 开启蓝牙
     */
    public void enabledBluetooth() {
        if (mBluetoothAdapter == null) {
            if (bluetoothCallback != null) {
                bluetoothCallback.nonsupport("设备不支持蓝牙");
            }
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            //蓝牙没有开启，开启蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            if (bluetoothCallback != null) {
                bluetoothCallback.bluetoothEnabled(mContext.getString(R.string.app_bluetooth_is_turned_on));
            }
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
            list.addAll(pairedDevices);
        }
        return list;
    }

    /**
     * 获取已配置的设备
     *
     * @return
     */
    public BluetoothDevice getConfigBondedDevice() {
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
     *打印
     */
    public void print(OnPrintTaskCallback printTaskCallback) {
        this.printTaskCallback = printTaskCallback;
        if (mBluetoothAdapter == null) {
            if (bluetoothCallback != null) {
                bluetoothCallback.nonsupport("设备不支持蓝牙");
            }
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            //蓝牙没有开启，开启蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            return;
        }

        printerAddress = getPrinterAddress();
        if (TextUtils.isEmpty(printerAddress)) {
            //没有打印机配对记录，请配对打印机
            configBondedDevice();
            return;
        }
        //蓝牙已开启,获取已经配对的打印机设备
        mBluetoothDevice = getConfigBondedDevice();

        if (mBluetoothDevice != null) {
            //已经找到了配置的打印机
            printerTask();
        } else {
            //没找到配置的设备
            configBondedDevice();
        }
    }


    /***
     * 蓝牙连接回调
     */
    public interface OnBluetoothCallback {
        /***
         * 设备不支持蓝牙功能
         */
        void nonsupport(String msg);

        /***
         * 蓝牙已打开
         */
        void bluetoothEnabled(String msg);
    }

    /****
     * 打印回调
     */
    public interface OnPrintTaskCallback {
        /***
         * 没有找到配置的打印机
         */
        void configBondedDevice(String msg);

        /***
         * 错误信息
         */
        void error(String er);

        /***
         * 异步打印
         * @param printer
         */
        void asyncPrint(String printerAddress, Print printer, @DefaultWords.Type int oneLineOfWords);

        /***
         * 打印完成
         * @param msg
         */
        void printComplete(String msg);
    }
}
