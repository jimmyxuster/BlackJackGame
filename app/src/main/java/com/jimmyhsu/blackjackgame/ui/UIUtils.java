package com.jimmyhsu.blackjackgame.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.util.TypedValue;

/**
 * Created by xuyanzhe on 17/10/17.
 */

public class UIUtils {

    private static SparseArray<Bitmap> cardBitmapResource = new SparseArray<>();

    public static int sp2px(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static Bitmap getCardBitmap(Context context, int resId, float targetWidth, float targetHeight) {
        Bitmap result = cardBitmapResource.get(resId);
        if (result == null) {
            result = getBitmap(context, resId, targetWidth, targetHeight);
            cardBitmapResource.put(resId, result);
        }
        return result;
    }

    private static Bitmap getBitmap(Context context, int resId, float targetWidth, float targetHeight) {
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
