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
public class Mutation {
    public static final int PASS = 0;
    public static final int FAIL = 1;
    public static final int TIMEOUT = 2;
    
    public Mutation(String s) {
        mDescription = s;
        if(s.startsWith("PASS"))
            mStatus = PASS;
        else if(s.startsWith("FAIL"))
            mStatus = FAIL;
        else if(s.startsWith("TIMEOUT"))
            mStatus = TIMEOUT;
        else
            throw new RuntimeException("Invalid mutation string");
    }
    
    public String getDescription() {
        return mDescription;
    }
    
    public boolean isPassed() {
        return mStatus == PASS;
    }
    public boolean isFailed() {
        return mStatus == FAIL;
    }
    public boolean isTimedOut() {
        return mStatus == TIMEOUT;
    }
    private String mDescription;
    private int mStatus;
    
}
