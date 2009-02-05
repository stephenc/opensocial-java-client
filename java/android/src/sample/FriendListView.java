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
package sample;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.opensocial.data.OpenSocialPerson;

import java.util.List;

/**
 * Demo class. Displays a list of contacts.
 *
 * @author Cassandra Doll
 */
public class FriendListView extends ListView {
  public FriendListView(Context context) {
    super(context);

    this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((ContactView) view).toggle();
      }
    });
  }

  public void setFriends(final List<OpenSocialPerson> contacts) {
    this.setAdapter(new BaseAdapter() {

      public int getCount() {
        return contacts.size();
      }

      public Object getItem(int i) {
        return contacts.get(i);
      }

      public long getItemId(int i) {
        return i;
      }

      public View getView(int position, View convertView, ViewGroup parent) {
        OpenSocialPerson c = contacts.get(position);
        return new ContactView(FriendListView.this.getContext(), c);
      }
    });
  }

  public static class ContactView extends LinearLayout {
    private boolean expanded = false;
    private TextView textView;
    private String compactText;
    private String expandedText;

    public ContactView(Context context, OpenSocialPerson c) {
      super(context);

      compactText = c.getDisplayName();
      expandedText = c.toString();

      textView = new TextView(context);
      textView.setText(compactText);
      textView.setTextSize(18);

      this.addView(textView);
    }

    public void toggle() {
      setExpanded(!this.expanded);
    }

    private void setExpanded(boolean b) {
      expanded = b;

      if (expanded) {
        textView.setText(expandedText);
      } else {
        textView.setText(compactText);
      }
    }

  }
}
