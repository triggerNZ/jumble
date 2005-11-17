package jumble.fast;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the corrresponding class.
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class FastRunnerTest extends TestCase {
  public void testComputeTimeout() {
    assertEquals(2000, FastRunner.computeTimeout(0));
    assertEquals(12000, FastRunner.computeTimeout(1000));
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FastRunnerTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

}
