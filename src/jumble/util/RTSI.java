/**
 * RTSI.java
 */

package jumble.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
/** 
 * The Run Time Subclass Identification class is used to determine all
 * subclasses of a given class in a set of given packages.
 * @author Tin Pavlinic
 */
public class RTSI {
    private final static String CLASSPATH = System.getProperty("java.class.path");
    private final static String PS = System.getProperty("path.separator");
    private final static String FS = System.getProperty("file.separator");
    /**
     * Display all the classes inheriting or implementing a given
     * class in a given package.
     * @param pckname the fully qualified name of the package
     * @param tosubclassname the name of the class to inherit from
     */
    public static Collection find(String pckname, String tosubclassname) 
    	throws ClassNotFoundException {
        	Class c = Class.forName(tosubclassname);
            return find(pckname, c); 
    }

    /**
     * Get all the classes inheriting or implementing a given
     * class in a given package.
     * @param pckgname the fully qualified name of the package
     * @param tosubclass the Class object to inherit from
     * @return a Collection containing the classes
     */
    public static Collection find(String pckgname, Class tosubclass) {
        
        StringTokenizer tokens = new StringTokenizer(CLASSPATH, PS);
        ArrayList dirs = new ArrayList();
        ArrayList jars = new ArrayList();
        
        while(tokens.hasMoreTokens()) {
            File f = new File(tokens.nextToken());
            if(f.isDirectory())
                dirs.add(f);
            else if(f.isFile() && f.getName().endsWith(".jar"))
                jars.add(f);
            else
                ; //ignore all other files
        }
        
        ArrayList ret = new ArrayList();
        
        ret.addAll(getClassesFromDirs(dirs, pckgname, tosubclass));
        ret.addAll(getClassesFromJars(jars, pckgname, tosubclass));
        return ret;
    }
    
    
    private static Collection getClassesFromDirs(Collection dirs, 
            String packageName, Class baseClass) {
        Collection ret = new HashSet();
        //System.out.println(packageName);
        Iterator dirIterator = dirs.iterator();
        
        while(dirIterator.hasNext()) {
            File currentDir = (File)dirIterator.next();
            
            //Need to check this
            File packageDir = new File(currentDir.getAbsolutePath() + 
                    FS + packageName.replace('.', FS.charAt(0)));
        
            File [] classes = packageDir.listFiles(new FileFilter() {
                public boolean accept(File f) {
                    return f.getName().endsWith(".class");
                }
            });
            if(classes == null)
                continue;
            for(int i = 0; i < classes.length; i++) {
                //[package name] + filename -".class"
                String className = packageName + "." + classes[i].getName().
                        substring(0, classes[i].getName().length() - 6);
                Class clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch(ClassNotFoundException e) {
                    //Should never happen
                    e.printStackTrace();
                }
                
                if(isSubClass(clazz, baseClass)) {
                    //System.out.println(className);
                    ret.add(className);
                }
            }
        }
        return ret;
    }
    
    private static boolean isSubClass(Class clazz, Class base) {
        if(base.isInterface()) {
            Class [] interfaces = clazz.getInterfaces();
            for(int i = 0; i< interfaces.length; i++) {
                if(interfaces[i] == base) {
                    return true;
                }
            }
            return false;
        } else {
            while(clazz!=null) {
                if(clazz == base)
                    return true;
                clazz = clazz.getSuperclass();
            }
            return false;
        }
    }
    
    private static Collection getClassesFromJars(Collection jars, 
            String packageName, Class baseClass) {
        Collection ret = new HashSet();
        
        Iterator jarsIt = jars.iterator();
        while(jarsIt.hasNext()) {
            try {
                JarFile jar = new JarFile((File)jarsIt.next());
                Enumeration entries = jar.entries();
                
                while(entries.hasMoreElements()) {
                    JarEntry curEntry = (JarEntry)entries.nextElement();
                    //System.out.println(curEntry);
                    //only look at actual class files
                    if(!curEntry.isDirectory() 
                          && curEntry.toString().endsWith(".class")) {
                        String className = curEntry.toString()
                        	.substring(0, curEntry.toString().length() - 6)
                        	.replace('/', '.');
                        
                        String pack = className.substring(0, className.lastIndexOf("."));
                        if(pack.equals(packageName)) {
                            try {
                            Class c = Class.forName(className);
                            
                            if(isSubClass(c, baseClass))
                                ret.add(className);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                        
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
            
        return ret;
    }
    
    public static void main(String []args) {
	try {
	    System.out.println(find(args[0],args[1]));
	} catch(Exception e) {
		System.out.println("Usage: java RTSI [<package>] <subclass>");
	 
		}
    }
}// RTSI
