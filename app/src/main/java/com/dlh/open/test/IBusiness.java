package com.dlh.open.test;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.content.Intent;

public interface IBusiness extends GenericLifecycleObserver {

    void init();

    void onNewIntent(Intent intent);


    void onActivityResult(int requestCode, int resultCode, Intent data);

}
