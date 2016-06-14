package com.gao.jiefly.discview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiefly on 2016/6/7.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class DiscView extends View implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "jiefly";
    //  播放列表list
    private List<Integer> uriList = new ArrayList<>();
    private Paint paint = new Paint();
    private Paint picPaint = new Paint();
    private float degree = 0;
    private int rotateWay = 1;
    private Map<String, Bitmap> showPics = new HashMap<>();
    //    index 当前的图片在uriList中的位置
    private int picIndex = 3;
    //  中间的裁减为圆形之后的图片
    private Bitmap circleBitmap;
    //    中圈黑环的图片
    private Bitmap blackDiskBitmap;
    //    外圈白色图片
    private Bitmap runCircleBitmap;
    //    view上部分指示器图片
    private Bitmap needleBitmap;
    //  view的宽度
    private int width;
    //  中间图片的原图
    private Bitmap pic;
    //  转动标志位
    private boolean isRotate = false;
    //图片的缩放比
    private float size = 0.88f;
    //    是否是第一次onDraw
    private boolean isFirstDraw = true;

    private boolean next = false;
    private boolean prev = false;

    //  唱片黑色边框旋转矩阵
    private Matrix boundMatrix = new Matrix();
    //    图片旋转矩阵
    private Matrix picMatrix = new Matrix();
    //    needle旋转矩阵
    private Matrix needleMatrix = new Matrix();
    private long startTime;
    private float translateValue = 0;


    //    设置播放列表
    public void setUriList(List<Integer> uriList) {
        this.uriList = uriList;
//        如果是在onMeasure之前设置了uriList，则不能直接在这里设置pic，因为此时width还没有measure出来
        if (!isFirstDraw)
            initShowPics();
    }

    //  设置view中间的图片
    public void setPic(Bitmap pic) {
        this.pic = pic;
        isRotate = false;
        showPics.put("current",getCircleBitmap(width,pic));
        degree = 0;
        invalidate();
    }


    public DiscView(Context context) {
        super(context);
        init();
    }

    private void init() {
//        设置画笔抗锯齿
        paint.setAntiAlias(true);
        picPaint.setAntiAlias(true);
        picPaint.setFilterBitmap(true);
        blackDiskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_disc);
        runCircleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fm_run_circle3);
        needleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_needle);
    }

    public DiscView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DiscView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
//        设置wrapContext时的size，保证宽高比为1：1.25
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
    }


    // 通过旋转画布的方式对图片进行旋转
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFirstDraw) {
            isFirstDraw = false;
            width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            pic = BitmapFactory.decodeResource(getResources(), R.drawable.defaule_pic);
            blackDiskBitmap = resizeBitmap(width, blackDiskBitmap);
            runCircleBitmap = resizeBitmap(width, runCircleBitmap);
            circleBitmap = getCircleBitmap(width, pic);
            needleBitmap = resizeBitmap((int) (width * 0.34), needleBitmap);
            if (showPics.size() == 0) {
                initShowPics();
            }
        }
        switch (rotateWay) {
            case 0:
                //      将坐标系零点移到正确的位置（y:view下半部分正方形区域）
                canvas.translate(getPaddingLeft(), (float) (0.25 * width + getPaddingTop()));
//      旋转画布
                canvas.rotate(degree, width / 2, width / 2);
//      画最外层的一小圈模糊透明的圈圈
                canvas.drawBitmap(runCircleBitmap, 0, 0, paint);
//        画中间的黑色圈圈
                canvas.drawBitmap(blackDiskBitmap, 0, 0, paint);
//       画最中间的图片
                canvas.drawBitmap(circleBitmap, (float) (width * (1 - size * 0.75) / 2), (float) (width * (1 - size * 0.75) / 2), new Paint());
//        将画布的旋转角度抵消
                canvas.rotate(-degree, width / 2, width / 2);
//        如果view的状态是转动时，将画布以指示器的支点为中心旋转20度
//        否则不旋转画布
                if (isRotate) {
                    canvas.rotate(-20, width / 2, (float) (-0.25 * width));
                }
//        画view上部分的指示器
                canvas.drawBitmap(needleBitmap, (float) (width / 2 - 6 * needleBitmap.getWidth() / 31), (float) (-0.25 * width - 24 * needleBitmap.getHeight() / 187), paint);
                break;
            case 1:
                boundMatrix.setRotate(degree, blackDiskBitmap.getWidth() / 2, blackDiskBitmap.getHeight() / 2);
                picMatrix.setRotate(degree, showPics.get("current").getWidth() / 2, showPics.get("current").getHeight() / 2);
                if (next) {
                    long delaTime = System.currentTimeMillis() - startTime;
                    if (delaTime < 1000 || translateValue < width) {
                        if (delaTime > 900) {
                            translateValue = width;
                        } else
                            translateValue = width * delaTime / 1000;
                    } else {
                        next = false;
                        isRotate = true;
                        picIndex++;
                        translateValue = 0;
                        degree = 0;
                        refreshShowPic(1);
                    }
                }
                if (prev) {
                    long delaTime = System.currentTimeMillis() - startTime;
                    if (delaTime < 1000 || Math.abs(translateValue) < width) {
                        if (delaTime > 900) {
                            translateValue = -width;
                        } else
                            translateValue = -width * delaTime / 1000;
                    } else {
                        prev = false;
                        isRotate = true;
                        picIndex--;
                        translateValue = 0;
                        degree = 0;
                        refreshShowPic(0);
                    }
                }
                canvas.translate(getPaddingLeft() - translateValue, getPaddingTop() + width / 4);
                canvas.drawBitmap(runCircleBitmap, 0, 0, paint);
                canvas.drawBitmap(blackDiskBitmap, boundMatrix, paint);
                if (next) {
                    canvas.translate(width, 0);
                    canvas.drawBitmap(blackDiskBitmap, boundMatrix, paint);
                    canvas.translate(-width, 0);
                }
                if (prev) {
                    canvas.translate(-width, 0);
                    canvas.drawBitmap(blackDiskBitmap, boundMatrix, paint);
                    canvas.translate(width, 0);
                }
                canvas.translate((float) ((1 - 0.75 * size) * width / 2), (float) ((1 - 0.75 * size) * width) / 2);
                canvas.drawBitmap(showPics.get("current"), picMatrix, paint);
                if (next) {
                    canvas.translate(width, 0);
                    canvas.drawBitmap(showPics.get("next"), picMatrix, paint);
                    canvas.translate(-width, 0);
                }
                if (prev) {
                    canvas.translate(-width, 0);
                    canvas.drawBitmap(showPics.get("prev"), picMatrix, paint);
                    canvas.translate(width, 0);
                }
                canvas.translate((float) (width / 2 - 6 * needleBitmap.getWidth() / 31 - ((1 - 0.75 * size) * width / 2) + translateValue), (float) (-0.25 * width - 24 * needleBitmap.getHeight() / 187 - ((1 - 0.75 * size) * width / 2)));
                if (!isRotate || next || prev) {
                    needleMatrix.setRotate(-20, 6 * needleBitmap.getWidth() / 31, 24 * needleBitmap.getHeight() / 187);
                } else {
                    needleMatrix.setRotate(0);
                }
                canvas.drawBitmap(needleBitmap, needleMatrix, paint);
        }

        if (isRotate) {
            if (degree == 360) {
                degree = 0;
            } else {
                degree += 4;
            }
//        每次刷新移动10°
            invalidate();
        }

        if (next || prev)
            invalidate();

//        Log.e(TAG, "onDraw");
    }

    private void refreshShowPic(int i) {
        switch (i) {
            case 0:
                if (picIndex != 0) {
                    showPics.put("current", getCircleBitmap(width, BitmapFactory.decodeResource(getResources(), uriList.get(picIndex))));
                    showPics.put("next", getCircleBitmap(width, BitmapFactory.decodeResource(getResources(), uriList.get(picIndex + 1))));

                    showPics.put("prev", getCircleBitmap(width, BitmapFactory.decodeResource(getResources(), uriList.get(picIndex - 1))));
                }
                break;
            case 1:
                if (picIndex + 1 < uriList.size()) {
                    showPics.put("current", getCircleBitmap(width, BitmapFactory.decodeResource(getResources(), uriList.get(picIndex))));
                    showPics.put("next", getCircleBitmap(width, BitmapFactory.decodeResource(getResources(), uriList.get(picIndex + 1))));
                    showPics.put("prev", getCircleBitmap(width, BitmapFactory.decodeResource(getResources(), uriList.get(picIndex - 1))));
                }
                break;
            default:
                showPics.put("prev", getCircleBitmap(width, BitmapFactory.decodeResource(getResources(), R.drawable.defaule_pic)));
                showPics.put("current", getCircleBitmap(width, BitmapFactory.decodeResource(getResources(), uriList.get(1))));
                showPics.put("next", getCircleBitmap(width, BitmapFactory.decodeResource(getResources(), uriList.get(2))));
                break;
        }
    }

    private void initShowPics() {
        refreshShowPic(3);
    }

    @Override
    public void setAnimation(Animation animation) {
        super.setAnimation(animation);

    }

    @Override
    public void startAnimation(Animation animation) {
        super.startAnimation(animation);
    }

    private Bitmap resizeBitmap(int dstWidth, Bitmap srcBitmap) {
//        缩放系数
        float scale = 1.0f * dstWidth / srcBitmap.getWidth();
//        缩放矩阵
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }

    private Bitmap getCircleBitmap(int width, Bitmap srcBitmap) {
        if (srcBitmap == null) {
            return null;
        }
        int picWidth = srcBitmap.getWidth();
        int picHeight = srcBitmap.getHeight();
        Log.e("jiefly", "width:" + picWidth + "height:" + picHeight);
        Matrix matrix = new Matrix();
        float scale = size * 2 * (3 * width) / (Math.min(picHeight, picWidth) * 8);
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(srcBitmap, 0, 0, Math.min(picHeight, picWidth), Math.min(picHeight, picWidth), matrix, true);
//        新建一个正方形的bitmap
        Bitmap target = Bitmap.createBitmap(Math.min(resizeBmp.getHeight(), resizeBmp.getWidth()), Math.min(resizeBmp.getHeight(), resizeBmp.getWidth()), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(size * width * 3 / 8, size * width * 3 / 8, size * width * 3 / 8, picPaint);
        picPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        分两种情况来截取正方形的图片
        if (resizeBmp.getHeight() > resizeBmp.getWidth()) {
            canvas.drawBitmap(resizeBmp, 0, (resizeBmp.getHeight() - resizeBmp.getWidth()) / 2, picPaint);
        } else {
            canvas.drawBitmap(resizeBmp, (resizeBmp.getWidth() - resizeBmp.getHeight()) / 2, 0, picPaint);
        }
//        将picPaint的Xfermode设置为空，以便下次切换图片的时候使用

        picPaint.setXfermode(null);
        Log.e("jiefly", ":" + (float) target.getWidth() / width);
        return target;
    }

    @Override
    public void onClick(View v) {
        togglePlay();
//        next();
//        prev();
    }

    //  改变view的状态
    private void togglePlay() {
        isRotate = !isRotate;
        next = false;
        prev = false;
        invalidate();
    }

    //   获取view状态
    public boolean isPlay() {
        return isRotate;
    }

    //    next
    public void next() {
        /*ValueAnimator valueAnimator = ObjectAnimator.ofFloat(this,"rotation",0,360);
        this.setPivotX(width/2);
        this.setPivotY((float) (0.75*width));
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setDuration(10000).start();*/
//        暂停旋转
        isRotate = false;
        invalidate();

        next = true;
        startTime = System.currentTimeMillis();
        invalidate();
    }

    //    prev
    public void prev() {
        isRotate = false;
        invalidate();

        prev = true;
        startTime = System.currentTimeMillis();
        invalidate();
    }

    float startX;

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
}
