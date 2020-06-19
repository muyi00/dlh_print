package com.dlh.open.test;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;



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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        printManage.onActivityResult(requestCode, resultCode, data);
    }

    private void print() {
        printManage.bluetoothPrintTest();
    }
}