package nl.surfnet.coin.shared.domain;

import static org.junit.Assert.*;

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
