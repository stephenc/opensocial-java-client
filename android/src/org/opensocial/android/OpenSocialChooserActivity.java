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
package org.opensocial.android;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import net.oauth.OAuthException;
import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialOAuthClient;
import org.opensocial.client.OpenSocialProvider;
import org.opensocial.client.Token;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Prompts the user to choose from one of the supported OpenSocial providers. Once the user has
 * choosen, a request token is fetched, and then the user isredirected to the browser to
 * login with their provider.
 *
 * @author Cassandra Doll
 */
public class OpenSocialChooserActivity extends ListActivity {
  public static final String ANDROID_SCHEME = "androidScheme";
  public static final String PROVIDERS = "providers";
  public static final String CURRENT_PROVIDER_PREF = "currentprovider";
  public static final String REQUEST_TOKEN_PREF = "requesttoken.publictoken";
  public static final String REQUEST_TOKEN_SECRET_PREF = "requesttoken.secret";

  public ArrayList<String> providers;
  public Intent intent;
  private String androidScheme;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    intent = getIntent();
    providers = intent.getStringArrayListExtra(PROVIDERS);
    androidScheme = intent.getStringExtra(ANDROID_SCHEME);
    setListAdapter(new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_single_choice, providers));
  }

  @Override
  protected void onListItemClick(ListView listView, View view, int position, long l) {
    final String providerString = providers.get(position);

    final OpenSocialClient client = new OpenSocialClient();
    final OpenSocialProvider provider = OpenSocialProvider.valueOf(providerString.toUpperCase());

    String[] consumerToken = intent.getStringArrayExtra(providerString.toUpperCase());

    if (consumerToken != null && consumerToken.length == 2) {
      client.setProperty(OpenSocialClient.Property.CONSUMER_KEY, consumerToken[0]);
      client.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, consumerToken[1]);
    }

    final AlertDialog alert = new AlertDialog.Builder(this).create();
    alert.setMessage("To get started, you will need to login to " + providerString);

    alert.setButton("Login", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        alert.dismiss();
        
        Token token;
        try {
          token = OpenSocialOAuthClient.getRequestToken(client, provider);
        } catch (IOException e) {
          throw new RuntimeException("Error occured fetching request token", e);
        } catch (URISyntaxException e) {
          throw new RuntimeException("Error occured fetching request token", e);
        } catch (OAuthException e) {
          throw new RuntimeException("Error occured fetching request token", e);
        }

        persistRequestToken(token, providerString);
        String url = OpenSocialOAuthClient.getAuthorizationUrl(provider, token,
            androidScheme + "://");

        // Browse to webpage
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
      }
    });
    alert.show();
  }

  private void persistRequestToken(Token requestToken, String provider) {
    // TODO: Integration is pretty tight here...
    SharedPreferences.Editor editor = getSharedPreferences("default", MODE_PRIVATE).edit();

    editor.putString(CURRENT_PROVIDER_PREF, provider);
    if (requestToken != null) {
      editor.putString(REQUEST_TOKEN_PREF, requestToken.token);
      editor.putString(REQUEST_TOKEN_SECRET_PREF, requestToken.secret);
    }
    editor.commit();
  }
}