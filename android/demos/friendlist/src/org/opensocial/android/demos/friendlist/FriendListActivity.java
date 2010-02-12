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

package org.opensocial.android.demos.friendlist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.opensocial.Client;
import org.opensocial.Request;
import org.opensocial.RequestException;
import org.opensocial.android.OpenSocialActivity;
import org.opensocial.models.Person;
import org.opensocial.providers.GoogleProvider;
import org.opensocial.services.PeopleService;

import java.io.IOException;
import java.util.List;

public class FriendListActivity extends OpenSocialActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    setScheme("x-opensocial-demo-app");

    addProvider(new GoogleProvider(), new String[] {"anonymous",
        "anonymous"});

    super.onCreate(savedInstanceState);

    setupClient();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);

    if (hasFocus) {
      setupClient();
    }
  }

  private void setupClient() {
    Client client = getClient();

    if (client == null) {
      showChooser();
    } else {
      showContacts(client);
    }
  }

  private void showContacts(Client client) {
    List<Person> friends = null;
    try {
      Request request = PeopleService.getFriends();
      friends = client.send(request).getEntries();
    } catch (RequestException e) {
      Log.i("DisplayFriendActivity", "Couldn't fetch friends from the " +
          "container: " + e.getMessage());
    } catch (IOException e) {
      Log.i("DisplayFriendActivity", "Couldn't fetch friends from the " +
          "container: " + e.getMessage());
    }

    LinearLayout linearLayout = new LinearLayout(this);
    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.FILL_PARENT,
        LinearLayout.LayoutParams.FILL_PARENT));
    linearLayout.setOrientation(LinearLayout.VERTICAL);

    if (friends != null && friends.size() > 0) {
      FriendListView contactsView = new FriendListView(this);
      contactsView.setFriends(friends);
      contactsView.setVerticalScrollBarEnabled(true);
      contactsView.setLayoutParams(new ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.FILL_PARENT, 300));
      linearLayout.addView(contactsView);
    } else {
      TextView textView = new TextView(this);
      textView.setText("No contacts found.");
      linearLayout.addView(textView);
    }

    final FriendListActivity activity = this;

    Button clearAuthButton = new Button(this);
    clearAuthButton.setText("Clear Auth");
    clearAuthButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        clearSavedAuthentication();
      }
    });

    Button fetchFriendsButton = new Button(this);
    fetchFriendsButton.setText("Fetch Friends");
    fetchFriendsButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        activity.setupClient();
      }
    });

    linearLayout.addView(fetchFriendsButton);
    linearLayout.addView(clearAuthButton);

    setContentView(linearLayout);
  }
}
