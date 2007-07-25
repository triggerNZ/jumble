package experiments;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
/**
 * Tests the JumblerExperiment class (inadequately) for
 * com.reeltwo.jumble testing. Uses JUnit 4.
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class JumblerExperimentJUnit4Test {
  private JumblerExperiment mExp = new JumblerExperiment();

    
  @Test public void testAdd() {
    assertEquals(3, mExp.add(2, 1));
  }
    
  @Test public void testMultiply() {
    assertEquals(4, mExp.multiply(2, 2));
  }
    
  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(JumblerExperimentJUnit4Test.class);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
