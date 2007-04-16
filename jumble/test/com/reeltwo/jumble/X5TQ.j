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
public class X5TQ extends TestCase {

  public X5TQ(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(X5TQ.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  private TestResult mTR;

  public void run(TestResult tr) {
    mTR = tr;
    mTR.stop();
    super.run(tr);
  }

  public void test() {
    mTR.stop();
  }

  public void test1() {
    fail(); // should cause Jumble to PASS (excepting above)
  }

  public void test2() {
  }
}
