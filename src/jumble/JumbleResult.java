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
public abstract class JumbleResult {
    public abstract String getClassName();
    public abstract String getTestName();
    public abstract int getMutationCount();
    public abstract Mutation [] getPassed();
    public abstract Mutation [] getTimeouts();
    public abstract Mutation [] getFailed();
    public abstract Mutation [] getAllMutations();
    public int getCoverage() {
        return (int)((double)(getPassed().length + getTimeouts().
                length) / getAllMutations().length);
    }
    
}
