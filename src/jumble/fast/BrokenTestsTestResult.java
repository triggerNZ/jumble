package jumble.fast;

import java.util.List;

/**
 * <code>BrokenTestsTestResult</code> is a JumbleResult for a failure (that
 * is a mutation that was not detected by tests).
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class BrokenTestsTestResult extends AbstractJumbleResult {

  private final List mTestClassNames;

  private int mMutationCount;

  public BrokenTestsTestResult(String className, List testClassNames, int mcount) {
    super(className);
    mTestClassNames = testClassNames;
    mMutationCount = mcount;
  }

  /** {@inheritDoc} */
  public String[] getTestClasses() {
    return (String[]) mTestClassNames.toArray(new String[mTestClassNames.size()]);
  }

  /** {@inheritDoc} */
  public int getNumberOfMutations() {
    return mMutationCount;
  }

  /** {@inheritDoc} */
  public boolean initialTestsPassed() {
    return false;
  }

}
