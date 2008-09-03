package com.reeltwo.jumble.annotations;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Runs tests.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision: 576 $
 */
public class AllTests extends TestSuite {
  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.addTest(JumbleAnnotationProcessorTest.suite());
    return suite;
  }
  
  public static void main(final String[] args) {
    TestRunner.run(suite());
  }
}
