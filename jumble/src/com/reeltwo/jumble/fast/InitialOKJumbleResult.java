package com.reeltwo.jumble.fast;

import java.util.List;

/**
 * <code>InitialOKJumbleResult</code> is a JumbleResult for a test pass
 * (that is, the mutation caused a test failure).
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class InitialOKJumbleResult extends AbstractJumbleResult {

  private List < String > mTestClassNames;

  private long mTimeoutLength;

  public InitialOKJumbleResult(String className, List < String > testClassNames, long timeout) {
    super(className);
    mTestClassNames = testClassNames;
    mTimeoutLength = timeout;
  }

  /** {@inheritDoc} */
  @Override
  public long getTimeoutLength() {
    return mTimeoutLength;
  }

  /** {@inheritDoc} */
  @Override
  public List < String > getTestClasses() {
    return mTestClassNames;
  }

}
