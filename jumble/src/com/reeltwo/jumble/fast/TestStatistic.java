package com.reeltwo.jumble.fast;

/**
 * This class holds the mutation test result statistics.
 * 
 * @author Celia Lai
 */
public class TestStatistic {

  public int status;

  public String time;

  public TestStatistic(int status, String time) {
    this.status = status;
    this.time = time;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }
}
