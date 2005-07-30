package jumble.fast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import jumble.Mutater;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import experiments.FailingTest;
import experiments.JumblerExperimentTest;
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
        new TestOrder(new Class[] { Mutater.class }, new long[] { 0 }), null,
        null, false, false).startsWith("PASS"));
  }

  public void testX5T() {
    assertTrue(JumbleTestSuite.run(
        new TestOrder(new Class[] { jumble.X5T.class }, new long[] { 0 }),
        null, null, false, false).startsWith("PASS"));
  }

  public void testX5TF() {
    assertEquals("FAIL: !!!sun.misc.Launcher$AppClassLoader.getModification()",
        JumbleTestSuite.run(new TestOrder(new Class[] { jumble.X5TF.class },
            new long[] { 0 }), null, null, false, false));
  }

  public void testX5TY() {
    assertTrue(JumbleTestSuite.run(
        new TestOrder(new Class[] { jumble.X5TY.class }, new long[] { 0, 1 }),
        null, null, false, false).startsWith("PASS"));
  }

  public void testNULL() {
    try {
      JumbleTestSuite.run((TestOrder) null, null, null, false, false);
      fail("Took null");
    } catch (NullPointerException e) {
      // ok
    }
  }

  public void testX5TQ() {
    assertEquals("FAIL: !!!sun.misc.Launcher$AppClassLoader.getModification()",
        JumbleTestSuite.run(new TestOrder(new Class[] { jumble.X5TQ.class },
            new long[] { 0, 1, 2 }), null, null, false, false));
  }

  public final void testOrder() throws Exception {
    PrintStream oldOut = System.out;

    //first time the tests (throw away output)
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    TimingTestSuite timingSuite = new TimingTestSuite(
        new Class[] { TimedTests.class });
    timingSuite.run(new TestResult());
    System.setOut(oldOut);

    //The timed tests write to standard out so the easiest way
    //to check the order is to hijack the output and read it
    ByteArrayOutputStream ba = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(ba);

    System.setOut(out);

    String s = JumbleTestSuite.run(timingSuite.getOrder(), null, null, false,
        false);
    assertTrue(s.startsWith("FAIL"));
    StringTokenizer tokens = new StringTokenizer(ba.toString());
    assertEquals("Short", tokens.nextToken());
    assertEquals("Medium", tokens.nextToken());
    assertEquals("Long", tokens.nextToken());
    //Now restore standard out
    System.setOut(oldOut);
  }

  public final void testRunMethodExistence() {
    Class clazz = JumbleTestSuite.class;
    try {
      Method m = clazz.getMethod("run", new Class[] { TestOrder.class,
          String.class, String.class, boolean.class, boolean.class });
      assertNotSame(null, m);
    } catch (NoSuchMethodException e) {
      fail();
    }
  }

  public final void testSaveFailures() throws Exception {
    //Run the failed test and some tests that pass too
    TestResult result = new TestResult();
    TimingTestSuite timingSuite = new TimingTestSuite(new Class[] {
        JumblerExperimentTest.class, FailingTest.class });
    timingSuite.run(result);
    assertFalse(result.wasSuccessful());

    File f = new File(JumbleTestSuite.CACHE_FILENAME);

    //Make sure the file doesn't exist initially
    if (f.exists()) {
      assertTrue(f.delete());
    }
    assertFalse(f.exists());
    
    JumbleTestSuite.run(timingSuite.getOrder(), "DummyClass", "dummyMethod",
        false, true);

    assertTrue(f.exists());
    
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
    HashMap hash = (HashMap)in.readObject();
    in.close();

    assertTrue(hash.containsKey("DummyClass.dummyMethod"));
    HashSet s = (HashSet)hash.get("DummyClass.dummyMethod");
    assertTrue(s.contains("testFail"));
    assertEquals(1, s.size());
  }
  
  
  
}
