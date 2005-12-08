package jumble;



import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jumble.dependency.DependencyExtractor;
import jumble.util.BCELRTSI;

/**
 * Class which performs Jumble tests for every class in the system.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 * 
 */
public class JumbleAll {
  public static final boolean DEBUG = true;

  private JumbleAll() { }

  /**
   * Main method. Runs Jumble on all classes found in the classpath.
   * 
   * @param args
   *          command line arguments - none
   */
  public static void main(String[] args) throws Exception {
    Set classNames = new HashSet();
    Set testNames = new HashSet();

    Set ignore = new HashSet();
    ignore.addAll(new DependencyExtractor().getIgnoredPackages());
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

    classNames.addAll(BCELRTSI.getAllClasses(false));
    testNames.addAll(BCELRTSI.getAllDerivedClasses("junit.framework.TestCase",
        false));

    for (Iterator it = classNames.iterator(); it.hasNext();) {
      String className = (String) it.next();
      
      for (Iterator it2 = ignore.iterator(); it2.hasNext();) {
        String pack = (String) it2.next();
        if (className.startsWith(pack)) {
          it.remove();
          break;
        }
      }
    }
    
    // Remove all the test classes from the other classes
    classNames.removeAll(testNames);
    
    System.out.println("DONE: " + classNames.size() + " classes and "
        + testNames.size() + " tests.");

    System.out.println();
    System.out.println("RESULTS:");
    System.out.println();

    for (Iterator it = classNames.iterator(); it.hasNext();) {
      String className = (String) it.next();
      Jumble.main(new String[] {className});
    }
  }
}
