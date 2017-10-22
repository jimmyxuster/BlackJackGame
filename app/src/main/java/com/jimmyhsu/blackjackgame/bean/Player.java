package com.jimmyhsu.blackjackgame.bean;

import com.jimmyhsu.blackjackgame.bean.common.CommonPlayer;

public class Player extends CommonPlayer {

    private int bet;
    private int insurance = 0;

    public Player(int balance) {
        super(balance);
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

}
