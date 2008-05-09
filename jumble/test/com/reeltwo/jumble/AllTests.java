package com.reeltwo.jumble;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Runs all com.reeltwo.jumble tests.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class AllTests extends TestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(JumbleTest.suite());
    
    suite.addTest(com.reeltwo.jumble.util.AllTests.suite());
    suite.addTest(com.reeltwo.jumble.mutation.AllTests.suite());
    suite.addTest(com.reeltwo.jumble.fast.AllTests.suite());
    suite.addTest(com.reeltwo.jumble.ui.AllTests.suite());
    return suite;
  }
  
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
}
