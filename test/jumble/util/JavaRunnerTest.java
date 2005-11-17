package jumble.util;

import java.util.Properties;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Tests the corresponding class
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class JavaRunnerTest extends TestCase {
  Process mProcess = null;
  
  public void tearDown() {
    if (mProcess != null) {
      mProcess.destroy();
      mProcess = null;
    }
  }
  
  public void testStart() throws IOException{
    Properties props = System.getProperties();
    mProcess = new JavaRunner("jumble.util.DisplayEnvironment").start();
    BufferedReader out = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
    BufferedReader err = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));

    if (err.readLine() != null) {
      fail();
    }
        
    assertEquals("java.home " + props.getProperty("java.home"), out.readLine() );
    assertEquals("java.class.path " + props.getProperty("java.class.path"), out.readLine());
  }

  public void testArguments() throws Exception {
    mProcess = new JavaRunner("jumble.util.DisplayArguments", new String[] {"one", "two", "three"}).start();
        
    BufferedReader out = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
        
    assertEquals("one", out.readLine());
    assertEquals("two", out.readLine());
    assertEquals("three", out.readLine());
    assertEquals(null, out.readLine());
  }

  public void testConstructor() {
    assertEquals(0, new JavaRunner("jumble.util.DisplayEnvironment").getArguments().length);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(JavaRunnerTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
