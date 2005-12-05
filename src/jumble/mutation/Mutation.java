package jumble.mutation;

/**
 * Class representing the result of a single mutation test
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class Mutation {
  public static final int PASS = 0;
  public static final int FAIL = 1;
  public static final int TIMEOUT = 2;
    
  public Mutation(String s, String className, int point) {
    mClassName = className;
    mPoint = point;
        
    mDescription = s;
    if (s.startsWith("PASS")) {
      mStatus = PASS;
    } else if (s.startsWith("FAIL")) {
      mStatus = FAIL;
    } else if (s.startsWith("TIMEOUT")) {
      mStatus = TIMEOUT;
    } else {
      throw new RuntimeException("Invalid mutation string: " + s);
    }
  }
    
  public String getClassName() {
    return mClassName;
  }
    
  public int getMutationPoint() {
    return mPoint;
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
    
  public int getStatus() {
    return mStatus;
  }
    
  public String toString() {
    return getDescription();
  }
  private String mDescription;
  private int mStatus;
  private String mClassName;
  private int mPoint;
}
