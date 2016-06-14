# DiscView:
一个仿网易云音乐播放界面旋转唱片的自定义View
======================================
效果图：
---------

![image](https://raw.githubusercontent.com/jiefly/DiscView/master/GIF_new.gif)
左右滑动可以切换图片（带有切换动画）
# 设置滑动手势
```
//MainActivity
discView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                discView.onTouch(v,event);
                return true;
            }
        });
//DiscView
@Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() - startX > width / 10) {
                    Log.e(TAG, "right swipe");
                    prev();
                }
                if (event.getX() < startX && (startX - event.getX()) > width / 10) {
                    Log.e(TAG, "left swipe");
                    next();
                }
                if (Math.abs(1.0d * (event.getX() - startX)) < width / 100) {
                    onClick(v);
                }
                break;
        }
        return false;
    }
```
# 设置切换时的图片
```
List<Integer> picList = new ArrayList<>();
        picList.add(R.drawable.pic);

        picList.add(R.drawable.pic_1);

        picList.add(R.drawable.pic_2);

        picList.add(R.drawable.pic_3);

        picList.add(R.drawable.pic_4);

        picList.add(R.drawable.pic_5);
        discView.setUriList(picList);
```

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
#通过点击view可以让view停止/旋转



