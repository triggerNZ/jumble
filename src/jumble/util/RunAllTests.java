package jumble.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Executes all Junit based test classes that are in the class path or in a specified
 * package in the class path.
 * <code>
 * java jumble.util.RunAllTests [-j] [-v] [-n] [package*]
 * 
 * </code>
 * @author <a href="mailto:jcleary@reeltwo.com">John Cleary</a>
 * @version $Revision$
 */
public class RunAllTests {

  /**
   * 
   */
  private static final Class[] NO_CLASSES = new Class[0];

  /**
   * 
   */
  private static final Object[] NO_OBJECTS = new Object[0];

  /**
   * 
   */
  private static final String TEST_NAME = "junit.framework.Test";

  private static final String LS = System.getProperty("line.separator");

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    runAllTests(args, System.out);
  }

  private static void runAllTests(final String[] args, final Appendable out) throws IOException {
    boolean verbose = false;
    boolean useJars = false;
    boolean noRun = false;
    boolean error = false;
    final Set packages = new HashSet();
    for (int i = 0; i < args.length; i++) {
      final String arg = args[i];
      final char prefix = arg.charAt(0);
      if (prefix == '-') {
        if (arg.equals("-j")) {
          useJars = true;
          continue;
        }
        if (arg.equals("-v")) {
          verbose = true;
          continue;
        }
        if (arg.equals("-n")) {
          noRun = true;
          verbose = true;
          continue;
        }
        error = true;
        out.append("Invalid option:" + arg + LS); 
      } else {
        packages.add(arg);
      }
    }
    if (error) {
      out.append("Errors in command line arguments. Should be:" + LS);
      out.append("    java jumble.util.RunAllTests [-j] [-v] [-n] [package*]" + LS);
      
    }
    final RunAllTests rat = new RunAllTests(packages, useJars, verbose, out);
    if (noRun) {
      if (verbose) {
        out.append("Not running tests" + LS);
      }
    } else {
      rat.runTests();
    }
  }
  
  private final boolean mVerbose;
  
  private final Appendable mOut;
  
  private final TestSuite mSuite;
  
  private RunAllTests(final Set packages, final boolean useJars, final boolean verbose, final Appendable out) throws IOException {
    mVerbose = verbose;
    mOut = out;
    mSuite = new TestSuite();
    if (packages.size() == 0) {
      if (mVerbose) {
        mOut.append("No packages specified finding all Test classes." + LS);
      }
      final Collection names = BCELRTSI.getAllDerivedClasses(TEST_NAME, useJars);
      addNames(names);
    } else {
      for (Iterator it = packages.iterator(); it.hasNext(); ) {
        final String packge = (String) it.next();
        if (mVerbose) {
          mOut.append("Package \"" + packge + "\"" + LS);
        }
        final Collection names = BCELRTSI.getAllDerivedClasses(TEST_NAME, packge, useJars);
        addNames(names);
      }
    }
    if (mVerbose) {
      mOut.append("Finished adding tests" + LS);
    }
  }

  /**
   * @param name
   * @throws IOException
   */
  private void addNames(final Collection names) throws IOException {
    for (Iterator jt = names.iterator(); jt.hasNext(); ) {
      final String name = (String) jt.next();
      if (mVerbose) {
        mOut.append("  \"" + name + "\"" + LS);
      }
      if (name.contains("AllTests")) {
        if (mVerbose) {
          mOut.append("    ignored" + LS);
        }
        continue;
      }
      final Class cl;
      try {
        cl = Class.forName(name);
      } catch (ClassNotFoundException e) {
        mOut.append("    Unable to construct class." + LS);
        continue;
      }
      final Method method;
      try {
        method = cl.getMethod("suite", NO_CLASSES);
      } catch (SecurityException e) {
        continue;
      } catch (NoSuchMethodException e) {
        mSuite.addTestSuite(cl);
        if (mVerbose) {
          mOut.append("    added from class" + LS);
        }
        continue;
      }
      final int modifiers = method.getModifiers();
      if (!Modifier.isStatic(modifiers)) {
        mOut.append("    suite() method found but not static" + LS);
      }
      if (!Modifier.isPublic(modifiers)) {
        mOut.append("    suite() method found but not public" + LS);
      }
      final Test test;
        try {
          test = (Test) method.invoke(null, NO_OBJECTS);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException("Error invoking suite() method for name=" + name, e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException("Error invoking suite() method for name=" + name, e);
        } catch (InvocationTargetException e) {
          throw new RuntimeException("Error invoking suite() method for name=" + name, e);
        }
      mSuite.addTest(test);
      if (mVerbose) {
        mOut.append("    added from suite()" + LS);
      }
    }
  }
  
  
  private void runTests() throws IOException {
    if (mVerbose) {
      mOut.append("Start running tests" + LS);
    }
    junit.textui.TestRunner.run(mSuite);
  }
}
