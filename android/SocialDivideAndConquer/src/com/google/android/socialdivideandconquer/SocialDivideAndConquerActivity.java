/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.socialdivideandconquer;

import java.util.Hashtable;
import java.util.Stack;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The activity for the game.  Listens for callbacks from the game engine, and
 * responds appropriately, such as bringing up a 'game over' dialog when a ball
 * hits a moving line and there is only one life left.
 */
public class SocialDivideAndConquerActivity extends Activity
        implements SocialDivideAndConquerView.BallEventCallBack,
        NewGameCallback,
        DialogInterface.OnCancelListener {

    private static final int NEW_GAME_NUM_BALLS = 1;
    private static final double LEVEL_UP_THRESHOLD = 0.8;
    private static final int TRY_AGAIN_PAUSE = 1000;
    private static final int COLLISION_VIBRATE_MILLIS = 100;

    private boolean mVibrateOn;
    
    private int mNumBalls = NEW_GAME_NUM_BALLS;
    
    private SocialDivideAndConquerView mBallsView;

    private static final int WELCOME_DIALOG = 20;
    private static final int GAME_OVER_DIALOG = 21;
    private static final int TOP_SCORES_DIALOG = 22;
    private WelcomeDialog mWelcomeDialog;
    private GameOverDialog mGameOverDialog;
    private TopScoresDialog mTopScoresDialog;

    private TextView mLivesLeft;
    private TextView mPercentContained;
    private int mNumLives;
    private Vibrator mVibrator;
    private TextView mLevelInfo;
    private int mNumLivesStart = 5;
    
    private Toast mCurrentToast;

    public static String ANDROID_SCHEME = "divide-and-conquer-opensocial";  
    public OpenSocialActivity util;
    private Hashtable<Integer, Long> mTimeTakenPerLevel = new Hashtable<Integer, Long>();
    
    private SocialHandler mSocialHandler;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Turn off the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        mBallsView = (SocialDivideAndConquerView) findViewById(R.id.ballsView);
        mBallsView.setCallback(this);

        mPercentContained = (TextView) findViewById(R.id.percentContained);
        mLevelInfo = (TextView) findViewById(R.id.levelInfo);
        mLivesLeft = (TextView) findViewById(R.id.livesLeft);

        // we'll vibrate when the ball hits the moving line
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        mSocialHandler = new SocialHandler(this);
        

    }
    
    //onNewIntent;
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        
        // If osClient is not already set up, set it up here
        if (! "None".equals(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Preferences.KEY_SNS_VALUE, "None"))
                && intent.getData() != null) {
            mSocialHandler.setupOSClient();
        }
    }

    /** {@inheritDoc} */
    public void onEngineReady(BallEngine ballEngine) {
        // display 10 balls bouncing around for visual effect
        ballEngine.reset(SystemClock.elapsedRealtime(), 10, System.currentTimeMillis());
        mBallsView.setMode(SocialDivideAndConquerView.Mode.Bouncing);

        // show the welcome dialog
        showDialog(WELCOME_DIALOG);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == WELCOME_DIALOG) {
            mWelcomeDialog = new WelcomeDialog(this, this);
            mWelcomeDialog.setOnCancelListener(this);
            return mWelcomeDialog;
        } else if (id == GAME_OVER_DIALOG) {
            mGameOverDialog = new GameOverDialog(this, this);
            mGameOverDialog.setOnCancelListener(this);
            return mGameOverDialog;
        } else if (id == TOP_SCORES_DIALOG) {
            mTopScoresDialog = new TopScoresDialog(this,mSocialHandler.getCachedTopScoreList(),mSocialHandler.getCachedTopScoreListForFriends(),mSocialHandler.getCachedFriendPictures(),this);
            mTopScoresDialog.setOnCancelListener(this);           
            return mTopScoresDialog;
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBallsView.setMode(SocialDivideAndConquerView.Mode.PausedByUser);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVibrateOn = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Preferences.KEY_VIBRATE, true);

        mNumLivesStart = Preferences.getCurrentDifficulty(this).getLivesToStart();
    }

    private static final int MENU_NEW_GAME = Menu.FIRST;
    private static final int MENU_SETTINGS = Menu.FIRST + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_NEW_GAME, MENU_NEW_GAME, "New Game");
        menu.add(0, MENU_SETTINGS, MENU_SETTINGS, "Settings");

        return true;        
    }

    /**
     * We pause the game while the menu is open; this remembers what it was
     * so we can restore when the menu closes
     */
    Stack<SocialDivideAndConquerView.Mode> mRestoreMode = new Stack<SocialDivideAndConquerView.Mode>();

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        saveMode();
        mBallsView.setMode(SocialDivideAndConquerView.Mode.Paused);
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case MENU_NEW_GAME:
                cancelToasts();
                onNewGame();
                break;
            case MENU_SETTINGS:
                final Intent intent = new Intent();
                intent.setClass(this, Preferences.class);
                startActivity(intent);
                break;
        }

        mRestoreMode.pop(); // don't want to restore when an action was taken

        return true;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        restoreMode();
    }


    private void saveMode() {
        // don't want to restore to a state where user can't resume game.
        final SocialDivideAndConquerView.Mode mode = mBallsView.getMode();
        final SocialDivideAndConquerView.Mode toRestore = (mode == SocialDivideAndConquerView.Mode.Paused) ?
                SocialDivideAndConquerView.Mode.PausedByUser : mode;
        mRestoreMode.push(toRestore);
    }

    private void restoreMode() {
        if (!mRestoreMode.isEmpty()) {
            mBallsView.setMode(mRestoreMode.pop());
        }
    }

    /** {@inheritDoc} */
    public void onBallHitsMovingLine(final BallEngine ballEngine, float x, float y) {
        saveMode();
        mBallsView.setMode(SocialDivideAndConquerView.Mode.Paused);
        if (--mNumLives == 0) {
            // vibrate three times
            if (mVibrateOn) {
                mVibrator.vibrate(
                    new long[]{0l, COLLISION_VIBRATE_MILLIS,
                                   50l, COLLISION_VIBRATE_MILLIS,
                                   50l, COLLISION_VIBRATE_MILLIS},
                        -1);
            }
            if (! showSocialDataIfConfigured()) {
                showDialog(GAME_OVER_DIALOG);
            }
            
        } else {
            if (mVibrateOn) {
                mVibrator.vibrate(COLLISION_VIBRATE_MILLIS);
            }
            mBallsView.postDelayed(new Runnable() {
                public void run() {
                    ballEngine.reset(SystemClock.elapsedRealtime(), mNumBalls, System.currentTimeMillis());
                    updateLivesDisplay(mNumLives, true);
                    updatePercentDisplay(0);
                    restoreMode();
                }
            }, TRY_AGAIN_PAUSE);
        }
    }
    
    private boolean showSocialDataIfConfigured() {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getString(SocialNetworkChooserListPreference.CURRENT_PROVIDER_PREF, null)
                != null && ! "None".equals(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Preferences.KEY_SNS_VALUE, "None"))) {
            mSocialHandler.postSocialScores(mTimeTakenPerLevel);

            try {
                // time to wait in seconds
                int waitForScoresTime = 3; 
                while (waitForScoresTime > 0 && 
                        (mSocialHandler.getCachedFriendPictures() == null ||
                         mSocialHandler.getCachedTopScoreList() == null ||
                         mSocialHandler.getCachedTopScoreListForFriends()== null)) {
                        
                    waitForScoresTime--;
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                // thread interrupted
            }
            
            if (mSocialHandler.getCachedFriendPictures() != null &&
                mSocialHandler.getCachedTopScoreList() != null &&
                mSocialHandler.getCachedTopScoreListForFriends()!= null) {
          
                    showDialog(TOP_SCORES_DIALOG);
            } else {
                showDialog(GAME_OVER_DIALOG);
            }
            return true;
        } else {
            return false;
        }
    }
    
    /** {@inheritDoc} */
    public void onAreaChange(final BallEngine ballEngine) {
        final float percentageFilled = ballEngine.getPercentageFilled();
        updatePercentDisplay(percentageFilled);
        if (percentageFilled > LEVEL_UP_THRESHOLD) {
            levelUp(ballEngine);
        }
    }

    /**
     * Go to the next level
     * @param ballEngine The ball engine.
     */
    private void levelUp(final BallEngine ballEngine) {
        mNumBalls++;

        updatePercentDisplay(0);
        updateLevelDisplay(mNumBalls);
        
        long currentTime = System.currentTimeMillis();
        long startTime = ballEngine.getStartTime();
        long diff = currentTime - startTime;
        
        // Save the time taken for each level in the hashtable
        mTimeTakenPerLevel.put(Integer.valueOf(mNumBalls-1), Long.valueOf(diff));

        ballEngine.reset(SystemClock.elapsedRealtime(), mNumBalls, System.currentTimeMillis());
        mBallsView.setMode(SocialDivideAndConquerView.Mode.Bouncing);
        showToast("level " + mNumBalls);
    }
      
    private void showToast(String text) {
        cancelToasts();
        mCurrentToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    private void cancelToasts() {
        if (mCurrentToast != null) {
            mCurrentToast.cancel();
            mCurrentToast = null;
        }
    }

    /**
     * Update the header that displays how much of the space has been contained.
     * @param amountFilled The fraction, between 0 and 1, that is filled.
     */
    private void updatePercentDisplay(float amountFilled) {
        final int prettyPercent = (int) (amountFilled *100);
        mPercentContained.setText(
                getString(R.string.percent_contained, prettyPercent));
    }

    /** {@inheritDoc} */
    public void onNewGame() {
        mNumBalls = NEW_GAME_NUM_BALLS;
        mNumLives = mNumLivesStart;
        updatePercentDisplay(0);
        updateLivesDisplay(mNumLives, false);
        updateLevelDisplay(mNumBalls);
        mBallsView.getEngine().reset(SystemClock.elapsedRealtime(), mNumBalls, System.currentTimeMillis());
        mBallsView.setMode(SocialDivideAndConquerView.Mode.Bouncing);
    }

    /**
     * Update the header displaying the current level
     */
    private void updateLevelDisplay(int numBalls) {
        mLevelInfo.setText(getString(R.string.level, numBalls));
    }

    /**
     * Update the display showing the number of lives left.
     * 
     * @param numLives The number of lives left.
     * @param showToast Whether to show a toast with the number of lives left
     *   too.
     */
    void updateLivesDisplay(int numLives, boolean showToast) {
        String text = (numLives == 1) ?
                getString(R.string.one_life_left) : getString(R.string.lives_left, numLives);
        mLivesLeft.setText(text);
        if (showToast) {
            showToast(text);
        }
    }

    /** {@inheritDoc} */
    public void onCancel(DialogInterface dialog) {
        if (dialog == mWelcomeDialog || dialog == mGameOverDialog || dialog == mTopScoresDialog) {          
          // user hit back, they're done!
          finish();
        }
    }
    
}
