package com.dlh.open.print.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @desc: 打印纸的宽度
 * @author: YJ
 * @time: 2020/6/29
 */
public class PaperWidthType {

    @IntDef({Width_58, Width_80})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int Width_58 = 0;
    public static final int Width_80 = 1;


    /***
     * 获取一行可打印标准字号的字数
     * @param type
     * @return
     */
    public static int getWords(@Type int type) {
        int words = 0;
        switch (type) {
            case Width_58:
                words = 16;
                break;
            case Width_80:
                words = 24;
                break;
            default:
                words = 16;
                break;
        }
        return words;
    }
}
