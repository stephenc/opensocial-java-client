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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


/**
 * Displays a dialog box containing top scores from the current player,
 * their friends from the attached social network, and the global top
 * scores
 */
public class TopScoresDialog extends Dialog implements View.OnClickListener {

    private View mNewGame;
    private View mQuit;
    private final NewGameCallback mCallback;

    private TableLayout mScoreTable;
    private ArrayList<Hashtable<String,String>> mTopScoreList;
    private ArrayList<Hashtable<String,String>> mTopScoreListForFriends;
    private Hashtable<String,String> mFriendPictures;

    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;
        private ArrayList<Hashtable<String,String>> mTopScoreListForFriends;
        private Hashtable<String,String> mFriendPictures;


        public ImageAdapter(Context c, ArrayList<Hashtable<String,String>> topScoreListForFriends, Hashtable<String,String> friendPictures) {
            mContext = c;
            mTopScoreListForFriends = topScoreListForFriends;
            mFriendPictures = friendPictures;

            TypedArray a = getContext().obtainStyledAttributes(R.styleable.TopScoresDialogGallery);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.TopScoresDialogGallery_android_galleryItemBackground, 0);
            a.recycle();
        }

        /** {@inheritDoc} */
        public int getCount() {
            return mTopScoreListForFriends.size();
        }

        /** {@inheritDoc} */
        public Object getItem(int position) {
            return mTopScoreListForFriends.get(position).get("picture");
        }

        /** {@inheritDoc} */
        public long getItemId(int position) {
            return position;
        }
        
        /** {@inheritDoc} */
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.VERTICAL);
            ImageView i = new ImageView(mContext);

            FileInputStream is;
            String fileName = null;
            try {
                fileName = mFriendPictures.get(mTopScoreListForFriends.get(position).get("user"));
                if (fileName != null) {
                    is = getContext().openFileInput(fileName);
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    i.setImageBitmap(bm);
                } else {
                    i.setImageResource(R.drawable.android);    
                }
                i.setScaleType(ImageView.ScaleType.FIT_XY);
                i.setLayoutParams(new Gallery.LayoutParams(85,85));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ll.addView(i);

            TextView tv = new TextView(getContext());
            tv.setBackgroundColor(Color.BLACK);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText("Level:" + mTopScoreListForFriends.get(position).get("level"));
            ll.addView(tv);

            TextView tv2 = new TextView(getContext());
            tv2.setBackgroundColor(Color.BLACK);
            tv2.setTextColor(Color.WHITE);
            tv2.setGravity(Gravity.CENTER_HORIZONTAL);
            long time = Long.valueOf(mTopScoreListForFriends.get(position).get("time")).longValue();
            String timeString = String.format("%dm %ds", 
                    time / 60000,
                    (time % 60000)/1000);
            tv2.setText(timeString);
            ll.addView(tv2);    

            ll.setBackgroundResource(mGalleryItemBackground);

            return ll;
        }
    }


    public TopScoresDialog(Context context, ArrayList<Hashtable<String,String>> topScoreList, ArrayList<Hashtable<String, String>> topScoreListForFriends, Hashtable<String,String> friendPictures, NewGameCallback callback) {
        super(context);
        mTopScoreList = topScoreList;
        mTopScoreListForFriends = topScoreListForFriends;
        mFriendPictures = friendPictures;
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.top_scores);


        setContentView(R.layout.top_scores_dialog);
        //View mLayout = findViewById(R.id.mainlayout);
        //mLayout.setBackgroundColor(Color.BLACK);

        mNewGame = findViewById(R.id.newGame);
        mNewGame.setOnClickListener(this);

        mQuit = findViewById(R.id.quit);
        mQuit.setOnClickListener(this);

        mScoreTable = (TableLayout) findViewById(R.id.scoreTable);
        if (mTopScoreList.size() > 0) {
            TableRow tr = new TableRow(getContext());
            TextView tvDisplayName = new TextView(getContext());
            tvDisplayName.setText("Name");
            tvDisplayName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvDisplayName.setPadding(5, 0, 5, 0);
            tr.addView(tvDisplayName);     
            TextView tvLevel = new TextView(getContext());
            tvLevel.setText("Level");
            tvLevel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvLevel.setPadding(5, 0, 5, 0);
            tr.addView(tvLevel);
            TextView tvTime = new TextView(getContext());
            tvTime.setText("Time");
            tvTime.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvTime.setPadding(5, 0, 5, 0);
            tr.addView(tvTime);
            mScoreTable.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }
        for (Hashtable<String,String>topScore : mTopScoreList) {
            TableRow tr = new TableRow(getContext());
            tr.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            tr.setPadding(0, 0, 0, 0);
            TextView tvDisplayName = new TextView(getContext());
            tvDisplayName.setText(topScore.get("displayName"));
            tvDisplayName.setPadding(5, 0, 5, 0);
            tr.addView(tvDisplayName);
            TextView tvLevel = new TextView(getContext());
            tvLevel.setText(topScore.get("level"));
            tvLevel.setPadding(5, 0, 5, 0);
            tr.addView(tvLevel);
            long time = Long.valueOf(topScore.get("time")).longValue();
            String timeString = String.format("%dm %ds", 
                    time / 60000,
                    (time % 60000)/1000);
            TextView tvTime = new TextView(getContext());
            tvTime.setText(timeString);
            tvTime.setPadding(5, 0, 5, 0);
            tr.addView(tvTime);

            mScoreTable.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
        }


        Gallery g = (Gallery) findViewById(R.id.gallery);
        g.setAdapter(new ImageAdapter(getContext(), mTopScoreListForFriends, mFriendPictures));
        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getContext(), mTopScoreListForFriends.get(position).get("displayName"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** {@inheritDoc} */
    public void onClick(View v) {
        if (v == mNewGame) {
            mCallback.onNewGame();
            dismiss();
        } else if (v == mQuit) {
            cancel();
        }
    }

}
