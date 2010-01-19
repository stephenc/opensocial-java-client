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

package org.opensocial.services;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.opensocial.Request;
import org.opensocial.models.Person;

public class PeopleServiceTest {

  @Test
  public void retrieveWithZeroArguments() {
    Request request = PeopleService.retrieve();

    testCommonAttributes(request);
    testCommonRetrieveAttributes(request);
    assertTrue(request.getGuid().equals(Service.ME));
    assertTrue(request.getSelector().equals(Service.SELF));
    
  }

  @Test
  public void retrieveWithOneArgument() {
    final String id = "03067092798963641994";
    Request request = PeopleService.retrieve(id);

    testCommonAttributes(request);
    testCommonRetrieveAttributes(request);
    assertTrue(request.getGuid().equals(id));
    assertTrue(request.getSelector().equals(Service.SELF));
  }

  @Test
  public void retrieveWithTwoArguments() {
    Request request = PeopleService.retrieve(Service.ME, Service.FRIENDS);

    testCommonAttributes(request);
    testCommonRetrieveAttributes(request);
    assertTrue(request.getGuid().equals(Service.ME));
    assertTrue(request.getSelector().equals(Service.FRIENDS));
  }

  private void testCommonAttributes(Request request) {
    assertTrue(request.getModelClass().equals(Person.class));
    assertTrue(request.getTemplate().equals("people/{guid}/{selector}/{pid}"));
  }

  private void testCommonRetrieveAttributes(Request request) {
    assertTrue(request.getRestMethod().equals("GET"));
    assertTrue(request.getRpcMethod().equals("people.get"));
  }
}
