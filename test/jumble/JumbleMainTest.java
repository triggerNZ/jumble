/*
 * Created on Apr 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;

import junit.framework.TestCase;
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
    
    public void testRunJumbleExperiments() throws Exception {
        JumbleResult res = JumbleMain.runJumble(
                "experiments.JumblerExperiment", 
                "experiments.JumblerExperimentTest", 
                true, true, sIgnore, 1000);
        assertEquals("experiments.JumblerExperiment", res.getClassName());
        assertEquals("experiments.JumblerExperimentTest", 
                res.getTestName());
        assertEquals(2, res.getFailed().length);
        assertEquals(2, res.getTimeouts().length);
        assertEquals(11, res.getAllMutations().length);
        
    }

}