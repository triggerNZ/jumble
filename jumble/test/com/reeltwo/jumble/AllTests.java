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
    final TestSuite suite = new TestSuite();
    suite.addTest(JumbleTest.suite());
    suite.addTest(com.reeltwo.jumble.annotations.AllTests.suite());
    suite.addTest(com.reeltwo.jumble.fast.AllTests.suite());
    suite.addTest(com.reeltwo.jumble.mutation.AllTests.suite());
    suite.addTest(com.reeltwo.jumble.ui.AllTests.suite());
    suite.addTest(com.reeltwo.jumble.util.AllTests.suite());
    return suite;
  }
  
  public static void main(final String[] args) {
    TestRunner.run(suite());
  }
}
