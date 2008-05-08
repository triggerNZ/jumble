package com.reeltwo.jumble.ui;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Runs all the tests in this package.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class AllTests extends TestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(JumbleScorePrinterListenerTest.suite());
    
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
