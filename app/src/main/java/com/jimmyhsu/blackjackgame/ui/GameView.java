package com.jimmyhsu.blackjackgame.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jimmyhsu.blackjackgame.R;
import com.jimmyhsu.blackjackgame.bean.Card;
import com.jimmyhsu.blackjackgame.biz.BlackJackGame;

import java.util.List;


/**
 * Created by xuyanzhe on 17/10/17.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int[] BACKGROUND_GRADIENT = new int[]{0xFF2A5E26, 0xFF429435, 0xFF2A5E26};
    private static final int TITLE_COLOR = 0xFF780D0B;
    private static final int ARC_COLOR = 0xFFF7FE29;
    private static final int HIGHTLIGHT_COLOR = 0xFF181E9D;
    private static final int ARC_START_ANGLE = 45;
    private static final int ARC_END_ANGLE = 135;
    private static final int ARC_PADDING_DP = 8;
    private static final String TITLE = "BlackJack Game for OOAD";

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread t;
    private boolean isRunning;

    private int mWidth;
    private int mHeight;

    private SweepGradient mBackgroundGradient;
    private Paint mBackgroundPaint;

    private Path mTitlePath;
    private Paint mTitlePaint;
    private Paint mArcPaint;
    private Paint mPointPaint;
    private Paint mHighlightPaint;
    private float mTitlePathLength;
    private float mTitleLength;
    private float mTitleHeight;

    private float[] mArcLines = new float[8];
    private RectF mArcOuterRectF = new RectF();
    private RectF mArcInnerRectF = new RectF();

    private int mFrameWidth;
    private int mFrameHeight;
    private int mFramePadding;
    private int mArcPaddingDp;
    private int mHighlightAlpha = 0;
    private int mHighlightAlphaFactor = 1;

    private Bitmap mCardBack;
    private Rect mCardPileRect;

    private AnimatorHelper mAnimatorHelper = AnimatorHelper.getInstance();
    private BlackJackGame mGame;

    private OnGameViewReady onGameViewReady;

    public void setOnGameViewReadyListener(OnGameViewReady l) {
        this.onGameViewReady = l;
    }

    public void setGame(BlackJackGame game) {
        mGame = game;
    }

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(false);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setFocusable(true);
        setFocusableInTouchMode(true);

        init();
    }

    private void init() {
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setColor(TITLE_COLOR);
        mTitlePaint.setTextSize(UIUtils.sp2px(getContext(), 18));
        mTitleLength = mTitlePaint.measureText(TITLE, 0, TITLE.length());
        mTitlePaint.setFakeBoldText(true);
        Rect textBounds = new Rect();
        mTitlePaint.getTextBounds(TITLE, 0, TITLE.length() - 1, textBounds);
        mTitleHeight = textBounds.bottom - textBounds.top;

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setColor(ARC_COLOR);
        mArcPaint.setStrokeWidth(UIUtils.sp2px(getContext(), 2));
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaddingDp = UIUtils.dp2px(getContext(), ARC_PADDING_DP);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setTextSize(UIUtils.sp2px(getContext(), 14));
        mPointPaint.setColor(0xffffffff);

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setColor(HIGHTLIGHT_COLOR);
        mHighlightPaint.setStrokeWidth(UIUtils.dp2px(getContext(), 2));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            try {
                if (end - start < 15) {
                    Thread.sleep(15 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                drawBackground();
                drawTitle();
                drawBackgroundArc();
                drawCardPendingFrame();
                drawCards();
                drawCardPile();
                drawHighlight();
                drawPointSums();
            }

        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    private void drawHighlight() {
        AnimatorHelper.POSITION currHighlight = mAnimatorHelper.getCurrHighlight();
        if (currHighlight != null) {
            int highlightWidth = mFrameWidth + 30 * (mAnimatorHelper.getCardCountOnScreen(currHighlight) - 1);
            float l = mWidth / 2f;
            float t = mWidth / 4f + mFramePadding;
            mCanvas.save();
            switch (currHighlight) {
                case POS_PLAYER_LEFT:
                    mCanvas.rotate(45, mWidth / 2f, 0);
                    l = mWidth / 2f;
                    break;
                case POS_PLAYER_CENTER:
                    l = mWidth / 2f - mFrameWidth / 2f;
                    break;
                case POS_PLAYER_RIGHT:
                    mCanvas.rotate(-45, mWidth / 2f, 0);
                    l = mWidth / 2f - mFrameWidth;
                    break;
            }
            mHighlightPaint.setAlpha(mHighlightAlpha);
            mCanvas.drawRect(l, t, l + highlightWidth, t + mFrameHeight, mHighlightPaint);
            mCanvas.restore();
            mHighlightAlpha = mHighlightAlpha + mHighlightAlphaFactor * 30;
            if (mHighlightAlpha > 255) mHighlightAlpha = 255;
            if (mHighlightAlpha < 0) mHighlightAlpha = 0;
            if (mHighlightAlpha == 255) {
                mHighlightAlphaFactor = -1;
            } else if (mHighlightAlpha == 0) {
                mHighlightAlphaFactor = 1;
            }
        }
    }

    private void drawPointSums() {
        if (mGame != null) {
            String dealerPoint = String.valueOf(mGame.getDealerPoint());
            String playerPointLeft = String.valueOf(mGame.getPlayerPointLeft());
            String playerPointCenter = String.valueOf(mGame.getPlayerPointCenter());
            String playerPointRight = String.valueOf(mGame.getPlayerPointRight());
            float dealerPointWidth = mPointPaint.measureText(dealerPoint, 0, dealerPoint.length());
            float playerPointLeftWidth = mPointPaint.measureText(playerPointLeft, 0, playerPointLeft.length());
            float playerPointCenterWidth = mPointPaint.measureText(playerPointCenter, 0, playerPointCenter.length());
            float playerPointRightWidth = mPointPaint.measureText(playerPointRight, 0, playerPointRight.length());
            if (mGame.shouldDrawDealerPoint() && Integer.parseInt(dealerPoint) > 0) {
                mCanvas.drawText(dealerPoint, mWidth / 2f - dealerPointWidth - 30 - mFrameWidth / 2f, 30 + mFrameHeight / 2f, mPointPaint);
            }
            if (Integer.parseInt(playerPointLeft) > 0) {
                mCanvas.save();
                mCanvas.rotate(45, mWidth / 2f, 0);
                mCanvas.drawText(playerPointLeft, mWidth / 2f - playerPointLeftWidth - 30, mWidth / 4f + mFramePadding + mFrameHeight / 2f, mPointPaint);
                mCanvas.restore();
            }
            if (Integer.parseInt(playerPointCenter) > 0) {
                mCanvas.drawText(playerPointCenter, mWidth / 2f - playerPointCenterWidth - 30 - mFrameWidth / 2f, mWidth / 4f + mFramePadding + mFrameHeight / 2f, mPointPaint);
            }
            if (Integer.parseInt(playerPointRight) > 0) {
                mCanvas.save();
                mCanvas.rotate(-45, mWidth / 2f, 0);
                mCanvas.drawText(playerPointRight, mWidth / 2f - playerPointRightWidth - 30 - mFrameWidth, mWidth / 4f + mFramePadding + mFrameHeight / 2f, mPointPaint);
                mCanvas.restore();
            }
        }
    }

    private void drawCardPile() {
        mCanvas.drawBitmap(mCardBack, null, mCardPileRect, null);
    }

    private void drawCards() {
        List<Card> cards = mAnimatorHelper.getCards();
        if (cards != null && cards.size() > 0) {
            for (Card card: cards) {
                card.draw(getContext(), mCanvas, mFrameWidth, mFrameHeight, mWidth / 2f, 0);
            }
        }
    }

    private void drawCardPendingFrame() {

        // 最右侧的方块区域
        mCanvas.save();
        mCanvas.rotate(-(90 - ARC_START_ANGLE), mWidth / 2f, 0);
        mCanvas.drawRect(mWidth / 2f - mFrameWidth, mWidth / 4f + mFramePadding, mWidth / 2f, mWidth / 4f + mFramePadding + mFrameHeight, mArcPaint);
        mCanvas.restore();

        // 最左侧的方块区域
        mCanvas.save();
        mCanvas.rotate(90 - ARC_START_ANGLE, mWidth / 2f, 0);
        mCanvas.drawRect(mWidth / 2f, mWidth / 4f + mFramePadding, mWidth / 2f + mFrameWidth, mWidth / 4f + mFramePadding + mFrameHeight, mArcPaint);
        mCanvas.restore();

        // 中间的方块区域
        mCanvas.drawRect(mWidth / 2f - mFrameWidth / 2f, mWidth / 4f + mFramePadding, mWidth / 2f + mFrameWidth / 2f, mWidth / 4f + mFramePadding + mFrameHeight, mArcPaint);
    }

    private void drawBackgroundArc() {
        mCanvas.drawArc(mArcOuterRectF, ARC_START_ANGLE, ARC_END_ANGLE - ARC_START_ANGLE, false, mArcPaint);
        mCanvas.drawArc(mArcInnerRectF, ARC_START_ANGLE, ARC_END_ANGLE - ARC_START_ANGLE, false, mArcPaint);
        mCanvas.drawLine(mArcLines[0], mArcLines[1], mArcLines[2], mArcLines[3], mArcPaint);
        mCanvas.drawLine(mArcLines[4], mArcLines[5], mArcLines[6], mArcLines[7], mArcPaint);
    }

    private void drawTitle() {
        mCanvas.drawTextOnPath(TITLE, mTitlePath, (mTitlePathLength - mTitleLength) / 2f, -mArcPaddingDp, mTitlePaint);
    }

    private void drawBackground() {
        mCanvas.drawRect(0, 0, mWidth, mHeight, mBackgroundPaint);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = width;
        mHeight= height;
        mBackgroundGradient = new SweepGradient(width / 2f, -height / 4f, BACKGROUND_GRADIENT, new float[]{0, 0.25f, 0.5f});
//        mBackgroundPaint.setShader(mBackgroundGradient);
        mBackgroundPaint.setColor(0xFF429435);
        mTitlePath = new Path();
        mTitlePath.addArc(mWidth / 4f, -mWidth / 4f, 3 * mWidth / 4f, mWidth / 4f, 135, -90);
        PathMeasure pathMeasure = new PathMeasure(mTitlePath, false);
        mTitlePathLength = pathMeasure.getLength();
        mFrameWidth = mHeight / 6;
        mFrameHeight = mFrameWidth * 150 / 105;
        mFramePadding = UIUtils.sp2px(getContext(), 15);
        mAnimatorHelper.init(mWidth - mFrameWidth - 20, 20, mWidth / 2f - mFrameWidth / 2f, 20,
                mWidth / 2f - mFrameWidth / 2f, mWidth / 4f + mFramePadding, mFrameWidth, mFrameHeight);


        float innerArcHeight = 2 * UIUtils.dp2px(getContext(), ARC_PADDING_DP) + mTitleHeight;
        float innerRadius = mWidth / 4f - innerArcHeight;
        mArcInnerRectF.set(mWidth / 4f + innerArcHeight, -mWidth / 4f + innerArcHeight, 3 * mWidth / 4f - innerArcHeight,
                mWidth / 4f - innerArcHeight);
        mArcOuterRectF.set(mWidth / 4f, -mWidth / 4f, 3 * mWidth / 4f, mWidth / 4f);
        mArcLines[0] = (float) (mWidth / 2f + mWidth / 4f * Math.cos(Math.toRadians(-ARC_START_ANGLE)));
        mArcLines[1] = (float) (mWidth / 4f * Math.sin(Math.toRadians(ARC_START_ANGLE)));
        mArcLines[2] = (float) (mWidth / 2f + innerRadius * Math.cos(Math.toRadians(-ARC_START_ANGLE)));
        mArcLines[3] = (float) (innerRadius * Math.sin(Math.toRadians(ARC_START_ANGLE)));
        mArcLines[4] = (float) (mWidth / 2f + mWidth / 4f * Math.cos(Math.toRadians(-ARC_END_ANGLE)));
        mArcLines[5] = (float) (mWidth / 4f * Math.sin(Math.toRadians(ARC_END_ANGLE)));
        mArcLines[6] = (float) (mWidth / 2f + innerRadius * Math.cos(Math.toRadians(-ARC_END_ANGLE)));
        mArcLines[7] = (float) (innerRadius * Math.sin(Math.toRadians(ARC_END_ANGLE)));

        mCardBack = UIUtils.getBitmap(getContext(), R.drawable.back, mFrameWidth, mFrameHeight);
        mCardPileRect = new Rect(mWidth - 20 - mFrameWidth, 20, mWidth - 20, 20 + mFrameHeight);
        if (onGameViewReady != null) {
            onGameViewReady.onReady();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    public interface OnGameViewReady {
        void onReady();
    }
}
