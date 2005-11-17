package experiments;

import junit.framework.TestCase;

/**
 * Silly test for jumble testing
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class JumblerExperimentSecondTest extends TestCase {

  public void testAdd() {
    JumblerExperiment exp = new JumblerExperiment();
    assertEquals(-1, exp.add(1, 2));
  }
}
