package com.jimmyhsu.blackjackgame.bean;

import com.jimmyhsu.blackjackgame.ui.AnimatorHelper;

import java.util.Collections;
import java.util.Stack;

public class Deck {
    public final int[] suits = {Card.SUIT_HEART, Card.SUIT_CLUB, Card.SUIT_DIAMOND, Card.SUIT_SPADE};
    private Stack<Card> cards;

    private static Deck instance;

    public static Deck getInstance() {
        if (instance == null) {
            synchronized (Deck.class) {
                if (instance == null) {
                    instance = new Deck();
                }
            }
        }
        return instance;
    }

    //初始化cards并洗牌
    private Deck() {
        cards = new Stack<>();
        for (int s : suits) {
            for (int point = 1; point < 14; point++) {
                Card card = new Card(point, s);
                cards.push(card);
            }
        }
        shuffle(cards);
    }

    public void shuffle(Stack<Card> cards) {
        Collections.shuffle(cards);
    }

    //假设cards.size() >= count
    public Card[] extractCardFromTop(int count) {
        Card[] extractedCards = new Card[count];
        for (int i = 0; i < count; i++) {
            extractedCards[i] = cards.pop();
        }
        return extractedCards;
    }
}
