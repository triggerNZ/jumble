package jumble.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test corresponding class.
 *
 * @author Tin
 * @version $Revision$
 */
public class IOThreadTest extends TestCase {

  public final void testIOThread() throws Exception {
    JavaRunner runner = new JavaRunner("jumble.util.IO1");
    Process p = runner.start();
    IOThread iot = new IOThread(p.getInputStream());
    iot.start();
    String cur;

    // Wait for output to appear
    while ((cur = iot.getNext()) == null) {
      Thread.sleep(100);
    }

    assertEquals("test", cur);

    for (int i = 0; i < 10; i++) {
      assertEquals(null, iot.getNext());
    }
    p.destroy();
  }

  public void testJumbleMultiRunner() throws Exception {
    JavaRunner runner = new JavaRunner("jumble.JumbleMultiRunner");
    runner
        .setArguments(new String[] {"-r -k -i experiments.JumblerExperiment "
            + "experiments.JumblerExperimentTest "
            + "experiments.JumblerExperimentEmptyTest "
            + "experiments.JumblerExperimentSecondTest 0"});

    Process p = runner.start();
    IOThread iot = new IOThread(p.getInputStream());
    iot.start();

    for (int i = 0; i < 10; i++) {
      Thread.sleep(500);
    }
    p.destroy();
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(IOThreadTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
