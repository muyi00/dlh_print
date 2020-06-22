package com.dlh.open.test;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dlh.open.print.PrintManage;
import com.dlh.open.print.Printer;


public class MainActivity extends BaseActivity {

    private PrintManage printManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printManage = new PrintManage(this);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print();
            }
        });

        printManage.setOnPrintTaskCallback(new PrintManage.OnPrintTaskCallback() {
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
            public void asyncPrint(Printer printer) {
                PrintBill printBill= new PrintBill(MainActivity.this, printer);
                printBill.printTest();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        printManage.onActivityResult(requestCode, resultCode, data);
    }

    private void print() {
        printManage.startPrinterTask();
    }
}