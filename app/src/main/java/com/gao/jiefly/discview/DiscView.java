package com.gao.jiefly.discview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by jiefly on 2016/6/7.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class DiscView extends View implements View.OnClickListener {
    Paint paint = new Paint();
    Paint picPaint = new Paint();
    Paint circelPaint = new Paint();
    float degree = 0;
    private Bitmap circleBitmap;
    private Bitmap flagBitmap;
    int width;
    private boolean isRotate = false;
    private float size = 0.95f;
    private int flagState = 0;

    private boolean isFirstDraw = true;

    public DiscView(Context context) {
        super(context);
        init();
    }

    private void init() {
//        设置画笔抗锯齿
        paint.setAntiAlias(true);
        picPaint.setAntiAlias(true);
        circelPaint.setAntiAlias(true);
        circelPaint.setStrokeWidth(20);
        circelPaint.setColor(Color.argb(200, 36, 36, 36));
        circelPaint.setStyle(Paint.Style.STROKE);
//        设置画笔颜色
        paint.setColor(Color.argb(200, 36, 36, 36));

    }

    public DiscView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (isFirstDraw) {
            width = getMeasuredWidth();
            circleBitmap = getCircleBitmap(width);
            isFirstDraw = false;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(width / 8);
            flagBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.flag);
        }
        if (flagState == 0 && isRotate){
            flagState = 1;
            canvas.rotate(30,width/2-50,0);
            canvas.drawBitmap(flagBitmap,width/2-50,0,paint);
            
        }

        canvas.rotate(degree, width / 2, width * 3 / 4);
//        将画布移到控件下方的正方形区域中心点
        canvas.translate(width / 2, width * 3 / 4);

//        先画外层的黑胶唱片的边缘
        canvas.drawCircle(0, 0, width / 2 - width / 16, paint);
//        之后画内层的照片
        canvas.drawBitmap(circleBitmap, -(size * width * 3 / 8), -(size * width * 3 / 8), picPaint);
        canvas.drawCircle(0, 0, size * width * 3 / 8,paint);
        if (isRotate) {
            if (degree == 360) {
                degree = 0;
            } else {
                degree += 10;
            }
//        每隔0.1秒移动10°
            postInvalidateDelayed(100);
        }

        Log.e("jiefly", "invalide:" + degree);
    }

    private Bitmap getCircleBitmap(int width) {
        Bitmap pic = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
        int picWidth = pic.getWidth();
        int picHeight = pic.getHeight();
        Log.e("jiefly", "width:" + picWidth + "height:" + picHeight);
        Matrix matrix = new Matrix();
        float scale = size * 2 * (3 * width) / (picWidth * 8);
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(pic, 0, 0, picWidth, picHeight, matrix, true);
        Bitmap target = Bitmap.createBitmap(resizeBmp.getWidth(), resizeBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(size * width * 3 / 8, size * width * 3 / 8, size * width * 3 / 8, picPaint);
        picPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resizeBmp, 0, 0, picPaint);
        /*picPaint.setXfermode(null);
        picPaint.setStyle(Paint.Style.STROKE);
        picPaint.setStrokeWidth(30);
        picPaint.setColor(Color.argb(255,0,0,0));
        canvas.drawCircle(size*width*3/8,size*width*3/8,size*width*3/8,picPaint);*/
        return target;
    }

    @Override
    public void onClick(View v) {
        isRotate = !isRotate;
        invalidate();
    }
}
