package com.lxt.infuser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lxt.annotation.Infuse;
import com.lxt.annotation.InfuseInt;
import com.lxt.annotation.InfuseString;
import com.lxt.library.Infuser;

public class MainActivity extends AppCompatActivity {

    @Infuse
    public Singer singer;

    @Infuse
    public String singer2;

    @InfuseString({"Taiker", "Boy"})
    public Singer singer3;

    @InfuseInt({1,2})
    public Singer singer4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Infuser.bind(this);
    }

}
