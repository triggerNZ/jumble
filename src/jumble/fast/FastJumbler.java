/*
 * Created on 20/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.fast;



import com.reeltwo.util.CLIFlags.Flag;
import com.reeltwo.util.CLIFlags;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import jumble.mutation.Mutater;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoader;

/**
 * A Jumble class loader which avoids the need to modify BCEL's <CODE>
 * ClassLoader</CODE>. Furthermore, it will avoid needing to reload all the
 * classes not that were not jumbled. The idea is: if we need to modify a given
 * class, we cache the unmodified version first. That way next time it is
 * loaded, we will use our cached unmodified version instead of BCEL's cached
 * modified version. All classes which are not modified can be loaded from
 * BCEL's cache.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 * 
 */
public class FastJumbler extends ClassLoader {
  /** Used to perform the actual mutation */
  private Mutater mMutater;

  /** The name of the class being mutated */
  private String mTarget;

  /** The cache of fresh classes */
  private HashMap mCache;

  public FastJumbler(final String target, final Mutater mutater) {
    super();
    mTarget = target;
    mMutater = mutater;
    mCache = new HashMap();
  }

  /**
   * Gets a string description of the modification produced.
   * 
   * @return the modification
   */
  public String getModification() {
    return mMutater.getModification();
  }

  /**
   * If the class matches the target then it is mutated, otherwise the class if
   * returned unmodified. Overrides the corresponding method in the superclass.
   * Classes are cached so that we always load a fresh version.
   * 
   * This method is public so we can test it
   * 
   * @param clazz
   *          modification target
   * @return possibly modified class
   */
  public JavaClass modifyClass(JavaClass clazz) {

    if (clazz.getClassName().equals(mTarget)) {
      try {
        if (mCache.containsKey(clazz.getClassName())) {
          clazz = (JavaClass) mCache.get(clazz.getClassName());
        } else {
          mCache.put(clazz.getClassName(), clazz);
        }
        JavaClass ret = mMutater.jumbler(clazz);
        return ret;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return clazz;
  }

  /**
   * Sets the mutater. This is so we don't need to recreate the Jumbler every
   * time
   * 
   * @param m
   *          the new mutater.
   */
  public void setMutater(Mutater m) {
    mMutater = m;
  }

  /**
   * Main method.
   * 
   * @param args
   *          command line arguments. format is
   * 
   * <PRE>
   * 
   * java jumble.fast.FastJumbler [OPTIONS] [CLASS] [START] [TESTSUITE]
   * 
   * CLASS the fully-qualified name of the class to mutate.
   * 
   * START the mutation point to start at
   * 
   * TESTSUITE filename of the test suite to run. The file should contain
   * serialized TestOrder objects
   * 
   * OPTIONS -c Count mode. The program outputs the number of possible mutations
   * in the class. -r Mutate return values. -k Mutate inline constants. -i
   * Mutate increments. -x Exclude specified methods. -h Display this help
   * message.
   * 
   * </PRE>
   */
  public static void main(String[] args) throws Exception {
    final CLIFlags flags = new CLIFlags("FastJumbler");

    final Flag exFlag = flags.registerOptional('x', "exclude", String.class, "METHOD", "Comma-separated list of methods to exclude.");
    final Flag verboseFlag = flags.registerOptional('v', "verbose", "Provide extra output during run.");
    final Flag retFlag = flags.registerOptional('r', "return-vals", "Mutate return values.");
    final Flag inlFlag = flags.registerOptional('k', "inline-consts", "Mutate inline constants.");
    final Flag incFlag = flags.registerOptional('i', "increments", "Mutate increments.");
    final Flag startFlag = flags.registerRequired('s', "start", Integer.class, "NUM", "The mutation point to start at.");
    final Flag classFlag = flags.registerRequired(String.class, "CLASS", "Name of the class to mutate.");
    final Flag testSuiteFlag = flags.registerRequired(String.class, "TESTFILE", "Name the test suite file containing serialized TestOrder objects.");
    final Flag cacheFileFlag = flags.registerRequired(String.class, "CACHEFILE", "Name the cache file file.");
    cacheFileFlag.setMinCount(0);
    flags.setFlags(args);
    
    // First, process all the command line options
    final String className = ((String) classFlag.getValue()).replace('/', '.');
    final Mutater m = new Mutater(0);
    // Process excludes
    Set ignore = new HashSet();
    if (exFlag.isSet()) {
      String[] tokens = ((String) exFlag.getValue()).split(",");
      for (int i = 0; i < tokens.length; i++) {
        ignore.add(tokens[i]);
      }
      m.setIgnoredMethods(ignore);
    }
    m.setMutateIncrements(incFlag.isSet());
    m.setMutateInlineConstants(inlFlag.isSet());
    m.setMutateReturnValues(retFlag.isSet());
    

    final int mutationCount = m.countMutationPoints(className);
    final int startPoint = ((Integer) startFlag.getValue()).intValue();

    ObjectInputStream ois = new ObjectInputStream(new FileInputStream((String) testSuiteFlag.getValue()));
    final TestOrder order = (TestOrder) ois.readObject();
    ois.close();

    FailedTestMap cache = null;
    if (cacheFileFlag.isSet()) {
      ois = new ObjectInputStream(new FileInputStream((String) cacheFileFlag.getValue()));
      cache = (FailedTestMap) ois.readObject();
      ois.close();
    }

    // Let the parent JVM know that we are ready to start
    System.out.println("START");

    // Now run all the tests for each mutation point
    for (int i = startPoint; i < mutationCount; i++) {
      Mutater tempMutater = new Mutater(i);
      tempMutater.setIgnoredMethods(ignore);
      tempMutater.setMutateIncrements(incFlag.isSet());
      tempMutater.setMutateInlineConstants(inlFlag.isSet());
      tempMutater.setMutateReturnValues(retFlag.isSet());
      //jumbler.setMutater(tempMutater);
      FastJumbler jumbler = new FastJumbler(className, tempMutater);
      Class clazz = jumbler.loadClass("jumble.fast.JumbleTestSuite");
      Method meth = clazz.getMethod("run", new Class[] {
                                      jumbler.loadClass("jumble.fast.TestOrder"),
                                      jumbler.loadClass("jumble.fast.FailedTestMap"), String.class,
                                      String.class, int.class, boolean.class });
      String out = (String) meth.invoke(null, new Object[] {
                                          order.changeClassLoader(jumbler),
                                          (cache == null ? null : cache.changeClassLoader(jumbler)),
                                          className,
                                          tempMutater.getMutatedMethodName(className),
                                          new Integer(tempMutater.getMethodRelativeMutationPoint(className)), Boolean.valueOf(verboseFlag.isSet()) });
      System.out.println(out);  // This is the magic line that the parent JVM is looking for.

      if (cache != null && out.startsWith("PASS: ")) {
        StringTokenizer tokens = new StringTokenizer(out.substring(6), ":");
        String clazzName = tokens.nextToken();
        assert clazzName.equals(className);
        String methodName = tokens.nextToken();
        //System.out.println(methodName);
        int mutPoint = Integer.parseInt(tokens.nextToken());
        //System.out.println(mutPoint);
        String testName = tokens.nextToken();
        //System.out.println(testName);
        cache.addFailure(className, methodName, mutPoint, testName);
      }
    }
  }

}
