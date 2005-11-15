package jumble.fast;

import jumble.Mutation;
import junit.framework.TestResult;

/**
 * A class representing the results of a Jumble run.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 *  
 */
public abstract class JumbleResult {
  /**
   * Gets the name of the class being Jumbled.
   * 
   * @return the class name.
   */
  public abstract String getClassName();

  /**
   * Gets an array of the names of test classes run.
   * 
   * @return the test classes
   */
  public abstract String[] getTestClasses();

  /**
   * Returns the results of the test on the unmodified class.
   * 
   * @return the original test results
   */
  public abstract TestResult getInitialTestResult();

  /**
   * Determines whether the initial tests passes. Calls <CODE>
   * getInitialTestResult()</CODE>.
   * 
   * @return true if the initial tests passed, false otherwise.
   */
  public boolean initialTestsPassed() {
    return getInitialTestResult().wasSuccessful();
  }

  /**
   * Gets an array of the actual mutations covered by the tests.
   * 
   * @return array of the covered mutations or <CODE>null</CODE> if the code
   *         was not jumbled.
   */
  public abstract Mutation[] getCovered();

  /**
   * Gets an array of mutations missed by the tests.
   * 
   * @return array of the missed mutations or <CODE>null</CODE> if the code
   *         was not jumbled.
   */
  public abstract Mutation[] getMissed();

  /**
   * Gets an array of mutations which caused the tests to time out. NOTE: A
   * timeout means that the mutation is covered. W
   * 
   * @return array of missed mutations or <CODE>null</CODE> if the code was
   *         not jumbled.
   */
  public abstract Mutation[] getTimeouts();

  /**
   * Returns the length of time, in milliseconds of the timeout.
   * 
   * @return the length of the timeout
   */
  public abstract long getTimeoutLength();

  /**
   * Gets an array of all mutations.
   * 
   * @return array of all the mutations or <CODE>null</CODE> if the code was
   *         not jumbled.
   */
  public abstract Mutation[] getAllMutations();
}
