package jumble.fast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import jumble.dependency.DependencyExtractor;
import jumble.util.BCELRTSI;

import org.apache.bcel.Repository;

/**
 * Class which ties the runtime test identification, dependency analysis and
 * Jumble stages of mutation testing.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 * 
 */
public class Jumble {

  private Jumble() { }

  /**
   * Main method. Finds all visible test classes, identifies those which use the
   * specified class and runs Jumble on them.
   * 
   * @param args
   *          the command line arguments. Should contain only one element - the
   *          name of the class to Jumble.
   */
  public static void main(String[] args) {
    try {
      String className = args[0];
      Class.forName(className);
      Set testClasses = new HashSet();
      
      if (args.length == 1) {
        Set allTests = new HashSet();
        System.out.println("Finding tests...");
        allTests.addAll(BCELRTSI.getAllDerivedClasses("junit.framework.TestCase",
            false));
        System.out.println("DONE: " + allTests.size() + " total tests classes");
  
        System.out.println("Doing dependency analysis: ");
        DependencyExtractor extractor = new DependencyExtractor();
  
        // ignore junit stuff, but not the samples
        extractor.getIgnoredPackages().add("junit.awtui");
        extractor.getIgnoredPackages().add("junit.framework");
        extractor.getIgnoredPackages().add("junit.extensions");
        extractor.getIgnoredPackages().add("junit.runner");
        extractor.getIgnoredPackages().add("junit.swingui");
        extractor.getIgnoredPackages().add("junit.textui");
  
        for (Iterator it = allTests.iterator(); it.hasNext();) {
          String testClass = (String) it.next();
          if (Repository.lookupClass(testClass).isAbstract()) {
            continue;
          }
          
          if (extractor.getAllDependencies(testClass, true).contains(className)) {
            testClasses.add(testClass);
          }
        }
        System.out.println("DONE: " + testClasses);
      } else if (args.length == 2) {
        StringTokenizer tokens = new StringTokenizer(args[1], ",");
        
        while (tokens.hasMoreTokens()) {
          testClasses.add(tokens.nextToken());
        }
      }
      // Now that we have the tests to run, run Jumble
      HashSet ignore = new HashSet();
      ignore.add("main");
      ignore.add("integrity");
      JumbleResult res = FastRunner
          .runJumble(className, new ArrayList(testClasses), ignore, true, true,
              true, false, true, true, true);
      new SeanResultPrinter(System.out).printResult(res);

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Usage: java jumble.fast.Jumble [CLASSNAME]");
    }
  }

}
