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
        POS_PLAYER
    }

    private static AnimatorHelper instance;

    private float cardStorageX;
    private float cardStorageY;

    private float bankerX;
    private float bankerY;

    private float playerX;
    private float playerY;

    private float cardWidth;
    private float cardHeight;

    private int bankerCardCount = 0;
    private int playerCardCount = 0;

    private List<Card> cards = new ArrayList<>();

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
        cards.add(card);
        card.setCurrPosition(cardStorageX, cardStorageY, cardStorageX + cardWidth, cardStorageY + cardHeight);
    }

    public void reset() {
        bankerCardCount = playerCardCount = 0;
    }


    public void init(float cardStorageX, float cardStorageY, float bankerX,
                     float bankerY, float playerX, float playerY, float cardWidth, float cardHeight) {
        this.cardStorageX = cardStorageX;
        this.cardStorageY = cardStorageY;
        this.bankerX = bankerX;
        this.bankerY = bankerY;
        this.playerX = playerX;
        this.playerY = playerY;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;

        for (Card card : cards) {
            card.setCurrPosition(cardStorageX, cardStorageY, cardStorageX + cardWidth, cardStorageY + cardHeight);
        }
    }

    public void animateMovement(final POSITION from, final POSITION to, final Card card) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        float startX = getPositionX(from);
        float endX = getPositionX(to);
        if (to == POSITION.POS_PLAYER) {
            endX += playerCardCount * 30;
            playerCardCount++;
        } else if (to == POSITION.POS_DEALER) {
            endX += bankerCardCount * 30;
            bankerCardCount++;
        } else if (from == POSITION.POS_PLAYER) {
            startX += (playerCardCount - 1) * 30;
            playerCardCount--;
        } else if (from == POSITION.POS_DEALER) {
            startX += (bankerCardCount - 1) * 30;
            bankerCardCount--;
        }
        final float dX = endX - startX;
        final float dY = getPositionY(to) - getPositionY(from);
        final float finalStartX = startX;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                float x = finalStartX + dX * val;
                float y = getPositionY(from) + dY * val;
                card.setCurrPosition(x, y,
                        x + cardWidth, y + cardHeight);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                card.setMoveAnimator(null);
                if (to == POSITION.POS_STORAGE) {
                    cards.remove(card);
                }
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
            case POS_PLAYER:
                return playerX;
        }
        return cardStorageX;
    }

    private float getPositionY(POSITION pos) {
        switch (pos) {
            case POS_STORAGE:
                return cardStorageY;
            case POS_DEALER:
                return bankerY;
            case POS_PLAYER:
                return playerY;
        }
        return cardStorageY;
    }

}
