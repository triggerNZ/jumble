package jumble.util;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(BCELRTSITest.suite());
    suite.addTest(IOThreadTest.suite());
    suite.addTest(JavaRunnerTest.suite());
    suite.addTest(RTSITest.suite());
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
