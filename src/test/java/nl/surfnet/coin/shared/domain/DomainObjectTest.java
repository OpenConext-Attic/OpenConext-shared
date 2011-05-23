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

package nl.surfnet.coin.shared.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class DomainObjectTest {

  @Test
  public void testEqualsObject() {
    DomainObject o1 = new DomainObjectImpl();
    DomainObject o2 = new DomainObjectImpl();
   
    assertNotSame(o1,o2);
    o1.setId(1L);
    assertNotSame(o1,o2);
    o2.setId(1L);
    assertEquals(o1,o2);
  }

  @SuppressWarnings("serial")
  private static class DomainObjectImpl extends DomainObject {}
  
}
