package com.reeltwo.jumble.fast;

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

  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (Enumeration e = errors(); e.hasMoreElements(); ) {
      TestFailure f = (TestFailure) e.nextElement();
      sb.append("TEST FINISHED WITH ERROR: ").append(f.toString()).append(LS);
      sb.append(f.trace());
    }
    for (Enumeration e = failures(); e.hasMoreElements(); ) {
      TestFailure f = (TestFailure) e.nextElement();
      sb.append("TEST FINISHED WITH FAILURE: ").append(f.toString()).append(LS);
      sb.append(f.trace());
    }
    return sb.toString();
  }
}
