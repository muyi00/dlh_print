package com.dlh.open.print.esc;

import com.dlh.open.print.Port;
import com.dlh.open.print.Printer;

public class BaseESC {
    public Port port;
    public int maxDots = 384;//允许打印的大点数
    public int canvasMaxHeight = 100;//打印机画板最大高度，单位dots.

    public BaseESC(Port port) {
        if (port == null)
            return;
        this.port = port;

    }

    /**
     * 设置打印对象的x，y坐标
     */
    public boolean setXY(int x, int y) {
        if (x < 0 || x >= maxDots || x > 0x1FF) {
            return false;
        }

        if (y < 0 || y >= canvasMaxHeight || y > 0x7F) {
            return false;
        }

        byte[] cmd = {0x1B, 0x24, 0x00, 0x00};
        int pos = ((x & 0x1FF) | ((y & 0x7F) << 9));
        cmd[2] = (byte) pos;
        cmd[3] = (byte) (pos >> 8);
        port.write(cmd);
        return true;
    }

    /**
     * 设置打印对象对齐方式
     * 支持打印对象:文本(text),条码(barcode)
     */
    public boolean setAlign(Printer.AlignType align) {
        byte[] cmd = {0x1B, 0x61, 0x00};
        cmd[2] = (byte) align.ordinal();
        return port.write(cmd);
    }

    public boolean setLineSpace(int dots) {
        byte[] cmd = {0x1B, 0x33, 0x00};
        cmd[2] = (byte) dots;
        return port.write(cmd);
    }

    public boolean init() {
        byte[] cmd = {0x1B, 0x40};
        return port.write(cmd);
    }

    /***
     * 换行回车
     * @return
     */
    public boolean enter() {
        byte[] cmd = {0x0D, 0x0A};
        return port.write(cmd);
    }
}
