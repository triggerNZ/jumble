package com.reeltwo.jumble.fast;

/**
 * This class holds the mutation test result statistics.
 * 
 * @author Celia Lai
 * @version $Revision: 743 $
 */
public class TestStatistic {

  private int mStatus;

  private String mTime;

  public TestStatistic(int status, String time) {
    this.mStatus = status;
    this.mTime = time;
  }

  public int getStatus() {
    return mStatus;
  }

  public void setStatus(int status) {
    this.mStatus = status;
  }

  public String getTime() {
    return mTime;
  }

  public void setTime(String time) {
    this.mTime = time;
  }
}
