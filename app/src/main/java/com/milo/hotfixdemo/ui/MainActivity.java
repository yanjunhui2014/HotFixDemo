package com.milo.hotfixdemo.ui;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.milo.hotfixdemo.R;
import com.milo.hotfixdemo.data.Sex;
import com.milo.hotfixdemo.hotfixtools.Constants;
import com.milo.hotfixdemo.hotfixtools.FileDexUtils;
import com.milo.hotfixdemo.hotfixtools.FileUtils;

import java.io.File;
import java.lang.annotation.Annotation;

@TargetApi(23)
public class MainActivity extends AppCompatActivity {

    public MainActivity(){}

    public MainActivity(int a){

    }

    public MainActivity(int a, String b){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    PopupWindow window = new PopupWindow();
                    window.setContentView(new View(MainActivity.this));
                    window.showAsDropDown(findViewById(R.id.mBtnTest));

                    WindowManager windowManager = getWindowManager();
                    windowManager.removeViewImmediate(window.getContentView());

                    window.dismiss();
                    window.dismiss();
                } catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        }, 3000);


        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        findViewById(R.id.mBtnTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(FixTestActivity.createIntent(MainActivity.this));
            }
        });

        findViewById(R.id.mBtnDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPatch();
            }
        });

        findViewById(R.id.mBtnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDexUtils.loadFixedDex(MainActivity.this.getApplicationContext());
            }
        });

        testMethod(Sex.MAN);
    }

    private void downloadPatch() {
        File fromFile = new File(Environment.getExternalStorageDirectory() + "/1test/patch.zip");
        if (!fromFile.exists()) {
            Toast.makeText(this, "未发现补丁包", Toast.LENGTH_SHORT).show();
            return;
        }
        File toFile = new File(getDir(Constants.DEX_DIR, Context.MODE_PRIVATE).getAbsolutePath() + "/" + Constants.DEX_NAME);

        if (toFile.exists()) {
            toFile.delete();
            Log.d("tag", "删除原补丁");
        }

        if (FileUtils.copy(fromFile.getAbsolutePath(), toFile.getAbsolutePath())) {
            Toast.makeText(this, "补丁拷贝成功至:" + toFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }
    }

    public void testMethod(@Sex int sex){
        int a = sex;
    }

}