package com.dlh.open.print;

import android.bluetooth.BluetoothAdapter;

import com.dlh.open.print.esc.ESC;
import com.dlh.open.print.jq.JQPrinterInfo;

public class Printer {

    /***
     * 枚举类型：打印机型号
     */
    public static enum PrinterType {
        COMM_16,
        COMM_24,
        JQ_VMP02,
        JQ_VMP02_P,
        JQ_JLP351,
        JQ_JLP351_IC,
        JQ_ULT113x,
        JQ_ULT1131_IC,
    }


    /***
     * 枚举类型：对齐方式
     */
    public static enum AlignType {
        LEFT,
        CENTER,
        RIGHT;
    }


    public JQPrinterInfo printerInfo = new JQPrinterInfo();
    private PrinterType printerType;
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
     * @param printer_type 设备类型
     * @return
     */
    public boolean open(PrinterType printer_type) {
        if (!isInit)
            return false;
        printerType = printer_type;
        if (isOpen)
            return true;

        if (!port.open(3000))
            return false;

        esc = new ESC(port, printer_type);
        isOpen = true;
        return true;
    }

    /**
     * 打开端口
     * 注意：最好不要将此函数放在Activity的onCreate函数中，因为bluetooth connect时会有定的延时，有时会造成页面显示很慢，而误认为没有点击按钮
     *
     * @param printer_type 设备类型
     * @param timeout      超时时间
     * @return
     */
    public boolean open(PrinterType printer_type, int timeout) {
        if (!isInit)
            return false;
        printerType = printer_type;
        if (isOpen)
            return true;

        if (!port.open(timeout))
            return false;

        esc = new ESC(port, printer_type);
        isOpen = true;
        return true;
    }

    /***
     * 打开端口
     * @param btDeviceString  蓝牙设备地址
     * @param printer_type 打印机类型
     * @return
     */
    public boolean open(String btDeviceString, PrinterType printer_type) {
        if (btDeviceString == null)
            return false;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null)
            return false;
        port = new Port(btAdapter, btDeviceString);
        if (port == null)
            return false;
        isInit = true;

        return open(printer_type, 3000);
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

    /***
     * 走纸到右黑标 (目前只支持济强打印机)
     * @return
     */
    public boolean feedRightMark() {
        if (!isInit) {
            return false;
        }
        if (Enterprise.getType(this.printerType) == Enterprise.JQ) {
            byte[] cmd = {0x00, 0x00};
            switch (this.printerType) {
                case JQ_JLP351: //JLP351系列没有右黑标传感器，只能使用中间的标签缝传感器代替有右黑标传感器
                case JQ_JLP351_IC:
                    cmd[0] = 0x1D;
                    cmd[1] = 0x0C;
                    return port.write(cmd, 2);//指令0x1D 0x01C 等效于 0x0E
                default:
                    cmd[0] = 0x0C;
                    return port.write(cmd, 1);
            }
        } else {
            return false;
        }
    }

    /***
     *  走纸到左黑标 (目前只支持济强打印机)
     * @return
     */
    public boolean feedLeftMark() {
        if (!isInit) {
            return false;
        }

        if (Enterprise.getType(this.printerType) == Enterprise.JQ) {
            switch (this.printerType) {
                case JQ_JLP351: //JLP351系列没有右黑标传感器，只能使用中间的标签缝传感器代替有右黑标传感器
                case JQ_JLP351_IC:
                    return port.write((byte) 0x0c);//0x0C
                default:
                    return port.write((byte) 0x0e);
            }
        } else {
            return false;
        }
    }

    /**
     * 获取打印机状态
     */
    public boolean getPrinterState(int timeout_read) {
        if (!isInit) {
            return false;
        }
        if (Enterprise.getType(this.printerType) == Enterprise.JQ) {
            printerInfo.stateReset();
            if (!esc.getState(state, timeout_read))
                return false;
            if ((state[0] & JQPrinterInfo.STATE_NOPAPER_UNMASK) != 0) {
                printerInfo.isNoPaper = true;
            }
            if ((state[0] & JQPrinterInfo.STATE_BATTERYLOW_UNMASK) != 0) {
                printerInfo.isBatteryLow = true;
            }
            if ((state[0] & JQPrinterInfo.STATE_COVEROPEN_UNMASK) != 0) {
                printerInfo.isCoverOpen = true;
            }
            if ((state[0] & JQPrinterInfo.STATE_OVERHEAT_UNMASK) != 0) {
                printerInfo.isOverHeat = true;
            }
            if ((state[0] & JQPrinterInfo.STATE_PRINTING_UNMASK) != 0) {
                printerInfo.isPrinting = true;
            }
        }
        return true;
    }

}
