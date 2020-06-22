package com.dlh.open.print.esc;

import com.dlh.open.print.Port;
import com.dlh.open.print.Printer;
import com.dlh.open.print.PrinterType;

/**
 * @desc: ECS指令打印文字
 * @author: YJ
 * @time: 2020/6/22
 */
public class Text extends BaseESC {


    /**
     * 枚举类型：字体ID
     */
    public static enum FontId {
        ASCII_12x24(0x00),
        ASCII_8x16(0x01),
        ASCII_16x32(0x03),
        ASCII_24x48(0x04),
        ASCII_32x64(0x05),
        GBK_24x24(0x10),
        GBK_16x16(0x11),
        GBK_32x32(0x13),
        GB2312_48x48(0x14);
        private int _value;

        private FontId(int id) {
            _value = id;
        }

        public int value() {
            return _value;
        }
    }

    public Text(Port port, @PrinterType.Type int printerType) {
        super(port, printerType);
    }

    /**
     * 设置文字的放大方式
     *
     * @param enlarge
     * @return
     */
    public boolean setFontEnlarge(ESC.TEXT_ENLARGE enlarge) {
        byte[] cmd = {0x1D, 0x21, 0x00};
        cmd[2] = (byte) enlarge.value();
        return port.write(cmd);
    }

    /***
     * 设置文本字体ID
     * @param id
     * @return
     */
    public boolean setFontID(FontId id) {
        switch (id) {
            case ASCII_16x32:
            case ASCII_24x48:
            case ASCII_32x64:
            case GBK_32x32:
            case GB2312_48x48:
//                if (printerType == Printer.PrinterType.JQ_VMP02 || printerType == Printer.PrinterType.JQ_ULT113x) {
//                    Log.e("JQ", "not support FONT_ID:" + id);
//                    return true;
//                }
                break;
            default:
                break;
        }
        byte[] cmd = {0x1B, 0x4D, 0x00};
        cmd[2] = (byte) id.value();
        return port.write(cmd);
    }

    /**
     * 设置文本字体高度
     *
     * @param height
     * @return
     */
    public boolean setFontHeight(ESC.FONT_HEIGHT height) {
        switch (height) {
            case x24:
                setFontID(FontId.ASCII_12x24);
                setFontID(FontId.GBK_24x24);
                break;
            case x16:
                setFontID(FontId.ASCII_8x16);
                setFontID(FontId.GBK_16x16);
                break;
            case x32:
                setFontID(FontId.ASCII_16x32);
                setFontID(FontId.GBK_32x32);
                break;
            case x48:
                setFontID(FontId.ASCII_24x48);
                setFontID(FontId.GB2312_48x48);
                break;
            case x64:
                setFontID(FontId.ASCII_32x64);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * 设置文本加粗方式
     *
     * @param bold
     * @return
     */
    public boolean setBold(boolean bold) {
        byte[] cmd = {0x1B, 0x45, 0x00};
        cmd[2] = (byte) (bold ? 1 : 0);
        return port.write(cmd);
    }

    /**
     * 打印输出文本
     *
     * @param text
     * @return
     */
    public boolean drawOut(String text) {
        return port.write(text);
    }

    /***
     * 打印输出文本
     * @param x
     * @param y
     * @param text
     * @return
     */
    public boolean drawOut(int x, int y, String text) {
        if (!setXY(x, y))
            return false;
        return port.write(text);
    }

    /***
     * 打印输出文本
     * @param x
     * @param y
     * @param enlarge
     * @param text
     * @return
     */
    public boolean drawOut(int x, int y, ESC.TEXT_ENLARGE enlarge, String text) {
        if (!setXY(x, y))
            return false;
        if (!setFontEnlarge(enlarge))
            return false;
        return port.write(text);
    }

    /***
     * 打印输出文本
     * @param height
     * @param text
     * @return
     */
    public boolean drawOut(ESC.FONT_HEIGHT height, String text) {
        if (!setFontHeight(height))
            return false;
        return port.write(text);
    }

    /***
     * 打印输出文本
     * @param x
     * @param y
     * @param height
     * @param enlarge
     * @param text
     * @return
     */
    public boolean drawOut(int x, int y, ESC.FONT_HEIGHT height, ESC.TEXT_ENLARGE enlarge, String text) {
        if (!setXY(x, y))
            return false;
        if (!setFontHeight(height))
            return false;
        if (!setFontEnlarge(enlarge))
            return false;
        return port.write(text);
    }

    /***
     * 打印输出文本
     * @param x
     * @param y
     * @param height
     * @param bold
     * @param enlarge
     * @param text
     * @return
     */
    public boolean drawOut(int x, int y, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text) {
        if (!setXY(x, y))
            return false;
        if (!setFontHeight(height))
            return false;
        if (!setFontEnlarge(enlarge))
            return false;
        if (!setBold(bold))
            return false;
        return port.write(text);
    }

    /***
     * 打印输出文本
     * @param x
     * @param y
     * @param height
     * @param bold
     * @param text
     * @return
     */
    public boolean drawOut(int x, int y, ESC.FONT_HEIGHT height, boolean bold, String text) {
        if (!setXY(x, y))
            return false;
        if (!setFontHeight(height))
            return false;
        if (!setBold(bold))
            return false;
        return port.write(text);
    }

    /***
     * 打印输出文本
     * @param align
     * @param height
     * @param bold
     * @param enlarge
     * @param text
     * @return
     */
    public boolean drawOut(Printer.AlignType align, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text) {
        if (!setAlign(align))
            return false;
        if (!setFontHeight(height))
            return false;
        if (!setBold(bold))
            return false;
        if (!setFontEnlarge(enlarge))
            return false;
        return port.write(text);
    }

    /***
     * 打印输出文本
     * @param align
     * @param bold
     * @param text
     * @return
     */
    public boolean drawOut(Printer.AlignType align, boolean bold, String text) {
        if (!setAlign(align))
            return false;
        if (!setBold(bold))
            return false;
        return port.write(text);
    }

    /***
     * 打印输出文本
     * 1)文件立即输出
     * 2)会恢复字体效果到缺省值
     * @param x
     * @param y
     * @param height
     * @param bold
     * @param enlarge
     * @param text
     * @return
     */
    public boolean printOut(int x, int y, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text) {
        if (!setXY(x, y)) return false;
        if (bold) if (!setBold(true)) return false;
        if (!setFontHeight(height)) return false;
        if (!setFontEnlarge(enlarge)) return false;
        if (!port.write(text)) return false;
        enter();
        //恢复字体效果
        if (bold) if (!setBold(false)) return false;
        if (!setFontHeight(ESC.FONT_HEIGHT.x24)) return false;
        if (!setFontEnlarge(ESC.TEXT_ENLARGE.NORMAL)) return false;

        return true;
    }

    public boolean printOut(Printer.AlignType align, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text) {
        if (!setAlign(align)) return false;
        if (!setFontHeight(height)) return false;
        if (bold) if (!setBold(true)) return false;
        if (!setFontEnlarge(enlarge)) return false;
        if (!port.write(text)) return false;
        enter();
        //恢复
        if (!setAlign(Printer.AlignType.LEFT)) return false;
        if (!setFontHeight(ESC.FONT_HEIGHT.x24)) return false;
        if (bold) if (!setBold(false)) return false;
        if (!setFontEnlarge(ESC.TEXT_ENLARGE.NORMAL)) return false;

        return true;
    }

    public boolean printOut(String text) {
        if (!port.write(text))
            return false;
        return enter();
    }

}
