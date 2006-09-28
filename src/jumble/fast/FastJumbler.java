package jumble.fast;

import com.reeltwo.util.CLIFlags.Flag;
import com.reeltwo.util.CLIFlags;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashSet;
import java.util.Set;
import jumble.mutation.Mutater;
import jumble.mutation.MutatingClassLoader;

/**
 * A class that gives process separation when running unit tests. A parent
 * virtual machine monitors the progress of the test runs and terminates this
 * process in the event of infinite loops etc. This class communicates to the
 * parent process via standard output.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 * 
 */
public class FastJumbler {

  public static final String INIT_PREFIX = "INIT: ";

  public static final String PASS_PREFIX = "PASS: ";

  public static final String FAIL_PREFIX = "FAIL: ";

  public static final String SIGNAL_START = "START";

  public static final String SIGNAL_MAX_REACHED = "MAX_REACHED";


  // Private c'tor
  private FastJumbler() {
  }

  static final String FLAG_EXCLUDE = "exclude";
  static final String FLAG_VERBOSE = "verbose";
  static final String FLAG_RETURN_VALS = "return-vals";
  static final String FLAG_INLINE_CONSTS = "inline-consts";
  static final String FLAG_INCREMENTS = "increments";
  static final String FLAG_CPOOL = "cpool";
  static final String FLAG_SWITCHES = "switch";
  static final String FLAG_START = "start";
  static final String FLAG_LENGTH = "length";
  static final String FLAG_CLASSPATH = "classpath";

  /**
   * Main method. Supply --help to get help on the expected arguments.
   * 
   * @param args
   *          command line arguments.
   */
  public static void main(String[] args) throws Exception {
    final CLIFlags flags = new CLIFlags("FastJumbler");

    final Flag exFlag = flags.registerOptional('x', FLAG_EXCLUDE, String.class, "METHOD", "Comma-separated list of methods to exclude.");
    final Flag verboseFlag = flags.registerOptional('v', FLAG_VERBOSE, "Provide extra output during run.");
    final Flag retFlag = flags.registerOptional('r', FLAG_RETURN_VALS, "Mutate return values.");
    final Flag inlFlag = flags.registerOptional('k', FLAG_INLINE_CONSTS, "Mutate inline constants.");
    final Flag cpoolFlag = flags.registerOptional('w', FLAG_CPOOL, "Mutate constant pool entries.");
    final Flag switchFlag = flags.registerOptional('j', FLAG_SWITCHES, "Mutate switch instructions.");
    final Flag incFlag = flags.registerOptional('i', FLAG_INCREMENTS, "Mutate increments.");
    final Flag startFlag = flags.registerRequired('s', FLAG_START, Integer.class, "NUM", "The mutation point to start at.");
    final Flag lengthFlag = flags.registerOptional('l', FLAG_LENGTH, Integer.class, "LEN", "The number of mutation points to execute");
    final Flag classpathFlag = flags.registerOptional('c', FLAG_CLASSPATH, String.class, "CLASSPATH", "The classpath to use for tests", System.getProperty("java.class.path"));
    final Flag classFlag = flags.registerRequired(String.class, "CLASS", "Name of the class to mutate.");
    final Flag testSuiteFlag = flags.registerRequired(String.class, "TESTFILE", "Name the test suite file containing serialized TestOrder objects.");
    final Flag cacheFileFlag = flags.registerRequired(String.class, "CACHEFILE", "Name the cache file file.");
    cacheFileFlag.setMinCount(0);
    flags.setFlags(args);

    // First, process all the command line options
    final String className = ((String) classFlag.getValue()).replace('/', '.');
    // Process excludes
    Set ignore = new HashSet();
    if (exFlag.isSet()) {
      String[] tokens = ((String) exFlag.getValue()).split(",");
      for (int i = 0; i < tokens.length; i++) {
        ignore.add(tokens[i]);
      }
    }

    final int startPoint = ((Integer) startFlag.getValue()).intValue();
    final int length = lengthFlag.isSet() ? ((Integer) lengthFlag.getValue()).intValue() : -1;
    final String classpath = (String) classpathFlag.getValue();
    final Mutater mutater = new Mutater(-1);
    mutater.setIgnoredMethods(ignore);
    mutater.setMutateIncrements(incFlag.isSet());
    mutater.setMutateCPool(cpoolFlag.isSet());
    mutater.setMutateSwitch(switchFlag.isSet());
    mutater.setMutateInlineConstants(inlFlag.isSet());
    mutater.setMutateReturnValues(retFlag.isSet());
    MutatingClassLoader jumbler = new MutatingClassLoader(className, mutater, classpath);
    final int mutationCount = jumbler.countMutationPoints(className);
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream((String) testSuiteFlag.getValue()));
    final TestOrder order = (TestOrder) ois.readObject();
    ois.close();

    FailedTestMap cache = null;
    if (cacheFileFlag.isSet()) {
      ois = new ObjectInputStream(new FileInputStream((String) cacheFileFlag.getValue()));
      cache = (FailedTestMap) ois.readObject();
      ois.close();
    }

    MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
    MemoryUsage usage = mxbean.getNonHeapMemoryUsage();
    long nonheapDelta;

    // Let the parent JVM know that we are ready to start
    System.out.println(SIGNAL_START);
    // Now run all the tests for each mutation point
    int count = 0;
    for (int i = startPoint; i < mutationCount; i++) {
      if (count++ >= length && lengthFlag.isSet()) {
        System.out.println(SIGNAL_MAX_REACHED);
        break;
      }
//       if (verboseFlag.isSet()) {
//         System.err.println("Attempting mutation point: " + i);
//       }
      mutater.setMutationPoint(i);
      jumbler = new MutatingClassLoader(className, mutater, classpath);
      jumbler.loadClass(className);
      String methodName = mutater.getMutatedMethodName(className);
      int mutPoint = mutater.getMethodRelativeMutationPoint(className);
      assert (mutPoint != -1) : "Couldn't get method relative mutation point";
      String modification = (i == -1) ? "No mutation made" : mutater.getModification();

      // Communicate to parent the current mutation being attempted
      System.out.println(INIT_PREFIX + modification); 

      // Do the run
      String out = JumbleTestSuite.run(jumbler,
                                       order, 
                                       cache,
                                       className,
                                       methodName, mutPoint, 
                                       verboseFlag.isSet());
      
      // Communicate the outcome to the parent JVM.
      if (out.startsWith("FAIL")) {
        // This is the magic line that the parent JVM is looking for.
        System.out.println(FAIL_PREFIX + modification); 
      } else if (out.startsWith("PASS: ")) {
        String testName = out.substring(6);
        if (cache != null) {
          cache.addFailure(className, methodName, mutPoint, testName);
        }
        // This is the magic line that the parent JVM is looking for.
        System.out.println(PASS_PREFIX + className + ":" + methodName + ":" + mutPoint + ":" + testName); 
      } else {
        throw new RuntimeException("Unexpected result from JumbleTestSuite: " + out);
      }

      long oldUsed = usage.getUsed() / 1024;
      usage = mxbean.getNonHeapMemoryUsage();
      nonheapDelta = usage.getUsed() / 1024 - oldUsed;
      long available = (usage.getMax() - usage.getUsed()) / 1024;
      if (verboseFlag.isSet()) {
        System.err.println("Non-Heap used:" + usage.getUsed() + "KB delta:" + nonheapDelta + "KB avail:" + available + "KB");
      }
      // Check non-heap usage and possibly bail out.
      if (nonheapDelta > 0 && available < ((nonheapDelta * 5) + 15000)) {
        // Communicate to the parent JVM if there's not enough non-heap memory to continue.
        System.out.println(SIGNAL_MAX_REACHED 
                           + "  Non-Heap used:" + usage.getUsed() 
                           + "KB delta:" + nonheapDelta
                           + "KB avail:" + available + "KB");
        break;
      }

    }
  }

}
