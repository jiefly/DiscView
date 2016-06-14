package com.gao.jiefly.discview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private static final int GET_PICTURE = 1;

    DiscView discView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         discView = (DiscView) findViewById(R.id.id_disc_view);
        discView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                discView.onTouch(v,event);
                return true;
            }
        });
        List<Integer> picList = new ArrayList<>();
        picList.add(R.drawable.pic);

        picList.add(R.drawable.pic_1);

        picList.add(R.drawable.pic_2);

        picList.add(R.drawable.pic_3);

        picList.add(R.drawable.pic_4);

        picList.add(R.drawable.pic_5);
        discView.setUriList(picList);

//        点击事件由Discview的touch事件处理了
        /*discView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discView.onClick(v);
            }
        });*/
        Button button = (Button) findViewById(R.id.button);
        if (button != null) {
            button.setText("改变图片");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentGetPic = new Intent("android.intent.action.GET_CONTENT");
                    intentGetPic.setType("image/*");
                    intentGetPic.putExtra("crop", true);
                    intentGetPic.putExtra("scale", true);
                    startActivityForResult(intentGetPic,GET_PICTURE);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == GET_PICTURE){
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    discView.setPic(bmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
