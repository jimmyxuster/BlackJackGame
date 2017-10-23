package com.jimmyhsu.blackjackgame.biz;

import com.jimmyhsu.blackjackgame.bean.Card;
import com.jimmyhsu.blackjackgame.bean.Dealer;
import com.jimmyhsu.blackjackgame.bean.Deck;
import com.jimmyhsu.blackjackgame.bean.Player;
import com.jimmyhsu.blackjackgame.ui.AnimatorHelper;
import com.jimmyhsu.blackjackgame.ui.InteractHelper;

import java.util.Locale;


public class BlackJackGame {

    private static final int PLAYER_INITIAL_BALANCE = 1000;
    private static final int DEALER_INITIAL_BALANCE = 5000000;
    private Deck deck;
    private Player player;
    private Dealer dealer;
    private boolean hasInsurance = false;
    private boolean isDoubled = false;

    private InteractHelper mInteractHelper;

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
        int rst = initAssignCard();
        if (rst == 1) {//玩家有黑杰克庄家没有
            setPlayerDoubleWin();
        } else if (rst == 2) {//玩家庄家都有黑杰克
            setEven();
        } else {
            //玩家没有黑杰克
            askDouble();//赌注翻倍，至多再抽一张牌
        }
    }

    public void proceedFirstRound() {
        if (dealer.haveBlackJack() && hasInsurance) {
            dealer.setCardVisible();//将dealer的暗牌翻过来
            returnInsurance();
            setPlayerWin();
        } else if (mInteractHelper != null) {
            proceedNextRound();
        }
    }

    private void proceedNextRound() {
        askPlayerAdd();
    }

    public void playerChooseAdd() {
        Card[] addedCard = deck.extractCardFromTop(1);
        player.assignCard(addedCard, AnimatorHelper.POSITION.POS_PLAYER);
        addedCard[0].setVisible(true);
        if (player.burst()) {
            setDealerWin();
        } else if (!isDoubled) {
            proceedNextRound();
        } else {
            playerStopAdd();
        }
    }

    public void playerStopAdd() {
        if (dealerJudge()) {
            dealer.setCardVisible();
            print("庄家抽的牌点数超过21");
            setPlayerWin();
        } else {
            judgeResult();
        }
    }

    private void setEven() {
        dealer.setCardVisible();
        dealer.setBalance(dealer.getBalance() - player.getBet());
        changePlayerBalance(player.getBalance() + player.getBet());
        print("游戏平局！");
        if (mInteractHelper != null) {
            mInteractHelper.gameEnd();
        }
    }

    private void setPlayerDoubleWin() {
        int bet = player.getBet();
        dealer.setCardVisible();
        dealer.setBalance(dealer.getBalance() - 3 * bet);
        changePlayerBalance(player.getBalance() + 3 * bet);
        print("玩家胜利且获得1赔2！");
        if (mInteractHelper != null) {
            mInteractHelper.gameEnd();
        }
    }

    private void returnInsurance() {
        int insurance = player.getInsurance();
        dealer.setBalance(dealer.getBalance() - insurance);
        changePlayerBalance(player.getBalance() + insurance);
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
        if (mInteractHelper != null) {
            mInteractHelper.gameEnd();
        }
    }

    private void judgeResult() {
        if (dealer.haveBlackJack()) {
            print("庄家有黑杰克！");
            setDealerWin();
        } else {
            int playerPoint = player.getPoint();
            int dealerPoint = dealer.getPoint();
            if (playerPoint == dealerPoint)
                setEven();
            else if (playerPoint > dealerPoint)
                setPlayerWin();
            else
                setDealerWin();
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

    public void resetGame() {
        hasInsurance = false;
        isDoubled = false;
        dealer.removeAllCards(AnimatorHelper.POSITION.POS_DEALER);
        player.removeAllCards(AnimatorHelper.POSITION.POS_PLAYER);
    }

    private void askInsurance() {
        if (mInteractHelper != null) {
            if (dealer.getCards().get(0).getPoint() == 1) {
                mInteractHelper.askInsurance();
            } else {
                proceedFirstRound();
            }
        }
    }

    private void askDouble() {
        if (mInteractHelper != null) {
            mInteractHelper.askDouble();
        }
    }

    public void doubleBet() {
        isDoubled = true;
        int bet = player.getBet();
        changePlayerBet(bet * 2);
        changePlayerBalance(player.getBalance() - bet);
        dealer.setBalance(dealer.getBalance() + bet);
        askInsurance();
    }

    public void singleBet() {
        askInsurance();
    }

    //第一次分牌，给dealer一明一暗，给player两张明牌，返回数组前两个为dealer的，后两个为player的。返回值0玩家没有黑杰克，1玩家有但庄家没有，2玩家庄家都有
    private int initAssignCard() {
        Card[] cards = deck.extractCardFromTop(2);
        cards[0].setVisible(true);
        dealer.assignCard(cards, AnimatorHelper.POSITION.POS_DEALER);
        cards = deck.extractCardFromTop(2);
        cards[0].setVisible(true);
        cards[1].setVisible(true);
        player.assignCard(cards, AnimatorHelper.POSITION.POS_PLAYER);
        if (player.haveBlackJack() && !dealer.haveBlackJack()) {
            return 1;
        } else if (player.haveBlackJack() && dealer.haveBlackJack()) {
            return 2;
        } else {
            return 0;
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

    public int getPlayerPoint() {
        return player.getPoint();
    }

    public boolean shouldDrawDealerPoint() {
        return dealer.isCardVisible();
    }
}
