package com.jimmyhsu.blackjackgame;

import android.app.Application;

/**
 * Created by xuyanzhe on 25/10/17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // memory leak detection, for debug only
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }
}
