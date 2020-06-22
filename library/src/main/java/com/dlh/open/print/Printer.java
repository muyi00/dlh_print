package com.dlh.open.print;

import android.bluetooth.BluetoothAdapter;

import com.dlh.open.print.esc.ESC;


public class Printer {
    /***
     * 枚举类型：对齐方式
     */
    public static enum AlignType {
        LEFT,
        CENTER,
        RIGHT;
    }

    private Port port = null;
    public ESC esc = null;
    public boolean isOpen = false;
    private boolean isInit = false;
    private byte[] state = {0, 0};


    public Printer() {
    }

    public Printer(BluetoothAdapter btAdapter, String btDeviceString) {
        if (btAdapter == null || btDeviceString == null) {
            isInit = false;
            return;
        }
        port = new Port(btAdapter, btDeviceString);
        isInit = true;
    }

    public Printer(String btDeviceString) {
        if (btDeviceString == null) {
            isInit = false;
            return;
        }
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            isInit = false;
            return;
        }
        port = new Port(btAdapter, btDeviceString);
        isInit = true;
    }

    /**
     * 打开端口
     * 注意：最好不要将此函数放在Activity的onCreate函数中，因为bluetooth connect时会有定的延时，有时会造成页面显示很慢，而误认为没有点击按钮
     *
     * @return
     */
    public boolean open() {
        if (!isInit)
            return false;
        if (isOpen)
            return true;

        if (!port.open(3000))
            return false;
        esc = new ESC(port);
        isOpen = true;
        return true;
    }

    /**
     * 打开端口
     * 注意：最好不要将此函数放在Activity的onCreate函数中，因为bluetooth connect时会有定的延时，有时会造成页面显示很慢，而误认为没有点击按钮
     *
     * @param timeout      超时时间
     * @return
     */
    public boolean open(int timeout) {
        if (!isInit)
            return false;
        if (isOpen)
            return true;

        if (!port.open(timeout))
            return false;

        esc = new ESC(port);
        isOpen = true;
        return true;
    }

    /***
     * 打开端口
     * @param btDeviceString  蓝牙设备地址
     * @return
     */
    public boolean open(String btDeviceString) {
        if (btDeviceString == null)
            return false;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null)
            return false;
        port = new Port(btAdapter, btDeviceString);
        if (port == null)
            return false;
        isInit = true;

        return open( 3000);
    }

    /***
     * 关闭连接
     * @return
     */
    public boolean close() {
        if (!isOpen)
            return false;

        isOpen = false;
        return port.close();
    }

    /**
     * 获取串口连接状态
     *
     * @return
     */
    public Port.PortState getPortState() {
        return port.getState();
    }

    /**
     * 唤醒打印机
     * 注意:部分手持蓝牙连接第一次不稳定，会造成开头字符乱码，可以通常这个方法来避免此问题
     */
    public boolean wakeUp() {
        if (!isInit)
            return false;
        if (!port.writeNULL())
            return false;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return esc.text.init();
    }
}
