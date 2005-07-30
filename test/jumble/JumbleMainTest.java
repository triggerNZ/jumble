/*
 * Created on Apr 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashSet;

/**
 * @author Tin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JumbleMainTest extends TestCase {
    static HashSet sIgnore = new HashSet();
    static {
        sIgnore.add("integrity");
        sIgnore.add("main");
    }
    
    public final void testRunJumbleExperiments() throws Exception {
        ExtendedJumbleResult res = JumbleMain.runJumble(
                "experiments.JumblerExperiment", 
                "experiments.JumblerExperimentTest", 
                true, true, true, sIgnore, 1000);
        
        assertEquals("experiments.JumblerExperiment", res.getClassName());
        assertEquals("experiments.JumblerExperimentTest", 
                res.getTestName());
        assertEquals(3, res.getFailed().length);
        assertEquals(1, res.getTimeouts().length);
        assertEquals(12, res.getAllMutations().length);
        
        
    }
    
    public final void testGetTestName() {
        String class1 = "java.lang.String";
        String class2 = "java.util.AbstractMap";
        
        assertEquals("java.lang.StringTest", JumbleMain.getTestName(class1));
        assertEquals("java.util.DummyMapTest", JumbleMain.getTestName(class2));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(JumbleMainTest.class);
        return suite;
      }
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
      }

}
