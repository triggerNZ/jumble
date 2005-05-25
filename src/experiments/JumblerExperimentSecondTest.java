/*
 * Created on May 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package experiments;

import junit.framework.TestCase;

/** Silly test for jumble testing
 * @author Tin Pavlinic
 */
public class JumblerExperimentSecondTest extends TestCase {

    public void testAdd() {
        JumblerExperiment exp = new JumblerExperiment();
        assertEquals(-1, exp.add(1,2));
    }
}
