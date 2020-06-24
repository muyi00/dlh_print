package com.dlh.open.print.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * 打印纸一行可打印的字数
 */
public class DefaultWords {

    @IntDef({SUM_16, SUM_24})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int SUM_16 = 16;

    public static final int SUM_24 = 24;

}
