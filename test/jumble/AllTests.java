package jumble;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests extends TestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(JumbleTestSuiteTest.suite());
    suite.addTest(MutaterTest.suite());
  
    suite.addTest(jumble.dependency.AllTests.suite());
    suite.addTest(jumble.fast.AllTests.suite());
    suite.addTest(jumble.util.AllTests.suite());
    return suite;
  }
  
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
}
