package com.milo.hotfixdemo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.milo.hotfixdemo.R;
import com.milo.hotfixdemo.utils.Constants;
import com.milo.hotfixdemo.utils.FileDexUtils;
import com.milo.hotfixdemo.utils.FileUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

}