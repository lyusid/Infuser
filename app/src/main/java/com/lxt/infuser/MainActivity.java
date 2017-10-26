package com.lxt.infuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.lxt.annotation.Infuse;

public class MainActivity extends AppCompatActivity {

    @Infuse
    private Singer singer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
