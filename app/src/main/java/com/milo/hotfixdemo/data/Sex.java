package com.milo.hotfixdemo.data;

import androidx.annotation.IntDef;

/**
 * Title：
 * Describe：
 * Remark：
 * <p>
 * Created by Milo
 * E-Mail : 303767416@qq.com
 * 5/26/21
 */
@IntDef({Sex.MAN, Sex.WOMAN})
public @interface Sex {

    int MAN   = 0;
    int WOMAN = 1;

}
