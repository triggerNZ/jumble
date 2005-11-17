package jumble.fast;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Comparator;

import jumble.util.ClassLoaderChanger;
import junit.framework.TestSuite;

/**
 * A class indicating the order in which tests should be run. Contains an array
 * of test classes and associates an ordering with tests.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class TestOrder implements Serializable, ClassLoaderChanger {
  /** Number for serialization */
  private static final long serialVersionUID = 4401643897371182214L;

  /** Flag to turn debugging on and off */
  public static final boolean DEBUG = true;

  /** The test classes used to produce this ordering */
  private String[] mTestClasses;

  /**
   * An array containing the indices of tests to run specifying the order in
   * which to run them.
   */
  private int[] mOrder;

  /** Runtimes for the tests */
  private long[] mRuntimes;

  /**
   * Creates a new TestOrder with the specified test classes and runtimes.
   * 
   * @param testClasses
   * @param runtimes
   *          the runtimes of the tests.
   */
  public TestOrder(Class[] testClasses, long[] runtimes) {
    mTestClasses = new String[testClasses.length];
    for (int i = 0; i < testClasses.length; i++) {
      mTestClasses[i] = testClasses[i].getName();
    }
    mRuntimes = runtimes;

    SortPair[] sortPairs = new SortPair[mRuntimes.length];
    mOrder = new int[mRuntimes.length];

    for (int i = 0; i < sortPairs.length; i++) {
      sortPairs[i] = new SortPair(i, mRuntimes[i]);
    }

    java.util.Arrays.sort(sortPairs, new Comparator() {
        public int compare(Object o1, Object o2) {
          SortPair p1 = (SortPair) o1;
          SortPair p2 = (SortPair) o2;

          if (p1.getTime() < p2.getTime()) {
            return -1;
          } else if (p1.getTime() == p2.getTime()) {
            return 0;
          } else {
            return 1;
          }
        }
      });
    for (int i = 0; i < mOrder.length; i++) {
      mOrder[i] = sortPairs[i].getPos();
    }

    TestSuite ts = new FlatTestSuite();
    try {
      ts.addTestSuite(Class.forName(mTestClasses[0]));
    } catch (Exception e) {
      e.printStackTrace();
    }
    assert integrity();

  }

  /**
   * Constructor used to clone the object.
   * 
   * @param testClasses
   *          string of test classes
   * @param order
   *          order permuation
   * @param runtimes
   *          runtimes array
   */
  public TestOrder(String[] testClasses, int[] order, long[] runtimes) {
    mTestClasses = testClasses;
    mOrder = order;
    mRuntimes = runtimes;

    assert integrity();
  }

  /**
   * Loads the class in a different class loader and clones the object.
   * 
   * @param loader
   *          the new class loader
   * @return a clone of <CODE>this</CODE> in the differen class loader
   */
  public Object changeClassLoader(ClassLoader loader)
    throws ClassNotFoundException {

    assert integrity();

    Class clazz = loader.loadClass(getClass().getName());

    try {
      Constructor c = clazz.getConstructor(new Class[] {String[].class, int[].class, long[].class});
      return c.newInstance(new Object[] {mTestClasses, mOrder, mRuntimes});
    } catch (Exception e) {
      e.printStackTrace();
      throw new ClassNotFoundException("Error invoking constructor");
    }
  }

  /**
   * Returns the total number of tests.
   * 
   * @return the number of tests.
   */
  public int getTestCount() {
    assert integrity();
    return mOrder.length;
  }

  /**
   * Returns the index of the test in order <CODE>order</CODE>.
   * 
   * @param order
   *          the ordered position of the test to run
   * @return the index of the <CODE>order</CODE> th test
   */
  public int getTestIndex(int order) {
    assert integrity();
    try {
      return mOrder[order];
    } catch (ArrayIndexOutOfBoundsException e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Gets the names of the test classes that were timed.
   * 
   * @return the test classes
   */
  public String[] getTestClasses() {
    assert integrity();
    return mTestClasses;
  }

  /**
   * Gets the runtime of the <CODE>order</CODE> th test in order.
   * 
   * @param order
   *          the ordered index of the test to run
   * @return the runtime
   */
  public long getRuntime(int order) {
    assert integrity();
    return mRuntimes[getTestIndex(order)];
  }

  public long getTotalRuntime() {
    assert integrity();

    long sum = 0;
    for (int i = 0; i < getTestCount(); i++) {
      sum += getRuntime(i);
    }
    return sum;
  }

  public String toString() {
    assert integrity();
    StringBuffer buf = new StringBuffer();

    for (int i = 0; i < getTestCount(); i++) {
      buf.append(getRuntime(i) + "\n");
    }
    return buf.toString();
  }

  /**
   * Integrity method. Checks this object for consistency. Should only be called
   * while debugging.
   * 
   * @return true if the object is consistent, false otherwise.
   */
  public boolean integrity() {
    try {
      TestSuite ts = new FlatTestSuite();
      for (int i = 0; i < mTestClasses.length; i++) {
        ts.addTestSuite(Class.forName(mTestClasses[i]));
      }

      int testCount = ts.testCount();

      return testCount == mOrder.length && testCount == mRuntimes.length;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Resets the order so that it is the default order. Useful when we want to quickly revert to 
   * default ordering.
   */
  public void dropOrder() {
    for (int i = 0; i < mOrder.length; i++) {
      mOrder[i] = i;
    }
  }

  /**
   * Just a little structure used in the sorting of runtimes.
   */
  private static class SortPair {
    private int mPos;

    private long mTime;

    public SortPair(int pos, long time) {
      mPos = pos;
      mTime = time;
    }

    public int getPos() {
      return mPos;
    }

    public long getTime() {
      return mTime;
    }
  }
}
