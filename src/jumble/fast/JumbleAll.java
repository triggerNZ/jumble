package jumble.fast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jumble.dependency.DependencyExtractor;
import jumble.util.RTSI;
import junit.framework.TestCase;

/**
 * Class which performs Jumble tests for every class in the system.
 * 
 * @author Tin Pavlinic
 * 
 */
public class JumbleAll {

  /**
   * Main method. Runs Jumble on all classes found in the classpath.
   * 
   * @param args
   *          command line arguments - none
   */
  public static void main(String[] args) throws Exception {
    Collection packages = RTSI.getAllVisiblePackages();
    Set classNames = new HashSet();
    Set testNames = new HashSet();
    Set dependentClasses = new HashSet();
    Map classTestMap = new HashMap();

    Set ignore = new HashSet();
    ignore.addAll(new DependencyExtractor()
        .getIgnoredPackages());
    ignore.add("junit.awtui");
    ignore.add("junit.framework");
    ignore.add("junit.extensions");
    ignore.add("junit.runner");
    ignore.add("junit.swingui");
    ignore.add("junit.textui");
    ignore.add("junit");
    ignore.add("org.apache");
    ignore.add("jumble");

    System.out.println("Finding all classes...");

    // Find all the tests and all the other classes too
    for (Iterator it = packages.iterator(); it.hasNext();) {
      String packageName = (String) it.next();
      boolean ignorePackage = false;

      for (Iterator i = ignore.iterator(); i.hasNext();) {
        if (packageName.startsWith(((String) i.next()) + ".")) {
          ignorePackage = true;
          break;
        }
      }

      if (!ignorePackage) {
        classNames.addAll(RTSI.find(packageName, Object.class));
        testNames.addAll(RTSI.find(packageName, TestCase.class));
      }
    }

    // Remove all the test classes from the other classes
    classNames.removeAll(testNames);

    System.out.println("DONE: " + classNames.size() + " classes and "
        + testNames.size() + " tests.");
    System.out.println("Doing dependency analysis...");
    // Now do the dependency analysis
    for (Iterator it = testNames.iterator(); it.hasNext();) {
      String curTest = (String) it.next();
      DependencyExtractor extractor = new DependencyExtractor();
      extractor.setIgnoredPackages(ignore);
      Collection dependent = extractor.getAllDependencies(curTest, true);

      for (Iterator dit = dependent.iterator(); dit.hasNext();) {
        String curClass = (String) dit.next();
        dependentClasses.add(curClass);
        Collection tests;

        if (classTestMap.containsKey(curClass)) {
          tests = (Collection) classTestMap.get(curClass);
        } else {
          tests = new HashSet();
          classTestMap.put(curClass, tests);
        }
        tests.add(curTest);
      }
    }

    System.out.println("DONE: " + dependentClasses.size() + " dependent "
        + "classes.");
    System.out.println();
    System.out.println("RESULTS:");
    System.out.println();

    Set exclude = new HashSet();
    exclude.add("main");
    exclude.add("integrity");

    for (Iterator it = classNames.iterator(); it.hasNext();) {
      String className = (String) it.next();
      System.out.print(className + ": ");
      if (Class.forName(className).isInterface()) {
        System.out.println("100 (INTERFACE)");
      } else if (classTestMap.containsKey(className)) {
        List testList = new ArrayList((Collection) classTestMap.get(className));
        JumbleResult res = FastRunner.runJumble(className, testList, exclude,
            true, true, true);

        if (res.getInitialTestResult().wasSuccessful()) {
          if (res.getAllMutations().length == 0) {
            System.out.println("100 (NO POSSIBLE MUTATIONS)");
          } else {
            System.out.println(res.getCovered().length * 100
                / res.getAllMutations().length);
          }
        } else {
          System.out.println("0 (TESTS FAILED)");
        }
      } else {
        System.out.println("0 (NO TEST)");
      }
    }
  }
}
