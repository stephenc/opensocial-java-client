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

A Game where the user attempts to contain bouncing lines by drawing horizontal
and vertical lines.  When the balls are sufficiently contained, a new level 
begins with an extra ball added.

This version provides tracking of top scores, using profile and friend
information from social networks like MySpace using the OpenSocial APIs
and contact providers like Gmail and Plaxo using the Portable Contacts
standard.  Top scores are tracked, and then submitted to a
Google App Engine backend.  At the end of a round in the game, the global
top scores along with top scores from friends of the user are displayed.

Divide and Conquer author: Karl Rosaen
Modified for social functionality by: Ryan Boyd, Vijaya Machavolu



ORIGINAL README.TXT
-----------
A Game where the user attempts to contain bouncing lines by drawing horizontal
and vertical lines.  When the balls are sufficiently contained, a new level 
begins with an extra ball added.

Author: Karl Rosaen

History:
While writing this game, I had in mind a game I fondly remembered playing 
years ago.  Recently it was pointed out that this game was in fact JezzBall.
As a result, this game is certainly heavily influenced by JezzBall, though does
not attempt to be a perfect clone.  See http://en.wikipedia.org/wiki/JezzBall.

Notes: 
- Lines are initiated by vertical and horizontal gestures.  On the emulator,
this can be done by clicking a point, dragging in a vertical or horizontal
direction, and then letting go.
- Vibration obviously won't work on the emulator, but has been tested on 
prototype hardware.

TODO/ideas:
- ability to draw multiple animating lines within the same region
- bonus rounds? maybe a lighting bolt bounces around and if you can hit
 it or contain it, you get faster lines?
- save high scores

