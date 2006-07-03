package jumble.fast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import jumble.mutation.Mutater;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Tests the corresponding class.
 * 
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class JumbleTestSuiteTest extends TestCase {

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

  public JumbleTestSuiteTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(JumbleTestSuiteTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  public void testTestClass() {
    assertTrue(JumbleTestSuite.run(getClass().getClassLoader(),
                                   new TestOrder(new Class[] {Mutater.class}, new long[] {0}),
                                   new FailedTestMap(), null, null, 0, false).startsWith("PASS"));
  }

  public void testX5T() {
    assertTrue(JumbleTestSuite.run(getClass().getClassLoader(),
                                   new TestOrder(new Class[] {jumble.X5T.class}, new long[] {0}),
                                   null, null, null, 0, false).startsWith("PASS"));
  }

  public void testX5TF() {
    assertEquals("FAIL",
        JumbleTestSuite.run(getClass().getClassLoader(),
                            new TestOrder(new Class[] {jumble.X5TF.class},
                                          new long[] {0}), null, null, null, 0, false));
  }

  public void testX5TY() {
    assertTrue(JumbleTestSuite.run(getClass().getClassLoader(),
                                   new TestOrder(new Class[] {jumble.X5TY.class}, new long[] {0, 1}),
                                   new FailedTestMap(), null, null, 0, false).startsWith("PASS"));
  }

  public void testNULL() {
    try {
      JumbleTestSuite.run(getClass().getClassLoader(), (TestOrder) null, null, null, null, 0, false);
      fail("Took null");
    } catch (NullPointerException e) {
      // ok
    }
  }

  // This test shows a problem with the FlatTestSuite not having deterministic test ordering
  // Seems to be in the way JUnit creates suites using reflection.. we should ensure that
  // FlatTestSuite creates suites with tests in a known order, otherwise the TestOrder is serving
  // no purpose.
//   public void testX5TQ() {
//     assertEquals("FAIL",
//         JumbleTestSuite.run(ClassLoader.getSystemClassLoader(), 
//                             new TestOrder(new Class[] {jumble.X5TQ.class },
//                                           new long[] {0, 1, 2}), null, null, null, 0, false));
//   }

  public final void testOrder() throws Exception {
    PrintStream oldOut = System.out;

    // first run the tests to get timing information (throw away output)
    TimingTestSuite timingSuite = new TimingTestSuite(getClass().getClassLoader(), new String[] {TimedTests.class.getName()});
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    try {
      timingSuite.run(new TestResult());
    } finally {
      System.setOut(oldOut);
    }

    // The timed tests write to standard out so the easiest way
    // to check the order is to hijack the output and read it
    ByteArrayOutputStream ba = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(ba);

    System.setErr(out);
    String s;
    try {
      s = JumbleTestSuite.run(getClass().getClassLoader(), timingSuite.getOrder(true), null, null, null, 0, true);
    } finally {
      System.setErr(oldOut);
    }
    assertTrue(s.startsWith("FAIL"));

    String errout = ba.toString();
    int si = errout.indexOf("Short");
    int mi = errout.indexOf("Medium");
    int li = errout.indexOf("Long");
    assertTrue(si >= 0);
    assertTrue(mi >= 0);
    assertTrue(li >= 0);
    assertTrue((si < mi) && (mi < li));
  }
}
