package com.reeltwo.jumble.fast;


/**
 * A class representing an interface result.
 *
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class InterfaceResult extends AbstractJumbleResult {

  /**
   * Construct a new interface result.
   *
   * @param className name of class
   */
  public InterfaceResult(final String className) {
    super(className);
  }


  /** {@inheritDoc} */
  @Override
public boolean isInterface() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
public boolean initialTestsPassed() {
    return false;
  }

}
