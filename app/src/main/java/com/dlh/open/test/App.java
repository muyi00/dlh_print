package com.dlh.open.test;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    public Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        ToastUtils.init(applicationContext);
    }
}
