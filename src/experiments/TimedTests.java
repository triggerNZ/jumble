package experiments;

import junit.framework.TestCase;

/**
 * A test with some dummy tests taking varying amounts of time for
 * Jumble heuristic testing.
 * @author Tin
 * @version $Revision$
 */
public class TimedTests extends TestCase {
  public final void testMedium() throws Exception {
    System.out.println("Medium");
    Thread.sleep(1000);
  }
  
  public final void testLong() throws Exception {
    System.out.println("Long");
    Thread.sleep(10000);
  }
  
  public final void testShort() {
    System.out.println("Short");
  }
}
