package com.gao.jiefly.discview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DiscView discView = (DiscView) findViewById(R.id.id_disc_view);
        assert discView != null;
        discView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discView.onClick(v);
            }
        });
    }
}
