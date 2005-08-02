package jumble.fast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jumble.dependency.DependencyExtractor;
import jumble.util.RTSI;
import junit.framework.TestCase;

/**
 * Class which ties the runtime test identification, dependency analysis and
 * Jumble stages of mutation testing.
 * 
 * @author Tin Pavlinic
 * 
 */
public class Jumble {

  /**
   * Main method. Finds all visible test classes, identifies those which use the
   * specified class and runs Jumble on them.
   * 
   * @param args the command line arguments. Should contain only one element
   * - the name of the class to Jumble. 
   */
  public static void main(String[] args) {
    try {
      String className = args[0];
      Class.forName(className);
      Set testClasses = new HashSet();
      
      System.out.println("Finding tests...");
      
      Collection allPackages = RTSI.getAllVisiblePackages();      
      Iterator it = allPackages.iterator();
      
      // Go through all the packages, get all the tests and get all the 
      //classes associated with each test
      while (it.hasNext()) {
        String packageName = (String)it.next();       
        Collection tests = RTSI.find(packageName, TestCase.class);        
        Iterator testIt = tests.iterator();      
        while (testIt.hasNext()) {
          String testClass = (String)testIt.next();
          DependencyExtractor extractor = new DependencyExtractor();
          //ignore junit stuff, but not the samples
          extractor.getIgnoredPackages().add("junit.awtui");
          extractor.getIgnoredPackages().add("junit.framework");
          extractor.getIgnoredPackages().add("junit.extensions");
          extractor.getIgnoredPackages().add("junit.runner");
          extractor.getIgnoredPackages().add("junit.swingui");
          extractor.getIgnoredPackages().add("junit.textui");
          
          Collection dependentClasses = 
            extractor.getAllDependencies(testClass, true);
          
          //Now if the class is a dependency, then we should do that test
          if (dependentClasses.contains(className)) {
            testClasses.add(testClass);
          }
        }
      }
      
      System.out.println("DONE: " + testClasses);
      
      //Now that we have the tests to run, run Jumble
      HashSet ignore = new HashSet();
      ignore.add("main");
      ignore.add("integrity");
      JumbleResult res = FastRunner.runJumble(className, 
          new ArrayList(testClasses), ignore, true, true, true);
      new SeanResultPrinter(System.out).printResult(res);
      
      
    } catch (Exception e) {
      System.out.println("Usage: java jumble.fast.Jumble [CLASSNAME]");
    }
  }

}
