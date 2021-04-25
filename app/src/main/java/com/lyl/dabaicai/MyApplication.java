package com.lyl.dabaicai;

import android.app.Application;

import com.lyl.runtime.skin.SkinManager;

/**
 * author : lyl
 * e-mail : laiyuling424@gmail.com
 * date   : 4/25/21 5:01 PM
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
