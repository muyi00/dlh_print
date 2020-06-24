package com.dlh.open.print.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * 打印机类型
 */
public class DeviceType {
    @IntDef({COMM, JQ})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }


    /**
     * 通用蓝牙打印机
     */
    public static final int COMM = 0;
    /**
     * 济强打印机
     */
    public static final int JQ = 1;

}
