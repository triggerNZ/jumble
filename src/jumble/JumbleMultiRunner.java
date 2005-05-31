
package jumble;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jumble.util.Utils;

/**
 * Class for running Jumble on a set of tests
 * @author Tin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JumbleMultiRunner {

    public static void main(String[] args) throws Exception {     
        //First process all the options
        final boolean mutateConstants = Utils.getFlag('k', args);
        final boolean mutateIncrements = Utils.getFlag('i', args);
        final boolean mutateReturns = Utils.getFlag('r', args);
        
        final String className = Utils.getNextArgument(args);
        final List tests = new ArrayList();
        int startingTest = 0;
        
        boolean finishedWithArguments = false;
        
        while(!finishedWithArguments) {
            String curArg = Utils.getNextArgument(args);
            
            try {
                //We have the start number = last argument
                startingTest = Integer.parseInt(curArg);
                finishedWithArguments = true;
            } catch(NumberFormatException e) {
                //Another test
                tests.add(curArg);
            }
        }
        
        Set ignore = new HashSet();
        ignore.add("main");
        ignore.add("integrity");

        //First count the number of possible mutaton points
        Mutater m = new Mutater(0); // run in count mode only, param ignored
	    m.setIgnoredMethods(ignore);
	    m.setMutateInlineConstants(mutateConstants);
	    m.setMutateReturnValues(mutateReturns);
	    m.setMutateIncrements(mutateIncrements);
	    
	    
	    int count = m.countMutationPoints(className);
	    
	    if(startingTest >= count)
	        throw new RuntimeException("StartingPoint must be smaller than the "
	                + "total number of mutation points");
	    //now run the tests for every mutation
	    for(int i = startingTest; i < count; i++) {
	        m = new Mutater(i);
	        m.setIgnoredMethods(ignore);
	        m.setMutateInlineConstants(mutateConstants);
	        m.setMutateReturnValues(mutateReturns);
	        m.setMutateIncrements(mutateIncrements);
	        
	        final ClassLoader loader = new Jumbler(className.replace('/', '.'), m);
	        final Class clazz = loader.loadClass("jumble.JumbleMultiTestSuite");
	        Class [] classes = new Class[tests.size()];
	        String [] arguments = new String[tests.size()];
	        
	        for(int j = 0; j < tests.size(); j++) {
	            arguments[j] = (String)tests.get(j);
	        }
	        System.out.println(clazz.getMethod("run", 
	                new Class[] {String[].class})
	                .invoke(null, new Object[]{arguments}));
	        System.out.flush();
	    }
        
    }
}
