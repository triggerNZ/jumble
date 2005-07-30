/*
 * Created on 20/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.fast;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import jumble.Mutater;
import jumble.util.Utils;

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
  public static void main(String[] args) {
    try {
      //First, process all the command line options
      final String className;
      final Mutater m;
      final int mutationCount;
      final boolean countMode = Utils.getFlag('c', args);
      final boolean returnVals = Utils.getFlag('r', args);
      final boolean inlineConstants = Utils.getFlag('k', args);
      final boolean increments = Utils.getFlag('i', args);
      final String excludes = Utils.getOption('x', args);
      final boolean help = Utils.getFlag('h', args);

      //Display help
      if (help) {
        printUsage();
        return;
      }

      //Process excludes
      Set ignore = new HashSet();
      if (!excludes.equals("")) {
        StringTokenizer tokens = new StringTokenizer(excludes, ",");
        while (tokens.hasMoreTokens()) {
          ignore.add(tokens.nextToken());
        }

      }
      className = Utils.getNextArgument(args).replace('/', '.');
      m = new Mutater(0);

      m.setIgnoredMethods(ignore);
      m.setMutateIncrements(increments);
      m.setMutateInlineConstants(inlineConstants);
      m.setMutateReturnValues(returnVals);
      mutationCount = m.countMutationPoints(className);

      if (countMode) {
        System.out.println(mutationCount);
        return;
      } else {
        final int startPoint = Integer.parseInt(Utils.getNextArgument(args));
        final String filename = Utils.getNextArgument(args);
        final TestOrder order;
        FastJumbler jumbler = new FastJumbler(className, m);

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
            filename));

        order = (TestOrder) ois.readObject();
        ois.close();

        Utils.checkForRemainingOptions(args);

        //Let the parent JVM know that we are ready to start
        System.out.println("START");
        
        //Now run all the tests for each mutation point
        for (int i = startPoint; i < mutationCount; i++) {
          Mutater tempMutater = new Mutater(i);
          tempMutater.setIgnoredMethods(ignore);
          tempMutater.setMutateIncrements(increments);
          tempMutater.setMutateInlineConstants(inlineConstants);
          tempMutater.setMutateReturnValues(returnVals);
          jumbler.setMutater(tempMutater);
          jumbler = new FastJumbler(className, tempMutater);
          Class clazz = jumbler.loadClass("jumble.fast.JumbleTestSuite");
          Method meth = clazz.getMethod("run", new Class[] { jumbler
              .loadClass("jumble.fast.TestOrder"), String.class, 
              String.class, boolean.class, boolean.class});
          System.out.println(meth.invoke(null, new Object[] { order
              .changeClassLoader(jumbler), className, 
              tempMutater.getMutatedMethodName(className), 
              Boolean.TRUE, Boolean.TRUE}));
        }
      }

    } catch (Exception e) {
      e.printStackTrace(System.out);
      System.out.println();
      printUsage();
    }
  }

  private static void printUsage() {
    System.out.println("Usage:");
    System.out
        .println("java jumble.fast.FastJumbler [OPTIONS] [CLASS] [START] [TESTSUITE]");
    System.out.println();

    System.out
        .println("CLASS the fully-qualified name of the class to mutate.");
    System.out.println();
    System.out
        .println(" START an integer indicating the mutation point. Not necessary if we");
    System.out.println("are in count mode.");
    System.out.println();
    System.out
        .println("TESTSUITE a test suite file containing the tests. Not necessary if");
    System.out.println("we are in count mode.");
    System.out.println();
    System.out.println("OPTIONS");
    System.out
        .println("         -c Count mode. The program outputs the number of possible mutations");
    System.out.println("            in the class.");
    System.out.println("         -r Mutate return values.");
    System.out.println("         -k Mutate inline constants.");
    System.out.println("         -i Mutate increments.");
    System.out.println("         -x Exclude specified methods. ");
    System.out.println("         -h Display this help message.");
  }
}
