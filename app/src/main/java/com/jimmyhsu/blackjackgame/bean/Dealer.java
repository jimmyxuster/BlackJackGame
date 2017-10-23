package com.jimmyhsu.blackjackgame.bean;

import com.jimmyhsu.blackjackgame.bean.common.CommonPlayer;

public class Dealer extends CommonPlayer {

    public static final int MAX_POINT = 17; //大于等于此值不再要牌

    public Dealer(int balance) {
        super(balance);
    }

    public void setCardVisible() { //将所有的牌设为明牌
        for (Card c : getCards()) {
            c.setVisible(true);
        }
    }

    public boolean isCardVisible() {
        for (Card card : getCards()) {
            if (!card.isVisible()) return false;
        }
        return true;
    }
}
