package com.dlh.open.print;

import com.dlh.open.print.enums.DeviceType;

/***
 * 打印设备
 */
public class PrintDeviceInfo {

    /***
     * 设备类型
     */
    @DeviceType.Type
    private int deviceType;
    /***
     * 设备名称前缀
     */
    private String devicePrefix;
    /***
     * 默认字体一行可打印的字数
     */
    private int wordCount;

    public PrintDeviceInfo(@DeviceType.Type int deviceType, String devicePrefix, int wordCount) {
        this.deviceType = deviceType;
        this.devicePrefix = devicePrefix;
        this.wordCount = wordCount;
    }

    @DeviceType.Type
    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(@DeviceType.Type int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDevicePrefix() {
        return devicePrefix;
    }

    public void setDevicePrefix(String devicePrefix) {
        this.devicePrefix = devicePrefix;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }
}
