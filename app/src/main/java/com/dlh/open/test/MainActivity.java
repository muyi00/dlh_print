package com.dlh.open.test;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.dlh.open.print.CommPrintUtil;
import com.dlh.open.print.PrinterConfig;
import com.dlh.open.print.PrinterConnectionHelper;


public class MainActivity extends BaseActivity {

    private PrinterConnectionHelper connectionHelper;
    private CheckBox cb1, cb2;
    private PrinterConfig printerConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionHelper = new PrinterConnectionHelper(this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print();
            }
        });
        printerConfig = new PrinterConfig(this);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        connectionHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void print() {
        if (cb1.isChecked()) {
            printerConfig.setPrinterAddress(cb1.getText().toString());
        }
        if (cb2.isChecked()) {
            printerConfig.setPrinterAddress(cb2.getText().toString());
        }
        connectionHelper.printInit(new PrinterConnectionHelper.OnPrintTaskCallback() {
            @Override
            public void configBondedDevice(String msg) {
                ToastUtils.showShort(msg);
            }

            @Override
            public void hint(String msg) {
                ToastUtils.showShort(msg);
            }

            @Override
            public void error(String er) {
                ToastUtils.showShort(er);
            }

            @Override
            public void asyncPrint(CommPrintUtil printer, int oneLineOfWords) {
                CommPrintBill commPrintBill = new CommPrintBill(MainActivity.this, printer, oneLineOfWords);
                commPrintBill.printTest();
            }
        });
    }
}