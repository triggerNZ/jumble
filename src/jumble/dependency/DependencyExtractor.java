package jumble.dependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.JavaClass;

/** Class for extracting dependencies from class files
 * @author Tin Pavlinic
 */
public class DependencyExtractor {
    public final static byte CONSTANT_Class = 7; // this should be somewhere in the BCEL
    public final static byte CONSTANT_Utf8 = 1; //so should this

    private String mClassName;
    private Set mIgnoredPackages;
    /**
     * Main method. Displays the dependencies for the class given as 
     * a command-line parameter.
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("Dependencies for " + args[0]);
        System.out.println();
        Collection dependencies = new DependencyExtractor(args[0])
        	.getAllDependencies(true);
        Iterator it = dependencies.iterator();
        while(it.hasNext())
           System.out.println(it.next());
        
    }
    /**
     * Constructor
     * @param className the name of the class which is to be checked
     * for dependencies.
     */
    public DependencyExtractor(String className) {
        mClassName = className;
        mIgnoredPackages = new HashSet();
        
        mIgnoredPackages.add("java");
        mIgnoredPackages.add("javax");
        mIgnoredPackages.add("sun");
        mIgnoredPackages.add("com.sun");
        mIgnoredPackages.add("org.w3c");
        mIgnoredPackages.add("org.xml");
        mIgnoredPackages.add("org.omg");
        mIgnoredPackages.add("org.ietf");
    }
    
    
    
    /** Gets the name of the root class
     * @return Returns the class name
     */
    public String getClassName() {
        return mClassName;
    }
    /** Sets the source class name
     * @param className The new class name to set.
     */
    public void setClassName(String className) {
        mClassName = className;
    }
    
    /** Finds classes that the given class depends on.
     *  Runs non recursively
     * @param className the name of the class to check
     * @return the names of the dependency classes in a 
     * Collection
     */
    private Collection getDependencies(String className, boolean ignore) {
        ArrayList ret = new ArrayList();
        
        //ignore classnames starting with [ (don't really)
        // know what they are
        if(className.startsWith("["))
            return ret;
        
        JavaClass clazz = Repository.lookupClass(className);

        ConstantPool cp = clazz.getConstantPool();
        
        for(int i = 0; i < cp.getLength(); i++) {
            Constant c = cp.getConstant(i);
            if(c == null)
                continue;
            
            if(c.getTag() == CONSTANT_Class) {
                ConstantClass cc = (ConstantClass)c;
                ConstantUtf8 utf8 = (ConstantUtf8)cp.
                	getConstant(cc.getNameIndex(), 
                	        CONSTANT_Utf8);
                ret.add(utf8.getBytes().replaceAll("/", "."));
            }
        }
        if(ignore) {
            return filterSystemClasses(ret);
        } else {
            return ret;
        }
    }
    /**
     * Gets the immediate dependencies of the class @see getClassName()
     * @param ignore a flag indicating whether to ignore system classes
     * @return a Collection of class names of the dependencies.
     */
    public Collection getImmediateDependencies(boolean ignore) {
        if(ignore)
            return filterSystemClasses(getDependencies(getClassName(), true));
        else
            return getDependencies(getClassName(), false);
    }
    /**
     * Gets all of the dependencies of the class @see getClassName()
     * @param ignore a flag indicating whether to ignore system classes
     * @return a Collection of class names of the dependencies.
     */
    public Collection getAllDependencies(boolean ignore) {
        Stack fringe = new Stack();
        HashSet ret = new HashSet();
        
        fringe.addAll(getImmediateDependencies(ignore));
        
        while(!fringe.isEmpty()) {
            String cur = (String)fringe.pop();
            
            if(!cur.startsWith("[") && !ret.contains(cur)) {
                ret.add(cur);
                fringe.addAll(getDependencies(cur, ignore));
            }
        }
        
        if(ret.contains(getClassName()))
            ret.remove(getClassName());
        
        if(ignore)
            return filterSystemClasses(ret);
        return ret;
    }
    
    private Collection filterSystemClasses(Collection c) {
        ArrayList ret = new ArrayList();
        Iterator it = c.iterator();
        
        while(it.hasNext()) {
            String cur = (String)it.next();
            Iterator packages = getIgnoredPackages().iterator();
            boolean allowed = true;
            while(packages.hasNext()) {
                String pack = (String)packages.next();
                if(cur.startsWith(pack + "."))
                    allowed = false;
        	}
            if(allowed)
                ret.add(cur);
        }
        return ret;
    }
    
    /**
     * Gets a Set of packages (as strings) ignored by the dependency extractor.
     * All subpackages are ignored also
     * @return the ignored packages.
     */
    public Set getIgnoredPackages() {
        return mIgnoredPackages;
    }
    /**
     * Sets the set of packages to ignore. All subpackages are ignored
     * alse
     * @param newIgnore mew ignore set.
     */
    public void setIgnoredPackages(Set newIgnore) {
        mIgnoredPackages = newIgnore;
    }
}
