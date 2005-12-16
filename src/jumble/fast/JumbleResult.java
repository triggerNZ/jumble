package jumble.fast;

import jumble.mutation.Mutation;

/**
 * A class representing the results of a Jumble run.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 *  
 */
public interface JumbleResult {

  /**
   * Gets the name of the class being Jumbled.
   * 
   * @return the class name.
   */
  String getClassName();

  /**
   * Gets an array of the names of test classes run.
   * 
   * @return the test classes
   */
  String[] getTestClasses();

  /**
   * Determines whether the initial tests passes. Calls <CODE>
   * getInitialTestResult()</CODE>.
   * 
   * @return true if the initial tests passed, false otherwise.
   */
  boolean initialTestsPassed();

  /**
   * Gets an array of the actual mutations covered by the tests.
   * 
   * @return array of the covered mutations or <CODE>null</CODE> if the code
   *         was not jumbled.
   */
  Mutation[] getCovered();

  /**
   * Gets an array of mutations missed by the tests.
   * 
   * @return array of the missed mutations or <CODE>null</CODE> if the code
   *         was not jumbled.
   */
  Mutation[] getMissed();

  /**
   * Gets an array of mutations which caused the tests to time out. NOTE: A
   * timeout means that the mutation is covered. W
   * 
   * @return array of missed mutations or <CODE>null</CODE> if the code was
   *         not jumbled.
   */
  Mutation[] getTimeouts();

  /**
   * Returns the length of time, in milliseconds of the timeout.
   * 
   * @return the length of the timeout
   */
  long getTimeoutLength();

  /**
   * Gets an array of all mutations.
   * 
   * @return array of all the mutations or <CODE>null</CODE> if the code was
   *         not jumbled.
   */
  Mutation[] getAllMutations();

  /**
   * Return the number of mutations points.
   *
   * @return number of mutation points.
   */
  int getNumberOfMutations();

  /**
   * Was the class tested actually an interface.
   *
   * @return true for an interface.
   */
  boolean isInterface();

  /**
   * Were any test classes missing.
   *
   * @return true if any of the expected test classes were missing.
   */
  boolean isMissingTestClass();

}
