package com.lxt.infuser;

import android.util.Log;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/26
 */

public class Singer {

    public Singer() {
        Log.d("TAG", "New a Singer");
    }

    public Singer(int age, int eye) {
        Log.d("TAG", "New a Singer age " + age + " eye " + eye);
    }

    public Singer(String name, String gender) {
        Log.d("TAG", "New a Singer name " + name + " gender " + gender);
    }

    public Singer(long hairCount) {
        Log.d("TAG", String.format("New a singer with %s hair", hairCount));
    }

    public Singer(double salaryOne, double salaryTwo, double salaryThree) {
        Log.d("TAG", String.format("New a singer who owns $%s in the first month,$%s in the second month " +
                "and $%s in the third month", salaryOne, salaryTwo, salaryThree));
    }

    public Singer(char luckyNum) {
        Log.d("TAG", "New a singer whose lucky number is " + luckyNum);
    }

    public Singer(float heigth) {
        Log.d("TAG", String.format("New a singer who is %s cm", heigth));
    }
}
