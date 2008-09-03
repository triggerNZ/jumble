package com.reeltwo.jumble.annotations;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the corresponding class.
 * @author Tin Pavlinic
 * @version $Revision: $
 */
public class JumbleAnnotationProcessorTest extends TestCase {
  public void testGetTestClassName() throws ClassNotFoundException {
    assertEquals(new ArrayList < String > () { { add("DummyClassTest"); } }, new JumbleAnnotationProcessor().getTestClassNames(DummyClass.class.getName()));
  }
  
  public void testGetTestClassNameWithNoAnnotationReturnsEmptyList() throws ClassNotFoundException {
    assertTrue(new JumbleAnnotationProcessor().getTestClassNames(DummyClass2.class.getName()).isEmpty());
  }
  
  @TestClass("DummyClassTest")
  private class DummyClass {
  }
  
  private class DummyClass2 {
  }

  public static Test suite() {
    return new TestSuite(JumbleAnnotationProcessorTest.class);
  }
}
