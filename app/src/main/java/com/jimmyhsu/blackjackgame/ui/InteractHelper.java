package com.jimmyhsu.blackjackgame.ui;

/**
 * Created by xuyanzhe on 22/10/17.
 */

public interface InteractHelper {
    void askInsurance();
    void showNotification(String message);
    void askPlayerAdd();
    void askDouble();
    void balanceChange(int oldBalance, int newBalance);
    void betChange(int oldBet, int newBet);
    void askBet();
    void gameEnd();
}
