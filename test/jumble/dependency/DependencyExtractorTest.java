/*
 * Created on May 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.dependency;

import java.util.Collection;
import java.util.Set;

import jumble.JumbleTestSuiteTest;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/** Tests the DependencyExtractor
 * @author Tin Pavlinic
 */
public class DependencyExtractorTest extends TestCase {
    private DependencyExtractor extractor;
    public void setUp() {
        extractor = 
            new DependencyExtractor("jumble.dependency.DT1");
        Set ignore = extractor.getIgnoredPackages();
        ignore.add("org.apache");
    }
    
    public void tearDown() {
        extractor.setClassName("jumble.dependency.DT1");
    }
    
    public void testDT1() {
        Collection classes = extractor.getAllFilteredDependencies();
        assertEquals(2, classes.size());
    }
    public void testDT2() {
        extractor.setClassName("jumble.dependency.DT2");
        Collection classes = extractor.getAllFilteredDependencies();
        assertEquals(3, classes.size());
    }
    public void testSilly() {
        extractor.setClassName("[[C");
        Collection classes = extractor.getAllFilteredDependencies();
        assertEquals(0, classes.size());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(DependencyExtractorTest.class);
        return suite;
      }

      public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
      }
}
