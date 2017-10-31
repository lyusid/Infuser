package com.lxt.infuser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lxt.annotation.Infuse;
import com.lxt.library.Infuser;

public class MainActivity extends AppCompatActivity {

    @Infuse
    public Singer singer;

    @Infuse
    public String singer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Infuser.bind(this);
        Log.d("TAG", "singer = " + singer
                + "\n" + "singer2 = " + singer2);
    }

}
