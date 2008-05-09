package com.reeltwo.jumble.fast;

import java.util.List;

/**
 * <code>MissingTestsTestResult</code> is a JumbleResult for a failure
 * due to test classes not being found.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class MissingTestsTestResult extends AbstractJumbleResult {

  private final List < String > mTestClassNames;

  private int mMutationCount;

  public MissingTestsTestResult(String className, List < String > testClassNames, int mcount) {
    super(className);
    mTestClassNames = testClassNames;
    mMutationCount = mcount;
  }

  /** {@inheritDoc} */
  @Override
  public List < String > getTestClasses() {
    return mTestClassNames;
  }

  /** {@inheritDoc} */
  @Override
  public int getNumberOfMutations() {
    return mMutationCount;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMissingTestClass() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean initialTestsPassed() {
    return false;
  }

}
