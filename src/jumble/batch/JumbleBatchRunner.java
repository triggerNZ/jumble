/*
 * Created on Apr 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.batch;

import jumble.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class JumbleBatchRunner {
    public static void main(String [] args) {
        try {
            HashSet ignore = new HashSet();
            ignore.add("main");
            ignore.add("integrity");
            ClassTestPair [] pairs = 
                new TextFilePairProducer(args[0]).producePairs();
           
            JumbleResult [] results = runBatch(pairs, true,  true, true, ignore, false);
            
            System.out.println("Results: ");
            for(int i = 0; i < results.length; i++) {
                System.out.println(results[i]);
                System.out.println();
            }
            
        } catch(Exception e) {
            System.out.println("Usage: java jumble.batch.JumbleBatchRunner <filename>");
            System.out.println("Exception: ");
            e.printStackTrace();
        }
    }
    
    public static JumbleResult [] runBatch(ClassTestPair [] pairs, 
            boolean returns, boolean inlineconstants, boolean increments,
            Set ignore, boolean display) 
    	throws Exception {
       ArrayList ret = new ArrayList();
        
        for(int i = 0; i < pairs.length; i++) {  
            final ClassTestPair p = pairs[i];
            JumbleResult res;
            try {
                int timeout = JumbleMain.getTimeOut(p.getTestName(), false);
               res = JumbleMain.runJumble(p.getClassName(),
                        p.getTestName(), returns, inlineconstants, increments, 
                        ignore, timeout);
            } catch(TestFailedException e) {
                res =  new JumbleResult() {
                    public Mutation [] getAllMutations() {return null;}
                    public String getClassName() {return p.getClassName();}
                    public Mutation [] getFailed() {return null;}
                    public Mutation [] getTimeouts() {return null;}
                    public int getMutationCount() {return 0;}
                    public Mutation [] getPassed() {return null;}
                    public String getTestName() {return p.getTestName();}
                    public boolean testFailed() {return true;}
                    public int getCoverage() {return 0;}
                    public String toString() {return getTestName() 
                        + " Test Failed";}
                };
            }
            if(display)
                System.out.println(res);
            ret.add(res);
        }
        return (JumbleResult [])ret.toArray(new JumbleResult[ret.size()]);
    }
}
