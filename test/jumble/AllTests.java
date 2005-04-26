package jumble;

import jumble.batch.*;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests.
 *
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class AllTests extends TestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(MutaterTest.suite());
    suite.addTest(JumbleTestSuiteTest.suite());
    suite.addTest(JumbleMainTest.suite());
    suite.addTest(ClassTestPairTest.suite());
    suite.addTest(TextFilePairProducerTest.suite());
    return suite;
  }


  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
