/* Copyright (c) 2009 Google Inc.
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
 
package org.opensocial.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import net.oauth.OAuth;
import net.oauth.OAuthException;

import org.opensocial.Client;
import org.opensocial.auth.OAuth3LeggedScheme;
import org.opensocial.providers.Provider;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class OpenSocialActivity extends Activity {

  public static final String CLASS = "class";
  public static final String SCHEME = "scheme";
  public static final String PROVIDERS = "providers";
  public static final String CREDENTIALS = "credentials";
  public static final String ACCESS_TOKEN = "accesstoken";
  public static final String REQUEST_TOKEN = "requesttoken";
  public static final String SELECTED_PROVIDER = "selectedprovider";
  public static final String ACCESS_TOKEN_SECRET = "accesstokensecret";
  public static final String REQUEST_TOKEN_SECRET = "requesttokensecret";

  private HashMap<String, Class<? extends Provider>> providerClasses =
    new HashMap<String, Class<? extends Provider>>();
  private HashMap<String, String[]> providerCredentials =
    new HashMap<String, String[]>();
  private String scheme = "";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getIntent().getData() != null) {
      Provider provider = loadSelectedProvider();
      OAuth3LeggedScheme.Token requestToken = loadRequestToken();

      if (provider != null && requestToken != null) {
        try {
          String[] credentials = providerCredentials.get(provider.getName());
          OAuth3LeggedScheme authScheme = new OAuth3LeggedScheme(provider,
              credentials[0], credentials[1]);
          authScheme.setRequestToken(requestToken);
          authScheme.requestAccessToken(requestToken.token);
          persistAccessToken(authScheme.getAccessToken());
        } catch (OAuthException e) {
          throw new RuntimeException("Error occured fetching access token", e);
        } catch (URISyntaxException e) {
          throw new RuntimeException("Error occured fetching access token", e);
        } catch (IOException e) {
          throw new RuntimeException("Error occured fetching access token", e);
        }
      }
    }
  }

  protected void setScheme(String scheme) {
    this.scheme = scheme;
  }

  protected void addProvider(Provider provider, String[] credentials) {
    if (provider != null && credentials != null && credentials.length == 2) {
      providerClasses.put(provider.getName(), provider.getClass());
      providerCredentials.put(provider.getName(), credentials);
    }
  }

  protected Client getClient() {
    OAuth3LeggedScheme.Token requestToken = loadRequestToken();
    OAuth3LeggedScheme.Token accessToken = loadAccessToken();
    Provider provider = loadSelectedProvider();

    if (requestToken != null && accessToken != null && provider != null) {
      String[] credentials = providerCredentials.get(provider.getName());
      OAuth3LeggedScheme authScheme = new OAuth3LeggedScheme(provider,
          credentials[0], credentials[1]);
      authScheme.setRequestToken(requestToken);
      authScheme.setAccessToken(accessToken);

      return new Client(provider, authScheme);
    }

    return null;
  }

  protected void showChooser() {
    Intent chooser = new Intent(this, OpenSocialChooserActivity.class);

    String[] providers = providerClasses.keySet().toArray(new String[] {});
    chooser.putExtra(PROVIDERS, providers);
    chooser.putExtra(SCHEME, scheme);

    for (String name : providers) {
      chooser.putExtra(name + CREDENTIALS, providerCredentials.get(name));
      chooser.putExtra(name + CLASS, providerClasses.get(name));
    }

    startActivity(chooser);
  }

  protected void clearSavedAuthentication() {
    SharedPreferences.Editor editor = getSharedPreferences("default",
        MODE_PRIVATE).edit();

    editor.remove(ACCESS_TOKEN_SECRET);
    editor.remove(SELECTED_PROVIDER);
    editor.remove(ACCESS_TOKEN);
    editor.commit();
  }

  private Provider loadSelectedProvider() {
    SharedPreferences preferences = getSharedPreferences("default",
        MODE_PRIVATE);

    String name = preferences.getString(SELECTED_PROVIDER, null);

    try {
      Class<? extends Provider> providerClass = providerClasses.get(name);

      return providerClass.newInstance();
    } catch (NullPointerException e) {
      return null;
    } catch (InstantiationException e) {
      return null;
    } catch (IllegalAccessException e) {
      return null;
    }
  }

  private OAuth3LeggedScheme.Token loadRequestToken() {
    SharedPreferences preferences = getSharedPreferences("default",
        MODE_PRIVATE);

    String requestTokenSecret = preferences.getString(REQUEST_TOKEN_SECRET,
        null);
    String requestToken = preferences.getString(REQUEST_TOKEN, null);
    Uri data = getIntent().getData();

    if (requestToken != null) {
      return new OAuth3LeggedScheme.Token(requestToken, requestTokenSecret);
    }

    if (data != null) {
      int tokenIndex = data.toString().indexOf("oauth_token");

      if (tokenIndex != -1) {
        String token = data.toString().substring(tokenIndex + 12);

        return new OAuth3LeggedScheme.Token(OAuth.decodePercent(token), "");
      }
    }

    return null;
  }

  private OAuth3LeggedScheme.Token loadAccessToken() {
    SharedPreferences preferences = getSharedPreferences("default",
        MODE_PRIVATE);
    
    String accessTokenSecret = preferences.getString(ACCESS_TOKEN_SECRET,
        null);
    String accessToken = preferences.getString(ACCESS_TOKEN, null);

    if (accessToken == null) {
      return null;
    }

    return new OAuth3LeggedScheme.Token(accessToken, accessTokenSecret);
  }

  private void persistAccessToken(OAuth3LeggedScheme.Token token) {
    SharedPreferences.Editor editor = getSharedPreferences("default",
        MODE_PRIVATE).edit();

    editor.putString(ACCESS_TOKEN_SECRET, token.secret);
    editor.putString(ACCESS_TOKEN, token.token);
    editor.commit();
  }
}
