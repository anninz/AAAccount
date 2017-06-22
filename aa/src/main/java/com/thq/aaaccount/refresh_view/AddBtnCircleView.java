package com.thq.aaaccount.refresh_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.thq.aaaccount.R;
import com.thq.aaaccount.Utils.Utils;

public class AddBtnCircleView extends View implements Animatable {

    Context mContext;

    /**
     * @param context
     */
    public AddBtnCircleView(Context context) {
        this(context, null);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public AddBtnCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    Paint paint=new Paint();


    private void createBitmaps() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        rotatBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_add_bill_selected, options);
        rotatBitmap = Bitmap.createScaledBitmap(rotatBitmap, width, height, true);
    }


    public void setPercent(float percent, boolean invalidate) {
        setPercent(percent);
        Log.i("THWQ", "setPercent: " + percent);
        if (invalidate) setRotate(percent);
    }


    public void setPercent(float percent) {
        mPercent = percent;
    }

    public void setRotate(float rotate) {
        mRotate = rotate;
        invalidate();
    }

    public void resetOriginals() {
        setPercent(0);
        setRotate(0);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (rotatBitmap != null) {
            ifNeedUpdateMatrix();
            //抗锯齿
            canvas.setDrawFilter(pfd);

            canvas.drawBitmap(rotatBitmap, matrix, paint);
        }
        super.onDraw(canvas);
    }

    Matrix matrix = new Matrix();
    private void ifNeedUpdateMatrix() {

        float dragPercent = mPercent;
        if (dragPercent > 1.0f) { // Slow down if pulling over set height
            dragPercent = (dragPercent + 9.0f) / 10;
        }

        float sunRotateGrowth = SUN_INITIAL_ROTATE_GROWTH;


        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            sunRotateGrowth += (SUN_FINAL_ROTATE_GROWTH - SUN_INITIAL_ROTATE_GROWTH) * scalePercent;
        }

        bitmapSize = rotatBitmap.getHeight();
        scaleX = scaleY = 1;//(float) (height - height/5) / bitmapSize;
        transform = (bitmapSize - height) / 2f;

//        Matrix matrix = new Matrix();
        // 设置转轴位置
        matrix.setTranslate((float)bitmapSize / 2, (float)bitmapSize / 2);

        // 开始转
        matrix.preRotate((isRefreshing ? -360 : 360) * mRotate * (isRefreshing ? 1 : sunRotateGrowth));
        // 转轴还原
        matrix.preTranslate(-(float)bitmapSize / 2, -(float)bitmapSize / 2);

        //scale view
        matrix.postScale(scaleX, scaleY, bitmapSize / 2f, bitmapSize / 2f);

        // 将位置送到view的中心
        matrix.postTranslate(-transform,  -transform);
    }

    /**
     * 抗锯齿
     */
    private PaintFlagsDrawFilter pfd;

    /**
     * 初始化handler与速度计算器
     */
    private void init() {
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        // 默认是有一张图片的

        initSize();
        createBitmaps();
        setupAnimations();
    }


    /**
     * 图片的宽度
     */
    int width;

    /**
     * 图片的高度
     */
    int height;

    /**
     * 旋转的图片
     */
    Bitmap rotatBitmap;

    private void initSize() {
        width = getWidth();
        height = getHeight();
        if (width < 1) {
            width = height = Utils.convertDpToPixel(60);
        }
    }

    float scaleX,scaleY;
    int bitmapSize;
    float transform;

    private float mPercent = 0.0f;
    private float mRotate = 0.0f;


    private boolean isRefreshing = false;
    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void start() {
        mAnimation.reset();
        isRefreshing = true;
        startAnimation(mAnimation);
    }

    @Override
    public void stop() {
        clearAnimation();

        if (mPercent > 0) {
            mAnimationEnd.reset();
            startAnimation(mAnimationEnd);
        } else {
            isRefreshing = false;
            resetOriginals();
        }

    }

    public void stop(boolean force) {
        clearAnimation();
        isRefreshing = false;
        resetOriginals();
    }

    private static final float SCALE_START_PERCENT = 0.5f;
    private static final int ANIMATION_DURATION = 1000;

    private static final float SUN_FINAL_SCALE = 0.75f;
    private static final float SUN_INITIAL_ROTATE_GROWTH = 1.2f;
    private static final float SUN_FINAL_ROTATE_GROWTH = 1.5f;

    private Animation mAnimation;
    private Animation mAnimationEnd;

    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private void setupAnimations() {
        mAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setRotate(interpolatedTime);
            }
        };
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        mAnimation.setDuration(ANIMATION_DURATION);

        mAnimationEnd = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mPercent = mPercent - 0.03f;
                setRotate(mPercent);
                if (mPercent < 0.01) {
                    isRefreshing = false;
                    resetOriginals();
                    clearAnimation();
                }
            }
        };
        mAnimationEnd.setRepeatCount(0);
        mAnimationEnd.setRepeatMode(Animation.RESTART);
        mAnimationEnd.setInterpolator(LINEAR_INTERPOLATOR);
        mAnimationEnd.setDuration(ANIMATION_DURATION);
    }

}