package com.lxt.infusertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.lxt.annotation.Infuse;

@Infuse
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
