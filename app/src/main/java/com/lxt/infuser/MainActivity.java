package com.lxt.infuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lxt.annotation.Infuse;
import com.org.lxt.infuse.InfuserFactory;

public class MainActivity extends AppCompatActivity {

    @Infuse
    public Singer singer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < 2; i++) {
            if (i == 1)
                InfuserFactory.bind(this);
            Log.d("TAG", "singer = " + singer);
        }
    }
}
