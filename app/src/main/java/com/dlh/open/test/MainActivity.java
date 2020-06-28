package com.dlh.open.test;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.dlh.open.print.Print;
import com.dlh.open.print.PrinterConfig;
import com.dlh.open.print.PrinterHelper;


public class MainActivity extends BaseActivity {

    private PrinterHelper printerHelper;
    private CheckBox cb1, cb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printerHelper = new PrinterHelper(this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print();
            }
        });
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        printerHelper.init(new PrinterHelper.OnBluetoothConnectCallback() {
            @Override
            public void nonsupport(String msg) {
                ToastUtils.showShort(msg);
            }

            @Override
            public void bluetoothEnabled(String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        printerHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void print() {
        if (cb1.isChecked()) {
            printerHelper.setPrinterAddress(cb1.getText().toString());
        }
        if (cb2.isChecked()) {
            printerHelper.setPrinterAddress(cb2.getText().toString());
        }
        printerHelper.print(new PrinterHelper.OnPrintTaskCallback() {
            @Override
            public void configBondedDevice(String msg) {
                ToastUtils.showShort(msg);
            }

            @Override
            public void error(String er) {
                ToastUtils.showShort(er);
            }

            @Override
            public void asyncPrint(Print printer, int oneLineOfWords) {
                CommPrintBill commPrintBill = new CommPrintBill(MainActivity.this, printer, oneLineOfWords);
                commPrintBill.printTest();
            }
        });

    }
}