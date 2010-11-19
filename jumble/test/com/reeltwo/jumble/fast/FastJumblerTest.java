package com.reeltwo.jumble.fast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.reeltwo.jumble.util.JavaRunner;

import experiments.JumblerExperimentTest;

/**
 * Tests the corresponding class.
 *
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class FastJumblerTest extends TestCase {
  private String mFileName;

  @Override
public final void setUp() throws Exception {
    // Unique filename
    mFileName = "tmpTest" + System.currentTimeMillis() + ".dat";

    TimingTestSuite suite = new TimingTestSuite(getClass().getClassLoader(), new String[] {JumblerExperimentTest.class.getName() });
    suite.run(new TestResult());
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(mFileName));
    out.writeObject(suite.getOrder(true));
    out.close();
  }

  @Override
public final void tearDown() {
    // System.err.println(mFileName);
    assertTrue(new File(mFileName).delete());
  }

  public void testMain() throws Exception {
    JavaRunner runner = new JavaRunner("com.reeltwo.jumble.fast.FastJumbler", new String[] {"experiments.JumblerExperiment", "-c",
        System.getProperty("java.class.path"), "-s", "0", "-r", "-k", "-i", mFileName, });
    Process p = runner.start();

    final BufferedReader outReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    try {
      String line = outReader.readLine();
      assertNotNull(line);
      assertEquals("START", line);
      line = outReader.readLine();
      assertNotNull(line);
      assertTrue("Unexpected output: " + line, line.startsWith(FastJumbler.INIT_PREFIX));
      line = outReader.readLine();
      assertNotNull(line);
      assertTrue("Unexpected output: " + line, line.startsWith(FastJumbler.PASS_PREFIX));
      p.destroy();
    } finally {
      outReader.close();
    }
  }

  public void testMain2() throws Exception {
    JavaRunner runner = new JavaRunner("com.reeltwo.jumble.fast.FastJumbler", new String[] {"experiments.JumblerExperiment", "-c",
        System.getProperty("java.class.path"), "-s", "0", "-l", "1", "-r", "-k", "-i", mFileName, });
    Process p = runner.start();

    final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    try {
      String line;

      line = reader.readLine();
      assertEquals("START", line);
      line = reader.readLine();
      assertEquals("INIT: experiments.JumblerExperiment:21: negated conditional", line);
      line = reader.readLine();
      assertTrue(line.startsWith("PASS: experiments.JumblerExperiment"));
      String[] tests = line.split(";");
      for (String test : tests) {
        String segs[] = test.split("/");
        if (Integer.parseInt(segs[2]) == 0) {
          assertEquals("testAdd", segs[1]);
        }
      }
      line = reader.readLine();
      assertEquals(FastJumbler.SIGNAL_MAX_REACHED, line);
    } finally {
      reader.close();
    }
  }

  public final void testSaveCache() throws Exception {
    File f = new File(System.getProperty("user.home"), ".com.reeltwo.jumble-cache.dat");
    assertTrue(!f.exists() || f.delete());

    ArrayList < String > tests = new ArrayList < String > ();
    tests.add("experiments.JumblerExperimentTest");
    FastRunner runner = new FastRunner();
    runner.runJumble("experiments.JumblerExperiment", tests, null);

    assertTrue(f.exists());

    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
    FailedTestMap map = (FailedTestMap) ois.readObject();
    ois.close();
    assertTrue(f.delete());
    // System.err.println(map);
    assertEquals("testAdd", map.getLastFailure("experiments.JumblerExperiment", "add(II)I", 0));
    assertEquals("testMultiply", map.getLastFailure("experiments.JumblerExperiment", "multiply(II)I", 1));
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(FastJumblerTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
