/*
 * Copyright 2012 SURFnet bv, The Netherlands
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
package nl.surfnet.coin.shared.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.DefaultKeyGenerator;
import org.springframework.cache.interceptor.KeyGenerator;

/**
 * {@link KeyGenerator} that also takes into account the methodName when
 * generating keys. This appeared to be necessary in JanusClientDetailsService
 * (see the #testCache method in the corresponding unit test class)
 *
 * Also added target.hashCode(), to differentiate between cacheable methods that have the same signature
 * (potentially even the same class, only being different instances of the class)
 */
public class MethodNameAwareCacheKeyGenerator extends DefaultKeyGenerator implements KeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    Object hash = super.generate(target, method, params);
    return new StringBuilder(hash != null ? hash.toString() : "31")
      .append(method.getName())
      .append(target.hashCode())
      .toString();
  }

}
