package com.milo.hotfixdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.milo.hotfixdemo.R;
import com.milo.hotfixdemo.utils.MathUtils;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 2021/4/5
 */
public class FixTestActivity extends AppCompatActivity {

    private Button mBtnCalculation;

    public static Intent createIntent(Context context){
        return new Intent(context, FixTestActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_test);

        mBtnCalculation =  findViewById(R.id.mBtnCalculation);
        mBtnCalculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FixTestActivity.this, "" + MathUtils.test(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
