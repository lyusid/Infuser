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
    public Singer singer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Infuser.bind(this);
        for (int i = 0; i < 2; i++) {
            if (i == 1)
                Log.d("TAG", "singer = " + singer);
        }
    }
}
