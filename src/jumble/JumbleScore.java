package jumble;

/*
 * Created on 18/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Tin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface JumbleScore {
  /** 
   * Gets the number of mutation points for the class being jumbled.
   * @return the number of mutation points.
   */
  abstract int getMutationCount();

  /** 
   * Gets the results of all the mutations of the class.
   * @return all the mutations
   */
  abstract Mutation[] getAllMutations();

  /**
   * Gets the names of the tests which have failed for this class
   * @return the failed tests
   */
  abstract String[] getFailedTests();

  /**
   * Gets the name of the class being jumbled
   * @return the class name
   */
  abstract String getClassName();

  /**
   * Gets all the tests run for this class
   * @return the tests
   */
  abstract String[] getAllTests();
}
