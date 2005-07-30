package jumble.fast;

import junit.framework.TestCase;

/**
 * Tests the corrresponding class.
 * @author Tin Pavlinic
 *
 */
public class FastRunnerTest extends TestCase {
  public void testComputeTimeout() {
    assertEquals(2000, FastRunner.computeTimeout(0));
    assertEquals(12000, FastRunner.computeTimeout(1000));
  }


}
