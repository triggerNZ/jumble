package jumble.fast;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import experiments.JumblerExperimentSecondTest;
import experiments.JumblerExperimentTest;

/**
 * Tests the corresponding class
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class FlatTestSuiteTest extends TestCase {
  private FlatTestSuite mTest;

  public final void setUp() {
    mTest = new FlatTestSuite();
  }

  public void tearDown() {
    mTest = null;
  }

  public final void testCountTestCases() {
    checkTestCases(3, new Class[] {JumblerExperimentTest.class, JumblerExperimentSecondTest.class });
  }

  public void testNoSuite() {
    checkTestCases(2, new Class[] {NoSuiteT.class });
  }

  public void testMismatchingSuite() {
    checkTestCases(2, new Class[] {MismatchingSuiteT.class });
  }

  private void checkTestCases(int cases, Class[] testClasses) {
    for (int i = 0; i < testClasses.length; i++) {
      mTest.addTestSuite(testClasses[i]);
    }

    assertEquals(cases, mTest.countTestCases());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FlatTestSuiteTest.class);
    return suite;
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      junit.textui.TestRunner.run(suite());
    } else {
      String testName = args[0];
      FlatTestSuite suite = new jumble.fast.FlatTestSuite();
      suite.addTest(suite());

      for (int i = 0; i < suite.testCount(); i++) {
        TestCase curTest = (TestCase) suite.testAt(i);
        if (curTest.getName().equals(testName)) {
          junit.textui.TestRunner.run(curTest);
        }
      }
    }
  }
}
