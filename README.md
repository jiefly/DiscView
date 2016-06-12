# DiscView:
一个仿网易云音乐播放界面旋转唱片的自定义View
======================================
效果图：
---------

![image](https://raw.githubusercontent.com/jiefly/DiscView/master/GIF.gif)

view的宽高比被固定为1：1.25。
```
super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(400, 500);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(heightSpecSize * 4 / 5, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, widthSpecSize * 5 / 4);
        } else {
            if (widthSpecSize * 1.25 != heightSpecSize) {
                setMeasuredDimension(widthSpecSize, widthSpecSize * 5 / 4);
            }
        }
```
通过DiscView的setPic()方法可以更换view中间的图片
```java
 Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
     discView.setPic(bmp);
```
通过DiscView的onclick方法可以让view停止/旋转
```
discView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discView.onClick(v);
            }
        });
```


