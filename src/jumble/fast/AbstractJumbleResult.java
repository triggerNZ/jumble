package jumble.fast;

import jumble.mutation.Mutation;

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

  /** {@inheritDoc} */
  public long getTimeoutLength() {
    return 0;
  }

  /** {@inheritDoc} */
  public Mutation[] getAllMutations() {
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
