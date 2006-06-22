package jumble;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jumble.fast.FastRunner;
import jumble.ui.EmacsFormatListener;
import jumble.ui.JumbleListener;
import jumble.ui.PrinterListener;

import com.reeltwo.util.CLIFlags;
import com.reeltwo.util.CLIFlags.Flag;

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
   *          command line arguments. Use -h to see the expected arguments.
   */
  public static void main(String[] args) throws Exception {
    // Process arguments
    FastRunner jumble = new FastRunner();
    CLIFlags flags = new CLIFlags("Jumble");
    Flag verboseFlag = flags.registerOptional('v', "verbose", "Provide extra output during run.");
    Flag exFlag = flags.registerOptional('x', "exclude", String.class, "METHOD", "Comma-separated list of methods to exclude.");
    Flag retFlag = flags.registerOptional('r', "return-vals", "Mutate return values.");
    Flag inlFlag = flags.registerOptional('k', "inline-consts", "Mutate inline constants.");
    Flag incFlag = flags.registerOptional('i', "increments", "Mutate increments.");
    Flag emacsFlag = flags.registerOptional('e', "emacs", "Use Emacs-format output.");
    Flag printFlag = flags.registerOptional('p', "printer", String.class, "CLASS", "Name of the class responsible for producing output.");
    Flag classpathFlag = flags.registerOptional('c', "classpath", String.class, "CLASSPATH", "The classpath to use for tests", System.getProperty("java.class.path"));
    Flag orderFlag = flags.registerOptional('o', "no-order", "Do not order tests by runtime.");
    Flag saveFlag = flags.registerOptional('s', "no-save-cache", "Do not save cache.");
    Flag loadFlag = flags.registerOptional('l', "no-load-cache", "Do not load cache.");
    Flag useFlag = flags.registerOptional('u', "no-use-cache", "Do not use cache.");
    Flag lengthFlag = flags.registerOptional('m', "max-external-mutations", Integer.class, "MAX",
        "Maximum number of mutations to run in the external JVM");
    Flag classFlag = flags.registerRequired(String.class, "CLASS", "Name of the class to mutate.");
    Flag testClassFlag = flags.registerRequired(String.class, "TESTCLASS", "Name of the unit test classes for testing the supplied class.");
    testClassFlag.setMinCount(0);
    testClassFlag.setMaxCount(Integer.MAX_VALUE);

    flags.setFlags(args);

    jumble.setInlineConstants(inlFlag.isSet());
    jumble.setReturnVals(retFlag.isSet());
    jumble.setIncrements(incFlag.isSet());
    jumble.setOrdered(!orderFlag.isSet());
    jumble.setLoadCache(!loadFlag.isSet());
    jumble.setSaveCache(!saveFlag.isSet());
    jumble.setUseCache(!useFlag.isSet());
    jumble.setVerbose(verboseFlag.isSet());
    jumble.setClassPath((String) classpathFlag.getValue());

    if (lengthFlag.isSet()) {
      int val = ((Integer) lengthFlag.getValue()).intValue();

      if (val >= 0) {
        jumble.setMaxExternalMutations(val);
      }
    }

    String className;
    List testList;

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
      for (Iterator it = testClassFlag.getValues().iterator(); it.hasNext();) {
        testList.add(((String) it.next()).replace('/', '.'));
      }
    } else {
      // no test class given, guess its name
      testList.add(guessTestClassName(className));
    }


    JumbleListener listener = emacsFlag.isSet() ? new EmacsFormatListener() : !printFlag.isSet() ? new PrinterListener()
        : getListener((String) printFlag.getValue());
    jumble.runJumble(className, testList, listener);
  }

  /**
   * Guesses the name of a test class used for testing a particular class. It
   * assumes the following conventions:
   * <p>
   * 
   * <ul>
   * 
   * <li> Unit test classes end with <code>Test</code>
   * 
   * <li> An abstract classes are named such as <code>AbstractFoo</code> and
   * have a test class named such as <code>DummyFooTest</code>
   * 
   * </ul>
   * 
   * @param className
   *          a <code>String</code> value
   * @return the name of the test class that is expected to test
   *         <code>className</code>.
   */
  public static String guessTestClassName(String className) {
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
    return testName + "Test";
  }

  /**
   * Returns a <code>JumbleListener</code> instance as specified by <CODE>className</CODE>.
   * 
   * @param className
   *          name of class to instantiate.
   * @return a <CODE>JumbleListener</CODE> instance.
   */
  private static JumbleListener getListener(String className) {
    try {
      final Class clazz = Class.forName(className); // Class to be found in jumble.jar
      try {
        final Constructor c = clazz.getConstructor(new Class[0]);
        return (JumbleListener) c.newInstance(new Object[0]);
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
    throw new IllegalArgumentException("Couldn't create JumbleListener: " + className);
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
