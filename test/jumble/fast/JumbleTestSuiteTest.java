package jumble.fast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import jumble.Mutater;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import experiments.TimedTests;

/**
 * Tests the corresponding class.
 * 
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class JumbleTestSuiteTest extends TestCase {

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
    assertTrue(JumbleTestSuite.run(
        new TestOrder(new Class[] { Mutater.class }, new long[] { 0 }),
        new FailedTestMap(), null, null, 0, true).startsWith("PASS"));
  }

  public void testX5T() {
    assertTrue(JumbleTestSuite.run(
        new TestOrder(new Class[] { jumble.X5T.class }, new long[] { 0 }),
        null, null, null, 0, true).startsWith("PASS"));
  }

  public void testX5TF() {
    assertEquals("FAIL: !!!sun.misc.Launcher$AppClassLoader.getModification()",
        JumbleTestSuite.run(new TestOrder(new Class[] { jumble.X5TF.class },
            new long[] { 0 }), null, null, null, 0, true));
  }

  public void testX5TY() {
    assertTrue(JumbleTestSuite.run(
        new TestOrder(new Class[] { jumble.X5TY.class }, new long[] { 0, 1 }),
        new FailedTestMap(), null, null, 0, true).startsWith("PASS"));
  }

  public void testNULL() {
    try {
      JumbleTestSuite.run((TestOrder) null, null, null, null, 0, true);
      fail("Took null");
    } catch (NullPointerException e) {
      // ok
    }
  }

  public void testX5TQ() {
    assertEquals("FAIL: !!!sun.misc.Launcher$AppClassLoader.getModification()",
        JumbleTestSuite.run(new TestOrder(new Class[] { jumble.X5TQ.class },
            new long[] { 0, 1, 2 }), null, null, null, 0, true));
  }

  public final void testOrder() throws Exception {
    PrintStream oldOut = System.out;

    // first time the tests (throw away output)
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    TimingTestSuite timingSuite = new TimingTestSuite(
        new Class[] { TimedTests.class });
    timingSuite.run(new TestResult());
    System.setOut(oldOut);

    // The timed tests write to standard out so the easiest way
    // to check the order is to hijack the output and read it
    ByteArrayOutputStream ba = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(ba);

    System.setOut(out);

    String s = JumbleTestSuite.run(timingSuite.getOrder(), null, null, null, 0,
        false);
    assertTrue(s.startsWith("FAIL"));
    StringTokenizer tokens = new StringTokenizer(ba.toString());
    assertEquals("Short", tokens.nextToken());
    assertEquals("Medium", tokens.nextToken());
    assertEquals("Long", tokens.nextToken());
    // Now restore standard out
    System.setOut(oldOut);
  }

  public final void testRunMethodExistence() {
    Class clazz = JumbleTestSuite.class;
    try {
      Method m = clazz.getMethod("run", new Class[] { TestOrder.class,
          FailedTestMap.class, String.class, String.class, int.class,
          boolean.class });
      assertNotSame(null, m);
    } catch (NoSuchMethodException e) {
      fail();
    }
  }

}
