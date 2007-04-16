package com.reeltwo.jumble.fast;


/**
 * An abstract JumbleResult that defaults most methods.
 * 
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class AbstractJumbleResult implements JumbleResult {

  private final String mClassName;

  /**
   * Construct a JumbleResult for a class.
   *
   * @param className name of class
   */
  public AbstractJumbleResult(final String className) {
    mClassName = className;
  }

  /** {@inheritDoc} */
  public String getClassName() {
    return mClassName;
  }

  /** {@inheritDoc} */
  public String[] getTestClasses() {
    return null;
  }

  /** {@inheritDoc} */
  public boolean initialTestsPassed() {
    return true;
  }

  /** {@inheritDoc} */
  public MutationResult[] getCovered() {
    return null;
  }

  /** {@inheritDoc} */
  public MutationResult[] getMissed() {
    return null;
  }

  /** {@inheritDoc} */
  public MutationResult[] getTimeouts() {
    return null;
  }

  /** {@inheritDoc} */
  public long getTimeoutLength() {
    return 0;
  }

  /** {@inheritDoc} */
  public MutationResult[] getAllMutations() {
    return null;
  }

  /** {@inheritDoc} */
  public int getNumberOfMutations() {
    return 0;
  }

  /** {@inheritDoc} */
  public boolean isInterface() {
    return false;
  }


  /** {@inheritDoc} */
  public boolean isMissingTestClass() {
    return false;
  }

}
