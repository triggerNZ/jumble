package jumble.fast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import jumble.mutation.Mutater;
import jumble.mutation.Mutation;
import jumble.util.IOThread;
import jumble.util.JavaRunner;
import junit.framework.TestResult;

/**
 * A runner for the <CODE>FastJumbler</CODE>. Runs the FastJumbler in a new
 * JVM and detects timeouts.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class FastRunner {

  /** Filename for the cache */
  public static final String CACHE_FILENAME = "jumble-cache.dat";

  /** Whether to mutate constants */
  private boolean mInlineConstants = true;

  /** Whether to mutate return values */
  private boolean mReturnVals = true;

  /** Whether to mutate increments */
  private boolean mIncrements = true;

  private boolean mNoOrder = false;

  private boolean mLoadCache = true;
  private boolean mSaveCache = true;
  private boolean mUseCache = true;

  private Set mExcludeMethods = new HashSet();

  /** The variable storing the failed tests - can get pretty big */
  FailedTestMap mCache = null;

  
  /**
   * Gets whether inline constants will be mutated.
   *
   * @return true if inline constants will be mutated.
   */
  public boolean isInlineConstants() {
    return mInlineConstants;
  }

  /**
   * Sets whether inline constants will be mutated.
   *
   * @param argInlineConstants true if inline constants should be mutated.
   */
  public void setInlineConstants(final boolean argInlineConstants) {
    mInlineConstants = argInlineConstants;
  }

  /**
   * Gets whether return values will be mutated.
   *
   * @return true if return values will be mutated.
   */
  public boolean isReturnVals() {
    return mReturnVals;
  }

  /**
   * Sets whether return values will be mutated.
   *
   * @param argReturnVals true return values should be mutated.
   */
  public void setReturnVals(final boolean argReturnVals) {
    mReturnVals = argReturnVals;
  }

  /**
   * Gets whether increments will be mutated.
   *
   * @return true if increments will be mutated.
   */
  public boolean isIncrements() {
    return mIncrements;
  }

  /**
   * Sets whether increments will be mutated.
   *
   * @param argIncrements true if increments will be mutated.
   */
  public void setIncrements(final boolean argIncrements) {
    mIncrements = argIncrements;
  }



  /**
   * Gets the value of noOrder
   *
   * @return the value of noOrder
   */
  public boolean isNoOrder() {
    return mNoOrder;
  }

  /**
   * Sets the value of noOrder
   *
   * @param argNoOrder Value to assign to noOrder
   */
  public void setNoOrder(final boolean argNoOrder) {
    mNoOrder = argNoOrder;
  }

  /**
   * Gets the value of loadCache
   *
   * @return the value of loadCache
   */
  public boolean isLoadCache() {
    return mLoadCache;
  }

  /**
   * Sets the value of loadCache
   *
   * @param argLoadCache Value to assign to loadCache
   */
  public void setLoadCache(final boolean argLoadCache) {
    mLoadCache = argLoadCache;
  }

  /**
   * Gets the value of saveCache
   *
   * @return the value of saveCache
   */
  public boolean isSaveCache() {
    return mSaveCache;
  }

  /**
   * Sets the value of saveCache
   *
   * @param argSaveCache Value to assign to saveCache
   */
  public void setSaveCache(final boolean argSaveCache) {
    mSaveCache = argSaveCache;
  }

  /**
   * Gets the value of useCache
   *
   * @return the value of useCache
   */
  public boolean isUseCache() {
    return mUseCache;
  }

  /**
   * Sets the value of useCache
   *
   * @param argUseCache Value to assign to useCache
   */
  public void setUseCache(final boolean argUseCache) {
    mUseCache = argUseCache;
  }

  /**
   * Gets the set of excluded method names
   *
   * @return the set of excluded method names
   */
  public Set getExcludeMethods() {
    return mExcludeMethods;
  }


  /**
   * A function which computes the timeout for given that the original test took
   * <CODE>runtime</CODE>
   * 
   * @param runtime
   *          the original runtime
   * @return the computed timeout
   */
  public static long computeTimeout(long runtime) {
    return runtime * 10 + 2000;
  }

  private void initCache() throws Exception {
    if (mUseCache) {
      boolean loaded = false;
      
      // Load the cache if it exists and is needed
      if (mLoadCache) {
        try {
          ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CACHE_FILENAME));
          mCache = (FailedTestMap) ois.readObject();
          loaded = true;
        } catch (IOException e) {
          loaded = false;
        }
      }
      if (!loaded) {
        mCache = new FailedTestMap();
      }
    }
  }

  private boolean writeCache(String cacheFileName) {
    try {
      File f = new File(cacheFileName);
      if (f.exists()) {
        f.delete();
      }
      ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(cacheFileName));
      o.writeObject(mCache);
      o.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public void addExcludeMethod(String methodName) {
    mExcludeMethods.add(methodName);
  }

  /** Constructs arguments to the FastJumbler */
  private String[] createArgs(String className, int currentMutation, String fileName, String cacheFileName) {
    ArrayList args = new ArrayList();
    // class name
    args.add(className);
    // mutation point
    args.add(String.valueOf(currentMutation));
    // test suite filename
    args.add(fileName);
    
    if (mUseCache) {
      // Write a temp cache
      if (writeCache(cacheFileName)) {
        args.add(cacheFileName);
      }
    }
    
    // exclude methods
    if (!mExcludeMethods.isEmpty()) {
      StringBuffer ex = new StringBuffer();
      ex.append("-x ");
      Iterator it = mExcludeMethods.iterator();
      for (int i = 0; i < mExcludeMethods.size(); i++) {
        if (i == 0) {
          ex.append(it.next());
        } else {
          ex.append("," + it.next());
        }
      }
      args.add(ex.toString());
    }
    // inline constants
    if (mInlineConstants) {
      args.add("-k");
    }
    // return values
    if (mReturnVals) {
      args.add("-r");
    }
    // increments
    if (mIncrements) {
      args.add("-i");
    }
    return (String[]) args.toArray(new String[args.size()]);
  }


  /**
   * Performs a Jumble run on the specified class with the specified tests.
   * 
   * @param className the name of the class to Jumble
   * @param testClassNames the names of the associated test classes
   * @return the results of the Jumble run
   * @throws Exception if something goes wrong
   * @see JumbleResult
   */
  public JumbleResult runJumble(final String className, final List testClassNames) throws Exception {

    Class[] testClasses = new Class[testClassNames.size()];
    final TestResult initialResult;
    final TimingTestSuite timingSuite;
    final TestOrder order;
    // Unique name for this test suite
    final String fileName = "testSuite" + System.currentTimeMillis() + ".dat";
    // Unique name for the cache
    final String cacheFileName = "cache" + System.currentTimeMillis() + ".dat";

    initCache();

    // Get the number of mutation points from the Jumbler
    final Mutater m = new Mutater(0);
    m.setIgnoredMethods(mExcludeMethods);
    m.setMutateIncrements(mIncrements);
    m.setMutateInlineConstants(mInlineConstants);
    m.setMutateReturnValues(mReturnVals);

    final int mutationCount = m.countMutationPoints(className);
    if (mutationCount == -1) {
      return new InterfaceResult(className); 
    }

    for (int i = 0; i < testClasses.length; i++) {
      try {
        testClasses[i] = Class.forName((String) testClassNames.get(i));
      } catch (ClassNotFoundException e) {
        // test class did not exist
        return new FailedTestResult(className, testClassNames, null, mutationCount);
      }
    }
    initialResult = new TestResult();
    timingSuite = new TimingTestSuite(testClasses);
    timingSuite.run(initialResult);
    order = timingSuite.getOrder();

    if (mNoOrder) {
      order.dropOrder();
    }

    // Now, if the tests failed, can return straight away
    if (!initialResult.wasSuccessful()) {
      return new FailedTestResult(className, testClassNames, initialResult, mutationCount);
    }

    // Store the timing stuff in a temporary file
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
    oos.writeObject(order);
    oos.close();

    // compute the timeout
    long timeLeft = order.getTotalRuntime();

    final JavaRunner runner = new JavaRunner("jumble.fast.FastJumbler");
    Process childProcess = null;
    IOThread iot = null;
    
    final Mutation[] allMutations = new Mutation[mutationCount];
    for (int currentMutation = 0; currentMutation < mutationCount; currentMutation++) {
      // If no process is running, start a new one
      if (childProcess == null) {
        // start process
        runner.setArguments(createArgs(className, currentMutation, fileName, cacheFileName));
        childProcess = runner.start();
        iot = new IOThread(childProcess.getInputStream());
        iot.start();
        // read the "START" to let us know the JVM has started
        // we don't want to time this.
        while (true) {
          String str = iot.getNext();
          // System.out.println(str);
          if (str == null) {
            Thread.sleep(10);
          } else if (str.equals("START")) {
            break;
          } else {
            throw new RuntimeException("jumble.fast.FastJumbler returned "
                                       + str + " instead of START");
          }
        }
      }
      long before = System.currentTimeMillis();
      long after = before;
      long timeout = computeTimeout(timeLeft);
      // Run until we time out
      while (true) {
        String out = iot.getNext();
        if (out == null) {
          if (after - before > timeout) {
            allMutations[currentMutation] = new Mutation("TIMEOUT", className,
                                                         currentMutation);
            childProcess.destroy();
            childProcess = null;
            break;
          } else {
            Thread.sleep(50);
            after = System.currentTimeMillis();
          }
        } else {
          try {
            // We have output so go to the next loop iteration
            allMutations[currentMutation] = new Mutation(out, className,
                                                         currentMutation);
            if (mUseCache && allMutations[currentMutation].isPassed()) {
              // Remove "PASS: " and tokenize
              StringTokenizer tokens = new StringTokenizer(out.substring(6),
                                                           ":");
              String clazzName = tokens.nextToken();
              assert clazzName.equals(className);
              String methodName = tokens.nextToken();
              int mutPoint = Integer.parseInt(tokens.nextToken());
              String testName = tokens.nextToken();
              mCache.addFailure(className, methodName, mutPoint, testName);
            }
          } catch (RuntimeException e) {
            throw e;
          }
          break;
        }
      }
    }

    JumbleResult ret = new NormalJumbleResult(className, testClassNames, initialResult, allMutations, computeTimeout(order.getTotalRuntime()));
    // finally, delete the test suite file
    if (!new File(fileName).delete()) {
      System.err.println("Error: could not delete temporary file");
    }
    // Also delete the temporary cache and save the cache if needed
    if (mUseCache) {
      if (!new File(cacheFileName).delete()) {
        System.err.println("Error: could not delete temporary cache file");
      }
      if (mSaveCache) {
        writeCache(CACHE_FILENAME);
      }
    }
    mCache = null;
    return ret;
  }
}

