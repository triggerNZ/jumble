package jumble;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
//import com.reeltwo.util.net.Runner;

/**
 * Tests the corresponding class.
 *
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class JumblerTest extends TestCase {

  public JumblerTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(JumblerTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  /** Local new line convention */
  private static final String LS = System.getProperty("line.separator");
/* removed it for now to get it to recompile
  public void testHelp() throws Exception {
    ByteArrayOutputStream boso = new ByteArrayOutputStream();
    ByteArrayOutputStream bose = new ByteArrayOutputStream();
    final Process proc = Runtime.getRuntime().exec(new String[] {"java", "jumble.Jumbler", "--help"});
    final Runner runner = new Runner(null, boso, bose, proc, -1);
    assertEquals("", boso.toString());
    assertEquals("Usage: Jumbler [OPTION]... CLASS" + LS + LS + "Required flags: " + LS + "      CLASS                  Class to be mutated" + LS + "" + LS + "Optional flags: " + LS + "  -c, --count                Count possible mutation points" + LS + "  -x, --exclude=[METHOD[,]]+ Comma separated list of method names to ignore" + LS + "  -h, --help                 Print help on command-line flag usage." + LS + "  -k, --inlineconstants      Allow mutation of inline constants" + LS + "  -r, --returns              Allow mutation of return values" + LS + "      MUTATION-POINT         point to mutate" + LS + "      TEST-CLASS             corresponding test class" + LS + LS + "Mutation testing tool." + LS, bose.toString());
  }
*/
}
