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
    
    public static void main(String[] args) {
        System.out.println("Dependencies for " + args[0]);
        System.out.println();
        Collection dependencies = new DependencyExtractor(args[0])
        	.getAllFilteredDependencies();
        Iterator it = dependencies.iterator();
        while(it.hasNext())
           System.out.println(it.next());
        
    }
    
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
    
    
    
    /**
     * @return Returns the mClassName.
     */
    public String getClassName() {
        return mClassName;
    }
    /**
     * @param className The mClassName to set.
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
    private Collection getDependencies(String className) {
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
        return ret;
    }
    
    public Collection getImmediateDependencies() {
        return getDependencies(getClassName());
    }
    
    public Collection getAllDependencies() {
        Stack fringe = new Stack();
        HashSet ret = new HashSet();
        
        fringe.addAll(getImmediateDependencies());
        
        while(!fringe.isEmpty()) {
            String cur = (String)fringe.pop();
            
            if(!cur.startsWith("[") && !ret.contains(cur)) {
                ret.add(cur);
                fringe.addAll(getDependencies(cur));
            }
        }
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
    
    public Collection getAllFilteredDependencies() {
        return filterSystemClasses(getAllDependencies());
    }
    
    public Set getIgnoredPackages() {
        return mIgnoredPackages;
    }
    public void setIgnoredPackages(Set newIgnore) {
        mIgnoredPackages = newIgnore;
    }
}
