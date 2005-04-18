
package jumble;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
/** Class for running Jumble on a single class with a single test.
 * 
 * @author Tin
 */
public class JumbleMain {
    public static JumbleResult runJumble(final String className, final String testName, 
            boolean returnVals, boolean inlineConstants, Set ignore,
            int timeout) 
    	throws IOException, InterruptedException {
        
        final List results = new ArrayList();
            
        final Mutater m = new Mutater(0);
        m.setIgnoredMethods(ignore);
        m.setMutateInlineConstants(inlineConstants);
        m.setMutateReturnValues(returnVals);
            
        int count = m.countMutationPoints(className);
            
        int curTest = 0;
        String command = "java jumble.JumbleRunner " + 
         	(returnVals?"-r ":"")  + 
           	(inlineConstants?"-k ":"") + 
           	className + " " + testName + " " + curTest;
      
        Process p = Runtime.getRuntime().exec(command);

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
                    results.add(new Mutation(cur));
                    curTest++;
                    before = System.currentTimeMillis();
                }
            } while(after - before < timeout && curTest < count);
        
            if(curTest < count) {
               results.add(new Mutation("TIMEOUT"));
               curTest++;
               p.destroy();
                  
               command = "java jumble.JumbleRunner " + 
               	(returnVals?"-r ":"")  + 
               	(inlineConstants?"-k ":"") + 
               	className + " " + testName + " " + curTest;
                    
               p = Runtime.getRuntime().exec(command);

               ioThread = new IOThread(p.getInputStream());
               ioThread.start();
            }
        }
 
        return new JumbleResult() {
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
            
       
    }
    
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
        
        if(after - before < 200)
            TIMEOUT = 800;
        else
            TIMEOUT = (int)((after - before)*5);
       
        if(res.indexOf("F") > 0 || res.indexOf("E") > 0)
            throw new TestFailedException();
        
        return TIMEOUT;
    }
    
    public static void main(String [] args) throws Exception {
   
        //For now, just get the test name - everything else is hardcoded
       String className = Utils.getNextArgument(args);
       String testName;
       try {
           testName = Utils.getNextArgument(args);
       } catch(Exception e) {
           testName = className + "Test";
       }
       
       System.out.println("Running test on unmodified class:");
       final int TIMEOUT;
       try {
           TIMEOUT = getTimeOut(testName, true);
       } catch(TestFailedException e) {
           System.out.println("Test failed");
           return;
       }
       HashSet ignore = new HashSet();
       ignore.add("main");
       ignore.add("integrity");
       
       System.out.println("Jumbling...\n");
       
       JumbleResult res = runJumble(className, testName, true, true, ignore, TIMEOUT);
       
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
   }
}