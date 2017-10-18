package com.jimmyhsu.blackjackgame.bean;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import com.jimmyhsu.blackjackgame.R;
import com.jimmyhsu.blackjackgame.ui.UIUtils;

/**
 * Created by xuyanzhe on 17/10/17.
 */

public class Card {

    public static final int SUIT_HEART = 0;
    public static final int SUIT_SPADE = 1;
    public static final int SUIT_DIAMOND = 2;
    public static final int SUIT_CLUB = 3;

    /**
     * 牌面数字
     */
    private int number;
    /**
     * 花色
     */
    private int suit;
    private boolean isVisible = false;

    private volatile RectF mCurrPosition = new RectF();

    private ValueAnimator mMoveAnimator;

    public ValueAnimator getMoveAnimator() {
        return mMoveAnimator;
    }

    public void setMoveAnimator(ValueAnimator moveAnimator) {
        this.mMoveAnimator = moveAnimator;
    }

    public int getNumber() {
        return number;
    }

    public int getComputedNumber() {
        return isFace() ? 10 : number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSuit() {
        return suit;
    }

    public void setSuit(int suit) {
        this.suit = suit;
    }

    public boolean isFace() {
        return number > 10;
    }

    public boolean isAce() {
        return number == 1;
    }

    public RectF getCurrPosition() {
        return mCurrPosition;
    }

    public void setCurrPosition(RectF currPosition) {
        this.mCurrPosition = currPosition;
    }

    public void setCurrPosition(float l, float t, float r, float b) {
        this.mCurrPosition.set(l, t, r, b);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void draw(Context context, Canvas canvas, int targetWidth, int targetHeight) {
        canvas.drawBitmap(UIUtils.getCardBitmap(context, R.drawable.c2, targetWidth, targetHeight),
                null, mCurrPosition, null);
    }
}
