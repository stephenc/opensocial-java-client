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
 * 
 * @author api.dwh@google.com (Dan Holevoet)
 */

var wua = wua || {};

// Util
wua.util = wua.util || {};

// UI
wua.ui = wua.ui || {};

/*
 * Enables the location form if an accurate position is available.
 */
wua.ui.updateLocationForm = function() {
  if (ds.accuracy <= 150) {
    $('#latitude').val(ds.position[0]);
    $('#longitude').val(ds.position[1]);

    $('#position').attr('disabled', false);
  }
};

/*
 * Disables the location form.
 */
wua.ui.disableLocationForm = function(error) {
  $('#position').attr('disabled', true);
};

/*
 * Updates the display of the selected option in the UI.
 */
wua.ui.changeSelectedOption = function(id) {
  $('#recent').attr('class', '');
  $('#local').attr('class', '');
  $('#friend').attr('class', '');
  
  $(id).attr('class', 'selected');
};

/*
 * Clears all point markers from the map.
 */
wua.ui.clearPointOverlay = function() {
  while (ds.pointOverlay.length > 0) {
    var point = ds.pointOverlay.pop();
    ds.map.removeOverlay(point);
  }
};

/*
 * Creates a marker on the map at the given position, with the given message.
 */
wua.ui.createMarker = function(lat, long, message) {
  if (lat && long && ds.map) {
    var point = new GLatLng(lat, long);
    var marker = new GMarker(point);
    ds.map.addOverlay(marker);
    GEvent.addListener(marker, "click", function() {
      marker.openInfoWindowHtml(message);
    });
    ds.pointOverlay.push(marker);
  }
};

/*
 * Renders recent updates in the UI and marks them on the map, if a position
 * is available.
 */
wua.ui.renderRecentUpdates = function() {
  ds.getRecentUpdates(function(data) {
    $('#updates').empty();
    
    wua.ui.clearPointOverlay();
    
    for (i in data) {
      var update = data[i];
      var date = new Date(update.posted);
      var dateString = (date.getMonth() + 1) + "/" + date.getDate();
      var message = [update.authorName, ' was ', update.content,
        ' on ', dateString].join('');
      var li = jQuery('<li></li>').
                 append(message);
      $('#updates').append(li);
      
      wua.ui.createMarker(update.lat, update.long, message);
    }
    
    wua.ui.changeSelectedOption('#recent');
  });
};

/*
 * Renders nearby updates in the UI and marks them on the map, if a position
 * is available.
 */
wua.ui.renderNearbyUpdates = function() {
  ds.getNearbyUpdates(function(data) {
    $('#updates').empty();
    
    wua.ui.clearPointOverlay();
    
    for (i in data) {
      var update = data[i];
      var date = new Date(update.posted);
      var dateString = (date.getMonth() + 1) + "/" + date.getDate();
      var message = [update.authorName, ' was ', update.content,
        ' on ', dateString].join('');
      var li = jQuery('<li></li>').
                 append(message);
      $('#updates').append(li);
      
      wua.ui.createMarker(update.lat, update.long, message);
    }
    
    wua.ui.changeSelectedOption('#local');
  });
};

/*
 * Renders friend updates in the UI and marks them on the map, if a position
 * is available.
 */
wua.ui.renderFriendUpdates = function() {
  ds.getFriendUpdates(function(data) {
    $('#updates').empty();
    
    wua.ui.clearPointOverlay();
    
    for (i in data) {
      var friend = data[i];
      for (j in friend) {
        var update = friend[j];
        var date = new Date(update.posted);
        var dateString = (date.getMonth() + 1) + "/" + date.getDate();
        var message = [update.authorName, ' was ', update.content,
          ' on ', dateString].join('');
        var li = jQuery('<li></li>').
                   append(message);
        $('#updates').append(li);

        wua.ui.createMarker(update.lat, update.long, message);
      }
    }
    
    wua.ui.changeSelectedOption('#friend');
  });
};

/*
 * Adds buttons for selecting 'recent', 'nearby', or 'friends' updates.
 */
wua.ui.renderUpdateOptions = function() {
  var a = jQuery('<a id="recent" class="selected"></a>').
            append('Recent').
            bind('click', function(e) {
              wua.ui.renderRecentUpdates();
            });
  $('#update_options').append(a).append(' | ');
  
  var a = jQuery('<a id="local"></a>').
            append('Local').
            bind('click', function(e) {
              wua.ui.renderNearbyUpdates();
            });
  $('#update_options').append(a).append(' | ');
  
  var a = jQuery('<a id="friend"></a>').
            append('Friend').
            bind('click', function(e) {
              wua.ui.renderFriendUpdates();
            });
  $('#update_options').append(a)
};

// Datastore
wua.datastore = wua.datastore || function() {
  this.llbound = null;
  this.trbound = null;
  this.center = [37.4195, -122.0823];
  this.zoom = 13;
  this.position = null;
  this.accuracy = null;
  this.overlay = null;
  this.pointOverlay = [];
  this.map = null;
  
  this.recentUpdates = null;
  this.nearbyUpdates = null;
  this.friendUpdates = null;
  this.mode = wua.datastore.MODE_RECENT;
  
  this.geo = null;
};
wua.datastore.MODE_RECENT = 0;
wua.datastore.MODE_NEARBY = 1;
wua.datastore.MODE_FRIEND = 2;

/*
 * Performs initialization of the UI. Collects the current location, if Gears
 * is available, and renders a map if the browser is compatible.
 */
wua.datastore.prototype.init = function() {
  if (typeof(google) != "undefined" && typeof(google.gears) != "undefined") {
    this.geo = google.gears.factory.create('beta.geolocation');
    this.watchPosition();
  }
  
  if (GBrowserIsCompatible()) {
    this.map = new GMap2(document.getElementById("map_canvas"));
    this.map.setCenter(new GLatLng(this.center[0], this.center[1]), this.zoom);
    this.map.addControl(new GSmallMapControl());
    
    this.updateBounds();
  }
  
  wua.ui.renderUpdateOptions();
  wua.ui.renderRecentUpdates();
};

/*
 * Updates the internal representation of the boundary of the visible map.
 */
wua.datastore.prototype.updateBounds = function() {
  var bounds = this.map.getBounds();
  
  var sw = bounds.getSouthWest();
  this.llbound = [sw.lat(), sw.lng()];
  
  var ne = bounds.getNorthEast();
  this.trbound = [ne.lat(), ne.lng()];
};

/*
 * Updates the internal representation of the location and sets the map to
 * center on this location.
 */
wua.datastore.prototype.updatePosition = function(position) {
  this.position = [position.latitude, position.longitude];
  this.accuracy = position.accuracy;
  
  if (this.map) {
    this.map.setCenter(new GLatLng(this.position[0], this.position[1]), this.zoom);
    
    this.updateBounds();
    if (this.mode == wua.datastore.MODE_NEARBY) {
      wua.ui.renderNearbyUpdates();
    }
  }
  
  wua.ui.updateLocationForm();
};

/*
 * Uses Gears to watch for changes to the user's location.
 */
wua.datastore.prototype.watchPosition = function() {
  var obj = this;
  obj.geo.watchPosition(function(position) {
    obj.updatePosition(position);
  }, wua.ui.disableLocationForm, {
      enableHighAccuracy: true
  });
  
  window.setTimeout(function() {
    obj.watchPosition();
  }, 30000);
};

/*
 * Posts an update.
 */
wua.datastore.prototype.postUpdate = function() {
  var obj = this;
  var args = [];
  args.push('/api?method=post');
  args.push('content=' + $('#content').val());
  if ($('#position').attr('checked')) {
    args.push('position=true');
  }
  if ($('#public').attr('checked')) {
    args.push('public=true');
  }
  if (this.position) {
    args.push('latitude=' + this.position[0]);
    args.push('longitude=' + this.position[1]);
  }

  $.getJSON(args.join('&'), function(data) {
    if (data.status == 'success') {
      if (obj.mode == wua.datastore.MODE_RECENT) {
        wua.ui.renderRecentUpdates();
      } else if (obj.mode == wua.datastore.MODE_NEARBY) {
        wua.ui.renderNearbyUpdates();
      } else if (obj.mode == wua.datastore.MODE_FRIEND) {
        wua.ui.renderFriendUpdates();
      }
    }
  });
  
  return false;
};

/*
 * Gets recent updates via the backend's API.
 */
wua.datastore.prototype.getRecentUpdates = function(callback) {
  callback = callback || function() {};
  
  var obj = this;
  $.getJSON('/api?method=get', function(data) {
    obj.recentUpdates = data;
    if (data.data) {
      obj.mode = wua.datastore.MODE_RECENT;
      callback(data.data);
    }
  });
};

/*
 * Gets nearby (within the map bounds) updates via the backend's API.
 */
wua.datastore.prototype.getNearbyUpdates = function(callback) {
  callback = callback || function() {};
  
  var obj = this;
  var url = [];
  url.push('/api?method=get&showNearby=true');
  url.push('&llboundlat=', this.llbound[0]);
  url.push('&llboundlong=', this.llbound[1]);
  url.push('&trboundlat=', this.trbound[0]);
  url.push('&trboundlong=', this.trbound[1]);
  $.getJSON(url.join(''), function(data) {
    obj.nearbyUpdates = data;
    if (data.data) {
      obj.mode = wua.datastore.MODE_NEARBY;
      callback(data.data);
    }
  });
}

/*
 * Gets friendly updates via the backend's API.
 */
wua.datastore.prototype.getFriendUpdates = function(callback) {
  callback = callback || function() {};
  
  var obj = this;
  $.getJSON('/api?method=get&showFriends=true', function(data) {
    obj.friendUpdates = data;
    if (data.data) {
      obj.mode = wua.datastore.MODE_FRIEND;
      callback(data.data);
    }
  });
};