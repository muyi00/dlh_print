package com.dlh.open.print.enums;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BoldType {

    @IntDef({DEFAULT, BOLD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    /**
     * 默认
     */
    public static final int DEFAULT = 0;
    /**
     * 加粗
     */
    public static final int BOLD = 1;

}
