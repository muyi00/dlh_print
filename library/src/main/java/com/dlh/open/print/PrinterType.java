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
    /**
     * 标准文字，一行16字
     */
    public static final int COMM_16 = 0;
    /***
     *标准文字，一行24字
     */
    public static final int COMM_24 = 1;

    @IntDef({COMM_16, COMM_24})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

}
