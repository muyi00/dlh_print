package com.dlh.open.print;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class PrinterLoadingDialog extends Dialog {

    private TextView tv;

    public PrinterLoadingDialog(Context context) {
        super(context, R.style.boxPrinterDialog);
        this.setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.loading_printer_dialog);
        this.tv = (TextView) findViewById(R.id.dialogContent);
    }

    public PrinterLoadingDialog show(String content) {
        super.show();
        tv.setText(content);
        return this;
    }

    public void setContent(String content) {
        tv.setText(content);
    }
}
