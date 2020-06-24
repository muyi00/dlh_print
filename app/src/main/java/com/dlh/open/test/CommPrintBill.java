package com.dlh.open.test;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.dlh.open.print.Print;
import com.dlh.open.print.PrinterConfig;
import com.dlh.open.print.enums.AlignType;
import com.dlh.open.print.enums.BoldType;
import com.dlh.open.print.enums.DefaultWords;
import com.dlh.open.print.enums.Enlarge;
import com.dlh.open.print.enums.FontSizeType;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class CommPrintBill {

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public Context mContext;
    private BluetoothSocket socket;
    private Print printUtil;
    private String corpName;
    private PrinterConfig printerConfig;
    private HashMap<Integer, String> csmTypeMap;
    @DefaultWords.Type
    private int oneLineOfWords = DefaultWords.SUM_16;

    public CommPrintBill(Context mContext, Print printUtil, @DefaultWords.Type int oneLineOfWords) {
        this.mContext = mContext;
        this.oneLineOfWords = oneLineOfWords;
        this.printUtil = printUtil;
        printerConfig = new PrinterConfig(mContext);
    }

    /***
     * 打印测试
     */
    public void printTest() {
        printUtil.printLine();
        printUtil.printText(AlignType.AT_CENTER, FontSizeType.DEFAULT, BoldType.DEFAULT, Enlarge.HEIGHT_WIDTH_DOUBLE, "没看错，你选的就是我！");
        printUtil.printText(AlignType.AT_CENTER, BoldType.DEFAULT, printerConfig.getPrinterAddress());
        printUtil.printLine(3);
    }

}
