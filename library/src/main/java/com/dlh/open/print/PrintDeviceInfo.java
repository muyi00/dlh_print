package com.dlh.open.print;

/***
 * 打印设备
 */
public class PrintDeviceInfo {


    /***
     * 设备名称前缀
     */
    private String devicePrefix;
    /***
     * 默认字体一行可打印的字数
     */
    private int wordCount;

    public PrintDeviceInfo(String devicePrefix, int wordCount) {
        this.devicePrefix = devicePrefix;
        this.wordCount = wordCount;
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
