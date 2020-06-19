package com.dlh.open.test;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class LoadingDialog extends Dialog {

    private TextView tv;


    public LoadingDialog(Context context) {
        super(context, R.style.boxDialog);
        this.setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.loadingdialog);
        this.tv = (TextView) findViewById(R.id.dialogContent);
    }

    public LoadingDialog show(String content) {
        super.show();
        tv.setText(content);
        return this;
    }

    public void setContent(String content) {
        tv.setText(content);
    }
}
