/*
 * Created on Apr 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.batch;

import jumble.JumbleTestSuiteTest;
import jumble.batch.ClassTestPair;
import jumble.batch.TextFilePairProducer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
/**
 * @author Tin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TextFilePairProducerTest extends TestCase {

    public void testProducePairs() throws IOException {
        TextFilePairProducer pp = new TextFilePairProducer("jumble/batch/pairs.txt");
        
        ClassTestPair [] pairs = pp.producePairs();
        
        assertEquals(5, pairs.length);
        
        assertEquals("experiments.JumblerExperiment",  pairs[0].getClassName());
        assertEquals("experiments.JumblerExperimentTest",  pairs[0].getTestName());
        
        assertEquals("experiments.JumblerExperiment", pairs[1].getClassName());
        assertEquals("experiments.FailingTest",  pairs[1].getTestName());
        
        assertEquals("junit.samples.money.MoneyBag",  pairs[2].getClassName());
        assertEquals("junit.samples.money.MoneyTest",  pairs[2].getTestName());
        
        assertEquals("junit.samples.money.IMoney",  pairs[3].getClassName());
        assertEquals("junit.samples.money.MoneyTest",  pairs[3].getTestName());
        
        assertEquals("junit.samples.money.Money",  pairs[4].getClassName());
        assertEquals("junit.samples.money.MoneyTest",  pairs[4].getTestName()); 
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TextFilePairProducerTest.class);
        return suite;
      }

}
