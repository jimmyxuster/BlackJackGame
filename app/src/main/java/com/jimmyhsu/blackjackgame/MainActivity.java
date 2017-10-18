package com.jimmyhsu.blackjackgame;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.jimmyhsu.blackjackgame.bean.Card;
import com.jimmyhsu.blackjackgame.ui.AnimatorHelper;

public class MainActivity extends AppCompatActivity {

    private Card card1;
    private Card card2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                AnimatorHelper.getInstance().animateMovement(AnimatorHelper.POSITION.POS_STORAGE, AnimatorHelper.POSITION.POS_PLAYER, card1);
            } else if (msg.what == 1) {
                AnimatorHelper.getInstance().animateMovement(AnimatorHelper.POSITION.POS_STORAGE, AnimatorHelper.POSITION.POS_PLAYER, card2);
            } else if (msg.what == 2) {
                AnimatorHelper.getInstance().animateMovement(AnimatorHelper.POSITION.POS_PLAYER, AnimatorHelper.POSITION.POS_STORAGE, card2);
            } else if (msg.what == 3) {
                AnimatorHelper.getInstance().animateMovement(AnimatorHelper.POSITION.POS_PLAYER, AnimatorHelper.POSITION.POS_STORAGE, card1);
            }
            Message newMsg = Message.obtain();
            newMsg.what = (msg.what + 1) % 4;
            mHandler.sendMessageDelayed(newMsg, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        card1 = new Card();
        card1.setNumber(1);
        card1.setSuit(Card.SUIT_DIAMOND);
        card2 = new Card();
        card2.setNumber(2);
        card2.setSuit(Card.SUIT_DIAMOND);
        AnimatorHelper.getInstance().addCard(card1);
        AnimatorHelper.getInstance().addCard(card2);

        Message msg = Message.obtain();
        msg.what = 0;
        mHandler.sendMessageDelayed(msg, 2000);
    }
}
