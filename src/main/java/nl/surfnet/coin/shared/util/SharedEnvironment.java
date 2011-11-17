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

package nl.surfnet.coin.shared.util;

/**
 *
 */
public class SharedEnvironment {

  private String openSocialUrl;
  private String oauthKey;
  private String oauthSecret;

  public String getOpenSocialUrl() {
    return openSocialUrl;
  }

  public void setOpenSocialUrl(String openSocialUrl) {
    this.openSocialUrl = openSocialUrl;
  }

  public String getOauthKey() {
    return oauthKey;
  }

  public void setOauthKey(String oauthKey) {
    this.oauthKey = oauthKey;
  }

  public String getOauthSecret() {
    return oauthSecret;
  }

  public void setOauthSecret(String oauthSecret) {
    this.oauthSecret = oauthSecret;
  }
}
