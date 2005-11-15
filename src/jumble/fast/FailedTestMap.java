package jumble.fast;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jumble.util.ClassLoaderChanger;

/**
 * A map from mutation points to failed tests. Used to cache previous test
 * failures for Jumble.
 * 
 * @author Tin Pavlinic
 * 
 */
public class FailedTestMap implements Serializable, ClassLoaderChanger {
  /**
   * The map itself: ClassName.MethodName(String) -> (mutationPoint(Integer) ->
   * testName(String))(Map)
   */
  private Map mCache;

  /**
   * Constructor. Creates a blank map.
   */
  public FailedTestMap() {
    mCache = new HashMap();
  }

  /**
   * Adds a test name to the cache.
   * 
   * @param mutatedClass
   *          the name of the mutated class.
   * @param mutatedMethod
   *          the name of the mutated method.
   * @param methodRelativeMutationPoint
   *          the number of the mutation point, relative to the mehtod.
   */
  public void addFailure(String mutatedClass, String mutatedMethod,
      int methodRelativeMutationPoint, String testName) {
    Map mutationToTest;
    
    //System.out.println(mutatedMethod);

    if (mCache.containsKey(mutatedClass + "." + mutatedMethod)) {
      mutationToTest = (Map) mCache.get(mutatedClass + "." + mutatedMethod);
    } else {
      mutationToTest = new HashMap();
      mCache.put(mutatedClass + "." + mutatedMethod, mutationToTest);
    }

    mutationToTest.put(new Integer(methodRelativeMutationPoint), testName);
  }

  /**
   * Gives us the same object loaded in a new class loader.
   */
  public Object changeClassLoader(ClassLoader cl) throws ClassNotFoundException {
    Class clazz = cl.loadClass("jumble.fast.FailedTestMap");

    try {
      Constructor constructor = clazz.getConstructor(new Class[0]);
      Object o = constructor.newInstance(new Object[0]);
      Method m = clazz.getMethod("addFailure", new Class[] { String.class,
          String.class, int.class, String.class });

      Set keys = mCache.keySet();
      for (Iterator it = keys.iterator(); it.hasNext();) {
        String curKey = (String) it.next();
        String className = curKey.substring(0, curKey.indexOf("."));
        String methodName = curKey.substring(curKey.indexOf(".") + 1);
        Map map = (Map) mCache.get(curKey);

        int points = map.size();

        for (int i = 0; i < points; i++) {
          String testName = (String) map.get(new Integer(i));
          m.invoke(o, new Object[] { className, methodName, new Integer(i),
              testName });
        }
      }
      return o;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ClassNotFoundException("Error creating object: "
          + e.getMessage());
    }
  }

  /**
   * Returns the name of the test associated with the last failure.
   * 
   * @param className
   *          mutated class name.
   * @param methodName
   *          mutated method name.
   * @param mutationPoint
   *          method relative mutation point.
   * @return the name of a test, or <code>null</code> if nothing was found.
   */
  public String getLastFailure(String className, String methodName,
      int mutationPoint) {
    Map map = (Map) mCache.get(className + "." + methodName);

    if (map == null) {
      return null;
    } else {
      String testName = (String) map.get(new Integer(mutationPoint));
      return testName;
    }
  }

  /**
   * Gets a set of all the tests associated with failing on mutating the
   * specified method.
   * 
   * @param className
   *          mutated class name.
   * @param methodName
   *          mutated method name
   * @return set containing the names of the tests which fail on the given
   *         method.
   */
  public Set getFailedTests(String className, String methodName) {
    // System.out.println(mCache);
    Map map = (Map) mCache.get(className + "." + methodName);
    if (map != null) {
      return new HashSet(map.values());
    } else {
      return new HashSet();
    }
  }
}
