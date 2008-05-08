package com.reeltwo.jumble.util;


import experiments.JumblerExperimentJUnit4Test;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the corresponding class.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class JumbleUtilsTest extends TestCase {
  public void testIsTestClass() {
    assertTrue(JumbleUtils.isTestClass(getClass()));
    assertFalse(JumbleUtils.isTestClass(String.class));
    assertTrue(JumbleUtils.isTestClass(JumblerExperimentJUnit4Test.class));
  }
  
  /**
   * Dummy class for testing
   * @author Tin
   */
  public static class DummyTest {
    @org.junit.Test public void doTest() {
    }
  }
  
  public void testGetTestName() {
    assertEquals("testGetTestName", JumbleUtils.getTestName(new TestSuite(getClass()).testAt(1)));
    assertEquals("doTest(com.reeltwo.jumble.util.JumbleUtilsTest$DummyTest)", JumbleUtils.getTestName(new JUnit4TestAdapter(DummyTest.class).getTests().get(0)));
  }

  public static Test suite() {
    return new TestSuite(JumbleUtilsTest.class);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
