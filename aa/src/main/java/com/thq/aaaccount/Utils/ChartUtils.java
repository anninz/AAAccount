//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.thq.aaaccount.Utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import java.util.Random;

public abstract class ChartUtils {
    public static final int DEFAULT_COLOR = Color.parseColor("#DFDFDF");
    public static final int DEFAULT_DARKEN_COLOR = Color.parseColor("#DDDDDD");
    public static final int COLOR_BLUE = Color.parseColor("#33B5E5");
    public static final int COLOR_VIOLET = Color.parseColor("#AA66CC");
    public static final int COLOR_GREEN = Color.parseColor("#99CC00");
    public static final int COLOR_ORANGE = Color.parseColor("#FFBB33");
    public static final int COLOR_RED = Color.parseColor("#FF4444");
    public static final int[] COLORS;
    private static final float DARKEN_SATURATION = 1.1F;
    private static final float DARKEN_INTENSITY = 0.9F;
    private static int COLOR_INDEX;

    public ChartUtils() {
    }

    static Random mRandom;
    static {
        mRandom = new Random();
    }
    public static int getRandomColor() {
        return Color.rgb(Color.red((int)Math.round(Math.random() * 88d)), Color.green((int)Math.round(Math.random() * 160d)), Color.blue((int)Math.round(Math.random() * 255d)));
    }

    public static final int pickColor() {
        return COLORS[(int)Math.round(Math.random() * (double)(COLORS.length - 1))];
    }

    public static final int nextColor() {
        if(COLOR_INDEX >= COLORS.length) {
            COLOR_INDEX = 0;
        }

        return COLORS[COLOR_INDEX++];
    }

    public static int dp2px(float density, int dp) {
        return dp == 0?0:(int)((float)dp * density + 0.5F);
    }

    public static int px2dp(float density, int px) {
        return (int)Math.ceil((double)((float)px / density));
    }

    public static int sp2px(float scaledDensity, int sp) {
        return sp == 0?0:(int)((float)sp * scaledDensity + 0.5F);
    }

    public static int px2sp(float scaledDensity, int px) {
        return (int)Math.ceil((double)((float)px / scaledDensity));
    }

    public static int mm2px(Context context, int mm) {
        return (int)(TypedValue.applyDimension(5, (float)mm, context.getResources().getDisplayMetrics()) + 0.5F);
    }

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        int alpha = Color.alpha(color);
        Color.colorToHSV(color, hsv);
        hsv[1] = Math.min(hsv[1] * 1.1F, 1.0F);
        hsv[2] *= 0.9F;
        int tempColor = Color.HSVToColor(hsv);
        return Color.argb(alpha, Color.red(tempColor), Color.green(tempColor), Color.blue(tempColor));
    }

    static {
        COLORS = new int[]{COLOR_BLUE, COLOR_VIOLET, COLOR_GREEN, COLOR_ORANGE, COLOR_RED, Color.LTGRAY, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.YELLOW};
        COLOR_INDEX = 0;
    }
}
