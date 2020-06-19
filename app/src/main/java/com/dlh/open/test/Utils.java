package com.dlh.open.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

public class Utils {
    /**
     * 获取蓝牙适配器
     *
     * @param context
     * @return
     */
    public static BluetoothAdapter getDefaultAdapter(Context context) {
        BluetoothAdapter adapter = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        return adapter;
    }
}
