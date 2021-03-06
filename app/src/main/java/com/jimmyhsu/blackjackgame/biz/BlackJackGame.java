package com.jimmyhsu.blackjackgame.biz;

import com.jimmyhsu.blackjackgame.bean.Card;
import com.jimmyhsu.blackjackgame.bean.Dealer;
import com.jimmyhsu.blackjackgame.bean.Deck;
import com.jimmyhsu.blackjackgame.bean.Player;
import com.jimmyhsu.blackjackgame.ui.AnimatorHelper;
import com.jimmyhsu.blackjackgame.ui.InteractHelper;

import java.util.Locale;


public class BlackJackGame {

    public static final int MODE_SINGLE = 1;
    public static final int MODE_MULTI = 2;

    private int mMode;

    private static final int PLAYER_INITIAL_BALANCE = 1000;
    private static final int DEALER_INITIAL_BALANCE = 5000000;
    private static final AnimatorHelper.POSITION[] POSITIONS = {AnimatorHelper.POSITION.POS_PLAYER_LEFT, AnimatorHelper.POSITION.POS_PLAYER_CENTER, AnimatorHelper.POSITION.POS_PLAYER_RIGHT};
    private int mCurrPosIndex = 0;
    private Deck deck;
    private Player player;
    private Dealer dealer;
    private boolean hasInsurance = false;

    private InteractHelper mInteractHelper;
    private AnimatorHelper sAnimatorHelper = AnimatorHelper.getInstance();

    public void setMode(int mode) {
        this.mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public void setInteractHelper(InteractHelper helper) {
        this.mInteractHelper = helper;
        changePlayerBalance(PLAYER_INITIAL_BALANCE);
    }

    public BlackJackGame() {
        deck = Deck.getInstance();
        player = new Player(PLAYER_INITIAL_BALANCE);
        dealer = new Dealer(DEALER_INITIAL_BALANCE);
        dealer.setBalance(DEALER_INITIAL_BALANCE);
    }

    public void initializeGame() {
        changePlayerBet(0);
        if (mInteractHelper != null) {
            mInteractHelper.askBet();
        }
    }

    public void startGame() {
        changePlayerBalance(player.getBalance() - player.getBet());
        dealer.setBalance(dealer.getBalance() + player.getBet());
        print("下注成功");
        initAssignCard();
        if (shouldAskInsurance()) {
            askInsurance();
        } else if (mMode == MODE_SINGLE) {
            askDouble();
        } else {
            proceedNextRound();
        }
    }

    public void proceedNextRound() {
        if (dealer.haveBlackJack()) {
            if (hasInsurance) {
                changePlayerBalance(player.getBalance() + player.getInsurance() + player.getBet());
                setPlayerWinDirect();
            } else {
                setDealerWinDirect();
            }
        } else {
            askPlayerAdd();
        }
    }

    public void playerChooseAdd() {
        sAnimatorHelper.setCurrHighlight(null);
        Card[] addedCard = deck.extractCardFromTop(1);
        addedCard[0].setVisible(true);
        player.assignCard(addedCard, mMode == MODE_SINGLE ? AnimatorHelper.POSITION.POS_PLAYER_CENTER : POSITIONS[mCurrPosIndex]);
        if (player.burst(mMode == MODE_SINGLE ? AnimatorHelper.POSITION.POS_PLAYER_CENTER : POSITIONS[mCurrPosIndex])) {
            playerStopAdd();
        } else {
            askPlayerAdd();
        }
    }

    public void playerStopAdd() {
        sAnimatorHelper.setCurrHighlight(null);
        if (mCurrPosIndex < 2 && mMode == MODE_MULTI) {
            mCurrPosIndex++;
            askPlayerAdd();
        } else {
            if (dealerJudge()) {
                dealer.setCardVisible();
                print("庄家抽的牌点数超过21");
                setPlayerWinDirect();
            } else {
                judgeResult();
            }
        }
    }

    public void buyInsurance() {
        hasInsurance = true;
        int insurance = player.getBet() / 2;
        changePlayerBalance(player.getBalance() - insurance);
        player.setInsurance(insurance);
        dealer.setBalance(dealer.getBalance() + insurance);
    }

    private void setPlayerWin() {
        int bet = player.getBet();
        dealer.setCardVisible();
        dealer.setBalance(dealer.getBalance() - 2 * bet);
        changePlayerBalance(player.getBalance() + 2 * bet);
        print("玩家胜利！");
        changePlayerBet(0);
        if (mInteractHelper != null) {
            mInteractHelper.gameEnd();
        }
    }

    private void judgeResult() {
        if (dealer.haveBlackJack()) {
            print("庄家有黑杰克！");
            setDealerWinDirect();
        } else {
            int dealerPoint = dealer.getPoint();
            if (mMode == MODE_SINGLE) {
                int playerPoint = player.getPoint(AnimatorHelper.POSITION.POS_PLAYER_CENTER);
                if (playerPoint <= 21 && playerPoint >= dealerPoint) {
                    setPlayerWin();
                } else {
                    setDealerWin();
                }
            } else {
                int winCount = 0;
                for (AnimatorHelper.POSITION pos : POSITIONS) {
                    int playerPoint = player.getPoint(pos);
                    if (playerPoint <= 21 && playerPoint >= dealerPoint) {
                        winCount++;
                    }
                }
                if (winCount >= 2) {
                    setPlayerWin();
                } else {
                    setDealerWin();
                }
            }
        }
    }

    //返回dealer是否burst
    private boolean dealerJudge() {
        int dealerAddCount = 0;
        while (dealer.getPoint() < Dealer.MAX_POINT) {
            dealerAddCount++;
            dealer.assignCard(deck.extractCardFromTop(1), AnimatorHelper.POSITION.POS_DEALER);
            if (dealer.burst())
                return true;
        }
        print(String.format(Locale.CHINA, "庄家选择抽%d张牌", dealerAddCount));
        return false;
    }

    private void askPlayerAdd() {
        sAnimatorHelper.setCurrHighlight(mMode == MODE_SINGLE ? AnimatorHelper.POSITION.POS_PLAYER_CENTER : POSITIONS[mCurrPosIndex]);
        if (mInteractHelper != null) {
            mInteractHelper.showNotification("请选择是否再抽一张牌");
            mInteractHelper.askPlayerAdd();
        }
    }

    private void setDealerWin() {
        dealer.setCardVisible();
        changePlayerBet(0);
        print("庄家胜利！");
        if (mInteractHelper != null) {
            mInteractHelper.gameEnd();
        }
    }

    private void setPlayerWinDirect() {
        print("玩家获胜");
        dealer.setBalance(dealer.getBalance() - 2 * player.getBet());
        changePlayerBalance(player.getBalance() + 2 * player.getBet());
        changePlayerBet(0);
        if (mInteractHelper != null) {
            mInteractHelper.gameEnd();
        }
    }

    private void setDealerWinDirect() {
        print("庄家通过黑杰克直接获胜");
        dealer.setCardVisible();
        dealer.setBalance(dealer.getBalance() - player.getBet());
        changePlayerBalance(player.getBalance() - player.getBet());
        changePlayerBet(0);
        if (mInteractHelper != null) {
            mInteractHelper.gameEnd();
        }
    }

    public void resetGame() {
        hasInsurance = false;
        mCurrPosIndex = 0;
        changePlayerBet(0);
        dealer.removeAllCards(AnimatorHelper.POSITION.POS_DEALER);
        player.removeAllCards();
    }

    private void askInsurance() {
        if (mInteractHelper != null) {
            mInteractHelper.askInsurance();
        }
    }

    private boolean shouldAskInsurance() {
        return dealer.getCards().get(0).getPoint() == 1;
    }

    public void askDouble() {
        if (mInteractHelper != null) {
            mInteractHelper.askDouble();
        }
    }

    public void doubleBet() {
        int bet = player.getBet();
        changePlayerBet(bet * 2);
        changePlayerBalance(player.getBalance() - bet);
        dealer.setBalance(dealer.getBalance() + bet);
        proceedNextRound();
    }

    public void singleBet() {
        proceedNextRound();
    }

    //第一次分牌，给dealer一明一暗，给player3组两张明牌
    private void initAssignCard() {
        Card[] cards = deck.extractCardFromTop(2);
        cards[0].setVisible(true);
        dealer.assignCard(cards, AnimatorHelper.POSITION.POS_DEALER);
        AnimatorHelper.POSITION[] positions;
        if (mMode == MODE_MULTI) {
            positions = new AnimatorHelper.POSITION[]{AnimatorHelper.POSITION.POS_PLAYER_LEFT, AnimatorHelper.POSITION.POS_PLAYER_CENTER, AnimatorHelper.POSITION.POS_PLAYER_RIGHT};
        } else {
            positions = new AnimatorHelper.POSITION[]{AnimatorHelper.POSITION.POS_PLAYER_CENTER};
        }
        for (AnimatorHelper.POSITION pos : positions) {
            cards = deck.extractCardFromTop(2);
            cards[0].setVisible(true);
            cards[1].setVisible(true);
            player.assignCard(cards, pos);
        }
    }

    private void changePlayerBalance(int newBalance) {
        int oldBalance = player.getBalance();
        player.setBalance(newBalance);
        if (mInteractHelper != null) {
            mInteractHelper.balanceChange(oldBalance, newBalance);
        }
    }

    public void changePlayerBet(int newBet) {
        int oldBet = player.getBet();
        player.setBet(newBet);
        if (mInteractHelper != null) {
            mInteractHelper.betChange(oldBet, newBet);
        }
    }

    private void print(String str) {
        mInteractHelper.showNotification(str);
    }

    public int getPlayerBalance() {
        return player.getBalance();
    }

    public void addPlayerBet(int dBet) {
        int oldBet = player.getBet();
        if (oldBet + dBet >= 0 && oldBet + dBet <= getPlayerBalance()) {
            changePlayerBet(oldBet + dBet);
        }
    }

    public int getPlayerBet() {
        return player.getBet();
    }

    public int getDealerPoint() {
        return dealer.getPoint();
    }

    public int getPlayerPointLeft() {
        return player.getPoint(AnimatorHelper.POSITION.POS_PLAYER_LEFT);
    }

    public int getPlayerPointCenter() {
        return player.getPoint(AnimatorHelper.POSITION.POS_PLAYER_CENTER);
    }

    public int getPlayerPointRight() {
        return player.getPoint(AnimatorHelper.POSITION.POS_PLAYER_RIGHT);
    }

    public boolean shouldDrawDealerPoint() {
        return dealer.isCardVisible();
    }
}
