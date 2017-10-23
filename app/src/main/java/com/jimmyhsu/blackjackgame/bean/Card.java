package com.jimmyhsu.blackjackgame.bean;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

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
    private int point;
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

    public Card(int point, int suit) {
        this.point = point;
        this.suit = suit;
    }

    public void setMoveAnimator(ValueAnimator moveAnimator) {
        this.mMoveAnimator = moveAnimator;
    }

    public int getPoint() {
        return point;
    }

    public int getComputedPoint() {
        return isFace() ? 10 : point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getSuit() {
        return suit;
    }

    public void setSuit(int suit) {
        this.suit = suit;
    }

    public boolean isFace() {
        return point > 10;
    }

    public boolean isAce() {
        return point == 1;
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
        canvas.drawBitmap(UIUtils.getCardBitmap(context, point, suit, isVisible, targetWidth, targetHeight),
                null, mCurrPosition, null);
    }
}
