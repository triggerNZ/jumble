/*
 * Created on Apr 16, 2005
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
public interface JumbleResult {
    public String getClassName();
    public String getTestName();
    public int getMutationCount();
    public Mutation [] getPassed();
    public Mutation [] getTimeouts();
    public Mutation [] getFailed();
    public Mutation [] getAllMutations();
    public int hashCode();
    public boolean equals(Object o);
}
