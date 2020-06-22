package com.dlh.open.print;

import android.content.Context;
import android.content.SharedPreferences;

public class PrinterConfig {

    private final SharedPreferences printer_sp;

    public PrinterConfig(Context context) {
        printer_sp = context.getSharedPreferences("printer_sp", Context.MODE_PRIVATE);
    }


    /**
     * 获取已保存的打印机设备mac
     *
     * @return
     */
    public String getPrinterAddress() {
        //"DC:1D:30:99:08:77"
        //"DC:0D:30:45:59:AB"
        return printer_sp.getString("printer_address", "DC:0D:30:45:59:AB");
    }

    /**
     * 保存打印机设备mac
     *
     * @param address
     * @return
     */
    public boolean setPrinterAddress(String address) {
        return printer_sp.edit().putString("printer_address", address).commit();
    }


    /**
     * 获取打印纸一行的字数
     *
     * @return
     */
    public int getALineWords() {
        return printer_sp.getInt("a_line_words", 16);
    }

    /**
     * 保存打印纸一行的字数
     *
     * @param words
     * @return
     */
    public boolean setALineWords(int words) {
        return printer_sp.edit().putInt("a_line_words", words).commit();
    }
}
