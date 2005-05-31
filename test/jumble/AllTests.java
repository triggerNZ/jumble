package jumble;

import jumble.batch.ClassTestPairTest;
import jumble.batch.DependencyPairProducerTest;
import jumble.batch.JumbleScoreTest;
import jumble.batch.TextFilePairProducerTest;
import jumble.dependency.DependencyExtractorTest;
import jumble.util.JavaRunnerTest;
import jumble.util.RTSITest;
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
    suite.addTest(JavaRunnerTest.suite());
    suite.addTest(DependencyExtractorTest.suite());
    suite.addTest(RTSITest.suite());
    suite.addTest(DependencyPairProducerTest.suite());
    suite.addTest(JumbleScoreTest.suite());
    return suite;
  }


  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
