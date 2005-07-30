package experiments;

import junit.framework.TestCase;

public class StaticClassTest extends TestCase {
  public void testAdd() {
    //Don't actually test but do invoke the method
    int x = StaticClass.sum(new int[] {1, 2, 3});
  }
}
