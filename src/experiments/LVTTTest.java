package experiments;

import junit.framework.TestCase;

/**
 * Class for testing LVTT bug.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class LVTTTest extends TestCase {
  public void test() {
    LVTT lvtt = new LVTT();

    assertEquals(26, lvtt.countEntries());
  }
}
