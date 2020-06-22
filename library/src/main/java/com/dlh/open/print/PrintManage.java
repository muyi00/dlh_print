package com.dlh.open.print;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * @desc: 打印机管理器
 * @author: YJ
 * @time: 2020/6/22
 */
public class PrintManage implements GenericLifecycleObserver {

    /***
     * 请求开启蓝牙
     */
    private static final int REQUEST_ENABLE_BLUETOOTH = 668;
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
    private PrinterAsyncTask asynTask;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private OnPrintTaskCallback printTaskCallback;


    public PrintManage(AppCompatActivity activity) {
        this.activity = activity;
        mContext = activity;
        printerConfig = new PrinterConfig(mContext);
        printerAddress = printerConfig.getPrinterAddress();
        activity.getLifecycle().addObserver(this);
    }

    /***
     * 在AppCompatActivity的onActivityResult中
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                if (printTaskCallback != null) {
                    printTaskCallback.hint("蓝牙已开启");
                }
            }
        }
    }

    /***
     *设置打印任务机回调
     * @param printTaskCallback
     */
    public void setOnPrintTaskCallback(OnPrintTaskCallback printTaskCallback) {
        this.printTaskCallback = printTaskCallback;
    }


    private static final String TAG_L = "print_manage";

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


    private void cancelConnectTask() {
        if (asynTask != null) {
            asynTask.cancel(true);
            asynTask = null;
        }
    }

    private void configBondedDevice() {
        //"没有找到配置的打印机，请配置打印机"
        if (printTaskCallback != null) {
            printTaskCallback.configBondedDevice("没有找到配置的打印机，请先配置打印");
        }
    }

    /**
     * 获取已经配对的打印机设备
     *
     * @return
     */
    private BluetoothDevice getBondedDevice() {
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
     *启动打印机任务
     */
    public void startPrinterTask() {
        mBluetoothAdapter = Utils.getDefaultAdapter(mContext);
        if (mBluetoothAdapter == null) {
            if (printTaskCallback != null) {
                printTaskCallback.error("设备不支持蓝牙");
            }
            return;
        }

        printerAddress = printerConfig.getPrinterAddress();
        if (TextUtils.isEmpty(printerAddress)) {
            //没有打印机配对记录，请配对打印机
            configBondedDevice();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            //蓝牙没有开启，开启蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            //蓝牙已开启,获取已经配对的打印机设备
            mBluetoothDevice = getBondedDevice();
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
     * 执行打印任务
     */
    private void printerTask() {
        asynTask = new PrinterAsyncTask(mContext, asyncPrintCallBack)
                .setMaskContent("正在检测打印机")
                .executeTask();
    }

    private final PrinterAsyncTask.AsyncCallBack asyncPrintCallBack = new PrinterAsyncTask.AsyncCallBack() {

        @Override
        public void postUI(int rsult) {

        }

        @Override
        public int asyncProcess() {
            print();
            return 0;
        }
    };


    private final Handler myHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(android.os.Message msg) {
            int result = msg.what;
            if (result == -1) {
                if (printTaskCallback != null) {
                    printTaskCallback.error("连接失败");
                }
            } else if (result == 1) {
                if (asynTask != null) {
                    asynTask.setMaskContent("鎵撳嵃涓?..");
                }
            } else if (result == 100) {
                if (printTaskCallback != null) {
                    printTaskCallback.error("打印完成");
                }
            } else {
                if (printTaskCallback != null) {
                    printTaskCallback.error("未知错误");
                }
            }
        }
    };

    /***
     * 打印机初始化
     */
    private void print() {
        Printer printer = new Printer(mBluetoothAdapter, mBluetoothDevice.getAddress());
        int c = 0;
        boolean bl = false;
        do {
            if (c != 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
            //五秒内连接打印机
            if (printer.open()) {
                bl = true;
                break;
            }
            c++;
        } while (c < 5 & !bl);

        if (!bl) {
            myHandler.obtainMessage(-1).sendToTarget();
            return;
        }
        if (!printer.wakeUp()) {
            myHandler.obtainMessage(-4).sendToTarget();
            return;
        }
        myHandler.obtainMessage(1).sendToTarget();

        if (printTaskCallback != null) {
            printTaskCallback.asyncPrint(printer);
        }
        if (printer != null) {
            printer.close();
        }
        myHandler.obtainMessage(100).sendToTarget();
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
        void asyncPrint(Printer printer);
    }
}
