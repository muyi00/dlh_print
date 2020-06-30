package com.dlh.open.print.enums;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @desc: 一行默认字数
 * @author: YJ
 * @time: 2020/6/30
 */
public class DefaultWords {


    @IntDef({SUM_16, SUM_24})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    /**
     * 一行16字
     */
    public static final int SUM_16 = 16;
    /**
     * 一行24字
     */
    public static final int SUM_24 = 24;

}
