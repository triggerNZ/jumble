package jumble.fast;

import experiments.JumblerExperimentTest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import jumble.mutation.Mutater;
import jumble.mutation.MutatingClassLoader;
import jumble.util.JavaRunner;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Tests the corresponding class.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class FastJumblerTest extends TestCase {
  private String mFileName;

  public final void setUp() throws Exception {
    // Unique filename
    mFileName = "tmpTest" + System.currentTimeMillis() + ".dat";

    TimingTestSuite suite = new TimingTestSuite(
        new Class[] {JumblerExperimentTest.class });
    suite.run(new TestResult());
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
        mFileName));
    out.writeObject(suite.getOrder(true));
    out.close();
  }

  public final void tearDown() {
    assertTrue(new File(mFileName).delete());
  }

  public void testMain() throws Exception {
    JavaRunner runner = new JavaRunner("jumble.fast.FastJumbler", new String[] {
                                         "experiments.JumblerExperiment", "-s", "0", mFileName, "-r", "-k", "-i" });
    Process p = runner.start();

    BufferedReader reader = new BufferedReader(new InputStreamReader(p
        .getInputStream()));
    String line = reader.readLine();
    assertEquals("START", line);
    line = reader.readLine();
    assertTrue("Unexpected output: " + line, line.startsWith("PASS"));
    reader.close();
    p.destroy();
  }

  /**
   * Test for a bug that was found - it seems that the classes are getting
   * corrupted.
   */
  public final void testRunMethodExistence() throws Exception {
    try {
      final ClassLoader loader = new MutatingClassLoader(
          "experiments.JumblerExperiment", new Mutater(0));
      final Class clazz = loader.loadClass("jumble.fast.JumbleTestSuite");
      clazz.getMethod("run", new Class[] {
          loader.loadClass("jumble.fast.TestOrder"),
          loader.loadClass("jumble.fast.FailedTestMap"), String.class,
          String.class, int.class, boolean.class });
    } catch (NoSuchMethodException e) {
      fail();
    }
  }


  public final void testSaveCache() throws Exception {
    File f = new File("jumble-cache.dat");
    assertTrue(!f.exists() || f.delete());

    ArrayList tests = new ArrayList();
    tests.add("experiments.JumblerExperimentTest");
    FastRunner runner = new FastRunner();
    runner.runJumble("experiments.JumblerExperiment", tests);

    assertTrue(f.exists());

    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
    FailedTestMap map = (FailedTestMap) ois.readObject();
    ois.close();
    assertTrue(f.delete());

    assertEquals("testAdd", map.getLastFailure("experiments.JumblerExperiment",
        "add(II)I", 0));
    assertEquals("testMultiply", map.getLastFailure(
        "experiments.JumblerExperiment", "multiply(II)I", 0));
  }


  public static Test suite() {
    TestSuite suite = new TestSuite(FastJumblerTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
