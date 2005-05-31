/*
 * Created on 18/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Tin Pavlinic
 */
public class JumbleTaskTest extends TestCase {
    private JumbleTask mTask;
    
    public void setUp() {
        mTask = new JumbleTask("experiments.JumblerExperiment");
        
        mTask.setMutateIncrements(true);
        mTask.setMutateInlineConstants(true);
        mTask.setMutateReturnValues(true);
        
        mTask.addTest("experiments.JumblerExperimentTest");
        mTask.addTest("experiments.JumblerExperimentEmptyTest");
        mTask.addTest("experiments.JumblerExperimentSecondTest");
    }
    public void testTask() throws Exception {
        mTask.run();
    }
}
