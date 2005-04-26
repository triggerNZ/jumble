/*
 * Created on Apr 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.batch;

import jumble.JumbleTestSuiteTest;
import jumble.batch.ClassTestPair;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Tin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClassTestPairTest extends TestCase {
    private ClassTestPair ctp;
    
    public void setUp() {
        ctp = new ClassTestPair(
                "experiments.JumblerExperiment", 
                "experiments.JumblerExperimentTest");
    }
    public void testValid() {     
        assertTrue(ctp.isValid());
    }
    
    public void testInvalid() {
        ClassTestPair ctp1 = new ClassTestPair(
                "ImaginaryClass", 
                "experiments.JumblerExperimentTest");
        
        assertFalse(ctp1.isValid());
        
        ClassTestPair ctp2 = new ClassTestPair(
                "java.util.HashSet", 
                "ImaginaryTest");
        assertFalse(ctp2.isValid());
    }
    
    public void testNames() {
        assertEquals("experiments.JumblerExperiment", ctp.getClassName());
        assertEquals("experiments.JumblerExperimentTest", ctp.getTestName());
    }
    
    public void testEquals() {
        assertEquals(ctp, ctp);
        assertEquals(new ClassTestPair(
                "experiments.JumblerExperiment", 
                "experiments.JumblerExperimentTest"), ctp);
        assertFalse(ctp.equals(new ClassTestPair(
                "experiments.JumblerExperiment", 
                "experiments.JumblerTest")));
        assertFalse(ctp.equals(new StringBuffer()));
    }
    
    public void testHashcode() {
        assertEquals(854912590, ctp.hashCode());
    }
    
    public void testToString() {
        assertEquals("[experiments.JumblerExperiment, experiments.JumblerExperimentTest]",
                ctp.toString());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ClassTestPairTest.class);
        return suite;
      }
}
