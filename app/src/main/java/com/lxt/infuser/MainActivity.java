package com.lxt.infuser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lxt.annotation.Infuse;
import com.lxt.annotation.InfuseChar;
import com.lxt.annotation.InfuseDouble;
import com.lxt.annotation.InfuseFloat;
import com.lxt.annotation.InfuseInt;
import com.lxt.annotation.InfuseLong;
import com.lxt.annotation.InfuseString;
import com.lxt.library.Infuser;

public class MainActivity extends AppCompatActivity {

    @Infuse
    public Singer singer;

    @Infuse
    public String singer2;

    @InfuseString({"Taiker", "Boy"})
    public Singer singer3;

    @InfuseInt({1, 2})
    public Singer singer4;

    @InfuseChar({'6'})
    public Singer singer5;

    @InfuseDouble({20000, 222222, 250000})
    public Singer singer6;

    @InfuseFloat({180.0f})
    public Singer singer7;

    @InfuseLong({1111132131L})
    public Singer singer8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Infuser.bind(this);
        new InnerClass();
    }

    class InnerClass {

        @InfuseString({"Ivy", "girl"})
        public Singer singer9;

        InnerClass() {
            Infuser.bind(this);
        }
    }
}
