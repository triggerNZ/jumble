/*
 * Created on 18/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;


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
    public abstract int getMutationCount();

    /** 
     * Gets the results of all the mutations of the class.
     * @return all the mutations
     */
    public abstract Mutation[] getAllMutations();

    /**
     * Gets the names of the tests which have failed for this class
     * @return the failed tests
     */
    public abstract String[] getFailedTests();

    /**
     * Gets the name of the class being jumbled
     * @return the class name
     */
    public abstract String getClassName();

    /**
     * Gets all the tests run for this class
     * @return the tests
     */
    public abstract String[] getAllTests();
}