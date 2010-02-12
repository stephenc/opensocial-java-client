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

import org.opensocial.auth.OAuth3LeggedScheme;
import org.opensocial.providers.Provider;

import java.io.IOException;
import java.net.URISyntaxException;

public class OpenSocialChooserActivity extends ListActivity {

  private String scheme;
  private String[] providers;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    scheme = getIntent().getStringExtra(OpenSocialActivity.SCHEME);
    providers = getIntent().getStringArrayExtra(OpenSocialActivity.PROVIDERS);

    setListAdapter(new ArrayAdapter<String>(this, android.R.layout.
        simple_list_item_single_choice, providers));
  }

  @Override
  protected void onListItemClick(ListView listView, View view, int position,
      long id) {
    final String providerName = providers[position];
    final OAuth3LeggedScheme authScheme = getAuth(providerName);

    if (authScheme != null) {
      final AlertDialog alert = new AlertDialog.Builder(this).create();
      alert.setMessage("To get started, you will need to login to " + providerName);
      alert.setButton("Login", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
          alert.dismiss();

          String url = null;
          try {
            url = authScheme.getAuthorizationUrl(scheme + "://");
            persistRequestToken(authScheme.getRequestToken(), providerName);
          } catch (IOException e) {
            throw new RuntimeException("Error occured fetching request token: " + e.getMessage(), e);
          } catch (URISyntaxException e) {
            throw new RuntimeException("Error occured fetching request token", e);
          } catch (OAuthException e) {
            throw new RuntimeException("Error occured fetching request token", e);
          }

          Intent i = new Intent(Intent.ACTION_VIEW);
          i.setData(Uri.parse(url));
          startActivity(i);

          finish();
        }
      });
      alert.show();
    }
  }

  private void persistRequestToken(OAuth3LeggedScheme.Token requestToken,
      String providerName) {
    SharedPreferences.Editor editor =
      getSharedPreferences("default", MODE_PRIVATE).edit();

    if (requestToken != null) {
      editor.putString(OpenSocialActivity.SELECTED_PROVIDER, providerName);
      editor.putString(OpenSocialActivity.REQUEST_TOKEN, requestToken.token);
      editor.putString(OpenSocialActivity.REQUEST_TOKEN_SECRET,
          requestToken.secret);
    }

    editor.commit();
  }

  private OAuth3LeggedScheme getAuth(String name) {
    Class<? extends Provider> providerClass = (Class<? extends Provider>)
        getIntent().getSerializableExtra(name + OpenSocialActivity.CLASS);
    String[] credentials = getIntent().getStringArrayExtra(name +
        OpenSocialActivity.CREDENTIALS);

    if (providerClass == null || credentials == null ||
        credentials.length < 2) {
      return null;
    }

    try {
      Provider provider = providerClass.newInstance();

      return new OAuth3LeggedScheme(provider, credentials[0], credentials[1]);
    } catch (InstantiationException e) {
      return null;
    } catch (IllegalAccessException e) {
      return null;
    }
  }
}
