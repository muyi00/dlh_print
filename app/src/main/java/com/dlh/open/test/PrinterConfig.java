package com.dlh.open.test;

import android.content.Context;

public class PrinterConfig {
    private Context context;
    private KV kv;

    public PrinterConfig(Context context) {
        this.context = context;
        kv = new KV(context);
    }


    /**
     * 获取已保存的打印机设备mac
     *
     * @return
     */
    public String getPrinterAddress() {
        return kv.getString("printer_address", "DC:1D:30:99:08:77");
    }

    /**
     * 保存打印机设备mac
     *
     * @param address
     * @return
     */
    public boolean setPrinterAddress(String address) {
        kv.put("printer_address", address);
        return kv.commit();
    }


    /**
     * 获取打印纸一行的字数
     *
     * @return
     */
    public int getALineWords() {
        return kv.getInt("a_line_words", 16);
    }

    /**
     * 保存打印纸一行的字数
     *
     * @param words
     * @return
     */
    public boolean setALineWords(int words) {
        kv.put("a_line_words", words);
        return kv.commit();
    }
}
