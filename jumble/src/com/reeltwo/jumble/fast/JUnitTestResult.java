package com.reeltwo.jumble.fast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import junit.framework.TestFailure;
import junit.framework.TestResult;


/**
 * <code>JUnitTestResult</code> extends the standard
 * <code>TestResult</code> class with a decent <code>toString</code>
 * method.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class JUnitTestResult extends TestResult {
  private static final String LS = System.getProperty("line.separator");

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    for (final Enumeration e = errors(); e.hasMoreElements(); ) {
      final TestFailure f = (TestFailure) e.nextElement();
      sb.append("TEST FINISHED WITH ERROR: ").append(f.toString()).append(LS);
      sb.append(f.trace());
    }
    for (final Enumeration e = failures(); e.hasMoreElements(); ) {
      final TestFailure f = (TestFailure) e.nextElement();
      sb.append("TEST FINISHED WITH FAILURE: ").append(f.toString()).append(LS);
      sb.append(f.trace());
    }
    return sb.toString();
  }
  
  /**
   * This method returns the {@link JUnitTestResult} for the current test run. 
   *  
   * @param lastRunErrorList    The error list of last test run
   * @param lastRunFailureList  The failure list of last test run
   * @return    
   */
  public JUnitTestResult getCurrentTestCaseResult(ArrayList<TestFailure> lastRunErrorList, ArrayList<TestFailure> lastRunFailureList) {
    JUnitTestResult currentTestCaseResult = new JUnitTestResult();
    
    // Create a list of all the errors so far
    ArrayList<TestFailure> currentErrorList = new ArrayList<TestFailure>(Collections.list(errors()));
    // Remove all errors up to last test run
    currentErrorList.removeAll(lastRunErrorList);
    // Add the remaining errors to the current run error list
    for (TestFailure error : currentErrorList) {
      currentTestCaseResult.addError(error.failedTest(), error.thrownException());
    }
    
    // Create a list of all the failures so far
    ArrayList<TestFailure> currntFailureList = new ArrayList<TestFailure>(Collections.list(failures()));
    // Remove all failures up to last test run
    currntFailureList.removeAll(lastRunFailureList);
    // Add the remaining failures to the current run failure list
    for (TestFailure failure : currntFailureList) {
      currentTestCaseResult.addError(failure.failedTest(), failure.thrownException());
    }
    
    return currentTestCaseResult;
  }
}
