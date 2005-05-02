/*
 * Created on May 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.batch;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Tin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DependencyPairProducerTest extends TestCase {
    public DependencyPairProducer mProducer;
    
    public void setUp() {
        mProducer = new DependencyPairProducer();
        mProducer.setPackages(new String[] {"jumble", "jumble.batch", 
                "jumble.dependency", "jumble.util"});
    }
    
    public void testGetAllTests() throws Exception {
        Collection c = mProducer.getAllTests();
        
        assertTrue(c.contains("jumble.JumbleMainTest"));
        assertTrue(c.contains("jumble.MutaterTest"));
        assertTrue(c.contains("jumble.JumbleTestSuiteTest"));
        assertTrue(c.contains("jumble.batch.ClassTestPairTest"));
        assertTrue(c.contains("jumble.batch.DependencyPairProducerTest"));
        assertTrue(c.contains("jumble.batch.TextFilePairProducerTest"));
        assertTrue(c.contains("jumble.dependency.DependencyExtractorTest"));
        assertTrue(c.contains("jumble.util.JavaRunnerTest"));
        assertTrue(c.contains("jumble.util.RTSITest"));
    }
    
    public void testProducePairs() throws Exception {
        mProducer.addIgnoredPrefix("org.apache");
        commonTest();
    }
    
    public void testFileDependency() throws Exception {
        mProducer = new DependencyPairProducer("jumble/batch/packages.txt");
        commonTest();
    }
    
    private void commonTest() {
        ClassTestPair [] pairs = mProducer.producePairs();
        //It doesn't matter what order they are in so I will just add
        //them to a set and see if a few of them are there
        HashSet s = new HashSet();
        for(int i = 0; i < pairs.length; i++) {
            s.add(pairs[i]);
        }
        assertTrue(s.contains(pairs[0]));
        assertTrue(s.contains(new ClassTestPair("jumble.Mutater", "jumble.MutaterTest")));
        assertTrue(s.contains(new ClassTestPair("jumble.JumbleMain", "jumble.JumbleMainTest")));
    }
    public static Test suite() {
        TestSuite suite = new TestSuite(DependencyPairProducerTest.class);
        return suite;
      }

      public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
      }
}
