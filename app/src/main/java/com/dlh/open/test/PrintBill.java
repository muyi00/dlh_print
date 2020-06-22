package com.dlh.open.test;

import android.content.Context;

import com.dlh.open.print.Printer;
import com.dlh.open.print.PrinterConfig;
import com.dlh.open.print.esc.ESC;

import java.text.SimpleDateFormat;


public class PrintBill {
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Context mContext;
    private Printer printer;
    private PrinterConfig printerConfig;


    public PrintBill(Context mContext, Printer printer) {
        this.mContext = mContext;
        this.printer = printer;
        printerConfig = new PrinterConfig(mContext);
    }


    /***
     * 打印文本
     * @param alignType 对齐方式
     * @param height 字体高度
     * @param bold 是否加粗
     * @param enlarge 字体扩大，倍高倍宽
     * @param text 文本内容
     */
    private void printText(Printer.AlignType alignType, ESC.FONT_HEIGHT height, boolean bold, ESC.TEXT_ENLARGE enlarge, String text) {
        printer.esc.text.printOut(alignType, height, bold, enlarge, text);
    }


    /***
     * 打印文本
     * @param alignType 对齐方式
     * @param bold 是否加粗
     * @param text 文本内容
     */
    private void printText(Printer.AlignType alignType, boolean bold, String text) {
        printText(alignType, ESC.FONT_HEIGHT.x24, bold, ESC.TEXT_ENLARGE.NORMAL, text);
    }


    /***
     * 打印加粗文本
     * @param text
     */
    private void printTextBold(String text) {
        printText(Printer.AlignType.LEFT, true, text);
    }

    /***
     * 打印居中加粗文本
     * @param text
     */
    private void printTextBoldCenter(String text) {
        printText(Printer.AlignType.CENTER, true, text);

    }

    /***
     * 打印文本
     * @param text
     */
    private void printText(String text) {
        printer.esc.text.printOut(text);
    }


    /***
     * 打印测试
     */
    public void printTest() {
        printer.esc.feedEnter();
        printer.esc.feedEnter();
        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x24, false, ESC.TEXT_ENLARGE.HEIGHT_DOUBLE, "没看错，你选的就是我！");
        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x24, false, ESC.TEXT_ENLARGE.HEIGHT_DOUBLE, printerConfig.getPrinterAddress());

        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x32, false, ESC.TEXT_ENLARGE.NORMAL, "多立恒");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x16, false, ESC.TEXT_ENLARGE.HEIGHT_DOUBLE, "多");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x16, false, ESC.TEXT_ENLARGE.WIDTH_DOUBLE, "多");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x16, false, ESC.TEXT_ENLARGE.HEIGHT_WIDTH_DOUBLE, "多");
//
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x32, false, ESC.TEXT_ENLARGE.NORMAL, "立");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x32, false, ESC.TEXT_ENLARGE.HEIGHT_DOUBLE, "立");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x32, false, ESC.TEXT_ENLARGE.WIDTH_DOUBLE, "立");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x32, false, ESC.TEXT_ENLARGE.HEIGHT_WIDTH_DOUBLE, "立");
//
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x48, false, ESC.TEXT_ENLARGE.NORMAL, "恒");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x48, false, ESC.TEXT_ENLARGE.HEIGHT_DOUBLE, "恒");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x48, false, ESC.TEXT_ENLARGE.WIDTH_DOUBLE, "恒");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x48, false, ESC.TEXT_ENLARGE.HEIGHT_WIDTH_DOUBLE, "恒");
//
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x64, false, ESC.TEXT_ENLARGE.NORMAL, "恒");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x64, false, ESC.TEXT_ENLARGE.HEIGHT_DOUBLE, "恒");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x64, false, ESC.TEXT_ENLARGE.WIDTH_DOUBLE, "恒");
//        printer.esc.text.printOut(Printer.AlignType.CENTER, ESC.FONT_HEIGHT.x64, false, ESC.TEXT_ENLARGE.HEIGHT_WIDTH_DOUBLE, "恒");

        printer.esc.feedEnter();
        printer.esc.feedEnter();
    }
}
