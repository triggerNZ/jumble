package jumble.fast;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

/**
 * A test suite which runs tests in order and inverts the result. Remembers the
 * tests that failed last time and does those first.
 * 
 * @author Tin
 * @version $Revision$
 */
public class JumbleTestSuite extends FlatTestSuite {

  /** Cache of previously failed tests */
  private FailedTestMap mCache;

  /** Order of tests */
  private TestOrder mOrder;

  /** Mutated class */
  private String mClass;

  /** Mutated method */
  private String mMethod;

  /** Mutation Point */
  private int mMethodRelativeMutationPoint;

  /** Should we surpress output? */
  private boolean mStopOutput = true;

  /**
   * Constructs test suite from the given order of tests.
   * 
   * @param order
   *          the order to run the tests in
   * @throws ClassNotFoundException
   *           if <CODE>order</CODE> is malformed.
   */
  public JumbleTestSuite(TestOrder order, FailedTestMap cache,
                         String mutatedClass, String mutatedMethod, int mutationPoint,
                         boolean stopOut) throws ClassNotFoundException {
    super();
    mCache = cache;
    mOrder = order;
    mClass = mutatedClass;
    mMethod = mutatedMethod;
    mStopOutput = stopOut;
    mMethodRelativeMutationPoint = mutationPoint;

    // Create the test suites from the order
    String[] classNames = mOrder.getTestClasses();
    for (int i = 0; i < classNames.length; i++) {
      addTestSuite(Class.forName(classNames[i]));
    }
  }

  /**
   * Runs the tests returning the result as a string. If any of the individual
   * tests fail then the run is aborted and "PASS" is returned (recall with a
   * mutation we expect the test to fail). If all tests run correctly then
   * "FAIL" is returned.
   */
  protected String run() {
    final TestResult result = new TestResult();
    Test[] tests = getOrder();
    PrintStream newOut;
    PrintStream oldOut = System.out;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    if (mStopOutput) {
      newOut = new PrintStream(bos);
    } else {
      newOut = oldOut;
    }

    for (int i = 0; i < testCount(); i++) {
      TestCase t = (TestCase) tests[i];

      System.setOut(newOut);
      bos.reset();
      t.run(result);
      System.setOut(oldOut);
      if (result.errorCount() > 0 || result.failureCount() > 0) {
        if (false) {  // Debugging to allow seeing how the tests picked up the mutation
          for (Enumeration e = result.errors(); e.hasMoreElements(); ) {
            TestFailure f = (TestFailure) e.nextElement();
            System.err.println("TEST FINISHED WITH ERROR: " + f.toString() + f.trace());
          }
          for (Enumeration e = result.failures(); e.hasMoreElements(); ) {
            TestFailure f = (TestFailure) e.nextElement();
            System.err.println("TEST FINISHED WITH FAILURE: " + f.toString() + f.trace());
          }
          if (bos.size() > 0) {
            System.err.println("CAPTURED OUTPUT: " + bos.toString());
          }
        }
        return "PASS: " + mClass + ":" + mMethod + ":"
          + mMethodRelativeMutationPoint + ":" + t.getName();
      }
      if (result.shouldStop()) {
        break;
      }
    }
    // all tests passed, this mutation is a problem, report it
    // this is made complicated because we must get the modification
    // details from a class loaded in a different name space

    return "FAIL: " + getMessage();
  }

  private String getMessage() {
    String message;
    try {
      message = (String) getClass().getClassLoader().getClass().getMethod("getModification", null).invoke(getClass().getClassLoader(), null);
    } catch (IllegalAccessException e) {
      message = "!!!" + e.getMessage();
    } catch (IllegalArgumentException e) {
      message = "!!!" + e.getMessage();
    } catch (InvocationTargetException e) {
      message = "!!!" + e.getMessage();
    } catch (NoSuchMethodException e) {
      message = "!!!" + e.getMessage();
    } catch (SecurityException e) {
      message = "!!!" + e.getMessage();
    }
    if (message == null) {
      message = "none: existing tests never caused class to be loaded";
    }
    return message;
  }

  /**
   * Run the tests for the given class.
   * 
   * @param order
   *          the order in which to run the tests.
   * @param cache the cache
   * @param mutatedClassName
   *          the name of the class which was mutated
   * @param mutatedMethodName
   *          the name of the method which was mutated
   * @param relativeMutationPoint
   *          the mutation point location relative to the mutated method
   * @param supressOutput
   *          flag whether to surpress output during the test run. Should be
   *          <CODE>true</CODE> for all Jumble runs.
   * @see TestOrder
   */
  public static String run(TestOrder order, FailedTestMap cache,
                           String mutatedClassName, String mutatedMethodName,
                           int relativeMutationPoint, boolean supressOutput) {
    try {
      JumbleTestSuite suite = new JumbleTestSuite(order, cache,
                                                  mutatedClassName, mutatedMethodName, relativeMutationPoint,
                                                  supressOutput);
      String ret = suite.run();

      return ret;
    } catch (ClassNotFoundException e) {
      return "FAIL: No test class: " + e.getMessage();
    }
  }

  /**
   * Basically separates out the tests for the current method so that they are
   * run first. Still keeps them in the same order.
   * 
   * @return array of tests in the order of timing but the ones that failed
   *         previously get run first.
   */
  private Test[] getOrder() {
    Test first = null;
    String firstTestName = null;
    Set frontTestNames = new HashSet();

    if (mCache != null) {
      firstTestName = mCache.getLastFailure(mClass, mMethod,
                                            mMethodRelativeMutationPoint);
      frontTestNames = mCache.getFailedTests(mClass, mMethod);
    }

    List front = new ArrayList();
    List back = new ArrayList();

    for (int i = 0; i < testCount(); i++) {
      TestCase curTest = (TestCase) testAt(mOrder.getTestIndex(i));

      if (first != null && curTest.getName().equals(firstTestName)) {
        first = curTest;
      } else if (frontTestNames.contains(curTest.getName())) {
        front.add(curTest);
      } else {
        back.add(curTest);
      }
    }

    Test[] ret = new Test[testCount()];

    int i;

    if (first == null) {
      i = 0;
    } else {
      i = 1;
      ret[0] = first;
    }

    for (int j = 0; j < front.size(); j++) {
      ret[i] = (Test) front.get(j);
      i++;
    }
    for (int j = 0; j < back.size(); j++) {
      ret[i] = (Test) back.get(j);
      i++;
    }
    return ret;
  }
}
