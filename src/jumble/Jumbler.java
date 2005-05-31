package jumble;

import java.util.HashSet;

import jumble.util.Utils;

import org.apache.bcel.classfile.JavaClass;


/**
 * Mutation tester.  Given a class file can either count the number of
 * possible mutation points or perform a mutations.  Mutations can be
 * specified by number or selected at random.  Uses a custom class
 * loader.
 *
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class Jumbler extends ClassLoader {

    public final static boolean DEBUG = false;
    
    /** Target class for mutation. */
    private final String mTarget;
    /** Mutation engine. */
    private final Mutater mMutater;
    
    public String getModification() {
	return mMutater.getModification();
    }
    
    /**
     * Construct a new loader which will induce a single point mutation
     * in the target.
     *
     * @param target class name to undergo mutation
     * @param mutater mutation engine
     */
    protected Jumbler(final String target, final Mutater mutater) {
	super();
	mTarget = target;
	mMutater = mutater;
    }
    
    /**
     * If the class matches the target then it is mutated, otherwise
     * the class if returned unmodified.  Overrides the corresponding
     * method in the superclass.
     *
     * @param clazz modification target
     * @return possibly modified class
     */
    protected JavaClass modifyClass(final JavaClass clazz) {
	if (clazz.getClassName().equals(mTarget)) {
	    try {
	        JavaClass ret = mMutater.jumbler(clazz.getClassName());
	        return ret;
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return clazz;
    }
    
    /**
     * Process each of the supplied files in turn. Reports problems with
     * variable names.
     *
     * @param args array of java file names
     */
    public static void main(String[] args) throws Exception {
	
	final HashSet ignore = new HashSet();

	//get the list of excludes
	String excludes = Utils.getOption('x', args);
	if(excludes.equals(""))
	    excludes = Utils.getOption("exclude", args);
	
	
	if (!excludes.equals("")) {
	    final String[] ig = excludes.split(",");
	    for (int i = 0; i < ig.length; i++) {
		ignore.add(ig[i]);
	    }
	} else {
	    ignore.add("main");
	    ignore.add("integrity");
	}
	
	//the flags must be processed first
	boolean count = Utils.getFlag('c',args) || Utils.getFlag("count", args);
	boolean constants = Utils.getFlag('k',args) || Utils.getFlag("inlineconstants", args);
	boolean returns = Utils.getFlag('r', args) || Utils.getFlag("returns", args);
	boolean increments = Utils.getFlag('i', args) || Utils.getFlag("increments", args);
	
	if (count) {
	    if(DEBUG) {
		System.out.println("Count mode\nArguments:");
		for(int i = 0; i < args.length; i++)
		    System.out.println(args[i]);
		
	    }
	    final Mutater m = new Mutater(0); // run in count mode only, param ignored
	    m.setIgnoredMethods(ignore);
	    m.setMutateInlineConstants(constants);
	    m.setMutateReturnValues(returns);
	    m.setMutateIncrements(increments);
	    System.out.println("" + m.countMutationPoints(Utils.getNextArgument(args)));
	} else {
	    String className = Utils.getNextArgument(args);
	    //System.out.println(className);
	    int mutationNumber = Integer.parseInt(Utils.getNextArgument(args));
	    String testName = Utils.getNextArgument(args);
	    
	    try {
	        final Mutater m = new Mutater(mutationNumber);
	        m.setIgnoredMethods(ignore);
	        m.setMutateInlineConstants(constants);
	        m.setMutateReturnValues(returns);
	        m.setMutateIncrements(increments);
	        
	        final ClassLoader loader = new Jumbler(className.replace('/', '.'), m);
	        final Class clazz = loader.loadClass("jumble.JumbleTestSuite");
	        System.err.println(clazz.getMethod("run", new Class[] { String.class }).invoke(null, new Object[] { testName.replace('/', '.') }));
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    
	}
    }
}
