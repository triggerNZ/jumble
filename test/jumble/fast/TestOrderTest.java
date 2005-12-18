package jumble.fast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.bcel.util.ClassLoader;

import experiments.JumblerExperimentSecondTest;
import experiments.JumblerExperimentTest;

/**
 * Tests the corresponding class.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class TestOrderTest extends TestCase {
  private TestOrder mOrder;

  public void setUp() {
    Class[] classes = new Class[] {JumblerExperimentTest.class,
        JumblerExperimentSecondTest.class };

    long[] runtimes = new long[] {300, 200, 100 };
    mOrder = new TestOrder(classes, runtimes);

  }

  public void tearDown() {
    mOrder = null;
  }

  public final void testGetTestCount() {
    assertEquals(3, mOrder.getTestCount());
  }

  public final void testGetTestIndex() {
    assertEquals(2, mOrder.getTestIndex(0));
    assertEquals(1, mOrder.getTestIndex(1));
    assertEquals(0, mOrder.getTestIndex(2));
  }

  public final void testGetTestClasses() {
    String[] classes = mOrder.getTestClasses();
    assertEquals("experiments.JumblerExperimentTest", classes[0]);
    assertEquals("experiments.JumblerExperimentSecondTest", classes[1]);
    assertEquals(2, classes.length);
  }

  public final void testSavingAndLoading() throws Exception {
    // Write the object
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
        "loadAndSaveTestOrder.tmp"));
    out.writeObject(mOrder);
    out.close();
    // Read the object
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(
        "loadAndSaveTestOrder.tmp"));
    mOrder = (TestOrder) in.readObject();
    in.close();
    // delete the temporary file
    assertTrue(new File("loadAndSaveTestOrder.tmp").delete());

    // run the tests again
    testGetTestCount();
    testGetTestIndex();
    testGetTestClasses();
  }

  public final void testChangeClassLoader() throws Exception {
    Object newOrder = mOrder.clone(new ClassLoader());
    assertNotSame(mOrder.getClass(), newOrder.getClass());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(TestOrderTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
