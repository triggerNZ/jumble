/*
 * Created on Apr 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.batch;

import jumble.*;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
           
            JumbleResult [] results = runBatch(pairs, true,  true, ignore);
            
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
            boolean returns, boolean inlineconstants, Set ignore) 
    	throws IOException, InterruptedException, ClassNotFoundException {
       ArrayList ret = new ArrayList();
        
        for(int i = 0; i < pairs.length; i++) {
            //redirect standard out and run the test
            PrintStream oldOut = System.out;
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            
            int timeout = JumbleMain.getTimeOut(pairs[i].getTestName());
            System.setOut(oldOut);
            
            JumbleResult res = JumbleMain.runJumble(pairs[i].getClassName(),
                    pairs[i].getTestName(), returns, inlineconstants, ignore, timeout);
            ret.add(res);
        }
        return (JumbleResult [])ret.toArray(new JumbleResult[ret.size()]);
    }
}
