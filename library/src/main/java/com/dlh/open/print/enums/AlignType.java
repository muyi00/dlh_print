package com.dlh.open.print.enums;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 文本对齐类型
 */
public class AlignType {

    @IntDef({DEFAULT, AT_CENTER, AT_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    /**
     * 左对齐
     */
    public static final int DEFAULT = 0;
    /**
     * 居中对齐
     */
    public static final int AT_CENTER = 1;
    /**
     * 右对齐
     */
    public static final int AT_RIGHT = 2;

}
