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
public class X5TY extends TestCase {

  public X5TY(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(X5TY.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  public void test() {
    assertTrue(X5.myFunction());
  }

  public void test2() {
    assertTrue(X5.dog());
  }

}
