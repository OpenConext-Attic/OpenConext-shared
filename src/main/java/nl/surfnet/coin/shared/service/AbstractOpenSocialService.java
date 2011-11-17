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

package nl.surfnet.coin.shared.service;

import nl.surfnet.coin.shared.util.SharedEnvironment;
import org.opensocial.Client;
import org.opensocial.auth.AuthScheme;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.providers.Provider;
import org.opensocial.providers.ShindigProvider;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public abstract class AbstractOpenSocialService {

  @Autowired
  private SharedEnvironment environment;

  /*
   * We can't store state, because the scheme and therefore the client are bound
   * to the Id of the person
   */
  protected Client getClient(String loggedInUser) {
    Provider provider = new ShindigProvider(true);

    provider.setRestEndpoint(environment.getOpenSocialUrl() + "/rest/");
    provider.setRpcEndpoint(null);
    provider.setVersion("0.9");

    AuthScheme scheme = new OAuth2LeggedScheme(environment.getOauthKey(),
            environment.getOauthSecret(), loggedInUser);
    return new Client(provider, scheme);
  }

  /**
   * @param environment the environment to set
   */
  public void setEnvironment(SharedEnvironment environment) {
    this.environment = environment;
  }
}
