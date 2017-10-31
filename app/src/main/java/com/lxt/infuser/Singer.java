package com.lxt.infuser;

import android.util.Log;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/26
 */

public class Singer {

    public Singer() {
        Log.d("TAG", "New Singer");
    }

    public Singer(int age, int eye) {
        Log.d("TAG", "New Singer age " + age + " eye " + eye);
    }

    public Singer(String name, String gender) {
        Log.d("TAG", "New Singer name " + name + " gender " + gender);
    }
}
