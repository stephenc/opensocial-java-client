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

import java.util.ArrayList;
import java.util.Map;

import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialOAuthClient;
import org.opensocial.client.OpenSocialProvider;
import org.opensocial.client.Token;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.Log;

public class SocialNetworkChooserListPreference extends ListPreference {

    public static final String ANDROID_SCHEME = "divide-and-conquer-opensocial";
    public static final String PROVIDERS = "providers";
    public static final String CURRENT_PROVIDER_PREF = "currentprovider";
    public static final String REQUEST_TOKEN_PREF = "requesttoken.publictoken";
    public static final String REQUEST_TOKEN_SECRET_PREF = "requesttoken.secret";

    public ArrayList<CharSequence> providerStrings = new ArrayList<CharSequence>();
    public ArrayList<CharSequence> providerStringKeys = new ArrayList<CharSequence>();

    public static int RESULT_CODE_EXPECTED = 1;

    public SocialNetworkChooserListPreference(Context context) {
        super(context);

        String noneString = "None";
        String noneKeyString = "NONE";
        providerStrings.add(noneString);
        providerStringKeys.add(noneKeyString);

        this.setDefaultValue(noneKeyString);


        for (Map.Entry<OpenSocialProvider, Token> entry : SocialHandler.SUPPORTED_PROVIDERS.entrySet()) {
            providerStrings.add((CharSequence) entry.getKey().providerName);
            providerStringKeys.add((CharSequence) entry.getKey().providerName.toUpperCase());
        }


        CharSequence[] charSequenceTemplate = new CharSequence[providerStrings.size()];
        this.setEntries(providerStringKeys.toArray(charSequenceTemplate));
        this.setEntryValues(providerStrings.toArray(charSequenceTemplate));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        final String providerString = this.getValue();

        if (positiveResult && !"NONE".equals(providerString.toUpperCase())) {

            final OpenSocialClient client = new OpenSocialClient();
            final OpenSocialProvider provider = OpenSocialProvider.valueOf(providerString.toUpperCase());

            String token = SocialHandler.SUPPORTED_PROVIDERS.get(provider).token;
            String secret = SocialHandler.SUPPORTED_PROVIDERS.get(provider).secret;
            client.setProperty(OpenSocialClient.Property.CONSUMER_KEY, token);
            client.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, secret);

            final AlertDialog alert = new AlertDialog.Builder(getContext()).create();
            alert.setMessage("To get started, you will need to login to " + providerString);

            alert.setButton("Login", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    alert.dismiss();

                    Token token;
                    try {
                        token = OpenSocialOAuthClient.getRequestToken(client, provider);

                        persistRequestToken(token, providerString);
                        String url = OpenSocialOAuthClient.getAuthorizationUrl(provider, token,
                                ANDROID_SCHEME + "://opensocial");

                        // Browse to OAuth access page
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        getContext().startActivity(i);
                    } catch (Exception e) {
                        Log.e("onDialogClosed", "Error opening up OAuth access page", e);
                    }
                }
            });
            alert.show();

        } else if (positiveResult && "NONE".equals(providerString)) {
            clearSavedAuthentication();
        }
    }


    private void clearSavedAuthentication() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

        editor.remove(OpenSocialActivity.ACCESS_TOKEN_PREF);
        editor.remove(OpenSocialActivity.ACCESS_TOKEN_SECRET_PREF);
        editor.remove(SocialNetworkChooserListPreference.CURRENT_PROVIDER_PREF);
        editor.remove(REQUEST_TOKEN_PREF);
        editor.remove(REQUEST_TOKEN_SECRET_PREF);
        editor.commit();
    }
    private void persistRequestToken(Token requestToken, String provider) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

        editor.putString(CURRENT_PROVIDER_PREF, provider);
        if (requestToken != null) {
            editor.putString(REQUEST_TOKEN_PREF, requestToken.token);
            editor.putString(REQUEST_TOKEN_SECRET_PREF, requestToken.secret);
        }
        editor.commit();
    }
}
