package jumble;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.TestResult;

/**
 * Tests the corresponding class.
 *
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class JumbleTestSuiteTest extends TestCase {

  public JumbleTestSuiteTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(JumbleTestSuiteTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  public void testNonTestClass() {
    assertEquals("FAIL: No test class: String", JumbleTestSuite.run("String"));
  }

  public void testTestClass() {
    assertEquals("PASS", JumbleTestSuite.run("jumble.Mutater"));
  }

  public void testX5T() {
    assertEquals("PASS", JumbleTestSuite.run("jumble.X5T"));
  }

  public void testX5TF() {
    assertEquals("FAIL: !!!sun.misc.Launcher$AppClassLoader.getModification()", JumbleTestSuite.run("jumble.X5TF"));
  }

  public void testX5TY() {
    assertEquals("PASS", JumbleTestSuite.run("jumble.X5TY"));
  }

  public void testNULL() {
    try {
      JumbleTestSuite.run((String) null);
      fail("Took null");
    } catch (NullPointerException e) {
      ; // ok
    }
  }

  public void testX5() {
    assertEquals("PASS", JumbleTestSuite.run("jumble.X5"));
  }

  public void testEmpty() {
    assertEquals("FAIL: No test class: ", JumbleTestSuite.run(""));
  }

  public void testX5TTF() {
    assertEquals("PASS", JumbleTestSuite.run("jumble.X5TTF"));
  }

  public void testX5TQ() {
    assertEquals("FAIL: !!!sun.misc.Launcher$AppClassLoader.getModification()", JumbleTestSuite.run("jumble.X5TQ"));
  }

}
