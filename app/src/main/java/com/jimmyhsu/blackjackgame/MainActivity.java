package com.jimmyhsu.blackjackgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jimmyhsu.blackjackgame.biz.BlackJackGame;
import com.jimmyhsu.blackjackgame.ui.AnimatorHelper;
import com.jimmyhsu.blackjackgame.ui.GameView;
import com.jimmyhsu.blackjackgame.ui.InteractHelper;
import com.jimmyhsu.blackjackgame.ui.common.AnimationListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GameView.OnGameViewReady, InteractHelper {

    private BlackJackGame mGame;

    @BindView(R.id.gameview)
    GameView mGameView;
    @BindView(R.id.message_view)
    ListView mMessageView;
    @BindView(R.id.balance)
    TextView mBalanceView;
    @BindView(R.id.bet)
    TextView mBetView;
    @BindView(R.id.bet_group)
    LinearLayout mBetButtons;
    @BindView(R.id.double_group)
    LinearLayout mDoubleButtons;
    @BindView(R.id.insurance_group)
    LinearLayout mInsuranceGroup;
    @BindView(R.id.add_card_group)
    LinearLayout mAddCardGroup;
    @BindView(R.id.start)
    Button mStartButton;
    @BindView(R.id.restart)
    Button mResetButton;

    private ArrayAdapter mMessageAdapter;
    private List<String> mMessages = new ArrayList<>();

    private ValueAnimator mBalanceAnimator;
    private ValueAnimator mBetAnimator;
    private ValueAnimator mBetColorAnimator;
    private ValueAnimator mBalanceColorAnimator;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mGame = new BlackJackGame();
        mGameView.setGame(mGame);
        mGameView.setOnGameViewReadyListener(this);
        initMessageView();
    }

    private void initMessageView() {
        mMessageView.setAdapter(mMessageAdapter = new ArrayAdapter<>(this, R.layout.message_item,
                R.id.msg_item_text, mMessages));
    }

    private void showPopupAnimation(final View target) {
        Animation btnPopupAnim = AnimationUtils.loadAnimation(this, R.anim.btn_pop_up);
        target.clearAnimation();
        btnPopupAnim.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationStart(Animation animation) {
                target.setVisibility(View.VISIBLE);
            }
        });
        target.startAnimation(btnPopupAnim);
    }

    private void showPopDownAnimation(final View target) {
        Animation btnPopDownAnim = AnimationUtils.loadAnimation(this, R.anim.btn_pop_down);
        target.clearAnimation();
        btnPopDownAnim.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                target.clearAnimation();
                target.setVisibility(View.GONE);
            }
        });
        target.startAnimation(btnPopDownAnim);
    }

    @Override
    public void onReady() {
        mMessages.add(getResources().getString(R.string.initializing));
        mMessageAdapter.notifyDataSetChanged();
        mGame.setInteractHelper(this);
        mMessages.add(getResources().getString(R.string.initialized));
        showPopupAnimation(mStartButton);
        mMessageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AnimatorHelper.getInstance().reset();
    }

    @Override
    public void askInsurance() {
        showPopupAnimation(mInsuranceGroup);
    }

    @Override
    public void showNotification(String message) {
        mMessages.add(message);
        mMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void askPlayerAdd() {
        showPopupAnimation(mAddCardGroup);
    }

    @Override
    public void askDouble() {
        showPopupAnimation(mDoubleButtons);
    }

    @Override
    public void askBet() {
        showPopupAnimation(mBetButtons);
    }

    @Override
    public void balanceChange(int oldBalance, int newBalance) {
        if (mBalanceAnimator != null && mBalanceAnimator.isRunning()) {
            mBalanceAnimator.cancel();
        }
        if (mBalanceColorAnimator != null && mBalanceColorAnimator.isRunning()) {
            mBalanceColorAnimator.cancel();
        }
        mBalanceAnimator = ValueAnimator.ofInt(oldBalance, newBalance);
        mBalanceAnimator.setDuration(800);
        mBalanceAnimator.setInterpolator(new DecelerateInterpolator());
        mBalanceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                mBalanceView.setText(String.format(Locale.CHINA, "%s%d",
                        getResources().getString(R.string.balance_title),
                        val));
            }
        });
        mBalanceColorAnimator = ValueAnimator.ofArgb(ContextCompat.getColor(this, R.color.buttonMain),
                ContextCompat.getColor(this, R.color.colorTitle));
        mBalanceColorAnimator.setDuration(800);
        mBalanceColorAnimator.setInterpolator(new DecelerateInterpolator());
        mBalanceColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                mBalanceView.setTextColor(val);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(mBalanceColorAnimator, mBalanceAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBalanceAnimator = null;
                mBalanceColorAnimator = null;
            }
        });
        set.start();
    }

    @Override
    public void betChange(int oldBet, int newBet) {
        if (mBetAnimator != null && mBetAnimator.isRunning()) {
            mBetAnimator.cancel();
        }
        if (mBetColorAnimator != null && mBetColorAnimator.isRunning()) {
            mBetColorAnimator.cancel();
        }
        mBetAnimator = ValueAnimator.ofInt(oldBet, newBet);
        mBetAnimator.setDuration(800);
        mBetAnimator.setInterpolator(new DecelerateInterpolator());
        mBetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                mBetView.setText(String.format(Locale.CHINA, "%s%d",
                        getResources().getString(R.string.bet_title),
                        val));
            }
        });
        mBetColorAnimator = ValueAnimator.ofArgb(ContextCompat.getColor(this, R.color.buttonMain),
                ContextCompat.getColor(this, R.color.colorTitle));
        mBetColorAnimator.setDuration(800);
        mBetColorAnimator.setInterpolator(new DecelerateInterpolator());
        mBetColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                mBetView.setTextColor(val);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(mBetColorAnimator, mBetAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBetAnimator = null;
                mBetColorAnimator = null;
            }
        });
        set.start();
    }

    @Override
    public void gameEnd() {
        showPopupAnimation(mResetButton);
    }

    /**
     * 开始游戏按钮监听
     */
    @OnClick(R.id.start)
    void startGame() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGame.initializeGame();
                mMessages.add("请下注");
                mMessageAdapter.notifyDataSetChanged();
            }
        }, 500);
        showPopDownAnimation(mStartButton);
    }

    /**
     * 重置游戏按钮监听
     */
    @OnClick(R.id.restart)
    void resetGame() {
        mGame.resetGame();
        showPopDownAnimation(mResetButton);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showPopupAnimation(mStartButton);
            }
        }, 1000);
    }

    /**
     * 下注按钮点击监听
     */
    @OnClick(R.id.bet_plus_100)
    void betPlus100() {
        mGame.addPlayerBet(100);
    }
    @OnClick(R.id.bet_plus_10)
    void betPlus10() {
        mGame.addPlayerBet(10);
    }
    @OnClick(R.id.bet_minus_100)
    void betMinus100() {
        mGame.addPlayerBet(-100);
    }
    @OnClick(R.id.bet_minus_10)
    void betMinus10() {
        mGame.addPlayerBet(-10);
    }
    @OnClick(R.id.bet_confirm)
    void layBet() {
        if (mGame.getPlayerBet() <= 0) return;
        showPopDownAnimation(mBetButtons);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGame.startGame();
            }
        }, 500);
    }
    /**
     * 加倍按钮点击监听
     */
    @OnClick(R.id.double_do_double)
    void doDouble() {
        showPopDownAnimation(mDoubleButtons);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGame.doubleBet();
            }
        }, 500);
    }
    @OnClick(R.id.double_dont_double)
    void dontDouble() {
        showPopDownAnimation(mDoubleButtons);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGame.singleBet();
            }
        }, 500);
    }
    @OnClick(R.id.insurance_do_buy)
    void buyInsurance() {
        showPopDownAnimation(mInsuranceGroup);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGame.buyInsurance();
                mGame.proceedFirstRound();
            }
        }, 500);
    }
    @OnClick(R.id.insurance_dont_buy)
    void dontBuyInsurance() {
        showPopDownAnimation(mInsuranceGroup);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGame.proceedFirstRound();
            }
        }, 500);
    }
    @OnClick(R.id.card_do_add)
    void addCard() {
        showPopDownAnimation(mAddCardGroup);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGame.playerChooseAdd();
            }
        }, 500);
    }
    @OnClick(R.id.card_dont_add)
    void dontAddCard() {
        showPopDownAnimation(mAddCardGroup);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGame.playerStopAdd();
            }
        }, 500);
    }
}
