package com.dlh.open.print;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @desc: 打印机制造企业
 * @author: YJ
 * @time: 2020/6/19
 */
public class Enterprise {

    /***
     * 通用企业
     */
    public static final int COMM = 0;
    /***
     * 济强打印机
     */
    public static final int JQ = 1;

    @IntDef({COMM, JQ})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }


    /***
     * 获取企业类
     * @param type
     * @return
     */
    public static int getType(Printer.PrinterType type) {
        switch (type) {
            case COMM_16:
            case COMM_24:
                return COMM;
            case JQ_VMP02:
            case JQ_VMP02_P:
            case JQ_JLP351:
            case JQ_JLP351_IC:
            case JQ_ULT113x:
            case JQ_ULT1131_IC:
                return JQ;
        }
        return COMM;
    }


}
