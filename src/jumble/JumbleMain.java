
package jumble;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import jumble.util.IOThread;
import jumble.util.JavaRunner;
import jumble.util.Utils;

/** Class for running Jumble on a single class with a single test.
 * 
 * @author Tin Pavlinic
 */
public class JumbleMain {
    /** Runs jumble on className and testName. The test must pass or we
     * will get silly results.
     * @param className name of class to mutate
     * @param testName name of corresponding test class
     * @param returnVals flag indicating whether to mutate 
     *        return values 
     * @param inlineConstants flag indicating whether to mutate
     * 		  inline constants
     * @param ignore Set of methods to ignore
     * @param timeout timeout for tests passing
     * @return the results of this jumble run
     * @throws IOException
     * @throws InterruptedException
     */
    public static JumbleResult runJumble(final String className, final String testName, 
            boolean returnVals, boolean inlineConstants, boolean increments,
            Set ignore, int timeout) 
    	throws IOException, InterruptedException {
        
        final String BASE_ARG = (returnVals?"-r ":"")  + 
       	(inlineConstants?"-k ":"") +
       	(increments?"-i ":"") +
       	className + " " + testName + " ";
        
        final List results = new ArrayList();
            
        final Mutater m = new Mutater(0);
        m.setIgnoredMethods(ignore);
        m.setMutateInlineConstants(inlineConstants);
        m.setMutateReturnValues(returnVals);
        m.setMutateIncrements(increments);
            
        int count = m.countMutationPoints(className);
            
        int curTest = 0;
        
        JavaRunner runner = new JavaRunner("jumble.JumbleRunner", 
                new String[] {BASE_ARG + curTest});
      
        Process p = runner.start();

        IOThread ioThread = new IOThread(p.getInputStream());
           ioThread.start();
            
        while(curTest < count) {
            long before = System.currentTimeMillis();
            long after = before;
            
            do {
                String cur = ioThread.getNext();
                if(cur == null) {
                    after = System.currentTimeMillis();
                    Thread.sleep(500);
                } else {
                    try {
                        results.add(new Mutation(cur, className, curTest));
                    } catch(Exception e) {
                        System.err.println(className + " - " + testName);
                        e.printStackTrace();
                    }
                    curTest++;
                    before = System.currentTimeMillis();
                }
            } while(after - before < timeout && curTest < count);
        
            if(curTest < count) {
               results.add(new Mutation("TIMEOUT", className, curTest));
               curTest++;
               BufferedReader reader = new BufferedReader(
                       new InputStreamReader(p.getErrorStream()));
               
               p.destroy();
                  
               runner.setArguments(new String [] {BASE_ARG + curTest});
                    
               p = runner.start();

               ioThread = new IOThread(p.getInputStream());
               ioThread.start();
            }
        }
        JumbleResult res = new JumbleResult() {
            private List mResults = results;
            private Mutation [] mFailed = null;
            private Mutation [] mPassed = null;
            private Mutation [] mTimedOut = null;
            private String mClassName = className;
            private String mTestName = testName;
            
            public boolean testFailed() {
                return false;
            }
            
            public String getClassName() {
                return mClassName;
            }
            public String getTestName() {
                return mTestName;
            }
            
            public Mutation [] getAllMutations() {
                return (Mutation [])mResults.toArray(new Mutation[mResults.size()]);
            }
            
            public int getMutationCount() {
                return mResults.size();
            }
              
            public Mutation [] getFailed() {
                if(mFailed != null)
                    return mFailed;
                   
                ArrayList failed = new ArrayList();
                for(int i = 0; i < mResults.size(); i++) {
                    Mutation m = (Mutation)mResults.get(i);
                    if(m.isFailed())
                        failed.add(m);
                }
                   
                mFailed = (Mutation [])failed.toArray(new Mutation[failed.size()]);
                return mFailed;
            }
                
            public Mutation [] getPassed() {
                if(mPassed != null)
                    return mPassed;
                 
                ArrayList passed = new ArrayList();
                for(int i = 0; i < mResults.size(); i++) {
                    Mutation m = (Mutation)mResults.get(i);
                    if(m.isPassed())
                        passed.add(m);
                    }
                    
                    mPassed =  (Mutation [])passed.toArray(new Mutation[passed.size()]);
                    return mPassed;
                }
                
                public Mutation [] getTimeouts() {
                    if(mTimedOut != null)
                        return mTimedOut;
                    
                    ArrayList timedOut = new ArrayList();
                    for(int i = 0; i < mResults.size(); i++) {
                        Mutation m = (Mutation)mResults.get(i);
                        if(m.isTimedOut())
                            timedOut.add(m);
                    }
                    
                    mTimedOut =  (Mutation [])timedOut.toArray(new Mutation[timedOut.size()]);
                    return mTimedOut;
                }
                
                public String toString() {
                    return getClassName() + ": " + getTestName() + "\n"
                    + "Passed: " + getPassed().length + " Failed: " 
                    + getFailed().length + " Timed out: " + 
                    getTimeouts().length;
                }
                
            };

            return res;
    }
    /** Times the running of a test 
     * @param testName name of test to run
     * @param output flag indicating whether to show the results
     * 		  of the test
     * @return the amount of time in milliseconds that the test took
     * @throws Exception
     */
    public static int getTimeOut(String testName, boolean output) throws Exception {
        //time the test so we know when to time out
        final int TIMEOUT;
        
        
        //capture the output
        PrintStream oldOut = System.out;
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        System.setOut(new PrintStream(log));
        long before = System.currentTimeMillis();
        junit.textui.TestRunner.run(Class.forName(testName)); 
        long after = System.currentTimeMillis();
        String res = log.toString();
        System.setOut(oldOut);
        
        if(output) {
            System.out.println(res);
        }
        
        if(after - before < 800)
            TIMEOUT = 800;
        else
            TIMEOUT = (int)((after - before)*2);
       
        if(res.indexOf("F") > 0 || res.indexOf("E") > 0)
            throw new TestFailedException();
        
        if(output) {
            System.out.println("TIMEOUT: " + TIMEOUT);
        }
        
        
        return TIMEOUT;
    }
    /** Main method - runs jumble on a class/test pair */
    public static void main(String [] args) throws Exception {
        boolean compatability = Utils.getFlag('c', args);
        try {
	        //For now, just get the test name - everything else is hardcoded
	       String className = Utils.getNextArgument(args);
	       String testName;
	       try {
	           testName = Utils.getNextArgument(args);
	       } catch(Exception e) {
	           testName = className + "Test";
	       }
	       Utils.checkForRemainingOptions(args);
	       
	       final int TIMEOUT;
	       if(!compatability) {
		       System.out.println("Running test on unmodified class:");
		       
		       try {
		           TIMEOUT = getTimeOut(testName, true);
		       } catch(TestFailedException e) {
		           System.out.println("Test failed");
		           return;
		       }
	       }  else {
	           try {
	               TIMEOUT = getTimeOut(testName, false);
	           } catch(TestFailedException e) {
	               System.out.println("Score: 0 (TEST CLASS IS BROKEN)");
	               return;
	           }
	           System.out.println("Mutating " + className);
	           if(Class.forName(className).isInterface()) {
	               System.out.println("Score: 100 (INTERFACE)");
	               return;
	           }
	           System.out.println("Test: " + testName);
	       }
	       HashSet ignore = new HashSet();
	       ignore.add("main");
	       ignore.add("integrity");
	       if(!compatability) {
	           System.out.println("Jumbling...\n");
	       }
	       
	       JumbleResult res = runJumble(className, testName, true, true, true, ignore, TIMEOUT);
	       if(!compatability) {
		       Mutation [] mut = res.getAllMutations();
		       for(int i = 0; i < mut.length; i++) {
		           if(mut[i].isPassed())
		               System.out.print(".");
		           else if(mut[i].isFailed())
		               System.out.print("M");
		           else if(mut[i].isTimedOut())
		               System.out.print("T");
		           else throw new RuntimeException();
		       }
		       
		       Mutation [] failed = res.getFailed();
		       System.out.println("\n");
		       if(failed.length > 0) {
		           System.out.println("Missed Mutations:");
		           for(int i = 0; i < failed.length; i++) {
		               System.out.println(failed[i].getDescription());
		           }
		       }
		      
		       System.out.println("Summary: ");
		       System.out.println("Class: " + res.getClassName());
		       System.out.println("Test: " + res.getTestName());
		       System.out.println("Covered: " + 
		               (res.getPassed().length + res.getTimeouts().length));
		       System.out.println("Missed: " + res.getFailed().length);
		       System.out.println("Total: " + res.getAllMutations().length);
	       } else {
	           if(res.getMutationCount() == 0) {
	               System.out.println("Score: 100 (NO MUTATIONS POSSIBLE)");
	           } else {
	               System.out.println("Mutation points = " + res.getMutationCount() 
	                      + ", unit test time limit " + ((double)TIMEOUT / 1000) + "s");
	               
	               Mutation [] mut = res.getAllMutations();
			       for(int i = 0; i < mut.length; i++) {
			           if(mut[i].isPassed())
			               System.out.print(".");
			           else if(mut[i].isFailed())
			               System.out.println("M " +
			                       mut[i].getDescription().substring(6));
			           else if(mut[i].isTimedOut())
			               System.out.print("T");
			           else throw new RuntimeException();
			       }
	               System.out.println();
	               System.out.println("Score: " + 
	                       (100-100*res.getFailed().length/res.getMutationCount()));
	               System.out.println();
	           }
	       }
	   }  catch(Exception e) {
        System.out.println("Usage: java jumble.JumbleMain [ClassName] [TestName]");
        e.printStackTrace();
    }
    }
}