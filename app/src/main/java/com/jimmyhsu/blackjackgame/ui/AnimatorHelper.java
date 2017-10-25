package com.jimmyhsu.blackjackgame.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.jimmyhsu.blackjackgame.bean.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyanzhe on 18/10/17.
 */

public class AnimatorHelper {

    public enum POSITION {
        POS_STORAGE,
        POS_DEALER,
        POS_PLAYER_CENTER,
        POS_PLAYER_LEFT,
        POS_PLAYER_RIGHT
    }

    private static AnimatorHelper instance;

    private float cardStorageX;
    private float cardStorageY;

    private float bankerX;
    private float bankerY;

    private float playerCenterX;
    private float playerCenterY;

    private float cardWidth;
    private float cardHeight;

    private int bankerCardCount = 0;
    private int playerCardCountCenter = 0;
    private int playerCardCountLeft = 0;
    private int playerCardCountRight = 0;

    private volatile POSITION mCurrHighlight = null;
    private POSITION mHighlightRestore = null;
    private boolean isAnimating = false;

    private volatile List<Card> cards = new ArrayList<>();

    private AnimatorHelper() {

    }

    public static AnimatorHelper getInstance() {
        if (instance == null) {
            synchronized (AnimatorHelper.class) {
                if (instance == null) {
                    instance = new AnimatorHelper();
                }
            }
        }
        return instance;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        if (cards.indexOf(card) >= 0) return;
        List<Card> newList = new ArrayList<>(cards);
        newList.add(card);
        card.setCurrPosition(cardStorageX, cardStorageY, cardStorageX + cardWidth, cardStorageY + cardHeight);
        cards = newList;
    }

    public void reset() {
        bankerCardCount = playerCardCountCenter = 0;
    }


    public void init(float cardStorageX, float cardStorageY, float bankerX,
                     float bankerY, float playerCenterX, float playerCenterY,
                     float cardWidth, float cardHeight) {
        this.cardStorageX = cardStorageX;
        this.cardStorageY = cardStorageY;
        this.bankerX = bankerX;
        this.bankerY = bankerY;
        this.playerCenterX = playerCenterX;
        this.playerCenterY = playerCenterY;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;

        for (Card card : cards) {
            card.setCurrPosition(cardStorageX, cardStorageY, cardStorageX + cardWidth, cardStorageY + cardHeight);
        }
    }

    public POSITION getCurrHighlight() {
        return mCurrHighlight;
    }

    public void setCurrHighlight(POSITION highlight) {
        if (!isAnimating) {
            this.mCurrHighlight = highlight;
        } else {
            this.mHighlightRestore = highlight;
        }
    }

    public int getCardCountOnScreen(POSITION pos) {
        switch (pos) {
            case POS_PLAYER_LEFT:
                return playerCardCountLeft;
            case POS_PLAYER_CENTER:
                return playerCardCountCenter;
            case POS_PLAYER_RIGHT:
                return playerCardCountRight;
            default:
                return playerCardCountCenter;
        }
    }

    public void animateMovement(final POSITION from, final POSITION to, final Card card) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        float startX = getPositionX(from);
        float endX = getPositionX(to);
        float targetAngle = 0;
        float fromAngle = 0;
        if (to == POSITION.POS_PLAYER_CENTER) {
            endX += playerCardCountCenter * 30;
            playerCardCountCenter++;
        } else if (to == POSITION.POS_PLAYER_LEFT) {
            endX += playerCardCountLeft * 30;
            playerCardCountLeft++;
            targetAngle = 45;
        } else if (to == POSITION.POS_PLAYER_RIGHT) {
            endX += playerCardCountRight * 30;
            playerCardCountRight++;
            targetAngle = -45;
        } else if (to == POSITION.POS_DEALER) {
            endX += bankerCardCount * 30;
            bankerCardCount++;
        } else if (from == POSITION.POS_PLAYER_CENTER) {
            startX += (playerCardCountCenter - 1) * 30;
            playerCardCountCenter--;
        } else if (from == POSITION.POS_PLAYER_LEFT) {
            startX += (playerCardCountLeft - 1) * 30;
            playerCardCountLeft--;
            fromAngle = 45;
        } else if (from == POSITION.POS_PLAYER_RIGHT) {
            startX += (playerCardCountRight - 1) * 30;
            playerCardCountRight--;
            fromAngle = -45;
        } else if (from == POSITION.POS_DEALER) {
            startX += (bankerCardCount - 1) * 30;
            bankerCardCount--;
        }
        final float dX = endX - startX;
        final float dY = getPositionY(to) - getPositionY(from);
        final float dAngle = targetAngle - fromAngle;
        final float finalStartX = startX;
        final float finalStartAngle = fromAngle;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                float x = finalStartX + dX * val;
                float y = getPositionY(from) + dY * val;
                card.setCurrPosition(x, y,
                        x + cardWidth, y + cardHeight);
                card.setAngle(finalStartAngle + dAngle * val);
            }
        });
        isAnimating = true;
        mHighlightRestore = mCurrHighlight;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                card.setMoveAnimator(null);
                if (to == POSITION.POS_STORAGE) {
                    cards.remove(card);
                }
                mCurrHighlight = mHighlightRestore;
                isAnimating = false;
            }
        });
        ValueAnimator currAnimator = card.getMoveAnimator();
        if (currAnimator != null && currAnimator.isRunning()) {
            currAnimator.cancel();
        }
        card.setMoveAnimator(animator);
        animator.start();
    }

    private float getPositionX(POSITION pos) {
        switch (pos) {
            case POS_STORAGE:
                return cardStorageX;
            case POS_DEALER:
                return bankerX;
            case POS_PLAYER_CENTER:
                return playerCenterX;
            case POS_PLAYER_LEFT:
                return playerCenterX + cardWidth / 2f;
            case POS_PLAYER_RIGHT:
                return playerCenterX - cardWidth / 2f;
        }
        return cardStorageX;
    }

    private float getPositionY(POSITION pos) {
        switch (pos) {
            case POS_STORAGE:
                return cardStorageY;
            case POS_DEALER:
                return bankerY;
            case POS_PLAYER_CENTER:
            case POS_PLAYER_LEFT:
            case POS_PLAYER_RIGHT:
                return playerCenterY;
        }
        return cardStorageY;
    }

}
