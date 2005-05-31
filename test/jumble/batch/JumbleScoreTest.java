/*
 * Created on May 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.batch;

import java.util.HashSet;

import jumble.JumbleResult;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the corresponding class
 * @author Tin Pavlinic
 */
public class JumbleScoreTest extends TestCase {  
    JumbleResult fail;
    JumbleResult test1;
    JumbleResult test2;
    JumbleResult empty;
    AggregateJumbleScore score;
    
    public void setUp() throws Exception {
        score = new AggregateJumbleScore("experiments.JumblerExperiment");
        HashSet ignore = new HashSet();
        ignore.add("main");
        ignore.add("integrity");
        
        JumbleResult [] temp = JumbleBatchRunner.runBatch
        	(new ClassTestPair [] {
                new ClassTestPair("experiments.JumblerExperiment", 
                        "experiments.FailingTest"),
                new ClassTestPair("experiments.JumblerExperiment", 
                        "experiments.JumblerExperimentTest"),
                new ClassTestPair("experiments.JumblerExperiment", 
                        "experiments.JumblerExperimentSecondTest"),
                new ClassTestPair("experiments.JumblerExperiment", 
                        "experiments.JumblerExperimentEmptyTest")
        }, true, true, true, ignore, false);
       fail = temp[0];
       test1 = temp[1];
       test2 = temp[2];
       empty = temp[3];
       score.addResult(fail);
       score.addResult(test1);
       score.addResult(test2);
       score.addResult(empty);
    }
    
    public void testTotalScore() {
       int failed = 0;
       assertEquals(1, score.getFailedTests().length);
       //count the failed tests
       for(int i = 0; i < score.getAllMutations().length; i++) {
           if(score.getAllMutations()[i].isFailed())
               failed++;
       }
       assertEquals(1, failed);
       
    }
    
    public void testToString() {
        String s = score.toString();
        
        assertEquals(
                "Class: experiments.JumblerExperiment\n" + 
                ".......M.T..\n\n" + 
                "Missed mutations:\n"
                , s);
    }
    
    public void testClassName() {
        assertEquals("experiments.JumblerExperiment", score.getClassName());
    }
    
    public void testGetMutationCount() {
        assertEquals(12, score.getMutationCount());
    }
    
    public void testGetAllTests() {
        String [] all = score.getAllTests();
        assertEquals(4, all.length);
        assertEquals("experiments.FailingTest", all[0]);
        assertEquals("experiments.JumblerExperimentTest", all[3]);
        assertEquals("experiments.JumblerExperimentSecondTest", all[2]);
        assertEquals("experiments.JumblerExperimentEmptyTest", all[1]);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(JumbleScoreTest.class);
        return suite;
      }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
      }
    
    
}
