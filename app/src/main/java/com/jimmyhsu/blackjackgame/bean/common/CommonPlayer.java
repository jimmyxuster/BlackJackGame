package com.jimmyhsu.blackjackgame.bean.common;

import com.jimmyhsu.blackjackgame.bean.Card;
import com.jimmyhsu.blackjackgame.ui.AnimatorHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xuyanzhe on 22/10/17.
 */

public class CommonPlayer {

    private int balance;
    private List<Card> cards;
    private AnimatorHelper sAnimatorHelper = AnimatorHelper.getInstance();

    public CommonPlayer(int balance) {
        this.balance = balance;
        this.cards = new ArrayList<>();
    }

    public boolean haveBlackJack() {
        return this.cards.size() == 2 && getPoint() == 21;
    }

    protected AnimatorHelper getAnimatorHelper() {
        return sAnimatorHelper;
    }

    //将参数中的card追加到成员变量cards
    public void assignCard(Card[] cards, AnimatorHelper.POSITION pos) {
        for (Card c : cards) {
            this.cards.add(c);
            sAnimatorHelper.addCard(c);
            sAnimatorHelper.animateMovement(AnimatorHelper.POSITION.POS_STORAGE, pos, c);
        }
    }

    public void removeAllCards(AnimatorHelper.POSITION pos) {
        for (Card c : cards) {
            sAnimatorHelper.animateMovement(pos, AnimatorHelper.POSITION.POS_STORAGE, c);
        }
        cards.clear();
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getPoint() {
        int totalPoint = 0;
        int aCount = 0; //"A"的牌数
        for (Card c : cards) {
            totalPoint += c.getComputedPoint();
            if (c.getPoint() == 1) {
                aCount++;
            }
        }
        while (aCount > 0) {
            aCount--;
            if (totalPoint <= 21 - 10) {
                totalPoint += 10;
            }
        }
        return totalPoint;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean burst() {
        return this.getPoint() > 21;
    }

    //打印持有的牌
    public void printCard() {
        for (Card c : cards) {
//            if(c.isCanSee()) {
            System.out.println(c.getSuit() + c.getPoint() + " ");
//            }
//            else {
//                System.out.println("■■■■■ ");
//            }
        }
    }
}
