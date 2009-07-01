/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.socialdivideandconquer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensocial.client.OpenSocialBatch;
import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialProvider;
import org.opensocial.client.OpenSocialRequest;
import org.opensocial.client.OpenSocialResponse;
import org.opensocial.client.Token;
import org.opensocial.data.OpenSocialPerson;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Handles all communication to social networks and a RPC service in order
 * to store and display top scores for this game
 */
public class SocialHandler {

    private Activity mContext;
    private SocialThread mThread;
    private ArrayList<Hashtable<String,String>> mTopScoreList;
    private ArrayList<Hashtable<String,String>> mTopScoreListForFriends;
    private Hashtable<String,String> mFriendPictures;

    public static String ANDROID_SCHEME = "divide-and-conquer-opensocial";  
    public static String RPC_SERVICE_URL = "http://divide-and-conquer.appspot.com/";
    public static Map<OpenSocialProvider, Token> SUPPORTED_PROVIDERS
        = new HashMap<OpenSocialProvider, Token>();
    public OpenSocialActivity util;
    private OpenSocialClient osClient;
    
    /**
     * Constructor - handles configuring the supported social network providers
     * and starting a SocialThread, if a social network is selected in the
     * preferences.
     * 
     * @param context {@inheritDoc}
     */
    public SocialHandler(Activity context) {
        mContext = context;

        SUPPORTED_PROVIDERS.put(OpenSocialProvider.PLAXO, new Token("anonymous", ""));
        SUPPORTED_PROVIDERS.put(OpenSocialProvider.MYSPACE, new Token("2715653118a942af95f73c7746bf2056", "4de574d169f54385aed12058f560937a"));

        // TODO: Replace with an anonymous/anonymous token, newly supported by Google
        SUPPORTED_PROVIDERS.put(OpenSocialProvider.GOOGLE, new Token("divide-and-conquer.appspot.com", "M/iB/OAFRZ8UM5GHFbMfvTeC"));

        mThread = new SocialThread(context, null);
        if (! "NONE".equals(PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(Preferences.KEY_SNS_VALUE, "NONE"))) {
            mThread.start();
        }
    }
    
    /**
     * Thread to process retrieval of social data
     */
    class SocialThread extends Thread {
        Handler mHandler;
        Context mContext;
        private ArrayList<Hashtable<String,String>> tmTopScoreList;
        private ArrayList<Hashtable<String,String>> tmTopScoreListForFriends;    
        private Hashtable<String,String> tmFriendPictures;

        public SocialThread (Context context, Handler handler) {
            mHandler = handler;
            mContext = context;
        } 

        @Override
        public void run() {
            int runCount = 1;
            setupOSClient();
            try {
                while (true) {
                    tmTopScoreList = getTopScoreList();
                    Hashtable<String,OpenSocialPerson> friends = getSelfAndFriends();
                    if (friends != null) {
                        tmTopScoreListForFriends = getTopScoreListForFriends(friends.keys());
                    }
                    if (tmTopScoreListForFriends != null) {
                        tmFriendPictures = getPicturesForFriends(tmTopScoreListForFriends, friends);
                    }
                    SocialHandler.this.setCachedTopScoreList(tmTopScoreList);
                    SocialHandler.this.setCachedTopScoreListForFriends(tmTopScoreListForFriends);
                    SocialHandler.this.setCachedFriendPictures(tmFriendPictures);
                    runCount++;
                    Thread.sleep(30000);    
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public synchronized ArrayList<Hashtable<String,String>> getCachedTopScoreList(){
        return mTopScoreList;
    }

    private synchronized void setCachedTopScoreList(ArrayList<Hashtable<String,String>> topScoreList) {
        mTopScoreList = topScoreList;
    }

    public synchronized ArrayList<Hashtable<String,String>> getCachedTopScoreListForFriends(){
        return mTopScoreListForFriends;
    }

    private synchronized void setCachedTopScoreListForFriends(ArrayList<Hashtable<String,String>> topScoreListForFriends) {
        mTopScoreListForFriends = topScoreListForFriends;
    }

    public synchronized Hashtable<String,String> getCachedFriendPictures(){
        return mFriendPictures;
    }

    private synchronized void setCachedFriendPictures(Hashtable<String,String> friendPictures) {
        mFriendPictures = friendPictures;
    }

    private Activity getContext() {
        return mContext;
    }

    public SocialThread getThread() {
        return mThread;
    }

    /**
     * Retrieves the thumbnail pictures for each of the users passed.
     * Saves the thumbnail binaries onto android local storage.
     * 
     * @param topScoreListForFriends Hashtable of friends, including "user" and "thumbnailUrl"
     * @param friends Hashtable of OpenSocialPerson objects, indexed by the "user"
     * @return
     */
    public Hashtable<String,String> getPicturesForFriends(ArrayList<Hashtable<String,String>> topScoreListForFriends, Hashtable<String,OpenSocialPerson> friends) {
        /*
         * TODO: If we already have an image from this session, don't fetch it again.
         */
        Hashtable<String,String> userPictures = new Hashtable<String,String>();
        for (Hashtable<String,String> score : topScoreListForFriends) {
            String user = score.get("user");
            OpenSocialPerson person = friends.get(user);
            String thumbnailUrl = null;
            if (person.getField("thumbnailUrl") != null) {
                thumbnailUrl = person.getField("thumbnailUrl").getStringValue();
            }
            if (thumbnailUrl != null) {
                FileOutputStream fOut;
                try {
                    URL url = new URL(thumbnailUrl); 
                    URLConnection conn = url.openConnection(); 
                    conn.connect(); 
                    InputStream is = conn.getInputStream(); 
                    BufferedInputStream bis = new BufferedInputStream(is); 
                    fOut = mContext.openFileOutput(user + ".img", Context.MODE_WORLD_READABLE);
                    int ob;
                    while ((ob = bis.read()) != -1) {
                        fOut.write(ob);
                    }
                    fOut.close();
                    userPictures.put(user, user + ".img");
                } catch (Exception e) {
                    Log.e("getPicturesForFriends","Error downloading and saving picture", e);
                } 
            }
        }
        return userPictures;
    }

    /**
     * Creates the OpenSocialClient object for use by the rest of this class
     */
    public void setupOSClient() {
        util = new OpenSocialActivity(getContext(), SUPPORTED_PROVIDERS, ANDROID_SCHEME);
        OpenSocialClient client = util.getOpenSocialClient();
        if (client != null) {    
            osClient = client;
        }

    }

    /**
     * Posts the latest level successfully completed by the current player,
     * along with the total time taken to reach that level
     * 
     * @param timeTakenPerLevel Hashtable of each level and the time taken
     */
    public void postSocialScores(Hashtable<Integer, Long> timeTakenPerLevel) {
        if (PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(SocialNetworkChooserListPreference.CURRENT_PROVIDER_PREF, null)
                != null && ! "None".equals(PreferenceManager.getDefaultSharedPreferences(getContext()))) {
            setupOSClient();
        }
        if (osClient != null) {    
            ArrayList<Integer> l = new ArrayList<Integer>(timeTakenPerLevel.keySet());
            Collections.sort(l);
            if (l.size() > 0) {
                String lastLevel = l.get(l.size() - 1).toString();
                long totalTime = 0;
                for (Long time: timeTakenPerLevel.values()) {
                    totalTime += time.longValue();
                }

                postUpdate(lastLevel, String.valueOf(totalTime));
            }
        }

    }

    /**
     * Uses the RPC service to get a global top scores list from the server
     * 
     * @return Hashtable of a user's displayName along with top score info
     */
    private ArrayList<Hashtable<String,String>> getTopScoreList() {
        ArrayList<Hashtable<String,String>> returnList = null;
        List<Object> args = new ArrayList<Object>();
        args.add("GetTopScores");
        try {
            returnList = getScoreInfoFromJson(callBackendRPC(args));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return returnList;
    }

    /**
     * Retrieves the top score list for the given friends of the current player
     * 
     * @param friends IDs of the friends
     * @return Hashtable of each friend and their top score
     */
    private ArrayList<Hashtable<String,String>> getTopScoreListForFriends(Enumeration<String> friends) {
        ArrayList<Hashtable<String,String>> returnList = null;
        List<Object> args = new ArrayList<Object>();
        args.add("GetTopScoresForFriends");

        while (friends.hasMoreElements()) {
            args.add(friends.nextElement());
        }
        returnList = getScoreInfoFromJson(callBackendRPC(args));

        return returnList;
    }

    /**
     * Returns a Hashtable of metadata contained in the passed JSON data
     * 
     * @param jsonText JSON-formatted data, containing displayName,level,time,user for each user int he array
     * @return
     */
    private ArrayList<Hashtable<String,String>> getScoreInfoFromJson (String jsonText) {
        ArrayList<Hashtable<String,String>> dataTable = new ArrayList<Hashtable<String,String>>();
        try {
            JSONArray array = new JSONArray(jsonText);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Hashtable<String,String> dataRow = new Hashtable<String,String>();
                dataRow.put("displayName", obj.getString("displayName"));
                dataRow.put("level", obj.getString("level"));
                dataRow.put("time", obj.getString("time"));
                if (obj.has("user")) {
                    dataRow.put("user", obj.getString("user"));
                }
                dataTable.add(dataRow);
            }
        } catch (JSONException e) {
            Log.e("getScoreInfoFromJson", "Error parsing JSON data", e);
        }
        return dataTable;
    }


    /**
     * Calls the backend RPC service (set at RPC_SERVICE_URL) with the 
     * specified arguments passed as a JSONArray
     * 
     * @param args List of arguments
     * @return String response from server
     */
    private String callBackendRPC(List<Object> args) {
        JSONArray array = new JSONArray();
        for (Object arg : args) {
            array.put(arg);
        }
        try {
            Log.i("callBackendRPC", "Making RPC request: " + array.toString());
            HttpURLConnection connection = (HttpURLConnection) new URL(RPC_SERVICE_URL ).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(array.toString());
            out.flush();
            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String response;
            while ((response = reader.readLine()) != null){
                sb.append(response + "\n");
            }
            Log.i("callBackendRPC", "Received RPC response: " + sb.toString());
            return sb.toString();
        } catch (IOException e) {
            Log.e("callBackendRPC", "IOException occurred", e);
        }
        return null;
    }

    /**
     * Retrieves OpenSocialPerson objects for the current player as well as
     * their friends from the connected social network.
     * 
     * @return Hashtble of OpenSocailPerson objects mapped to the user's (NETWORK)_(ID)
     */
    private Hashtable<String,OpenSocialPerson> getSelfAndFriends() {
        Hashtable<String,OpenSocialPerson> returnHash = new Hashtable<String,OpenSocialPerson>();
        List<OpenSocialPerson> friends = new ArrayList<OpenSocialPerson>();
        OpenSocialPerson self = null;  
        OpenSocialClient c = osClient;
        OpenSocialProvider provider = util.getProvider();
        try {
            if (provider.isOpenSocial) {
                // TODO: could optimize using a batch request
                friends = c.fetchFriends();
                self = c.fetchPerson();
            } else {
                // portable contacts provider with a different URL structure
                OpenSocialBatch batch = new OpenSocialBatch();
                batch.addRequest(new OpenSocialRequest("@me/@self", ""), "self");
                batch.addRequest(new OpenSocialRequest("@me/@all", ""), "friends");
                OpenSocialResponse response = batch.send(c);
                friends = response.getItemAsPersonCollection("friends");
                self = response.getItemAsPerson("self");
            }
            for (OpenSocialPerson person : friends) {
                // prefix the ID of each friend with the name of the network
                returnHash.put(provider.toString() + "_" + person.getId(), person);
            }
            if (self != null) {
                // prefix the ID of ourself with the name of the network
                returnHash.put(provider.toString() + "_" + self.getId(), self);
            }
        } catch (Exception e) {
        }
        return returnHash;
    }


    /**
     * Posts the latest level and time taken for the current player to the
     * server.
     * 
     * @param level
     * @param timeTaken
     */
    private void postUpdate(String level, String timeTaken) {
        OpenSocialClient c = osClient;
        OpenSocialProvider provider = util.getProvider();
        OpenSocialPerson person;
        try {
            // TODO: Instead of making an additional request for the user,
            // the user should be differentiated and stored at time of calling
            // getSelfAndFriends()
            if (provider.isOpenSocial) {
                person = c.fetchPerson();
            } else {
                OpenSocialBatch batch = new OpenSocialBatch();
                batch.addRequest(new OpenSocialRequest("@me/@self", ""), "self");
                person = batch.send(c).getItemAsPerson("self");
            }

            List<Object> args = new ArrayList<Object>();
            args.add("SetScore");
            args.add(provider.toString() + "_" + person.getId());
            args.add(level);
            args.add(timeTaken);
            args.add(person.getDisplayName());
            callBackendRPC(args);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("postUpdate", "Couldn't fetch friends from the container: "
                    + e.getMessage());
        }
    }
}
