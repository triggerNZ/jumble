package com.reeltwo.jumble.fast;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestFailure;
import com.reeltwo.jumble.util.JumbleUtils;

/**
 * A test suite which runs tests in order and inverts the result. Remembers the
 * tests that failed last time and does those first.
 * 
 * @author Tin
 * @version $Revision$
 */
public class JumbleTestSuite extends FlatTestSuite {

  /** Semicolon */
  private static final String SEMICOLON = ";";

  /** Forward slash */
  private static final String FORWARD_SLASH = "/";

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

  /** Should we dump extra output from the test runs? */
  private boolean mVerbose;

  /**
   * Constructs test suite from the given order of tests.
   * 
   * @param order
   *          the order to run the tests in
   * @throws ClassNotFoundException
   *           if <CODE>order</CODE> is malformed.
   */
  public JumbleTestSuite(ClassLoader loader, TestOrder order, FailedTestMap cache,
                         String mutatedClass, String mutatedMethod, int mutationPoint,
                         boolean verbose) throws ClassNotFoundException {
    super();
    mCache = cache;
    mOrder = order;
    mClass = mutatedClass;
    mMethod = mutatedMethod;
    mVerbose = verbose;
    mMethodRelativeMutationPoint = mutationPoint;

    // Create the test suites from the order
    String[] classNames = mOrder.getTestClasses();
    for (int i = 0; i < classNames.length; i++) {
      addTestSuite(loader.loadClass(classNames[i]));
    }
  }

  /**
   * Runs the tests returning the result as a string. If any of the individual
   * tests fail then the run is aborted and "PASS" is returned (recall with a
   * mutation we expect the test to fail). If all tests run correctly then
   * "FAIL" is returned.
   */
  protected String run() {
    final JUnitTestResult result = new JUnitTestResult();
    Test[] tests = getOrder();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintStream newOut = new PrintStream(bos);
    PrintStream oldOut = System.out;
    boolean isFailed = false;
    String pass = "PASS: ";
    String fail = "FAIL: ";
    String desc = "";
    
    for (int i = 0; i < testCount(); i++) {
      Test t = tests[i];
      ArrayList<TestFailure> lastRunErrorList = Collections.list(result.errors());
      ArrayList<TestFailure> lastRunFailureList = Collections.list(result.failures());
            
      double runTime;
      System.setOut(newOut);
      try {
        bos.reset();
        long startTime = System.nanoTime();
        t.run(result);
        long endTime = System.nanoTime();
        runTime = (double) (endTime - startTime) / 1000000000;
        System.err.println("This test took " + runTime + "s");
      } finally {
        System.setOut(oldOut);
      }

      JUnitTestResult currentResult = result.getCurrentTestCaseResult(lastRunErrorList, lastRunFailureList);
      if (mVerbose) {  // Debugging to allow seeing how the tests picked up the mutation
        String curResult = currentResult.toString();
        if (curResult.length() > 0) {
          System.err.println(currentResult);
        }
        if (bos.size() > 0) {
          System.err.println("CAPTURED OUTPUT: " + bos.toString());
        }
        System.err.flush();
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          ; // Don't care
        }
      }
      if (currentResult.errorCount() > 0 || currentResult.failureCount() > 0) {
        desc = desc + t.getClass().getName() + FORWARD_SLASH + JumbleUtils.getTestName(t) + FORWARD_SLASH + MutationResult.PASS + FORWARD_SLASH + runTime + SEMICOLON;
        isFailed = true;
      } else {
        desc = desc + t.getClass().getName() + FORWARD_SLASH + JumbleUtils.getTestName(t) + FORWARD_SLASH + MutationResult.FAIL + FORWARD_SLASH + runTime + SEMICOLON;
      }

//      if (result.shouldStop()) {
//        break;
//      }
    }
    
    if (isFailed) {
      return pass + desc;
    }
    // all tests passed, this mutation is a problem, report it as a FAIL
    return fail + desc;
  }

  /**
   * Run the tests for the given class.
   * 
   * @param order the order in which to run the tests.
   * @param cache the cache
   * @param mutatedClassName the name of the class which was mutated
   * @param mutatedMethodName the name of the method which was mutated
   * @param relativeMutationPoint the mutation point location relative to the mutated method
   * @see TestOrder
   */
  public static String run(ClassLoader loader, TestOrder order, FailedTestMap cache,
                           String mutatedClassName, String mutatedMethodName,
                           int relativeMutationPoint, boolean verbose) {
    try {
      ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(loader);
      try {
        JumbleTestSuite suite = new JumbleTestSuite(loader, order, cache,
                                                    mutatedClassName, mutatedMethodName, relativeMutationPoint,
                                                    verbose);
        String ret = suite.run();
        return ret;
      } finally {
        Thread.currentThread().setContextClassLoader(oldLoader);
      }
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e); // Should have been picked up before now.
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
    Set<String> frontTestNames = new HashSet<String>();

    if (mCache != null) {
      firstTestName = mCache.getLastFailure(mClass, mMethod, mMethodRelativeMutationPoint);
      frontTestNames = mCache.getFailedTests(mClass, mMethod);
    }

    List<Test> front = new ArrayList<Test>();
    List<Test> back = new ArrayList<Test>();

    for (int i = 0; i < testCount(); i++) {
      int indx = mOrder.getTestIndex(i);
      Test curTest = testAt(indx);
      if (first == null && JumbleUtils.getTestName(curTest).equals(firstTestName)) {
        first = curTest;
      } else if (frontTestNames.contains(JumbleUtils.getTestName(curTest))) {
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
      ret[i] = front.get(j);
      i++;
    }
    for (int j = 0; j < back.size(); j++) {
      ret[i] = back.get(j);
      i++;
    }
    return ret;
  }
}
