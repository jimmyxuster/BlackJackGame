package com.jimmyhsu.blackjackgame.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.util.TypedValue;

import com.jimmyhsu.blackjackgame.R;

/**
 * Created by xuyanzhe on 17/10/17.
 */

public class UIUtils {

    private static LruCache<Integer, Bitmap> mBitmapCache;
    static {
        mBitmapCache = new LruCache<>((int) (Runtime.getRuntime().maxMemory() / 2));
    }
    private static final int[] CARD_RES = new int[]{R.drawable.ha, R.drawable.h2, R.drawable.h3, R.drawable.h4,R.drawable.h5, R.drawable.h6,R.drawable.h7, R.drawable.h8,R.drawable.h9, R.drawable.h10,R.drawable.hj, R.drawable.hq,R.drawable.hk,
            R.drawable.sa, R.drawable.s2, R.drawable.s3, R.drawable.s4,R.drawable.s5, R.drawable.s6,R.drawable.s7, R.drawable.s8,R.drawable.s9, R.drawable.s10,R.drawable.sj, R.drawable.sq,R.drawable.sk,
            R.drawable.da, R.drawable.d2, R.drawable.d3, R.drawable.d4,R.drawable.d5, R.drawable.d6,R.drawable.d7, R.drawable.d8,R.drawable.d9, R.drawable.d10,R.drawable.dj, R.drawable.dq,R.drawable.dk,
            R.drawable.ca, R.drawable.c2, R.drawable.c3, R.drawable.c4,R.drawable.c5, R.drawable.c6,R.drawable.c7, R.drawable.c8,R.drawable.c9, R.drawable.c10,R.drawable.cj, R.drawable.cq,R.drawable.ck};
    public static int sp2px(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static Bitmap getCardBitmap(Context context, int point, int suit, boolean visible, float targetWidth, float targetHeight) {
        Bitmap result = mBitmapCache.get(visible ? CARD_RES[suit * 13 + point - 1] : -1);
        if (result == null) {
            if (visible) {
                result = getBitmap(context, CARD_RES[suit * 13 + point - 1], targetWidth, targetHeight);
            } else {
                result = getBitmap(context, R.drawable.back, targetWidth, targetHeight);
            }
            mBitmapCache.put(visible ? CARD_RES[suit * 13 + point - 1] : -1, result);
        }
        return result;
    }

    public static Bitmap getBitmap(Context context, int resId, float targetWidth, float targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        int ratio = 1;
        if (options.outWidth > targetWidth || options.outHeight > targetHeight) {
            ratio = (int) Math.max(options.outWidth / targetWidth, options.outHeight / targetHeight);
        }
        options.inSampleSize = ratio;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }
}
