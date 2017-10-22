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

    private InteractHelper mInteractHelper;

    public void setInteractHelper(InteractHelper helper) {
        this.mInteractHelper = helper;
    }

    public BlackJackGame() {
        deck = Deck.getInstance();
        player = new Player(PLAYER_INITIAL_BALANCE);
        dealer = new Dealer(DEALER_INITIAL_BALANCE);
    }

    public void initializeGame() {
        changePlayerBalance(PLAYER_INITIAL_BALANCE);
        changePlayerBet(0);
        dealer.setBalance(DEALER_INITIAL_BALANCE);
        if (mInteractHelper != null) {
            mInteractHelper.askBet();
        }
    }

    public void startGame() {
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
//        if (askInsurance()) {
//            buyInsurance();
//            printGame();
//            if (dealer.haveBlackJack()) {
//                dealer.setCardWhite();//将dealer的暗牌翻过来
//                returnInsurance();
//                setPlayerWin();
//                return;
//            } else {
//                print("庄家没有黑杰克，游戏继续");
//            }
//        }
//        if (!doubleRst) {//没加倍赌注可以轮询抽牌
//            boolean addRst = askPlayerAdd();
//            while (addRst) {
//                player.assignCard(deck.extractTopCard(1));
//                printGame();
//                if (player.burst()) {
//                    setDealerWin();
//                    return;
//                } else
//                    addRst = askPlayerAdd();
//            }
//        } else {
//            if (askPlayerAddOne()) {
//                if (player.burst()) {
//                    setDealerWin();
//                    return;
//                } else {
//                    dealer.setCardWhite();
//                    if (dealerJudge()) {
//                        setPlayerWin();
//                        return;
//                    }
//                    judgeResult();
//                    return;
//                }
//            }
//        }
//        dealer.setCardWhite();
//        if (dealerJudge()) {//庄家自己判断加不加牌，返回值为庄家是否burst
//            setPlayerWin();
//            return;
//        }
//        judgeResult();
    }

    public void proceedFirstRound() {
        if (dealer.haveBlackJack()) {
            dealer.setCardVisible();//将dealer的暗牌翻过来
            returnInsurance();
            setPlayerWin();
        } else if (mInteractHelper != null) {
            mInteractHelper.showNotification("庄家没有黑杰克，游戏继续");
            proceedNextRound();
        }
    }

    private void proceedNextRound() {
        askPlayerAdd();
    }

    public void playerChooseAdd() {
        player.assignCard(deck.extractCardFromTop(1), AnimatorHelper.POSITION.POS_PLAYER);
        printGame();
        if (player.burst()) {
            setDealerWin();
        } else {
            proceedNextRound();
        }
    }

    public void playerStopAdd() {
        if (dealerJudge()) {
            print("庄家抽的牌点数超过21");
            setPlayerWin();
        } else {
            judgeResult();
        }
    }

//    private boolean askPlayerAddOne() {
//        print("您选择了加倍赌注，至多再抽一张牌，要抽吗？[y/n]");
//        String input = getInput();
//        while (!input.equals("y") && !input.equals("n")) {
//            print("请输入正确的选项[y/n]");
//            input = getInput();
//        }
//        if (input.equals("y")) {
//            player.assignCard(deck.extractTopCard(1));
//            printGame();
//            return true;
//        } else {
//            return false;
//        }
//    }

    private void setEven() {
        dealer.setBalance(dealer.getBalance() - player.getBet());
        changePlayerBalance(player.getBalance() + player.getBet());
        print("游戏平局！");
        printGame();
    }

    private void setPlayerDoubleWin() {
        int bet = player.getBet();
        dealer.setBalance(dealer.getBalance() - 3 * bet);
        changePlayerBalance(player.getBalance() + 3 * bet);
        print("玩家胜利且获得1赔2！");
        printGame();
    }

    private void returnInsurance() {
        int insurance = player.getInsurance();
        dealer.setBalance(dealer.getBalance() - insurance);
        changePlayerBalance(player.getBalance() + insurance);
    }

    private void buyInsurance() {
        int insurance = player.getBet() / 2;
        changePlayerBalance(player.getBalance() - insurance);
        player.setInsurance(insurance);
        dealer.setBalance(dealer.getBalance() + insurance);
    }

    private void setPlayerWin() {
        int bet = player.getBet();
        dealer.setBalance(dealer.getBalance() - 2 * bet);
        changePlayerBalance(player.getBalance() + 2 * bet);
        printPoint();
        print("玩家胜利！");
        printGame();
    }

    private void printGame() {
//        System.out.print(Constants.flag + "\n");
        printCards();
//        System.out.println("庄家余额："+dealer.getBalance());
        System.out.println("玩家赌注：" + player.getBet());
        System.out.println("保险金额：" + player.getInsurance());
        System.out.println("玩家余额：" + player.getBalance());
//        System.out.println(Constants.flag);
    }

    private void printCards() {
        System.out.println("庄家持有：");
        dealer.printCard();
        System.out.println("玩家持有：");
        player.printCard();
    }

    private void judgeResult() {
        if (dealer.haveBlackJack()) {
            System.out.println("庄家有黑杰克！");
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
            dealer.assignCard(deck.extractCardFromTop(1), AnimatorHelper.POSITION.POS_PLAYER);
            printGame();
            if (dealer.burst())
                return true;
        }
        print(String.format(Locale.CHINA, "庄家选择抽%d张牌", dealerAddCount));
        return false;
    }

    private void askPlayerAdd() {
        if (mInteractHelper != null) {
            mInteractHelper.askPlayerAdd();
        }
    }

    private void setDealerWin() {
        printPoint();
        print("庄家胜利！");
        printGame();
    }

    private void printPoint() {
        int playerPoint = player.getPoint();
        int dealerPoint = dealer.getPoint();
        System.out.println("庄家得分：" + dealerPoint);
        System.out.println("玩家得分：" + playerPoint);
    }

    private void askInsurance() {
        if (mInteractHelper != null) {
            mInteractHelper.askInsurance();
        }
    }

    private void askDouble() {
        System.out.println("您想将赌注翻倍吗？[y/n]");
        if (mInteractHelper != null) {
            mInteractHelper.askDouble();
        }
//        while (!input.equals("y") && !input.equals("n")) {
//            System.out.println("请输入正确的选项[y/n]");
//            input = getInput();
//        }
//        if (input.equals("y")) {
//            doubleBet();
//        } else
//            return false;
    }

    private void doubleBet() {
        int bet = player.getBet();
        changePlayerBet(bet * 2);
        changePlayerBalance(player.getBalance() - bet);
        dealer.setBalance(dealer.getBalance() + bet);
        askInsurance();
    }

    private void singleBet() {
        askInsurance();
    }

    //第一次分牌，给dealer一明一暗，给player两张明牌，返回数组前两个为dealer的，后两个为player的。返回值0玩家没有黑杰克，1玩家有但庄家没有，2玩家庄家都有
    private int initAssignCard() {
        Card[] cards = deck.extractCardFromTop(2);
        cards[0].setVisible(true);
        dealer.assignCard(cards, AnimatorHelper.POSITION.POS_BANKER);
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

    //读取用户输入，设置player的bet
    private void setBet(int bet) {
//        System.out.println("请输入您的赌注：");
//        Scanner scanner = new Scanner(System.in);
//        int bet = scanner.nextInt();
//        while (bet <= 0) {
//            System.out.println("请输入正确的赌注：");
//            bet = scanner.nextInt();
//        }
        changePlayerBet(bet);
        changePlayerBalance(player.getBalance() - bet);
        dealer.setBalance(dealer.getBalance() + bet);
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
}
