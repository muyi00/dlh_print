package com.dlh.open.print;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @desc: 打印机类型
 * @author: YJ
 * @time: 2020/6/22
 */
public class PrinterType {

    /***
     *纸张宽度58mm；一行打印16个汉字
     */
    public static final int PAPER_WIDTH_58_MM = 0;
    /**
     * 纸张宽度80mm；一行打印24个汉字
     */
    public static final int PAPER_WIDTH_80_MM = 1;

    private static final int SUM_16 = 16;
    private static final int SUM_24 = 24;

    @IntDef({PAPER_WIDTH_58_MM, PAPER_WIDTH_80_MM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    /***
     * 获取每行可以打印默认字体汉字个数
     * @param printerType 打印机类型
     * @return
     */
    public static int aLineOfWords(@Type int printerType) {
        if (printerType == PAPER_WIDTH_58_MM) {
            return SUM_16;
        } else if (printerType == PAPER_WIDTH_80_MM) {
            return SUM_24;
        } else {
            return SUM_16;
        }
    }

}
