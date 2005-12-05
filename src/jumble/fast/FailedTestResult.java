package jumble.fast;

import java.util.List;
import jumble.mutation.Mutation;
import junit.framework.TestResult;

/**
 * <code>FailedTestResult</code> is a JumbleResult for a failure (that
 * is a mutation that was not detected by tests).
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class FailedTestResult extends JumbleResult {

  private String mClassName;

  private List mTestClassNames;

  private TestResult mInitialResult;

  public FailedTestResult(String className, List testClassNames, TestResult result) {
    mClassName = className;
    mTestClassNames = testClassNames;
    mInitialResult = result;
  }


  /** {@inheritDoc} */
  public String getClassName() {
    return mClassName;
  }

  /** {@inheritDoc} */
  public Mutation[] getAllMutations() {
    return null;
  }

  /** {@inheritDoc} */
  public String[] getTestClasses() {
    return (String[]) mTestClassNames
        .toArray(new String[mTestClassNames.size()]);
  }

  /** {@inheritDoc} */
  public long getTimeoutLength() {
    return 0;
  }

  /** {@inheritDoc} */
  public TestResult getInitialTestResult() {
    return mInitialResult;
  }

  /** {@inheritDoc} */
  public Mutation[] getCovered() {
    return null;
  }

  /** {@inheritDoc} */
  public Mutation[] getMissed() {
    return null;
  }

  /** {@inheritDoc} */
  public Mutation[] getTimeouts() {
    return null;
  }
}
