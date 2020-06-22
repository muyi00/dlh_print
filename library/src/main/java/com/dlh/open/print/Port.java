package com.dlh.open.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * @desc: 蓝牙端口
 * @author: YJ
 * @time: 2020/6/17
 */
public class Port {

    private static final String TAG = "Printer";

    public static enum PortState {
        /***
         * 端口打开
         */
        PORT_OPEND,
        /***
         * 端口关闭
         */
        PORT_CLOSED,
        /***
         * BluetoothAdapter = null
         */
        BT_ADAPTER_NULL,
        /**
         * 远程蓝牙设备NULL，打印机MAC地址NULL
         */
        BT_REMOTE_DEVICE_NULL,
        /***
         * BluetoothAdapter 错误
         */
        BT_ADAPTER_ERROR,
        BT_CREAT_RFCOMM_SERVICE_ERROR,
        /***
         * 蓝牙设备连接错误
         */
        BT_CONNECT_ERROR,
        /***
         * 蓝牙Socket连接关闭错误
         */
        BT_SOCKET_CLOSE_ERROR,
        /***
         * 获取出流错误（手机输出到蓝设备）
         */
        BT_GET_OUT_STREAM_ERROR,
        /***
         * 获取输入流错误（手机读取蓝设备）
         */
        BT_GET_IN_STREAM_ERROR,
    }

    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private byte[] cmd = {0};
    private PortState portState;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket mmBtSocket;
    private String btDeviceString;
    private OutputStream mmOutStream = null;
    private InputStream mmInStream = null;
    public boolean isOpen = false;

    /***
     * 构造
     * @param bt_adapter BluetoothAdapter
     * @param bt_device_mac 设备mac地址
     */
    public Port(BluetoothAdapter bt_adapter, String bt_device_mac) {
        if (bt_adapter == null) {
            portState = PortState.BT_ADAPTER_NULL;
            return;
        }
        if (bt_device_mac == null) {
            portState = PortState.BT_REMOTE_DEVICE_NULL;
            return;
        }
        btAdapter = bt_adapter;
        btDeviceString = bt_device_mac;
        if (btAdapter.getState() != BluetoothAdapter.STATE_ON) {
            portState = PortState.BT_ADAPTER_ERROR;
            return;
        }
        portState = PortState.PORT_CLOSED;
    }

    /***
     * 当对象被销毁时，释放资源。
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    /***
     * 获取连接状态
     * @return
     */
    public PortState getState() {
        return portState;
    }

    /***
     * 连接打开端口
     * @return
     */
    public boolean open() {
        if (btAdapter == null || btDeviceString == null) {
            isOpen = false;
            return false;
        }
        BluetoothSocket TmpSock = null;
        try {
            BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDeviceString);
            TmpSock = btDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception ex) {
            TmpSock = null;
            Log.e(TAG, "createRfcommSocketToServiceRecord exception");
            isOpen = false;
            portState = PortState.BT_REMOTE_DEVICE_NULL;
            return false;
        } finally {
            mmBtSocket = TmpSock;
        }
        try {
            mmBtSocket.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "connect exception");

            try {
                mmBtSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                portState = PortState.BT_SOCKET_CLOSE_ERROR;
            }
            isOpen = false;
            portState = PortState.BT_CONNECT_ERROR;
            return false;
        }

        try {
            mmOutStream = mmBtSocket.getOutputStream();
        } catch (IOException e) {
            portState = PortState.BT_GET_OUT_STREAM_ERROR;
            e.printStackTrace();
        }
        try {
            mmInStream = mmBtSocket.getInputStream();
        } catch (IOException e) {
            portState = PortState.BT_GET_IN_STREAM_ERROR;
            e.printStackTrace();
        }
        isOpen = true;
        portState = PortState.PORT_OPEND;
        return true;
    }

    /***
     *  连接打开端口
     * @param timeout 超时（秒）
     * @return
     */
    public boolean open(int timeout) {
        if (btAdapter == null || btDeviceString == null) {
            isOpen = false;
            return false;
        }
        if (timeout < 1000)
            timeout = 1000;
        if (timeout > 6000)
            timeout = 6000;

        long start_time = SystemClock.elapsedRealtime();
        for (; ; ) {
            if (BluetoothAdapter.STATE_ON == btAdapter.getState()) {
                break;
            }
            if (SystemClock.elapsedRealtime() - start_time > timeout) {
                Log.e(TAG, "adapter state on timeout");
                return false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        BluetoothSocket TmpSock = null;
        try {
            BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDeviceString);
            TmpSock = btDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception ex) {
            TmpSock = null;
            Log.e(TAG, "createRfcommSocketToServiceRecord exception");
            isOpen = false;
            portState = PortState.BT_REMOTE_DEVICE_NULL;
            return false;
        }
        mmBtSocket = TmpSock;

        start_time = SystemClock.elapsedRealtime();
        for (; ; ) {
            try {
                mmBtSocket.connect();
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, "connect exception");

                if (SystemClock.elapsedRealtime() - start_time > timeout) {
                    try {
                        mmBtSocket.close();
                    } catch (IOException e) {
                        portState = PortState.BT_SOCKET_CLOSE_ERROR;
                        e.printStackTrace();
                    }
                    isOpen = false;
                    Log.e(TAG, "connet timeout");

                    portState = PortState.BT_CONNECT_ERROR;
                    return false;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            break;
        }

        try {
            mmOutStream = mmBtSocket.getOutputStream();
        } catch (IOException e) {
            portState = PortState.BT_GET_OUT_STREAM_ERROR;
            e.printStackTrace();
        }
        try {
            mmInStream = mmBtSocket.getInputStream();
        } catch (IOException e) {
            portState = PortState.BT_GET_IN_STREAM_ERROR;
            e.printStackTrace();
        }
        isOpen = true;
        portState = PortState.PORT_OPEND;
        Log.e(TAG, "connect ok");
        return true;
    }

    /***
     * 关闭
     * @return
     */
    public boolean close() {
        if (mmBtSocket == null) {
            isOpen = false;
            Log.e(TAG, "mmBtSocket null");
            return false;
        }
        if (isOpen) {
            try {
                if (mmOutStream != null) {
                    mmOutStream.close();
                    mmOutStream = null;
                }
                if (mmInStream != null) {
                    mmInStream.close();
                    mmOutStream = null;
                }
                mmBtSocket.close(); //SB close会使Socket无效，必须下次使用必须再次createRfcommSocketToServiceRecord来创建
            } catch (Exception ex) {
                isOpen = false;
                Log.e(TAG, "close exception");
                return false;
            }
        }
        isOpen = false;
        mmBtSocket = null;
        portState = PortState.PORT_CLOSED;
        return true;
    }

    /***
     * 清空缓冲区
     * @return
     */
    public boolean flushReadBuffer() {
        byte[] buffer = new byte[64];
        if (!isOpen)
            return false;
        while (true) {
            int r = 0;
            try {
                r = mmInStream.available();
                if (r == 0) break;
                if (r > 0) {
                    if (r > 64) r = 64;
                    mmInStream.read(buffer, 0, r);
                }
            } catch (IOException e) {
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }

        }
        return true;
    }

    /**
     * 发送指令
     *
     * @param buffer
     * @param offset
     * @param length
     * @return
     */
    public boolean write(byte[] buffer, int offset, int length) {
        if (!isOpen)
            return false;
        if (mmBtSocket == null) {
            Log.e(TAG, "mmBtSocket null");
            return false;
        }
        if (mmOutStream == null) {
            return false;
        }
        try {
            mmOutStream.write(buffer, offset, length);
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    /**
     * 发送指令
     *
     * @param cmd
     * @return
     */
    public boolean write(byte cmd) {
        byte[] buffer = {0x00};
        buffer[0] = cmd;
        return write(buffer, 0, 1);
    }

    /***
     * 发送指令
     * @param buffer
     * @param length
     * @return
     */
    public boolean write(byte[] buffer, int length) {
        if (length > buffer.length)
            return false;
        return write(buffer, 0, length);
    }

    /***
     * 发送指令
     * @param buffer
     * @return
     */
    public boolean write(byte[] buffer) {
        return write(buffer, 0, buffer.length);
    }

    /***
     * 发送指令
     * @param s
     * @return
     */
    public boolean write(short s) {
        byte[] buffer = {0, 0};
        buffer[0] = (byte) s;
        buffer[1] = (byte) (s >> 8);
        return write(buffer, 0, buffer.length);
    }

    /**
     * 发送空指令
     *
     * @return
     */
    public boolean writeNULL() {
        cmd[0] = 0;
        return write(cmd, 0, 1);
    }

    /***
     * 发送空指令
     * @param text
     * @return
     */
    public boolean write(String text) {
        byte[] data = null;
        try {
            data = text.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Sting getBytes('GBK') failed");
            return false;
        }
        if (!write(data, 0, data.length))
            return false;
        return writeNULL();
    }

    /***
     * 读取输入流内容
     * @param buffer
     * @param offset
     * @param length
     * @param timeout_read
     * @return
     */
    public boolean read(byte[] buffer, int offset, int length, int timeout_read) {
        if (!isOpen)
            return false;
        if (timeout_read < 200) timeout_read = 200;
        if (timeout_read > 5000) timeout_read = 5000;

        try {
            long start_time = SystemClock.elapsedRealtime();
            long cur_time = 0;
            int need_read = length;
            int cur_readed = 0;
            for (; ; ) {
                if (mmInStream.available() > 0) {
                    cur_readed = mmInStream.read(buffer, offset, need_read);
                    offset += cur_readed;
                    need_read -= cur_readed;
                }
                if (need_read == 0) {
                    break;
                }
                cur_time = SystemClock.elapsedRealtime();
                if (cur_time - start_time > timeout_read) {
                    Log.e(TAG, "read timeout");
                    return false;
                }
                Thread.sleep(20);
            }
        } catch (Exception ex) {
            Log.e(TAG, "read exception");
            close();
            return false;
        }
        return true;
    }

    /***
     * 读取输入流内容
     * @param buffer
     * @param length
     * @param timeout_read
     * @return
     */
    public boolean read(byte[] buffer, int length, int timeout_read) {
        if (length > buffer.length)
            return false;
        return read(buffer, 0, length, timeout_read);
    }

}
