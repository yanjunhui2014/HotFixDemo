package com.milo.hotfixdemo;

import android.app.Application;
import android.content.Context;

import com.milo.hotfixdemo.utils.FileDexUtils;
import com.milo.hotfixdemo.utils.FileUtils;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 2021/4/5
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        FileDexUtils.loadFixedDex(this);
//    }

}
