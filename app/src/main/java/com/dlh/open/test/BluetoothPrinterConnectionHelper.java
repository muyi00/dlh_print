package com.dlh.open.test;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.dlh.open.print.Printer;

import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class BluetoothPrinterConnectionHelper implements IBusiness {

    /***
     * 请求开启蓝牙
     */
    private static final int REQUEST_ENABLE_BLUETOOTH = 668;

    private BaseActivity activity;
    private Context mContext;
    /***
     * 打印机配置工具类
     */
    public PrinterConfig printerConfig;
    /***
     * 已经配置的设备地址
     */
    private String printerAddress;

    private AsyncTaskService asynTask;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private OnPrintTaskCallback printTaskCallback;


    public BluetoothPrinterConnectionHelper(BaseActivity activity) {
        this.activity = activity;
        mContext = activity;
        printerConfig = new PrinterConfig(mContext);
        printerAddress = printerConfig.getPrinterAddress();
        activity.getLifecycle().addObserver(this);
    }

    @Override
    public void init() {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                ToastUtils.showShort("蓝牙已经开启");
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
     *打印初始化
     */
    public void printInit(OnPrintTaskCallback printTaskCallback) {
        this.printTaskCallback = printTaskCallback;
        mBluetoothAdapter = Utils.getDefaultAdapter(mContext);
        if (mBluetoothAdapter == null) {
            ToastUtils.showShort("设备不支持蓝牙");
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
     * 打印任务
     */
    private void printerTask() {
        asynTask = new AsyncTaskService(mContext, asyncPrintCallBack)
                .setMaskContent("正在检测打印机")
                .executeTask();
    }

    @Nullable
    private final AsyncTaskService.AsyncCallBack asyncPrintCallBack = new AsyncTaskService.AsyncCallBack() {

        @Override
        public void postUI(int rsult) {

        }

        @Override
        public int asyncProcess() {
            //读取打印机字数配置
            //济强打印机
            jqPrintInit();
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
                    msgContent = "连接打印机失败";
                    break;
                case -2:
                    msgContent = "打印机纸仓盖未关闭";

                    break;
                case -3:
                    msgContent = "打印机缺纸";

                    break;
                case -4:
                    msgContent = "唤醒打印机失败";

                    break;
                case -8:
                    msgContent = "打印出错";

                    break;
                case 1:
                    if (asynTask != null) {
                        asynTask.setMaskContent("打印中...");
                    }
                    return;
                case 8:
                    msgContent = "打印完毕";
                    break;
                default:
            }
            if (!TextUtils.isEmpty(msgContent)) {
                ToastUtils.showShort(msgContent);
            }
        }
    };

    //<editor-fold desc="济强打印">

    /**
     * 获取打印机状态
     *
     * @param printer
     * @return
     */
    private boolean getPrintStatus(Printer printer) {
        int i = 0;
        for (i = 0; i < 10; i++) {
            if (!printer.getPrinterState(5000)) {
                // 超时时间过短也会造成获取状态失败，此超时时间和打印机内容多少有关。
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                continue;
            }
            if (printer.printerInfo.isCoverOpen) {
                myHandler.obtainMessage(-2).sendToTarget();
                return false;
            } else if (printer.printerInfo.isNoPaper) {
                myHandler.obtainMessage(-3).sendToTarget();
                return false;
            }
            if (!printer.printerInfo.isPrinting) {
                // 表示打印结束
                myHandler.obtainMessage(8).sendToTarget();
                return true;
            } else {// 否则等待500ms,并继续获取状态
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
        }
        if (i == 10) {
            myHandler.obtainMessage(-8).sendToTarget();
        }
        return false;
    }

    /**
     * 济强印机
     */
    private void jqPrintInit() {
        Printer printer = new Printer(mBluetoothAdapter, mBluetoothDevice.getAddress());
        int c = 0;
        boolean bl = false;
        do {
            if (c != 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    myHandler.obtainMessage(-8).sendToTarget();
                    return;
                }
            }
            if (printer.open(Printer.PrinterType.COMM_16)) {
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
        PrintBill jqPrintBill = new PrintBill(mContext, printer);
        if (printTaskCallback != null) {
            printTaskCallback.jqPrint(jqPrintBill);
        }
        getPrintStatus(printer);
        if (printer != null) {
            printer.close();
        }
        //myHandler.obtainMessage(8).sendToTarget();
    }

    //</editor-fold>


    public interface OnPrintTaskCallback {
        /***
         * 济强打印机打印
         * @param jqPrintBill
         */
        void jqPrint(PrintBill jqPrintBill);
    }
}