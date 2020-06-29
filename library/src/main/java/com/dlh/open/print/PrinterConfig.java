package com.dlh.open.print;

import android.content.Context;
import android.content.SharedPreferences;

import com.dlh.open.print.enums.PaperWidthType;

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
        return printer_sp.getString("printer_address", "");
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
     * 获取打印纸宽度类型
     *
     * @return
     */
    public int getPaperWidthType() {
        return printer_sp.getInt("paper_width_type", PaperWidthType.Width_58);
    }

    /**
     * 保存打印纸宽度类型
     *
     * @param type
     * @return
     */
    public boolean setPaperWidthType(@PaperWidthType.Type int type) {
        return printer_sp.edit().putInt("paper_width_type", type).commit();
    }

}
