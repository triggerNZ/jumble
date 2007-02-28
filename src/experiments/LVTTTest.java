package experiments;

import junit.framework.TestCase;

public class LVTTTest extends TestCase {
  public void test() {
    LVTT lvtt = new LVTT();

    assertEquals(26, lvtt.countEntries());
  }
}
