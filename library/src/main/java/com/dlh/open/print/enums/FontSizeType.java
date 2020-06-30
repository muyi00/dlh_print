package com.dlh.open.print.enums;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FontSizeType {

    @IntDef({DEFAULT, x24, x32, x48, x64})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    /**
     * 默认字体大小default
     */
    public static final int DEFAULT = 0;
    public static final int x24 = 24;//<-----
    public static final int x32 = 32;
    public static final int x48 = 48;
    public static final int x64 = 64;
}
