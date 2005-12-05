package jumble.fast;

import java.util.List;
import junit.framework.TestResult;

/**
 * <code>FailedTestResult</code> is a JumbleResult for a failure (that
 * is a mutation that was not detected by tests).
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class FailedTestResult extends AbstractJumbleResult {

  private final List mTestClassNames;

  private final TestResult mInitialResult;

  public FailedTestResult(String className, List testClassNames, TestResult result) {
    super(className);
    mTestClassNames = testClassNames;
    mInitialResult = result;
  }

  /** {@inheritDoc} */
  public String[] getTestClasses() {
    return (String[]) mTestClassNames.toArray(new String[mTestClassNames.size()]);
  }

  /** {@inheritDoc} */
  public TestResult getInitialTestResult() {
    return mInitialResult;
  }

}
