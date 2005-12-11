package jumble;

import com.reeltwo.util.CLIFlags.Flag;
import com.reeltwo.util.CLIFlags;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jumble.fast.FastRunner;
import jumble.fast.JumbleResult;
import jumble.fast.JumbleResultPrinter;
import jumble.fast.SeanResultPrinter;

/**
 * A CLI interface to the <CODE>FastRunner</CODE>.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class Jumble {

  /** Prevent instantiation */
  private Jumble() {
  }

  /**
   * Main method.
   * 
   * @param args
   *          command line arguments. format is
   * 
   * <PRE>
   * 
   * java jumble.fast.Jumble [OPTIONS] [CLASS] [TESTS]
   * 
   * CLASS the fully-qualified name of the class to mutate.
   * 
   * TESTS a list of test names to run on this class
   * 
   * OPTIONS 
   * 
   * </PRE>
   */
  public static void main(String[] args) throws Exception {
    try {
      // Process arguments
      FastRunner jumble = new FastRunner();
      CLIFlags flags = new CLIFlags("FastRunner");
      Flag exFlag = flags.registerOptional('x', "exclude", String.class, "METHOD", "Comma-separated list of methods to exclude.");
      Flag retFlag = flags.registerOptional('r', "return-vals", "Mutate return values.");
      Flag inlFlag = flags.registerOptional('k', "inline-consts", "Mutate inline constants.");
      Flag incFlag = flags.registerOptional('i', "increments", "Mutate increments.");
      Flag printFlag = flags.registerOptional('p', "printer", String.class, "CLASS", "Name of the class responsible for producing output.");
      Flag orderFlag = flags.registerOptional('o', "no-order", "Do not order tests by runtime.");
      Flag saveFlag = flags.registerOptional('s', "no-save-cache", "Do not save cache.");
      Flag loadFlag = flags.registerOptional('l', "no-load-cache", "Do not load cache.");
      Flag useFlag = flags.registerOptional('u', "no-use-cache", "Do not use cache.");
      Flag classFlag = flags.registerRequired(String.class, "CLASS", "Name of the class to mutate.");
      Flag testClassFlag = flags.registerRequired(String.class, "TESTCLASS", "Name of the unit test classes for testing the supplied class.");
      testClassFlag.setMinCount(0);
      testClassFlag.setMaxCount(Integer.MAX_VALUE);

      flags.setFlags(args);

      jumble.setInlineConstants(inlFlag.isSet());
      jumble.setReturnVals(retFlag.isSet());
      jumble.setIncrements(incFlag.isSet());
      jumble.setNoOrder(orderFlag.isSet());
      jumble.setLoadCache(!loadFlag.isSet());
      jumble.setSaveCache(!saveFlag.isSet());
      jumble.setUseCache(!useFlag.isSet());

      String className;
      List testList;

      boolean finishedTests = false;
      if (exFlag.isSet()) {
        String[] tokens = ((String) exFlag.getValue()).split(",");
        for (int i = 0; i < tokens.length; i++) {
          jumble.addExcludeMethod(tokens[i]);
        }
      }

      className = ((String) classFlag.getValue()).replace('/', '.');
      testList = new ArrayList();

      // We need at least one test
      if (testClassFlag.isSet()) {
        for (Iterator it = testClassFlag.getValues().iterator(); it.hasNext(); ) {
          testList.add(((String) it.next()).replace('/', '.'));
        }
      } else {
        // no test class given, guess its name
        String testName = className;
        if (className.startsWith("Abstract")) {
          testName = "Dummy" + className.substring(8);
        } else {
          final int ab = className.indexOf(".Abstract");
          if (ab != -1) {
            testName = className.substring(0, ab) + ".Dummy" + className.substring(ab + 9);
          }
        }
        final int dollar = testName.indexOf('$');
        if (dollar != -1) {
          testName = testName.substring(0, dollar);
        }
        testList.add(testName + "Test");
      }

      JumbleResult res = jumble.runJumble(className, testList);
      JumbleResultPrinter printer = printFlag.isSet() 
        ? getPrinter((String) printFlag.getValue()) 
        : new SeanResultPrinter(System.out);
      printer.printResult(res);

    } catch (Exception e) {
      printUsage();
      System.out.println();
      e.printStackTrace(System.out);
    }
  }

  /**
   * Returns a result printer instance as specified by <CODE>className</CODE>.
   * The printer is constructed with <CODE>System.out</CODE> as the argument.
   * If this is not allowed, then the no arguments constructor is ibvoked.
   * 
   * @param className
   *          name of result printer class.
   * @return a <CODE>JumbleResultPrinter</CODE> instance.
   */
  private static JumbleResultPrinter getPrinter(String className) {
    try {
      final Class clazz = Class.forName(className);
      try {
        final Constructor c = clazz.getConstructor(new Class[] {PrintStream.class });
        return (JumbleResultPrinter) c.newInstance(new Object[] {System.out });
      } catch (IllegalAccessException e) {
        ; // too bad
      } catch (InvocationTargetException e) {
        ; // too bad
      } catch (InstantiationException e) {
        ; // too bad
      } catch (NoSuchMethodException e) {
        ; // too bad
      }
      try {
        final Constructor c = clazz.getConstructor(new Class[0]);
        return (JumbleResultPrinter) c.newInstance(new Object[0]);
      } catch (IllegalAccessException e) {
        System.err.println("Invalid output class. Exception: ");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        System.err.println("Invalid output class. Exception: ");
        e.printStackTrace();
      } catch (InstantiationException e) {
        System.err.println("Invalid output class. Exception: ");
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        System.err.println("Invalid output class. Exception: ");
        e.printStackTrace();
      }
    } catch (ClassNotFoundException e) {
      ; // too bad
    }
    throw new IllegalArgumentException("Couldn't create JumbleResultPrinter: " + className);
  }

  /**
   * Prints command line usage help for this class.
   */
  private static void printUsage() {
    System.out.println("Usage:");
    System.out.println("java jumble.fast.Jumble [OPTIONS] [CLASS] [TESTS]");
    System.out.println();

    System.out.println("CLASS the fully-qualified name of the class to mutate.");
    System.out.println();
    System.out.println("TESTS a test suite file containing the tests.");
    System.out.println();
    System.out.println("OPTIONS");
    System.out.println("         -r Mutate return values.");
    System.out.println("         -k Mutate inline constants.");
    System.out.println("         -i Mutate increments.");
    System.out.println("         -x Exclude specified methods. ");
    System.out.println("         -n Do not order tests according to runtime");
    System.out.println("         -h Display this help message.");
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

}

