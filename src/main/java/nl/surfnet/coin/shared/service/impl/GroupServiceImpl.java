/*
 * Copyright 2011 SURFnet bv, The Netherlands
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

package nl.surfnet.coin.shared.service.impl;

import nl.surfnet.coin.shared.service.AbstractOpenSocialService;
import nl.surfnet.coin.shared.service.GroupService;
import org.opensocial.Request;
import org.opensocial.RequestException;
import org.opensocial.models.Group;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 *
 */
@Component("opensocialGroupService")
public class GroupServiceImpl extends AbstractOpenSocialService implements GroupService {

  private static final String REST_TEMPLATE = "groups/{guid}";
  private static final String ME = "@me";

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Group> getGroups(String loggedInUser) {
    Request request = new Request(REST_TEMPLATE, "groups.get", "GET");
    request.setModelClass(Group.class);
    request.setGuid(ME);
    try {
      return getClient(loggedInUser).send(request).getEntries();
    } catch (RequestException e) {
      throw new IllegalArgumentException(
              "Unable to retrieve the groups for uid: '" + loggedInUser + "'", e);
    } catch (IOException e) {
      throw new IllegalArgumentException(
              "Unable to retrieve the groups for uid: '" + loggedInUser + "'", e);
    }
  }
}
