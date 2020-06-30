package com.dlh.open.print.enums;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***
 * 内容倍宽倍高
 */
public class Enlarge {

    @IntDef({DEFAULT, WIDTH_DOUBLE, HEIGHT_DOUBLE, HEIGHT_WIDTH_DOUBLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int DEFAULT = 0;
    public static final int WIDTH_DOUBLE = 1;
    public static final int HEIGHT_DOUBLE = 2;
    public static final int HEIGHT_WIDTH_DOUBLE = 3;
}
