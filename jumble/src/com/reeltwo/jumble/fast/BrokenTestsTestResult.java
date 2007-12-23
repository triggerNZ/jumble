package com.reeltwo.jumble.fast;

import java.util.List;

/**
 * <code>BrokenTestsTestResult</code> is a JumbleResult for a failure
 * due tests failing prior to any mutations being applied.
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
  @Override
public String[] getTestClasses() {
    return (String[]) mTestClassNames.toArray(new String[mTestClassNames.size()]);
  }

  /** {@inheritDoc} */
  @Override
public int getNumberOfMutations() {
    return mMutationCount;
  }

  /** {@inheritDoc} */
  @Override
public boolean initialTestsPassed() {
    return false;
  }

}
