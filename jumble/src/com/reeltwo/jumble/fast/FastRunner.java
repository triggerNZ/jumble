package com.reeltwo.jumble.fast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import com.reeltwo.jumble.mutation.Mutater;
import com.reeltwo.jumble.mutation.MutatingClassLoader;
import com.reeltwo.jumble.ui.JumbleListener;
import com.reeltwo.jumble.ui.NullListener;
import com.reeltwo.jumble.util.IOThread;
import com.reeltwo.jumble.util.JavaRunner;
import com.reeltwo.jumble.util.JumbleUtils;

/**
 * A runner for the <CODE>FastJumbler</CODE>. Runs the FastJumbler in a new
 * JVM and detects timeouts.
 *
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class FastRunner {
  /** Filename for the cache */
  public static final File CACHE_FILE = new File(System.getProperty("user.home"), ".com.reeltwo.jumble-cache.dat");

  // Configuration properties

  /** Whether to mutate constants */
  private boolean mInlineConstants = true;

  /** Whether to mutate return values */
  private boolean mReturnVals = true;

  /** Whether to mutate stores */
  private boolean mStores = false;

  /** Whether to mutate increments */
  private boolean mIncrements = true;

  /** Whether to mutate constant pool */
  private boolean mCPool = true;

  /** Whether to mutate switches */
  private boolean mSwitches = true;

  private boolean mOrdered = true;

  private boolean mVerbose = false;

  private boolean mLoadCache = true;

  private boolean mSaveCache = true;

  private boolean mUseCache = true;

  /** Whether to record the statistic results */
  private boolean mRecStat = false;

  private String mClassPath = System.getProperty("java.class.path");

  /** Maximum number of mutations per JVM */
  private int mMaxExternalMutations = -1;

  /**
   * Index of the first mutation to attempt. Mainly useful for
   * testing when there is a problematic mutation.
   */
  private int mFirstMutation = 0;

  private Set<String> mExcludeMethods = new HashSet<String>();

  private Set<String> mDeferredClasses = new HashSet<String>();

  private List<String> mJvmArgs = new ArrayList<String>();

  // State during run

  /** The class being tested */
  private String mClassName;

  private File mCacheFile;

  private File mTestSuiteFile;

  private JavaRunner mRunner = null;

  private Process mChildProcess = null;

  private IOThread mIot = null;

  private IOThread mEot = null;

  private int mMutationCount;

  private long mTotalRuntime;

  /** The variable storing the failed tests - can get pretty big */
  FailedTestMap mCache = null;

  /** {@link MutationKey} maps to each {@link TestStatistic} of this mutation */
  Map<MutationKey, TestStatistic> mStats = null;
  
  /** ClassName maps to a list of Mutations in this class */
  Map<String, List<String>> mClassMutMap = null;
  
  /** TestClassName maps to a list of TestMethods in this test class */
  Map<String, List<String>> mTestMap = null;

  public FastRunner() {
    // Add a shutdown hook so that if this JVM is interrupted, any
    // child process will be destroyed.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        Process childProcess = mChildProcess;
        if (childProcess != null) {
          if (mVerbose) {
            System.err.println("Shutting down child process");
          }
          childProcess.destroy();
        }
      }
    });
    // This lets us run more mutation tests in each sub-JVM
    // without running out of space for classes.  Also consider
    // setting setMaxExternalMutations.
    mJvmArgs.add("-XX:PermSize=128m");
    mJvmArgs.add("-cp");
    mJvmArgs.add(System.getProperty("java.class.path"));
  }

  private JavaRunner getJavaRunner() {
    if (mRunner == null) {
      mRunner = new JavaRunner("com.reeltwo.jumble.fast.FastJumbler");
      mRunner.setJvmArguments(mJvmArgs);
    }
    return mRunner;
  }

  /**
   * Returns the classpath used to load test and source classes.
   *
   * @return the classpath string.
   */
  public String getClassPath() {
    return mClassPath;
  }

  /**
   * Sets the classpath used to load test and source classes.
   *
   * @param classpath a <code>String</code> value
   */
  public void setClassPath(String classpath) {
    mClassPath = classpath;
    System.setProperty("java.class.path", mClassPath);  // Make classpath available to code doing classpath scanning.
  }

  /**
   * Gets whether verbose mode is set.
   *
   * @return true if verbose mode is enabled.
   */
  public boolean isVerbose() {
    return mVerbose;
  }

  /**
   * Sets whether verbose mode is enabled.
   *
   * @param newVerbose
   *          true if verbose mode should be enabled.
   */
  public void setVerbose(final boolean newVerbose) {
    mVerbose = newVerbose;
  }

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
   * @param argInlineConstants
   *          true if inline constants should be mutated.
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
   * Gets whether stores will be mutated.
   *
   * @return true if stores will be mutated.
   */
  public boolean isStores() {
    return mStores;
  }

  /**
   * Sets whether return values will be mutated.
   *
   * @param argReturnVals
   *          true return values should be mutated.
   */
  public void setReturnVals(final boolean argReturnVals) {
    mReturnVals = argReturnVals;
  }

  /**
   * Sets whether stores will be mutated.
   *
   * @param argStores true if stores should be mutated.
   */
  public void setStores(final boolean argStores) {
    mStores = argStores;
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
   * @param argIncrements
   *          true if increments will be mutated.
   */
  public void setIncrements(final boolean argIncrements) {
    mIncrements = argIncrements;
  }

  /**
   * Gets whether constant pool will be mutated.
   *
   * @return true if constant pool will be mutated.
   */
  public boolean isCPool() {
    return mCPool;
  }

  /**
   * Sets whether constant pool will be mutated.
   *
   * @param cpool true if constants will be mutated.
   */
  public void setCPool(final boolean cpool) {
    mCPool = cpool;
  }

  /**
   * Sets whether switches will be mutated.
   *
   * @param switches true if switches will be mutated.
   */
  public void setSwitch(final boolean switches) {
    mSwitches = switches;
  }

  /**
   * Gets whether switches will be mutated.
   *
   * @return true if switches will be mutated.
   */
  public boolean isSwitch() {
    return mSwitches;
  }

  /**
   * Gets whether tests are ordered by their run time.
   *
   * @return true if tests are ordered by their run time.
   */
  public boolean isOrdered() {
    return mOrdered;
  }

  /**
   * Sets whether tests are ordered by their run time.
   *
   * @param argOrdered true if tests should be ordered by their run time.
   */
  public void setOrdered(final boolean argOrdered) {
    mOrdered = argOrdered;
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
   * @param argLoadCache
   *          Value to assign to loadCache
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
   * @param argSaveCache
   *          Value to assign to saveCache
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
   * @param argUseCache
   *          Value to assign to useCache
   */
  public void setUseCache(final boolean argUseCache) {
    mUseCache = argUseCache;
  }

  /**
   * Gets the set of excluded method names
   *
   * @return the set of excluded method names
   */
  public Set<String> getExcludeMethods() {
    return mExcludeMethods;
  }

  /**
   * Gets whether record statistics mode is set.
   * 
   * @return true if record statistics mode is enabled.
   */
  public boolean isRecStat() {
    return mRecStat;
  }

  /**
   * Sets whether record statistic mode is enabled.
   * 
   * @param recStat
   */
  public void setRecStat(final boolean recStat) {
    mRecStat = recStat;
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
    boolean loaded = false;

    // Load the cache if it exists and is needed
    if (mLoadCache) {
      try {
        final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CACHE_FILE));
        try {
          mCache = (FailedTestMap) ois.readObject();
        } finally {
          ois.close();
        }
        loaded = true;
      } catch (IOException e) {
        loaded = false;
      }
    }
    if (!loaded) {
      mCache = new FailedTestMap();
    }
  }

  private void updateCache(MutationResult mutation) {
    if (mutation.isPassed()) {
      StringTokenizer tokens = new StringTokenizer(mutation.getTestDescription(), ":");
      String clazzName = tokens.nextToken();
      assert clazzName.equals(mutation.getClassName());
      String methodName = tokens.nextToken();
      int mutPoint = Integer.parseInt(tokens.nextToken());
      String tests[] = tokens.nextToken().split(";");
      for (String test : tests) {
        String segs[] = test.split("/");
        String testName = segs[1];
        int status = Integer.parseInt(segs[2]);
        if (status == 0) { 
          mCache.addFailure(clazzName, methodName, mutPoint, testName);
          break;
        }
      }
    }
  }

  private boolean writeCache(File f) {
    try {
      if (f.exists() && !f.delete()) {
        throw new IOException("Could not delete existing cache " + f);
      }
      ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
      o.writeObject(mCache);
      o.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  private void initStats() {
    mStats = new HashMap<MutationKey, TestStatistic>();
    mClassMutMap = new HashMap<String, List<String>>();
    mTestMap = new HashMap<String, List<String>>();
  }

  /**
   * When each mutation result is read from the child process, the corresponding statistic records
   * get updated accordingly and is put into the mStats map.
   *  
   * The statistic records come from the mutation.getTestDescription() string.
   * 
   * A typical mutation.getTestDescription() string for a PASS test is like:
   * example.Mover:move(Ljava/lang/String;I)V:8:example.MoverTest/testLeft/1/0.416045;example.MoverTest/testRight/0/0.126273;example.MoverTestP3/testPrettyString/1/0.155607;example.MoverTest/testDown/1/0.11028;example.MoverTest/testUp/1/0.074451;
   * 
   * A typical mutation.getTestDescription() string for a FAIL test is like:
   * example.Mover:31: - -> +:example.Mover:move(Ljava/lang/String;I)V:6:example.MoverTest/testLeft/1/0.421073;example.MoverTest/testRight/1/0.114819;example.MoverTestP3/testPrettyString/1/0.154279;example.MoverTest/testDown/1/0.109441;example.MoverTest/testUp/1/0.083111;
   * 
   * The elements in mutation.getTestDescription() string are separated by ":". A standard PASS test
   * description should start with the class name that has been mutated, followed by the method name,  
   * and then line number, and a string of the passed test class name with methods and their run time 
   * (this string again is separated by "/").  
   * 
   * The only difference between a PASS test description and a FAIL test description is that the FAIL 
   * test description has three extra elements after the class name and before the string of test
   * method names and times. These extra elements are lineNumber, modification, and className. 
   * 
   * StringTokenizer is used to extract each of the elements and the elements are used as attributes
   * to construct the MutationKey and TestStatistic respectively. 
   * 
   * @param mutation
   */
  private void updateStats(MutationResult mutation) {
    if (mutation.getTestDescription() != null &&
        mutation.getTestDescription().startsWith("No mutation made")) {
      return;
    }

    if (mutation.getDescription().startsWith("No mutation made")) {
      return;
    }

    StringTokenizer tokens = new StringTokenizer(mutation.getTestDescription(), ":");
    String clazzName = tokens.nextToken();
    assert clazzName.equals(mutation.getClassName());

    if (mutation.isFailed()) {
      // extra tokens when mutation failed
      String lineNum = tokens.nextToken();
      String modification = tokens.nextToken();
      String className = tokens.nextToken();
    }

    String methodName = tokens.nextToken();
    int mutPoint = Integer.parseInt(tokens.nextToken());
    String testMethodName = tokens.nextToken();
    String modification = mutation.getDescription();
    
    
    if (mClassMutMap.get(clazzName) == null) {
      mClassMutMap.put(clazzName, Collections.singletonList(modification));
    } else {
      List<String> mods = new ArrayList<String>(mClassMutMap.get(clazzName));
      mods.add(modification);
      mClassMutMap.put(clazzName, mods);
    }
    
    String[] testMethods = testMethodName.split(";");
    for (int i = 0; i < testMethods.length; i++) {
      StringTokenizer ts = new StringTokenizer(testMethods[i], "/");
      String testClassName = ts.nextToken();
      String testName = testClassName + "." + ts.nextToken();
      int status = Integer.valueOf(ts.nextToken());
      String testTime = ts.nextToken();

      MutationKey mutationKey = new MutationKey(
          clazzName, testClassName, testName, modification);
      TestStatistic testStatistic = new TestStatistic(status, testTime);
      mStats.put(mutationKey, testStatistic);
      
      if (mTestMap.get(testClassName) == null) {
        mTestMap.put(testClassName, Collections.singletonList(testName));
      } else {
        List<String> tMethods = new ArrayList<String>(mTestMap.get(testClassName));
        if (!tMethods.contains(testName)) {
          tMethods.add(testName);
          mTestMap.put(testClassName, tMethods);
        }
      }
    }
  }

  /**
   * Writes the statistic results into file.
   *  
   * @param cName   statistic file name
   * @return
   */
  private boolean writeStats(String cName) {
    try {
      Writer o = constructBufferWriter("jumble-stat-" + cName + ".csv");
      // First line - test class name
      o.append("\t");
      o.append("\t");
      for (String tClass : mTestMap.keySet()) {
        // Output the test class name as column name
        o.append(tClass + "\t");
        // Reserve columns for test methods that belongs to the same test class
        for (int i = 0; i < mTestMap.get(tClass).size()-1; i++) {
          o.append("\t");
        }
      }
      o.append("\n");
      
      // Second line - test method name
      o.append("\t");
      o.append("\t");
      for (List<String> tMethods : mTestMap.values()) {
        // Output test methods in each test class
        for (String method : tMethods) {
          o.append(method + "\t");
        }
      }
      o.append("\n");
      
      // Following lines are the results of all the mutations
      for (String className : mClassMutMap.keySet()) {
        // Output mutated class name
        o.append(className + "\t");
        boolean isFirst = true;
        List<String> muts = mClassMutMap.get(className);
        for (String mut : muts) {
          if (!isFirst) {
            // Not the first mutation line of the same mutated class,
            // we don't need to duplicate the class name
            o.append("\t");
          }
          isFirst = false;

          // Output mutation line number and description
          o.append(mut + "\t");
          for (String tClass : mTestMap.keySet()) {
            List<String> tests = mTestMap.get(tClass);
            for (String test : tests) {
              /**
               * For each of the MutationKey combination, we try to find a corresponding
               * statistic record from the mStats map. The stat contains the mutation
               * test status : PASS | FAIL | TIMEOUT; and the test run time.
               */
              MutationKey key = new MutationKey(className, tClass, test, mut);
              
              boolean isFound = false;
              for (MutationKey mutKey : mStats.keySet()) {
                if (mutKey.equals(key)) {
                  TestStatistic stat = mStats.get(mutKey);
                  if (MutationResult.PASS == stat.status) {
                    o.append("P/" + stat.getTime() + "s\t");
                  } else if (MutationResult.FAIL == stat.status) {
                    o.append("F/" + stat.getTime() + "s\t");
                  } else if (MutationResult.TIMEOUT == stat.status) {
                    o.append("T" + "\t");
                  } else {
                    o.append("--" + "\t");
                  }
                  isFound = true;
                  break;
                }
              }
              
              /**
               * If no stat record is found, the test is not run.
               */
              if (!isFound) {
                o.append("--" + "\t");
              }
            }
          }
          o.append("\n");
        }
      }

      o.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  /**
   * Writes the statistic results into file as a binary matrix.
   * 1  passed
   * 0  failed
   * 
   * @param cName   statistic file name
   * @return
   */
  private boolean writeToBinaryMatrix(String cName) {
    try {
      Writer o = constructBufferWriter("jumble-stat-" + cName + "-binary-matrix.csv");

      Map<String, Double> testTimeMap = new HashMap<String, Double>();
      List<String> orderedMuts = new ArrayList<String>();
      List<String> orderedTests = new ArrayList<String>();
      Map<String, Integer> colCountMap = new HashMap<String, Integer>();
      int testCount = 0;
      
      // Following lines are the results of all the mutations
      for (String className : mClassMutMap.keySet()) {
        List<String> muts = mClassMutMap.get(className);

        for (String mut : muts) {
          orderedMuts.add(mut);

          if (!isDetect(mut, className)) {
            orderedMuts.remove(mut);
            continue;
          }
          
          int rowCount = 0;
          for (String tClass : mTestMap.keySet()) {
            List<String> tests = mTestMap.get(tClass);
            for (String test : tests) {
              if (!orderedTests.contains(test)) {
                orderedTests.add(test);
                testCount++;
              }
              
              /**
               * For each of the MutationKey combination, we try to find a corresponding
               * statistic record from the mStats map. The stat contains the mutation
               * test status : PASS | FAIL | TIMEOUT; and the test run time.
               */
              MutationKey key = new MutationKey(className, tClass, test, mut);
              
              boolean isFound = false;
              for (MutationKey mutKey : mStats.keySet()) {
                if (mutKey.equals(key)) {
                  TestStatistic stat = mStats.get(mutKey);
                  if (MutationResult.PASS == stat.status) {
                    o.append("1" + " ");
                    rowCount++;
                    
                    if (colCountMap.get(test) == null) {
                      colCountMap.put(test, 1);
                    } else {
                      Integer totalColCount = colCountMap.get(test) + 1;
                      colCountMap.put(test, totalColCount);
                    }
                    
                  } else if (MutationResult.FAIL == stat.status) {
                    o.append("0" + " ");
                  } else if (MutationResult.TIMEOUT == stat.status) {
                    o.append("0" + " ");
                  } else {
                    o.append("0" + " ");
                  }
                  
                  if (MutationResult.PASS == stat.status || MutationResult.FAIL == stat.status) {
                    if (testTimeMap.get(test) == null) {
                      testTimeMap.put(test, Double.parseDouble(stat.getTime()));
                    } else {
                      Double totalTime = testTimeMap.get(test) + Double.parseDouble(stat.getTime());
                      testTimeMap.put(test, totalTime);
                    }
                  }
                  
                  isFound = true;
                  break;
                }
              }
              
              /**
               * If no stat record is found, the test is not run.
               */
              if (!isFound) {
                o.append("0" + " ");
              }
            }
          }
          
          o.append("" + rowCount);
          o.append("\n");
        }
      }
      
      writeExtraFiles(cName, o, testTimeMap, orderedMuts, orderedTests, colCountMap);
      
      o.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  private boolean isDetect(String mut, String className) {
    for (String tClass : mTestMap.keySet()) {
      List<String> tests = mTestMap.get(tClass);
      for (String test : tests) {
        MutationKey key = new MutationKey(className, tClass, test, mut);
        
        // Remove the requirement that none of the tests detected
        for (MutationKey mutKey : mStats.keySet()) {
          if (mutKey.equals(key)) {
            TestStatistic stat = mStats.get(mutKey);
            if (MutationResult.PASS == stat.status) {
              return true;
            }
          }
        }
      }
    }
    
    return false;
  }

  private void writeExtraFiles(String cName, Writer o, 
      Map<String, Double> testTimeMap, List<String> orderedMuts, List<String> orderedTests,
      Map<String, Integer> colCountMap) throws IOException {
    
    Writer timingWriter = constructBufferWriter("jumble-stat-" + cName + "-time.csv");
    // Mapping file contains all the tests and detected mutation mapping.
    // If a mutation is not detected by any of the tests, it's not included in this mapping file.
    Writer mappingWriter = constructBufferWriter("jumble-stat-" + cName + "-mapping.csv");
    
    timingWriter.append("Name\tTime\n");
    mappingWriter.append("Tests mapping: \n");
    int counter = 1;
    for (String t : orderedTests) {
      mappingWriter.append("" + counter + "\t" + t + "\n");
      timingWriter.append(counter + "\t" + testTimeMap.get(t) + "\n");
      o.append(colCountMap.get(t) + " ");
      counter++;
    }
    o.append("0");
    
    mappingWriter.append("\n");
    mappingWriter.append("Mutations mapping: \n");
    counter = 1;
    for (String m : orderedMuts) {
      mappingWriter.append("" + counter++ + "\t" + m + "\n");
    }

    mappingWriter.close();
    timingWriter.close();
  }

  private BufferedWriter constructBufferWriter(String fileName) throws IOException {
    File dir = new File(System.getProperty("user.dir") + "/jumble_stats");
    if (!dir.exists()) {
      dir.mkdir();
    }
    
    File file = new File(dir, fileName);
    if (file.exists() && !file.delete()) {
      throw new IOException("Could not delete existing file " + file);
    }
    return new BufferedWriter(new FileWriter(file));
  }
  
  public void addExcludeMethod(String methodName) {
    if (methodName == null) {
      throw new NullPointerException();
    }
    mExcludeMethods.add(methodName);
  }

  public void addDeferredClass(String className) {
    if (className == null) {
      throw new NullPointerException();
    }
    mDeferredClasses.add(className);
  }

  public void addJvmArg(String arg) {
    if (arg == null) {
      throw new NullPointerException();
    }
    mJvmArgs.add(arg);
  }

  public void addSystemProperty(String property) {
    if (property == null) {
      throw new NullPointerException();
    }
    int pos = property.indexOf('=');
    if (pos == -1) {
      throw new IllegalArgumentException("Malformed property definition, expected name=value");
    }
    System.setProperty(property.substring(0, pos), property.substring(pos + 1));
    mJvmArgs.add("-D" + property);
  }

  /** Constructs arguments to the FastJumbler */
  private List<String> createArgs(int currentMutation, int max) {
    ArrayList<String> args = new ArrayList<String>();
    args.add("--" + FastJumbler.FLAG_CLASSPATH);
    args.add(mClassPath);

    // mutation point
    args.add("--" + FastJumbler.FLAG_START);
    args.add(String.valueOf(currentMutation));

    // class name
    args.add(mClassName);
    // test suite filename
    args.add(mTestSuiteFile.toString());

    if (mUseCache) {
      // Write a temp cache
      if (writeCache(mCacheFile)) {
        args.add(mCacheFile.toString());
      }
    }

    // exclude methods
    if (!mExcludeMethods.isEmpty()) {
      StringBuffer ex = new StringBuffer();
      Iterator<String> it = mExcludeMethods.iterator();
      for (int i = 0; i < mExcludeMethods.size(); i++) {
        if (i == 0) {
          ex.append(it.next());
        } else {
          ex.append("," + it.next());
        }
      }
      args.add("--" + FastJumbler.FLAG_EXCLUDE);
      args.add(ex.toString());
    }

    // Deferred classes
    if (!mDeferredClasses.isEmpty()) {
      for (String classname : mDeferredClasses) {
        args.add("--" + FastJumbler.FLAG_DEFER);
        args.add(classname);
      }
    }

    // inline constants
    if (mInlineConstants) {
      args.add("--" + FastJumbler.FLAG_INLINE_CONSTS);
    }
    // return values
    if (mReturnVals) {
      args.add("--" + FastJumbler.FLAG_RETURN_VALS);
    }
    // return values
    if (mStores) {
      args.add("--" + FastJumbler.FLAG_STORES);
    }
    // increments
    if (mIncrements) {
      args.add("--" + FastJumbler.FLAG_INCREMENTS);
    }
    // constant pool
    if (mCPool) {
      args.add("--" + FastJumbler.FLAG_CPOOL);
    }
    // switches
    if (mSwitches) {
      args.add("--" + FastJumbler.FLAG_SWITCHES);
    }
    // verbose
    if (mVerbose) {
      args.add("--" + FastJumbler.FLAG_VERBOSE);
    }
    // record statistic 
    if (mRecStat) {
      args.add("--" + FastJumbler.FLAG_REC_STAT);
    }

    if (max >= 0) {
      args.add("--" + FastJumbler.FLAG_LENGTH);
      args.add("" + max);
    }
    return args;
  }

  private Mutater createMutater(int mutationpoint) {
    // Get the number of mutation points from the Jumbler
    final Mutater m = new Mutater(mutationpoint);
    m.setIgnoredMethods(mExcludeMethods);
    m.setMutateIncrements(mIncrements);
    m.setMutateCPool(mCPool);
    m.setMutateSwitch(mSwitches);
    m.setMutateInlineConstants(mInlineConstants);
    m.setMutateReturnValues(mReturnVals);
    m.setMutateStores(mStores);
    return m;
  }

  private int countMutationPoints(MutatingClassLoader loader, String classname) throws ClassNotFoundException {
    return loader.countMutationPoints(classname);
  }

  private boolean debugOutput(String out, String err) {
    if (err != null) {
      System.err.println("Child.err->" + err);
    }
    if (out != null) {
      System.err.println("Child.out->" + out);
    }
    return true; // So we can be enabled/disabled via assertion mechanism.
  }

  private void waitForStart(IOThread iot, IOThread eot) throws InterruptedException {
    // read the "START" to let us know the JVM has started
    // we don't want to time this.
    // FIXME this looks dangerous. What if the test can't even get to the point
    // of outputting START (e.g. class loading issues)
    while (true) {
      String out = iot.getNext();
      String err = eot.getAvailable();
      if (mVerbose) {
        debugOutput(out, err);
      }
      if ((out == null) && (err == null)) {
        Thread.sleep(10);
      } else if (FastJumbler.SIGNAL_START.equals(out)) {
        break;
      } else {
        throw new RuntimeException("com.reeltwo.jumble.fast.FastJumbler returned " + ((out != null) ? out : err + " on stderr") + " instead of " + FastJumbler.SIGNAL_START);
      }
    }
  }

  private void startChildProcess(List<String> args) throws IOException, InterruptedException {
    JavaRunner runner = getJavaRunner();
    runner.setArguments(args);
    mChildProcess = runner.start();
    mIot = new IOThread(mChildProcess.getInputStream());
    mIot.setDaemon(true);
    mIot.start();
    mEot = new IOThread(mChildProcess.getErrorStream());
    mEot.setDaemon(true);
    mEot.start();
    waitForStart(mIot, mEot);
  }

  /** Reads a mutation result from the child process */
  private MutationResult readMutation(int currentMutation, long timeout) throws InterruptedException {
    long before = System.currentTimeMillis();
    long after = before;
    String modification = null;
    // Run until we have a result or time out
    while (true) {
      String out = mIot.getNext();
      if (mVerbose) {
        debugOutput(out, mEot.getAvailable());
      }
      if (out == null) {
        if (after - before > timeout) {
          mChildProcess.destroy();
          mChildProcess = null;
          return new MutationResult(MutationResult.TIMEOUT, mClassName, currentMutation, modification);
        } else {
          Thread.sleep(50);
          after = System.currentTimeMillis();
        }
      } else {
        if (out.startsWith(FastJumbler.SIGNAL_MAX_REACHED)) {
          return null; // Child JVM requested continuing in a new JVM
        } else if (out.startsWith(FastJumbler.INIT_PREFIX)) {
          modification = out.substring(FastJumbler.INIT_PREFIX.length());
        } else {
          MutationResult m = null;
          if (out.startsWith(FastJumbler.PASS_PREFIX)) {
            m = new MutationResult(MutationResult.PASS, mClassName, currentMutation, modification, out.substring(FastJumbler.PASS_PREFIX.length()));
          } else if (out.startsWith(FastJumbler.FAIL_PREFIX)) {
            m = new MutationResult(MutationResult.FAIL, mClassName, currentMutation, modification, out.substring(FastJumbler.FAIL_PREFIX.length()));
          }
          if (m != null) {
            if (mUseCache) {
              updateCache(m);
            }
            if (mRecStat) {
              updateStats(m);
            }
            return m;
          }
        }
      }
    }
  }

  /**
   * Runs tests without mutating at all. If all OK, write out testsuitefile for
   * later use, otherwise return a JumbleResult
   */
  private JumbleResult runInitialTests(List<String> testClassNames) {
    MutatingClassLoader jumbler = new MutatingClassLoader(mClassName, createMutater(-1), mClassPath);
    if (!mDeferredClasses.isEmpty()) {
      jumbler.addDeferredPrefixes(mDeferredClasses);
    }
    ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(jumbler);
    try {

      mMutationCount = countMutationPoints(jumbler, mClassName);
      if (mMutationCount == -1) {
        return new InterfaceResult(mClassName);
      }
      TimingTestSuite suite = null;
      try {
        suite = new TimingTestSuite(jumbler, testClassNames.toArray(new String[testClassNames.size()]));
      } catch (ClassNotFoundException e) {
        // test class did not exist
        return new MissingTestsTestResult(mClassName, testClassNames, mMutationCount);
      }

      JUnitTestResult result = new JUnitTestResult();
      suite.run(result);
      boolean successful = result.wasSuccessful();

      // Now, if the tests failed, can return straight away
      if (!successful) {
        if (mVerbose) {
          System.err.println(result);
        }
        return new BrokenTestsTestResult(mClassName, testClassNames, mMutationCount);
      }

      // Set the test runtime so we can calculate timeouts when running the
      // mutated tests
      mTotalRuntime = suite.getTotalRuntime();

      // Store the test suite information serialized in a temporary file so
      // FastJumbler can load it.
      Object order = suite.getOrder(mOrdered);
      final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mTestSuiteFile));
      try {
        oos.writeObject(order);
      } finally {
        oos.close();
      }

      // Now try the tests again in a separate JVM to detect if there
      // are problems due to invocation within a separate JVM.
      mChildProcess = null;
      mIot = null;
      mEot = null;
      startChildProcess(createArgs(-1, 1));
      MutationResult thisResult = readMutation(-1, computeTimeout(mTotalRuntime));
      if (mChildProcess != null) {
        mChildProcess = null;
      }
      if (thisResult == null) {
        // This is a problem due to unknown reasons
        System.err.println("WARNING: Child JVM requested restart before completing any mutations!!");
      } else if (thisResult.getStatus() == MutationResult.PASS) {
        // This is a problem, we expect a run without any mutations to look like a fail (i.e. all tests pass)
        if (mVerbose) {
          System.err.println("Problem jumbling: Tests failed when running unmutated in external JVM!");
        }
        return new BrokenTestsTestResult(mClassName, testClassNames, mMutationCount);
      }


    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      RuntimeException r = new IllegalStateException("Problem using reflection to set up run under another classloader");
      r.initCause(e);
      throw r;
    } finally {
      Thread.currentThread().setContextClassLoader(oldLoader);
    }

    return null;
  }

  /**
   * Performs a Jumble run on the specified class with the specified tests.
   *
   * @param className
   *          the name of the class to Jumble
   * @param testClassNames
   *          the names of the associated test classes
   * @param listener
   *          the listener associated with this Jumble run.
   * @throws Exception
   *           if something goes wrong
   * @see JumbleResult
   * @see JumbleListener
   */
  public void runJumble(final String className, final List<String> testClassNames, JumbleListener listener) throws Exception {
    runJumbleProxy(className, testClassNames, listener);
  }

  /**
   * Goes through class names to find out if they can be resolved. Also checks
   * that all declared test classes are actually test cases.
   *
   * @param out
   *          listener where the error is reported.
   * @param className
   *          name of the class being jumbled.
   * @param testClassNames
   *          list of test class names.
   * @return
   */
  private boolean checkClasses(JumbleListener out, String className, List<String> testClassNames) {
    boolean ok = true;

    try {
      MutatingClassLoader jumbler = new MutatingClassLoader(mClassName, createMutater(-1), mClassPath);
      if (!mDeferredClasses.isEmpty()) {
        jumbler.addDeferredPrefixes(mDeferredClasses);
      }
      Class<?> clazz = jumbler.loadClass(className);
      if (!clazz.isInterface()) {
        for (int i = 0; i < testClassNames.size(); i++) {
          String testName = testClassNames.get(i);
          Class<?> test = null;
          try {
            test = jumbler.loadClass(testName);
          } catch (ClassNotFoundException e) {
            ; // Do nothing. No test class is handled elswhere
          }
          if (test != null && !JumbleUtils.isTestClass(test)) {
            out.error(testName + " is not a test class.");
            ok = false;
          }
        }
      }
    } catch (ClassNotFoundException e) {
      out.error("Class " + className + " not found.");
      ok = false;
    }

    return ok;
  }

  /**
   * Performs a Jumble run on the specified class with the specified tests.
   *
   * @param className
   *          the name of the class to Jumble
   * @param testClassNames
   *          the names of the associated test classes
   * @param listener
   *          the listener associated with this Jumble run.
   * @return the results of the Jumble run
   * @throws Exception
   *           if something goes wrong
   * @see JumbleResult
   * @see JumbleListener
   */
  private JumbleResult runJumbleProxy(final String className, final List<String> testClassNames, JumbleListener listener) throws Exception {
    if (listener == null) {
      listener = new NullListener();
    }

    if (!checkClasses(listener, className, testClassNames)) {
      return null;
    }

    mClassName = className;
    mCacheFile = File.createTempFile("cache", ".dat");
    mTestSuiteFile = File.createTempFile("testSuite", ".dat");

    if (mUseCache) {
      initCache();
    }

    if (mRecStat) {
      initStats();	
    }

    listener.jumbleRunStarted(mClassName, testClassNames);

    JumbleResult initialResult = runInitialTests(testClassNames);
    if (initialResult != null) {
      listener.performedInitialTest(initialResult, mMutationCount);
      // Jumbling will not happen here
      listener.jumbleRunEnded();
      return initialResult;
    }

    // compute the timeout
    long timeout = computeTimeout(mTotalRuntime);

    listener.performedInitialTest(new InitialOKJumbleResult(className, testClassNames, timeout), mMutationCount);

    mChildProcess = null;
    mIot = null;
    mEot = null;

    final List<MutationResult> allMutations = new ArrayList<MutationResult>();
    int count = 0;
    final int max = getMaxExternalMutations();
    for (int currentMutation = getFirstMutation(); currentMutation < mMutationCount; currentMutation++) {
      if (mChildProcess == null) {
        startChildProcess(createArgs(currentMutation, max));
        count = 0;
      }
      MutationResult thisResult = readMutation(currentMutation, timeout);
      if (thisResult == null) {
        mChildProcess = null;
        if (count == 0) {
          System.err.println("WARNING: Child JVM requested restart before completing any mutations!!");
        } else {
          // Restart current mutation in a new JVM
          currentMutation--;
        }
      } else {
        allMutations.add(thisResult);
        count++;
        if (max >= 0 && count >= max) {
          mChildProcess = null;
        }
        listener.finishedMutation(thisResult);
      }
    }
    if (mChildProcess != null) {
      mChildProcess = null;
    }

    JumbleResult ret = new NormalJumbleResult(className, testClassNames, allMutations, timeout);

    // finally, delete the test suite file
    if (mTestSuiteFile.exists() && !mTestSuiteFile.delete()) {
      System.err.println("Error: could not delete temporary file");
    }
    // Also delete the temporary cache and save the cache if needed
    if (mUseCache) {
      if (mCacheFile.exists() && !mCacheFile.delete()) {
        System.err.println("Error: could not delete temporary cache file " + mCacheFile);
      }
      if (mSaveCache) {
        writeCache(CACHE_FILE);
      }
    }
    // Record statistics
    if (mRecStat) {
      writeStats(className);
      writeToBinaryMatrix(className);
    }
    
    listener.jumbleRunEnded();
    mCache = null;
    mStats = null;
    return ret;
  }

  /**
   * Gets the maximum number of mutations performed by the external JVM.
   *
   * @return the maximum number of external mutations. A negative value implies
   *         no maximum.
   */
  public int getMaxExternalMutations() {
    return mMaxExternalMutations;
  }

  /**
   * Sets the maximum number of mutations performed by the external JVM.
   *
   * @param maxExternalMutations
   *          the maximum number of external mutations. A negative value implies
   *          no maximum.
   */
  public void setMaxExternalMutations(int maxExternalMutations) {
    mMaxExternalMutations = maxExternalMutations;
  }

  /**
   * Get the index of the first mutation to attempt.
   *
   * @return the first mutation index.
   */
  public int getFirstMutation() {
    return mFirstMutation;
  }

  /**
   * Set the index of the first mutation to attempt.
   *
   * @param newFirstMutation the new FirstMutation value.
   */
  public void setFirstMutation(final int newFirstMutation) {
    mFirstMutation = newFirstMutation;
  }
}
