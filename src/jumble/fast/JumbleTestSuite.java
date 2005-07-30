package jumble.fast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * A test suite which runs tests in order and inverts the result. Remembers the
 * tests that failed last time and does those first.
 * 
 * @author Tin
 */
public class JumbleTestSuite extends FlatTestSuite {
  public final static String CACHE_FILENAME = "jumble-cache.dat";

  /** Order of tests */
  private TestOrder mOrder;

  /** Mutated class */
  private String mClass;

  /** Mutated method */
  private String mMethod;

  /** The cache which remembers what methods failed where */
  private HashMap mCache;

  /**
   * Constructs test suite from the given order of tests.
   * 
   * @param order
   *          the order to run the tests in
   * @throws ClassNotFoundException
   *           if <CODE>order</CODE> is malformed.
   */
  public JumbleTestSuite(TestOrder order, String mutatedClass,
      String mutatedMethod, boolean loadCache) throws ClassNotFoundException {
    super();
    mOrder = order;
    mClass = mutatedClass;
    mMethod = mutatedMethod;
    
    //Create the test suites from the order
    String[] classNames = mOrder.getTestClasses();
    for (int i = 0; i < classNames.length; i++) {
      addTestSuite(Class.forName(classNames[i]));
    }

    initCache(loadCache);
  }

  /**
   * Runs the tests returning the result as a string. If any of the individual
   * tests fail then the run is aborted and "PASS" is returned (recall with a
   * mutation we expect the test to fail). If all tests run correctly then
   * "FAIL" is returned.
   */
  protected String run() {
    final TestResult result = new TestResult();
    Test[] tests = getOrder();

    for (int i = 0; i < testCount(); i++) {
      TestCase t = (TestCase) tests[i];
      t.run(result);
      if (result.errorCount() > 0 || result.failureCount() > 0) {
        recordFail(t.getName());
        return "PASS: " + getMessage();
      }
      if (result.shouldStop()) {
        break;
      }
    }

    // all tests passed, this mutation is a problem, report it
    // this is made complicated because we must get the modification
    // details from a class loaded in a different name space

    return "FAIL: " + getMessage();
  }

  private String getMessage() {
    String message;
    try {
      message = (String) getClass().getClassLoader().getClass().getMethod(
          "getModification", null).invoke(getClass().getClassLoader(), null);
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
    return message;
  }

  /**
   * Run the tests for the given class.
   * 
   * @param order
   *          the order in which to run the tests.
   * @see TestOrder
   */
  public static String run(TestOrder order, String mutatedClassName,
      String mutatedMethodName, boolean loadLast, boolean saveLast) {
    try {
      JumbleTestSuite suite = new JumbleTestSuite(order, mutatedClassName,
          mutatedMethodName, loadLast);
      String ret = suite.run();
      if (saveLast) {
        try {
          suite.saveCache();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return ret;
    } catch (ClassNotFoundException e) {
      return "FAIL: No test class: " + e.getMessage();
    }
  }

  /**
   * Initializes the cache. If the file CACHE_FILENAME exists, the cache is
   * loaded from that file unless the <CODE>loadCache</CODE> parameter is 
   * false. Otherwise, a new cache is created.
   */
  private void initCache(boolean loadCache) {
    if (!loadCache) {
      mCache = new HashMap();
      return;
    }
    
    File f = new File(CACHE_FILENAME);
    boolean loaded;

    if (f.exists()) {
      try {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
        mCache = (HashMap) in.readObject();
        in.close();

        loaded = true;
      } catch (Exception e) {
        loaded = false;
      }
    } else {
      loaded = false;
    }

    if (!loaded) {
      mCache = new HashMap();
    }
  }

  /**
   * Basically separates out the tests for the current method so that they are
   * run first. Still keeps them in the same order.
   * 
   * @return array of tests in the order of timing but the ones that failed
   *         previously get run first.
   */
  private Test[] getOrder() {
    ArrayList headList = new ArrayList();
    ArrayList tailList = new ArrayList();

    Set failedTests = (Set) mCache.get(getCacheKey());
    if (failedTests == null) {
      //null just means empty set in this case
      failedTests = new HashSet();
    }

    for (int i = 0; i < testCount(); i++) {
      tailList.add(testAt(mOrder.getTestIndex(i)));
    }

    for (int i = 0; i < tailList.size(); i++) {
      TestCase cur = (TestCase) tailList.get(i);
      String name = cur.getName();

      if (failedTests.contains(name)) {
        headList.add(cur);
        failedTests.remove(name);
        tailList.remove(i);
        i--;
      }
    }

    Test[] ret = new Test[headList.size() + tailList.size()];
    //First the head list
    int i;
    for (i = 0; i < headList.size(); i++) {
      ret[i] = (Test) headList.get(i);
    }

    //Now the tail
    for (int j = 0; j < tailList.size(); j++) {
      ret[i + j] = (Test) tailList.get(j);
    }

    return ret;

  }

  /**
   * Creates a key into the cache from the mutated class name and the mutated
   * method name.
   * 
   * @return hashtable key (Class.Method)
   */
  private String getCacheKey() {
    return mClass + "." + mMethod;
  }

  /**
   * Caches a failure.
   * 
   * @param testName
   *          the name of the test that failed
   */
  private void recordFail(String testName) {
    Set s = (Set) mCache.get(getCacheKey());
    //Create the record if it doesn't exist
    if (s == null) {
      s = new HashSet();
    }

    s.add(testName);

    mCache.put(getCacheKey(), s);
  }

  /**
   * Saves the cache to the file specified by CACHE_FILENAME. Needs to be
   * invoked explicitly.
   * 
   * @throws IOException
   *           if there is a problem saving the cache
   */
  public void saveCache() throws IOException {
    File f = new File(CACHE_FILENAME);
    ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
    o.writeObject(mCache);
    o.close();
  }
}
