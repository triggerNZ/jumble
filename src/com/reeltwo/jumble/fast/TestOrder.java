package com.reeltwo.jumble.fast;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Comparator;

import java.lang.reflect.InvocationTargetException;

import com.reeltwo.jumble.util.ClassLoaderCloneable;

/**
 * A class indicating the order in which tests should be run. Contains an array
 * of test classes and associates an ordering with tests.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class TestOrder implements Serializable, ClassLoaderCloneable {

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

  /**
   * Creates a new TestOrder with the specified test classes and no
   * particular ordering.
   * 
   * @param testClasses
   */
  public TestOrder(Class[] testClasses) {
    this(testClasses, createOrdering(testClasses.length));
  }
  
  /**
   * Creates a new TestOrder with the specified test classes and runtimes.
   * 
   * @param testClasses
   * @param runtimes the runtimes of the tests.
   */
  public TestOrder(Class[] testClasses, long[] runtimes) {
    this(testClasses, createOrdering(runtimes));
  }

  /**
   * Creates a new TestOrder with the specified test classes and ordering
   * 
   * @param testClasses
   * @param order order permuation
   */
  public TestOrder(Class[] testClasses, int[] order) {
    mTestClasses = new String[testClasses.length];
    for (int i = 0; i < testClasses.length; i++) {
      mTestClasses[i] = testClasses[i].getName();
    }

    mOrder = order;
  }

  /**
   * Constructor used to clone the object.
   * 
   * @param testClasses string of test classes
   * @param order order permuation
   */
  public TestOrder(String[] testClasses, int[] order) {
    mTestClasses = testClasses;
    mOrder = order;
  }

  /** Creates a default ordering */
  static int[] createOrdering(int length) {
    int[] order = new int[length];
    for (int i = 0; i < order.length; i++) {
      order[i] = i;
    }
    return order;
  }

  /** Creates an ordering based on the runtimes */
  static int[] createOrdering(long[] runtimes) {
    int[] order = new int[runtimes.length];

    SortPair[] sortPairs = new SortPair[runtimes.length];
    for (int i = 0; i < sortPairs.length; i++) {
      sortPairs[i] = new SortPair(i, runtimes[i]);
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
    for (int i = 0; i < order.length; i++) {
      order[i] = sortPairs[i].getPos();
      //System.err.println("order[" + i + "]=" + order[i]);
    }
    return order;
  }

  /**
   * Clones this object using a different class loader to achieve
   * class isolation.  In our application, the classloader will mutate
   * the class being tested.
   * 
   * @param loader the new class loader
   * @return a clone of <CODE>this</CODE> in the different class loader
   */
  public Object clone(ClassLoader loader)
      throws ClassNotFoundException {

    Class clazz = loader.loadClass(getClass().getName());

    try {
      Constructor c = clazz.getConstructor(new Class[] {String[].class, int[].class});
      return c.newInstance(new Object[] {mTestClasses, mOrder});
    } catch (InstantiationException e) {
      e.printStackTrace();
      throw new ClassNotFoundException("Error invoking constructor");
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      throw new ClassNotFoundException("Error invoking constructor");
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      throw new ClassNotFoundException("Error invoking constructor");
    } catch (NoSuchMethodException e) {
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
    return mTestClasses;
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
