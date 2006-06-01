package experiments;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * Test class used for testing Junble's use of the suite() method.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class JumblerExperimentSillySuiteTest extends TestCase {
  public static Test suite() {
    return JumblerExperimentTest.suite();
  }
}
