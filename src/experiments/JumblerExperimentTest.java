package experiments;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * Tests the JumblerExperiment class (inadequately) for
 * jumble testing.
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class JumblerExperimentTest extends TestCase {
  private JumblerExperiment mExp = new JumblerExperiment();

    
  public void testAdd() {
    assertEquals(3, mExp.add(2, 1));
    //assertEquals(-1, mExp.add(1,2));
	
  }
    
  public void testMultiply() {
    assertEquals(4, mExp.multiply(2, 2));
  }
    
  public static Test suite() {
    TestSuite suite = new TestSuite(JumblerExperimentTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
