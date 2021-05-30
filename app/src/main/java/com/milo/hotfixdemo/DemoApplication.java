package com.milo.hotfixdemo;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.milo.hotfixdemo.hotfixtools.FileDexUtils;

import java.io.Serializable;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 2021/4/5
 */
public class DemoApplication extends MultiDexApplication implements Serializable {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        FileDexUtils.loadFixedDex(this);
    }

}
