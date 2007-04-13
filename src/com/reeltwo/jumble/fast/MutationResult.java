package com.reeltwo.jumble.fast;

/**
 * Class representing the result of a single mutation test. Possibly should be
 * moved to package com.reeltwo.jumble.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class MutationResult {
  public static final int PASS = 0;

  public static final int FAIL = 1;

  public static final int TIMEOUT = 2;

  private int mStatus;

  private String mClassName;

  private int mPoint;

  // Describes the mutation applied
  private String mDescription;

  // Describes the test that detected the mutation
  private String mTestDescription;



  public MutationResult(int status, String className, int point, String description, String testDescription) {
    this(status, className, point, description);
    mTestDescription = testDescription;
  }

  public MutationResult(int status, String className, int point, String description) {
    if (status != PASS && status != FAIL && status != TIMEOUT) {
      throw new RuntimeException("Invalid mutation status: " + status);
    }
    mClassName = className;
    mPoint = point;
    mStatus = status;
    mDescription = description;
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

  public String getTestDescription() {
    return mTestDescription;
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

}
