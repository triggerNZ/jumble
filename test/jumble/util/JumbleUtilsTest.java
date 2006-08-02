package jumble.util;

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
  }

  public static Test suite() {
    return new TestSuite(JumbleUtilsTest.class);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
