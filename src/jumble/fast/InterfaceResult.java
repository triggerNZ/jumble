package jumble.fast;

import jumble.Mutation;
import junit.framework.TestResult;

/**
 * A class representing an interface result.
 * 
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class InterfaceResult extends JumbleResult {

  private final String mClassName;

  /**
   * Construct a new interface result.
   *
   * @param className name of class
   */
  public InterfaceResult(final String className) {
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
  public TestResult getInitialTestResult() {
    return null;
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
  public boolean isInterface() {
    return true;
  }
}
