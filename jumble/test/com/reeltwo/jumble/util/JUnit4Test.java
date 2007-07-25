package com.reeltwo.jumble.util;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.reeltwo.jumble.fast.JUnitTestResult;

/**
 * Tests assumptions about JUnit 4 behaviour.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class JUnit4Test extends TestCase {
  
  public static class Dummy4Test {
    public Dummy4Test() {
    }
    
    @org.junit.Test public void test() {
      
    }
    
    @org.junit.Test public void test2() {
      
    }
  }
  
  public static class Dummy3Test extends TestCase {
    public Dummy3Test() {
      super();
    }
    public Dummy3Test(String name) {
      super(name);
    }
    public void test1() {
      
    }
    
    public void test2() {
      
    }
  }
  
  public static class NotATest {
  }
  
  //Junit 4 tests are not allowed in Junit 3 test suites
  public void testJunit4TestInJunit3TestSuite() {
    try {
      new TestSuite(Dummy4Test.class);
      fail();
    } catch (ClassCastException e) {
      //ok
    }
  }
  
  //JUnit 3 tests are allowed in JUnit 4 test adapters
  public void testJUnit3TestinJunit4TestSuite() {
    JUnitTestResult tr = new JUnitTestResult();
    new JUnit4TestAdapter(Dummy3Test.class).run(tr);
    assertEquals(0, tr.errorCount());
    assertEquals(0, tr.failureCount());
  }
  
  //Sanity
  public void testJUnit4TestJunit4Suite() {
    JUnitTestResult tr = new JUnitTestResult();
    new JUnit4TestAdapter(Dummy4Test.class).run(tr);
    assertEquals(tr.toString(), 0, tr.errorCount());
  }
  
  public static Test suite() {
    return new TestSuite(JUnit4Test.class);
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
