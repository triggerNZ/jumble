package experiments;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test for com.reeltwo.jumble testing.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class FloatReturnTest extends TestCase {
  private static final float FLOAT_THRESHOLD = 1.0e-20f;
  private static final double DOUBLE_THRESHOLD = 1.0e-20;
  
  public void testFloat() {
    assertEquals(-1.0f, new FloatReturn().getFloat(), FLOAT_THRESHOLD);
  }

  public void testDouble() {
    assertEquals(-1.0, new FloatReturn().getDouble(), DOUBLE_THRESHOLD);
  }

  public static Test suite() {
    return new TestSuite(FloatReturnTest.class);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
