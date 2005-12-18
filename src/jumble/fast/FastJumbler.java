package jumble.fast;

import com.reeltwo.util.CLIFlags.Flag;
import com.reeltwo.util.CLIFlags;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import jumble.mutation.Mutater;
import jumble.mutation.MutatingClassLoader;

/**
 * A class that gives process separation when running unit tests.  A
 * parent virtual machine monitors the progress of the test runs and
 * terminates this process in the event of infinite loops etc.  This
 * class communicates to the parent process via standard output.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 * 
 */
public class FastJumbler {

  // Private c'tor
  private FastJumbler() { }
  
  /**
   * Main method.
   * 
   * @param args command line arguments. format is
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
    }

    m.setIgnoredMethods(ignore);
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
      // jumbler.setMutater(tempMutater);
      MutatingClassLoader jumbler = new MutatingClassLoader(className, tempMutater);
      Class clazz = jumbler.loadClass("jumble.fast.JumbleTestSuite");
      Method meth = clazz.getMethod("run", new Class[] {
                                      jumbler.loadClass("jumble.fast.TestOrder"),
                                      jumbler.loadClass("jumble.fast.FailedTestMap"), String.class,
                                      String.class, int.class, boolean.class });
      String out = (String) meth.invoke(null, new Object[] {
                                          order.clone(jumbler),
                                          (cache == null ? null : cache.clone(jumbler)),
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
