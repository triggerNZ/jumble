package com.reeltwo.jumble.fast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Tests the corresponding class
 *
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class TimingTestSuiteTest extends TestCase {

  private static final int MED_DELAY = 500;
  private static final int LONG_DELAY = 2000;

  /**
   * <code>TimedTests</code> is a small set of tests that take varying
   * times to execute.
   */
  public static class TimedTests extends TestCase {
    // Warning, the declaration order of these tests is important to testGetOrder below
    public final void testMedium() throws Exception {
      System.out.println("Medium");
      Thread.sleep(MED_DELAY);
    }

    public final void testLong() throws Exception {
      System.out.println("Long");
      Thread.sleep(LONG_DELAY);
    }

    public final void testShort() {
      System.out.println("Short");
    }
  }

  private TimingTestSuite mSuite;

  private TestResult mResult;

  @Override
protected void setUp() throws Exception {
    mSuite = new TimingTestSuite(getClass().getClassLoader(), new String[] {TimedTests.class.getName()});
    mResult = new TestResult();
    PrintStream oldOut = System.out;
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    try {
      mSuite.run(mResult);
    } finally {
      System.setOut(oldOut);
    }
  }

  @Override
protected void tearDown() {
    mSuite = null;
    mResult = null;
  }

  public final void testGetOrder() {
    TestOrder order = mSuite.getOrder(true);
    assertEquals(3, order.getTestCount());
    assertEquals(2, order.getTestIndex(0));
    assertEquals(0, order.getTestIndex(1));
    assertEquals(1, order.getTestIndex(2));
    // A bit dangerous
    assertTrue(MED_DELAY + LONG_DELAY <= mSuite.getTotalRuntime());
    assertTrue(mSuite.getTotalRuntime() < (MED_DELAY + LONG_DELAY * 1.5));
  }

  public final void testResult() {
    assertEquals(3, mResult.runCount());
    assertEquals(0, mResult.errorCount());
    assertEquals(0, mResult.failureCount());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TimingTestSuiteTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
