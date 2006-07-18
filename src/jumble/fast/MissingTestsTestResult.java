package jumble.fast;

import java.util.List;

/**
 * <code>MissingTestsTestResult</code> is a JumbleResult for a failure
 * due to test classes not being found.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class MissingTestsTestResult extends AbstractJumbleResult {

  private final List mTestClassNames;

  private int mMutationCount;

  public MissingTestsTestResult(String className, List testClassNames, int mcount) {
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
  public boolean isMissingTestClass() {
    return true;
  }

  /** {@inheritDoc} */
  public boolean initialTestsPassed() {
    return false;
  }

}
