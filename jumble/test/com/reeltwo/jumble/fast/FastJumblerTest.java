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

    BufferedReader outReader = new BufferedReader(new InputStreamReader(p.getInputStream()));

    String line = outReader.readLine();
    assertEquals("START", line);
    line = outReader.readLine();
    assertTrue("Unexpected output: " + line, line.startsWith(FastJumbler.INIT_PREFIX));
    line = outReader.readLine();
    assertTrue("Unexpected output: " + line, line.startsWith(FastJumbler.PASS_PREFIX));
    outReader.close();
    p.destroy();
  }

  public void testMain2() throws Exception {
    JavaRunner runner = new JavaRunner("com.reeltwo.jumble.fast.FastJumbler", new String[] {"experiments.JumblerExperiment", "-c",
        System.getProperty("java.class.path"), "-s", "0", "-l", "1", "-r", "-k", "-i", mFileName, });
    Process p = runner.start();

    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

    String line;

    line = reader.readLine();
    assertEquals("START", line);
    line = reader.readLine();
    assertEquals("INIT: experiments.JumblerExperiment:21: negated conditional", line);
    line = reader.readLine();
    assertEquals("PASS: experiments.JumblerExperiment:add(II)I:0:testAdd", line);
    line = reader.readLine();
    assertEquals(FastJumbler.SIGNAL_MAX_REACHED, line);
  }

  public final void testSaveCache() throws Exception {
    File f = new File(System.getProperty("user.home"), ".com.reeltwo.jumble-cache.dat");
    assertTrue(!f.exists() || f.delete());

    ArrayList<String> tests = new ArrayList<String>();
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
