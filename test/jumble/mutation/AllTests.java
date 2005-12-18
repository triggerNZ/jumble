package jumble.mutation;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Runs all jumble tests.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class AllTests extends TestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(MutaterTest.suite());
    suite.addTest(MutatingClassLoaderTest.suite());

    return suite;
  }
  
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
}
