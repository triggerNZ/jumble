package com.reeltwo.jumble.fast;


import java.lang.reflect.Method;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.reeltwo.jumble.mutation.Mutater;
import com.reeltwo.jumble.mutation.MutatingClassLoader;

/**
 * Tests the corresponding class.
 *
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class FailedTestMapTest extends TestCase {
  private FailedTestMap mMap;

  @Override
public void setUp() throws Exception {
    mMap = new FailedTestMap();

    mMap.addFailure("DummyClass", "dummyMethod", 0, "dummyTest1");
    mMap.addFailure("DummyClass", "dummyMethod", 1, "dummyTest1");
    mMap.addFailure("DummyClass", "dummyMethod", 2, "dummyTest2");

    mMap.addFailure("DummyClass", "dummyMethod2", 0, "dummyMethod2Test");
    mMap.addFailure("DummyClass", "dummyMethod2", 1, "dummyMethod2Test");
    mMap.addFailure("DummyClass", "dummyMethod2", 2, "dummyMethod2Test");

    mMap.addFailure("DummyClass2", "dummyMethod", 0, "dummyTest1");
  }

  @Override
public void tearDown() {
    mMap = null;
  }

  public void testChangeClassLoader() throws Exception {
    ClassLoader cl = new MutatingClassLoader("DummyClass", new Mutater(0), System.getProperty("java.class.path"));

    Object other = mMap.clone(cl);

    assertEquals(other.getClass().getName(), mMap.getClass().getName());
    assertNotSame(other.getClass(), mMap.getClass());
    Method m = other.getClass().getMethod("getLastFailure", new Class[] {String.class, String.class, int.class});
    String ret = (String) m.invoke(other, new Object[] {"DummyClass", "dummyMethod", new Integer(0)});
    assertEquals("dummyTest1", ret);
  }

  public void testGetFailedTests() {
    Set fail1 = mMap.getFailedTests("DummyClass", "dummyMethod");
    assertEquals(2, fail1.size());
    assertTrue(fail1.contains("dummyTest1"));
    assertTrue(fail1.contains("dummyTest2"));

    Set fail2 = mMap.getFailedTests("DummyClass", "dummyMethod2");
    assertEquals(1, fail2.size());
    assertTrue(fail2.contains("dummyMethod2Test"));

    Set fail3 = mMap.getFailedTests("DummyClass2", "dummyMethod");
    assertEquals(1, fail3.size());
    assertTrue(fail3.contains("dummyTest1"));

    Set empty = mMap.getFailedTests("DummyClass", "fakeMethod");
    assertTrue(empty.isEmpty());
  }

  public void testGetLastFailure() {
    assertEquals("dummyTest1", mMap.getLastFailure("DummyClass", "dummyMethod",
        0));

    assertEquals("dummyTest2", mMap.getLastFailure("DummyClass", "dummyMethod",
        2));

    assertEquals(null, mMap.getLastFailure("DummyClass", "dummyMethod",
        3));

    assertEquals(null, mMap.getLastFailure("DummyClass", "fakeMethod",
        0));
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FailedTestMapTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
