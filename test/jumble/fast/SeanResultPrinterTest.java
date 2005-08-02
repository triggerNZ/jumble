package jumble.fast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import jumble.Mutation;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Tests the corresponding class.
 * 
 * @author Tin Pavlinic
 */
public class SeanResultPrinterTest extends TestCase {
  ByteArrayOutputStream mBA;

  PrintStream mPrint;

  protected void setUp() throws Exception {
    mBA = new ByteArrayOutputStream();
    mPrint = new PrintStream(mBA);
  }
  
  public final void testOutput() throws Exception {
    JumbleResultPrinter printer = new SeanResultPrinter(mPrint);
    printer.printResult(new NormalResult());
    
    String out = mBA.toString();
    BufferedReader in = new BufferedReader(new StringReader(out));
    
    assertEquals("Mutating java.util.LinkedList", in.readLine());
    assertEquals("Tests: java.util.LinkedListTest", in.readLine());
    assertEquals("Mutation points = 3, unit test time limit 1.0s",
        in.readLine());
    assertEquals(".M FAIL", in.readLine());
    assertEquals("T", in.readLine());
  }

  private abstract class AbstractDummyResult extends JumbleResult {
    public Mutation[] getAllMutations() {
      Mutation[] muts = new Mutation
      [(getCovered() != null?getCovered().length:0)
          + (getMissed() != null?getMissed().length:0)
          + (getTimeouts()!=null?getTimeouts().length:0)];

      if(muts.length == 0) {
        return null;
      }
      
      int i = 0;
      if (getCovered() != null) {
        for (int j = 0; j < getCovered().length; j++, i++) {
          muts[i] = getCovered()[j];
        }
      }
      if (getMissed() != null) {
        for (int j = 0; j < getMissed().length; j++, i++) {
          muts[i] = getMissed()[j];
        }
      }
      if (getTimeouts() != null) {
        for (int j = 0; j < getTimeouts().length; j++, i++) {
          muts[i] = getTimeouts()[j];
        }
      }
      return muts;
    }
  }

  private class NormalResult extends AbstractDummyResult {
    public Mutation[] getMissed() {
      return new Mutation[] {new Mutation("FAIL", "java.util.LinkedList", 0)};
    }
    public Mutation[] getCovered() {
      return new Mutation[] {new Mutation("PASS", "java.util.LinkedList", 1)};
    }
    
    public Mutation [] getTimeouts() {
      return new Mutation[] {new Mutation("TIMEOUT", "java.util.LinkedList", 2)};
    }
    
    public String getClassName() {
      return "java.util.LinkedList";
    }
    
    public TestResult getInitialTestResult() {
      return new TestResult();
    }
    
    public String[] getTestClasses() {
      return new String[] {"java.util.LinkedListTest"};
    }
    
    public long getTimeoutLength() {
      return 1000;
    }
  }
  
  public static Test suite() {
    TestSuite suite = new TestSuite(SeanResultPrinterTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

}
