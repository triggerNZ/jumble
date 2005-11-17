package experiments;

import junit.framework.TestCase;

/**
 * Test corresponding class.
 *
 * @author Tin
 * @version $Revision$
 */
public class StaticClassTest extends TestCase {
  public void testAdd() {
    //Don't actually test but do invoke the method
    StaticClass.sum(new int[] {1, 2, 3});
  }
}
