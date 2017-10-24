package com.jimmyhsu.blackjackgame.bean;

import com.jimmyhsu.blackjackgame.bean.common.CommonPlayer;
import com.jimmyhsu.blackjackgame.ui.AnimatorHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends CommonPlayer {

    private int bet;
    private int insurance = 0;
    private Map<AnimatorHelper.POSITION, List<Card>> cardsMap;

    public Player(int balance) {
        super(balance);
        cardsMap = new HashMap<>();
        cardsMap.put(AnimatorHelper.POSITION.POS_PLAYER_LEFT, new ArrayList<Card>());
        cardsMap.put(AnimatorHelper.POSITION.POS_PLAYER_CENTER, new ArrayList<Card>());
        cardsMap.put(AnimatorHelper.POSITION.POS_PLAYER_RIGHT, new ArrayList<Card>());
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getBet() {
        return this.bet;
    }

    public int getInsurance() {
        return insurance;
    }

    public void setInsurance(int insurance) {
        this.insurance = insurance;
    }

    public List<Card> gerCards(AnimatorHelper.POSITION pos) {
        return cardsMap.get(pos);
    }

    @Override
    public void assignCard(Card[] cards, AnimatorHelper.POSITION pos) {
        for (Card c : cards) {
            cardsMap.get(pos).add(c);
            getAnimatorHelper().addCard(c);
            getAnimatorHelper().animateMovement(AnimatorHelper.POSITION.POS_STORAGE, pos, c);
        }
    }

    public boolean haveBlackJack(AnimatorHelper.POSITION pos) {
        return cardsMap.get(pos).size() == 2 && getPoint(pos) == 21;
    }

    public boolean burst(AnimatorHelper.POSITION pos) {
        return getPoint(pos) > 21;
    }

    public int getPoint(AnimatorHelper.POSITION pos) {
        int totalPoint = 0;
        int aCount = 0; //"A"的牌数
        for (Card c : cardsMap.get(pos)) {
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

    public void removeAllCards() {
        for (AnimatorHelper.POSITION key : cardsMap.keySet()) {
            for (Card card : cardsMap.get(key)) {
                getAnimatorHelper().animateMovement(key, AnimatorHelper.POSITION.POS_STORAGE, card);
            }
            cardsMap.get(key).clear();
        }
    }
}
