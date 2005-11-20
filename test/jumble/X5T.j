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
public class X5T extends TestCase {

  public X5T(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(X5T.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  public void test() {
    assertTrue(X5.myFunction());
  }

}
