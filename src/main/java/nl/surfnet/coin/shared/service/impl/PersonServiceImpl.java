/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package nl.surfnet.coin.shared.service.impl;

import nl.surfnet.coin.shared.service.AbstractOpenSocialService;
import nl.surfnet.coin.shared.service.PersonService;
import org.opensocial.Request;
import org.opensocial.RequestException;
import org.opensocial.models.Person;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Implementation that fetches a person using the OpenSocial java client
 */
@Component("opensocialPersonService")
public class PersonServiceImpl extends AbstractOpenSocialService implements PersonService {
  private static final String REST_TEMPLATE = "people/{guid}/{selector}/{pid}";

  protected static final String SELF = "@self";

  /**
   * {@inheritDoc}
   */
  @Override
  public Person getPerson(String userId, String loggedInUser) {
    Request request = new Request(REST_TEMPLATE, "people.get", "GET");
    request.setModelClass(Person.class);
    request.setSelector(SELF);
    request.setGuid(userId);
    try {
      return getClient(loggedInUser).send(request).getEntry();
    } catch (RequestException e) {
      throw new IllegalArgumentException(
              "Unable to retrieve the person with uid: '" + userId + "'", e);
    } catch (IOException e) {
      throw new IllegalArgumentException(
              "Unable to retrieve the person with uid: '" + userId + "'", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Person> getPeople(String groupId, String loggedInUser) {
    Request request = new Request(REST_TEMPLATE, "people.get", "GET");
    request.setModelClass(Person.class);
    request.setSelector(groupId);
    request.setGuid(loggedInUser);
    try {
      return getClient(loggedInUser).send(request).getEntries();
    } catch (RequestException e) {
      throw new IllegalArgumentException(
              "Unable to retrieve the Group Members for groupId: '" + groupId + "'", e);
    } catch (IOException e) {
      throw new IllegalArgumentException(
              "Unable to retrieve the Group Members for groupId: '" + groupId + "'", e);
    }  }
}