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
import android.view.View;

/**
 * Created by jiefly on 2016/6/7.
 * Email:jiefly1993@gmail.com
 * Fighting_jiiiiie
 */
public class DiscView extends View implements View.OnClickListener {
    private Paint paint = new Paint();
    private Paint picPaint = new Paint();
    private float degree = 0;
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
//  设置view中间的图片
    public void setPic(Bitmap pic) {
        this.pic = pic;
        isRotate = false;
        circleBitmap = getCircleBitmap(width);
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



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFirstDraw) {
            isFirstDraw = false;
            width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            pic = BitmapFactory.decodeResource(getResources(), R.drawable.defaule_pic);
            blackDiskBitmap = resizeBitmap(width, blackDiskBitmap);
            runCircleBitmap = resizeBitmap(width, runCircleBitmap);
            circleBitmap = getCircleBitmap(width);
            needleBitmap = resizeBitmap((int) (width * 0.34), needleBitmap);
        }
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

        if (isRotate) {
            if (degree == 360) {
                degree = 0;
            } else {
                degree += 10;
            }
//        每隔0.1秒移动10°
            postInvalidateDelayed(100);
        }
    }

    private Bitmap resizeBitmap(int dstWidth, Bitmap srcBitmap) {
//        缩放系数
        float scale = 1.0f * dstWidth / srcBitmap.getWidth();
//        缩放矩阵
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }

    private Bitmap getCircleBitmap(int width) {
        if (pic == null) {
            return null;
        }
        int picWidth = pic.getWidth();
        int picHeight = pic.getHeight();
        Log.e("jiefly", "width:" + picWidth + "height:" + picHeight);
        Matrix matrix = new Matrix();
        float scale = size * 2 * (3 * width) / (Math.min(picHeight, picWidth) * 8);
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(pic, 0, 0, picWidth, picHeight, matrix, true);
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
    }

    //  改变view的状态
    private void togglePlay() {
        isRotate = !isRotate;
        invalidate();
    }

    //   获取view状态
    public boolean isPlay() {
        return isRotate;
    }
}
