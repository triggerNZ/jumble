package jumble;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/** Class implementing a java version of jumble.sh. Nowhere near finished
 *  @author Tin Pavlinic
 *  @version 0.01
 */
public class JumbleRunner {
    public static void main(String [] args) throws Exception {
	String className = Utils.getNextArgument(args);
	String testName = Utils.getNextArgument(args);

	boolean printUsage = false;
	
	if(testName.equals("")) {
	    testName = className + "Test";
	}

	try {
	    Class c = Class.forName(className);
	} catch(Exception e) {
	    System.err.println("Error: class " + className + " not found.");
	    return;
	}

	try {
	    Class c = Class.forName(testName);
	} catch(Exception e) {
	    System.err.println("Error: class " + testName + " not found.");
	    return;
	}

	PrintStream oldOut = System.out;
	PrintStream oldErr = System.err;
	
	try {

	    //hijack standard in and err
	    
	    ByteArrayOutputStream outBA = new ByteArrayOutputStream();
	    ByteArrayOutputStream errBA = new ByteArrayOutputStream();
	    PrintStream outP = new PrintStream(outBA);
	    PrintStream errP = new PrintStream(errBA);
	
	    System.setOut(outP);
	    System.setErr(errP);

	    //read the number of possible mutations
	    Jumbler.main(new String[] {"-c", className});
	    
	    if(!errBA.toString().equals("")) {
		throw new RuntimeException("Output on standard error");
	    }
	    int count = Integer.parseInt(outBA.toString().trim());
	    oldOut.println("count: " + count);

	    
	} catch(Exception e) {
	    printUsage = true;
	    oldErr.println(e);
	} finally { 
	    //restore out and err
	    System.setOut(oldOut);
	    System.setErr(oldErr);
	}


	if(printUsage) {
	    System.out.println("Usage: java jumble.JumbleRunner [Class]");
	}
	
    }
}
