package jumble;

import org.apache.bcel.classfile.JavaClass;
import java.util.HashSet;

import weka.core.Utils;

/**
 * Mutation tester.  Given a class file can either count the number of
 * possible mutation points or perform a mutations.  Mutations can be
 * specified by number or selected at random.  Uses a custom class
 * loader.
 *
 * @author Sean A. Irvine
 * @version $Revision$
 */
public class Jumbler extends org.apache.bcel.util.ClassLoader {
    
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
	//    System.err.println("Mod " + clazz.getClassName());
	if (clazz.getClassName().equals(mTarget)) {
	    try {
		return mMutater.jumbler(clazz.getClassName());
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
	
	/*
	  // set up and parse command line arguments
	  final CLIFlags flags = new CLIFlags("Jumbler", "Mutation testing tool.");
	  flags.registerOptional('c', "count", "Count possible mutation points");
	  flags.registerOptional('x', "exclude", String.class, "[method[,]]+", "Comma separated list of method names to ignore");
	  flags.registerOptional('k', "inlineconstants", "Allow mutation of inline constants");
	  flags.registerOptional('r', "returns", "Allow mutation of return values");
	  flags.registerRequired(String.class, "class", "Class to be mutated");
	  final Flag a1 = flags.registerRequired(Integer.class, "mutation-point", "point to mutate");
	  final Flag a2 = flags.registerRequired(String.class, "test-class", "corresponding test class");
	  a1.setMinCount(0);
	  a2.setMinCount(0);
	  flags.setValidator(new Validator() {
	  public boolean isValid(CLIFlags flags) {
	  if (flags.getAnonymousValue(1) != null && flags.getAnonymousValue(2) == null) {
	  flags.setParseMessage("must give both mutation point and test class");
	  return false;
	  }
	  return true;
	  }
	  });
	  // following call will cause exit if problem occurs in parsing options
	  flags.setFlags(args);
	*/
	// ignore set
	
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
	
	if (Utils.getFlag('c',args) || Utils.getFlag("count", args)) {
	    final Mutater m = new Mutater(0); // run in count mode only, param ignored
	    m.setIgnoredMethods(ignore);
	    m.setMutateInlineConstants(Utils.getFlag('k',args) || Utils.getFlag("inlineconstants", args));
	    m.setMutateReturnValues(Utils.getFlag('r', args) || Utils.getFlag("returns", args));
	    System.out.println("" + m.countMutationPoints(Utils.getNextArgument(args)));
	} else {
	    String className = Utils.getNextArgument(args);
	    int mutationNumber = Integer.parseInt(Utils.getNextArgument(args));
	    String testName = Utils.getNextArgument(args);

	    try {
		final Mutater m = new Mutater(mutationNumber);
		m.setIgnoredMethods(ignore);
		m.setMutateInlineConstants(Utils.getFlag('k',args) || Utils.getFlag("inlineconstants", args));
		m.setMutateReturnValues(Utils.getFlag('r', args) || Utils.getFlag("returns", args));
		final ClassLoader loader = new Jumbler(className.replace('/', '.'), m);
		final Class clazz = loader.loadClass("jumble.JumbleTestSuite");
		System.err.println(clazz.getMethod("run", new Class[] { String.class }).invoke(null, new Object[] { testName.replace('/', '.') }));
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	    
	}
    }
}
