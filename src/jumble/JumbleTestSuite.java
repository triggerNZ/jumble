package jumble;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * An extension of TestSuite with fast failure and inverting
 * of the result.  Useful for mutation testing.  It runs the
 * tests in the class specified at construction time.  If a
 * tests fails then the test run immediately halts (this is
 * contrary to the usual JUnit behaviour).
 *
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class JumbleTestSuite extends TestSuite {

  /**
   * Constructs a JumbleTestSuite from the given class.
   */
  JumbleTestSuite(final Class theClass) {
    super(theClass);
  }
	
  /**
   * Runs the tests returning the result as a string.  If any
   * of the individual tests fail then the run is aborted and
   * "PASS" is returned (recall with a mutation we expect the
   * test to fail).  If all tests run correctly then "FAIL"
   * is returned.
   */
  private String run() {
    final TestResult result = new TestResult();
    for (Enumeration e = tests(); e.hasMoreElements();) {
      ((Test) e.nextElement()).run(result);
      if (result.errorCount() > 0 || result.failureCount() > 0) {
        return "PASS";
      }
      if (result.shouldStop()) {
        break;
      }
    }

    // all tests passed, this mutation is a problem, report it
    // this is made complicated because we must get the modification
    // details from a class loaded in a different name space
    String message;
    try {
      message = (String) getClass().getClassLoader().getClass().getMethod("getModification", null).invoke(getClass().getClassLoader(), null);
    } catch (IllegalAccessException e) {
      message = "!!!" + e.getMessage();
    } catch (IllegalArgumentException e) {
      message = "!!!" + e.getMessage();
    } catch (InvocationTargetException e) {
      message = "!!!" + e.getMessage();
    } catch (NoSuchMethodException e) {
      message = "!!!" + e.getMessage();
    } catch (SecurityException e) {
      message = "!!!" + e.getMessage();
    }
    if (message == null) {
      message = "none: existing tests never caused class to be loaded";
    }

    return "FAIL: " + message;
  }

  /**
   * Run the tests for the given class.
   *
   * @param testClassName the name of the test class
   */
  public static String run(String testClassName) {
    try {
      return new JumbleTestSuite(Class.forName(testClassName)).run();
    } catch (ClassNotFoundException e) {
      return "FAIL: No test class: " + testClassName;
    }
  }
}
