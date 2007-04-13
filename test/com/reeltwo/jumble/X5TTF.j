package jumble;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the corresponding class.
 *
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class X5TTF extends TestCase {

  public X5TTF(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(X5TTF.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  public void test() {
    assertFalse(X5.myFunction());
  }

  public void testConverse() {
    assertTrue(X5.myFunction());
  }

}
