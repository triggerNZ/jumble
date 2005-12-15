package jumble.fast;

import junit.framework.Test;
import junit.framework.TestResult;

/**
 * A test suite which times test runs. The idea is that tests are run and sorted
 * in order of runtime. Example usage: <BR>
 * <BR>
 * 
 * <PRE>
 * 
 * TimingTestSuite suite = new TimingTestSuite (new Class[] { MathTest.class,
 *        StringTest.class});
 * 
 * suite.run(new TestResult()); 
 * TestOrder order = suite.getOrder();
 * </PRE>
 * <BR>
 * This can then be used by Jumble to run the tests on the mutated class in
 * order so that it fails quickly.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class TimingTestSuite extends FlatTestSuite {
  public static final boolean DEBUG = false;
  
  /** The runtimes for the tests */
  private long[] mRuntimes = null;

  /**
   * The test classes used to create this suite. Only so they can be passed to
   * the <CODE>TestOrder</CODE>
   */
  private Class[] mTestClasses;

  /**
   * Constructs a test suite from the test classes given in <CODE>testClasses
   * </CODE>
   * 
   * @param testClasses an array of the classes of test suites to run
   */
  public TimingTestSuite(Class[] testClasses) {
    super();
    mTestClasses = testClasses;
    for (int i = 0; i < testClasses.length; i++) {
      addTestSuite(testClasses[i]);
    }
  }

  /**
   * Runs the tests and records their runtimes. The test results are returned in
   * <CODE>result</CODE> as usual in JUnit.
   */
  public void run(TestResult result) {
    mRuntimes = new long[testCount()];
    for (int i = 0; i < mRuntimes.length; i++) {
      Test curTest = testAt(i);
      if (DEBUG) {
        System.out.println("Running initially " + curTest);
      }
      long before = System.currentTimeMillis();
      curTest.run(result);
      long after = System.currentTimeMillis();
      mRuntimes[i] = after - before;
    }
  }

  /**
   * Returns the tests sorted in order.
   * 
   * @return the ordered tests.
   * @throws RuntimeException if the tests have not been run yet
   */
  public TestOrder getOrder() {
    if (mRuntimes == null) {
      throw new RuntimeException("Cannot call getOrder() before the tests have been run");
    }

    return new TestOrder(mTestClasses, mRuntimes);
  }
}
