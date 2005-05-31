/*
 * Created on 18/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;

import jumble.batch.ClassTestPair;
import jumble.util.IOThread;
import jumble.util.JavaRunner;

/** Class which mutates a given class and runs tests on it.
 * @author Tin Pavlinic
 */
public class JumbleTask {
    private String mClassName;
    private Collection mTests = new HashSet();
    private boolean mMutateIncrements = false;
    private boolean mMutateInlineConstants = false;
    private boolean mMutateReturnValues = false;
    
    /**
     * Constructs a JumbleTask for the class with the given
     * className and no associated tests.
     * @param className name of the class to jumble
     */
    public JumbleTask(String className) {
        mClassName = className;
    }
    /**
     * Constructs a JumbleTask for the given ClassTestPair
     * set. 
     * @param pairs the array of Class-Test pairs to use
     * All elements must have the same class name.
     */
    public JumbleTask(ClassTestPair [] pairs) {
        for(int i = 0; i < pairs.length; i++) {
            if(getClassName() == null) {
                setClassName(pairs[i].getClassName());
            } else if(getClassName().equals(pairs[i].getClassName())) {
                addTest(pairs[i].getTestName());
            } else {
                throw new RuntimeException("Inconsistent test name");
            }
        }
    }
    /**
     * Gets the name of the class to jumble.
     * @return the class name.
     */
    public String getClassName() {
        return mClassName;
    }
    /**
     * Sets the name of the class to jumble.
     * @param name the new class name
     */
    public void setClassName(String name) {
        mClassName = name;
    }
    /**
     * Adds a test to the test set
     * @param testName the name of the test class to add
     */
    public void addTest(String testName) {
        mTests.add(testName);
    }
	 /**
	  * Returns all tests that will be run on this class.
	  * @return all tests
	  */
    public String [] getAllTests() {
        return (String [])mTests.toArray(new String[0]);
    }
    /**
     * Runs a jumble test on the class.
     * @return the results of the jumble test.
     * @throws IOException if there is a problem with running the
     * Jumble process
     */
    public JumbleScore run() throws IOException, InterruptedException {
        //First get the number of mutation points
        final Mutater m = new Mutater(0);

        m.setMutateIncrements(getMutateIncrements());
        m.setMutateInlineConstants(getMutateInlineConstants());
        m.setMutateReturnValues(getMutateReturnValues());
        
        final int mutationCount = m.countMutationPoints(getClassName());
        
        final StringBuffer BASE_ARG = new StringBuffer(
	        (getMutateReturnValues()?"-r ":"")  + 
	       	(getMutateInlineConstants()?"-k ":"") +
	       	(getMutateIncrements()?"-i ":"") +
	       	getClassName());
        
        final String [] tests = getAllTests();
        for(int i = 0; i < tests.length; i++) {
            BASE_ARG.append(" " + tests[i]);
        }
        //now we want to time the tests initially
        final long testBefore = System.currentTimeMillis();
        for(int i = 0; i < tests.length; i++) {
            JavaRunner testRunner = 
                new JavaRunner("junit.textui.TestRunner", 
                        new String[] {tests[i]});
            Process p = testRunner.start();
            p.waitFor();
        }
        long testAfter =  System.currentTimeMillis();
        final long timeout = (testAfter - testBefore > 800)
        					?(testAfter - testBefore):800; 
        
        System.out.println("Timeout: " + timeout);
        
        int curTest = 0;
        JavaRunner runner = new JavaRunner("jumble.JumbleMultiRunner", 
                new String[] {BASE_ARG.toString() + " " + curTest});
        System.out.println(runner);
      
        Process p = runner.start();
        
        IOThread ioThread = new IOThread(p.getErrorStream());
        ioThread.start();
        
        while(curTest < mutationCount) {
            long before = System.currentTimeMillis();
            long after = before;
            
            do {
                String cur = ioThread.getNext();
                if(cur == null) {
                    after = System.currentTimeMillis();
                    Thread.sleep(500);
                } else {
                    try {
                        System.out.println(cur);
                        if(cur.equals("PASS"))
                            System.out.print(".");
                        else
                            System.out.print("M");
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    curTest++;
                    before = System.currentTimeMillis();
                }
            } while(after - before < timeout && curTest < mutationCount);
        
            if(curTest < mutationCount) {
               System.out.print("T");
               curTest++;
               BufferedReader reader = new BufferedReader(
                       new InputStreamReader(p.getInputStream()));
               
               p.destroy();
                  
               runner.setArguments(new String [] {BASE_ARG.toString() + " " + curTest});
                   
               p = runner.start();
               System.out.println(runner);
               
               ioThread = new IOThread(p.getErrorStream());
               ioThread.start();
            }
        }
        
        return null;
    }
    
    
    public void setMutateIncrements(boolean b) {
        mMutateIncrements = b;
    }
    
    public boolean getMutateIncrements() {
        return mMutateIncrements;
    }
    
    public void setMutateInlineConstants(boolean b) {
        mMutateInlineConstants = b;
    }
    
    public boolean getMutateInlineConstants() {
        return mMutateInlineConstants;
    }
    
    public void setMutateReturnValues(boolean b) {
        mMutateReturnValues = b;
    }
    public boolean getMutateReturnValues() {
        return mMutateReturnValues;
    }
}
