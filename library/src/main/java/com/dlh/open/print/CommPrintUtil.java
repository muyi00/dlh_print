package com.dlh.open.print;

import com.dlh.open.print.enums.AlignType;
import com.dlh.open.print.enums.BoldType;
import com.dlh.open.print.enums.Enlarge;
import com.dlh.open.print.enums.FontSizeType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CommPrintUtil {

    private OutputStreamWriter mWriter = null;
    private OutputStream mOutputStream = null;


    final byte[][] byteCommands = {
            {0x1b, 0x40},      // 复位打印机
            {0x1b, 0x4d, 0x00},// 标准ASCII字体
            {0x1b, 0x4d, 0x01},// 压缩ASCII字体
            {0x1d, 0x21, 0x00},// 字体不放大
            {0x1d, 0x21, 0x01},//   宽加倍
            {0x1d, 0x21, 0x10},//   高加倍
            {0x1d, 0x21, 0x11},// 宽高加倍
            {0x1b, 0x45, 0x00},// 取消加粗模式
            {0x1b, 0x45, 0x01},// 选择加粗模式
            {0x1b, 0x7b, 0x00},// 取消倒置打印
            {0x1b, 0x7b, 0x01},// 选择倒置打印
            {0x1d, 0x42, 0x00},// 取消黑白反显
            {0x1d, 0x42, 0x01},// 选择黑白反显
            {0x1b, 0x56, 0x00},// 取消顺时针旋转90°
            {0x1b, 0x56, 0x01},// 选择顺时针旋转90°
    };


    /**
     * 初始化Pos实例
     *
     * @param encoding 编码
     * @throws IOException
     */
    public CommPrintUtil(OutputStream outputStream, String encoding) throws IOException {
        mWriter = new OutputStreamWriter(outputStream, encoding);
        mOutputStream = outputStream;
        initPrinter();
    }

    private void print(byte[] bs) throws IOException {
        mOutputStream.write(bs);
    }

    private void printRawBytes(byte[] bytes) throws IOException {
        mOutputStream.write(bytes);
        mOutputStream.flush();
    }

    /**
     * 初始化打印机
     *
     * @throws IOException
     */
    private void initPrinter() throws IOException {
        mWriter.write(0x1B);
        mWriter.write(0x40);
        mWriter.flush();
    }

    /**
     * 打印换行
     *
     * @return length 需要打印的空行数
     * @throws IOException
     */
    public void printLine(int lineNum) {
        try {
            for (int i = 0; i < lineNum; i++) {
                mWriter.write("\n");
            }
            mWriter.flush();
        } catch (IOException e) {

        }
    }

    /**
     * 打印换行(只换一行)
     *
     * @throws IOException
     */
    public void printLine() {
        printLine(1);
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printTabSpace(int length) {
        try {
            for (int i = 0; i < length; i++) {
                mWriter.write("\t");
            }
            mWriter.flush();
        } catch (IOException e) {

        }
    }


    /***
     * 对齐
     * @param align
     * @throws IOException
     */
    private void printAlign(@AlignType.Type int align) throws IOException {
        mWriter.write(0x1b);
        mWriter.write(0x61);
        mWriter.write(align);
    }

    /***
     * 倍高倍宽
     * @param enlarge
     * @throws IOException
     */
    private void printEnlarge(@Enlarge.Type int enlarge) throws IOException {
        mWriter.write(0x1d);//<-------
        mWriter.write(0x21);
        mWriter.write(enlarge);
    }

    /***
     * 字体大小
     * @param fontSize
     * @throws IOException
     */
    private void printFontSize(@FontSizeType.Type int fontSize) throws IOException {
        mWriter.write(0x1b);//<-------
        mWriter.write(0x21);
        mWriter.write(fontSize);
    }


    /***
     * 加粗
     * @param bold
     * @throws IOException
     */
    private void printBold(@BoldType.Type int bold) throws IOException {
        mWriter.write(0x1b);
        mWriter.write(0x45);
        mWriter.write(bold);
    }


    /***
     * 打印文字
     * @param alignType 对齐方式
     * @param fontSize 字体大小
     * @param bold 是否加粗
     * @param enlarge 是否倍高宽
     * @param text
     */
    public void printText(@AlignType.Type int alignType,
                          @FontSizeType.Type int fontSize,
                          @BoldType.Type int bold,
                          @Enlarge.Type int enlarge,
                          String text) {
        try {
            if (alignType != AlignType.DEFAULT) {
                printAlign(alignType);
            }
            if (fontSize != FontSizeType.DEFAULT) {
                printFontSize(fontSize);
            }

            if (bold != BoldType.DEFAULT) {
                printBold(bold);
            }
            if (enlarge != Enlarge.DEFAULT) {
                printEnlarge(enlarge);
            }

            mWriter.write(text);
            mWriter.write("\n");
            if (alignType != AlignType.DEFAULT) {
                printAlign(AlignType.DEFAULT);
            }

            if (fontSize != FontSizeType.DEFAULT) {
                printFontSize(FontSizeType.DEFAULT);
            }

            if (bold != BoldType.DEFAULT) {
                printBold(BoldType.DEFAULT);
            }

            if (enlarge != Enlarge.DEFAULT) {
                printEnlarge(Enlarge.DEFAULT);
            }

            mWriter.flush();
        } catch (IOException e) {

        }
    }

    /**
     * 打印文字
     *
     * @param alignType 对齐方式
     * @param fontSize  字体大小
     * @param bold      是否加粗
     * @param text
     */
    public void printText(@AlignType.Type int alignType,
                          @FontSizeType.Type int fontSize,
                          @BoldType.Type int bold,
                          String text) {
        printText(alignType, fontSize, bold, Enlarge.DEFAULT, text);
    }

    /**
     * 打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printText(String text) {
        printText(AlignType.DEFAULT, FontSizeType.DEFAULT, BoldType.DEFAULT, Enlarge.DEFAULT, text);
    }


    /**
     * 打印文字
     *
     * @param alignType 对齐方式
     * @param bold      是否加粗
     * @param text      内容
     */
    public void printText(@AlignType.Type int alignType, @BoldType.Type int bold, String text) {
        printText(alignType, FontSizeType.DEFAULT, bold, Enlarge.DEFAULT, text);
    }

    /***
     * 打印加粗文字
     * @param text
     */
    public void printTextBold(String text) {
        printText(AlignType.DEFAULT, BoldType.BOLD, text);
    }

    /***
     * 打印居中加粗文本
     * @param text
     */
    private void printTextBoldCenter(String text) {
        printText(AlignType.AT_CENTER, BoldType.BOLD, text);

    }


//    public void printBitmap(Bitmap bmp) throws IOException {
//        byte[] bmpByteArray = PrintUtil.draw2PxPoint(PrintUtil.compressPic(bmp));
//        printRawBytes(bmpByteArray);
//    }


}
