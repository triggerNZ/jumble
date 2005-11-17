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
 */
public class FlatTestSuiteTest extends TestCase {
  private FlatTestSuite mTest;

  public final void setUp() {
    mTest = new FlatTestSuite();
    mTest.addTestSuite(JumblerExperimentTest.class);
    mTest.addTestSuite(JumblerExperimentSecondTest.class);
  }

  public void tearDown() {
    mTest = null;
  }

  public final void testCountTestCases() {
    assertEquals(3, mTest.countTestCases());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FlatTestSuiteTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
