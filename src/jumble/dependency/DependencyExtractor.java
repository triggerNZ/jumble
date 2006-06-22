package jumble.dependency;



import com.reeltwo.util.CLIFlags.Flag;
import com.reeltwo.util.CLIFlags;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import org.apache.bcel.util.Repository;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.SyntheticRepository;

/**
 * Class for extracting dependencies from class files
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class DependencyExtractor {

  /** CONSTANT_Class value. */
  private static final byte CONSTANT_CLASS = 7;

  /** CONSTANT_Utf8 value. */
  private static final byte CONSTANT_UTF8 = 1;

  private final ClassPath mClassPath;

  private final Repository mRepository;

  /** The name of the class being analyzed. */
  private String mClassName;

  /** Set of packages to ignore. (Subpackages are ignored automatically) */
  private Set mIgnoredPackages;

  /** A cache for the classes which have already been analyzed */
  private HashMap mCache;
  
  /**
   * Main method. Displays the dependencies for the class given as a
   * command-line parameter.
   * 
   * @param args
   *          the command line arguments. Only accepts one argument: the name of
   *          the class to analyze for dependencies.
   */
  public static void main(String[] args) {
    CLIFlags flags = new CLIFlags("DependencyExtractor");
    Flag ignoreFlag = flags.registerOptional('i', "ignore", String.class, "METHOD", "Comma-separated list of packages to exclude.");
    Flag classpathFlag = flags.registerOptional('c', "classpath", String.class, "CLASSPATH", "The classpath to use for tests", System.getProperty("java.class.path"));
    Flag classFlag = flags.registerRequired(String.class, "CLASS", "Name of the class to analyse.");
    flags.setFlags(args);

    Set ignore = null;
    if (ignoreFlag.isSet()) {
      ignore = new HashSet();
      String[] tokens = ((String) ignoreFlag.getValue()).split(",");
      for (int i = 0; i < tokens.length; i++) {
        ignore.add(tokens[i]);
      }
    }
    final String className = ((String) classFlag.getValue()).replace('/', '.');

    System.out.println("Dependencies for " + className);
    System.out.println();
    
    DependencyExtractor extractor = new DependencyExtractor((String) classpathFlag.getValue());
    
    if (ignore != null) {
      extractor.setIgnoredPackages(ignore);
    }
    
    Collection dependencies = extractor.getAllDependencies(className, true);
    Iterator it = dependencies.iterator();
    while (it.hasNext()) {
      System.out.println(it.next());
    }
  }

  /**
   * Constructor
   */
  public DependencyExtractor(String classPath) {
    mCache = new HashMap();
    mIgnoredPackages = new HashSet();
    mClassPath = new ClassPath(classPath);
    mRepository = SyntheticRepository.getInstance(mClassPath);
    
    mIgnoredPackages.add("java");
    mIgnoredPackages.add("javax");
    mIgnoredPackages.add("sun");
    mIgnoredPackages.add("com.sun");
    mIgnoredPackages.add("org.w3c");
    mIgnoredPackages.add("org.xml");
    mIgnoredPackages.add("org.omg");
    mIgnoredPackages.add("org.ietf");
    //integrity();
  }

  /**
   * Gets the name of the root class
   * 
   * @return Returns the class name
   */
  public String getClassName() {
    return mClassName;
  }

  /**
   * Sets the source class name
   * 
   * @param className
   *          The new class name to set.
   */
  public void setClassName(String className) {
    mClassName = className;
    //integrity();
  }

  /**
   * Finds classes that the given class depends on. Runs non recursively
   * 
   * @param className
   *          the name of the class to check
   * @return the names of the dependency classes in a Collection
   */
  private Collection getDependencies(String className, boolean ignore) {
    //First look in the cache
    if (mCache.containsKey(className)) {
      return (Collection) mCache.get(className);
    }
    
    ArrayList ret = new ArrayList();
    if (isPrimitiveArray(className)) {
      // System.out.println(className + " primitive array");
      return ret;
    }
    className = cleanClassName(className);

    JavaClass clazz = loadClass(className);
    
    if (clazz == null) {
      System.err.println("Could not find " + className);
      return ret;
    }
    ConstantPool cp = clazz.getConstantPool();

    for (int i = 0; i < cp.getLength(); i++) {
      Constant c = cp.getConstant(i);
      if (c == null) {
        continue;
      }
      if (c.getTag() == CONSTANT_CLASS) {
        ConstantClass cc = (ConstantClass) c;
        ConstantUtf8 utf8 = (ConstantUtf8) cp.getConstant(cc.getNameIndex(),
            CONSTANT_UTF8);
        ret.add(utf8.getBytes().replaceAll("/", "."));
      }
    }

    Set methodTypes = new HashSet();
    Method[] methods = clazz.getMethods();
    for (int i = 0; i < methods.length; i++) {
      methodTypes.add(methods[i].getReturnType());
      for (int j = 0; j < methods[i].getArgumentTypes().length; j++) {
        methodTypes.add(methods[i].getArgumentTypes()[j]);
      }
    }

    Set stringTypes = getStringTypes(methodTypes);
    ret.addAll(stringTypes);

    Set fieldTypes = new HashSet();
    Field[] fields = clazz.getFields();

    for (int i = 0; i < fields.length; i++) {
      fieldTypes.add(fields[i].getType());
    }
    ret.addAll(getStringTypes(fieldTypes));

    if (ignore) {
      ret = new ArrayList(filterSystemClasses(ret));
    }
    mCache.put(className, ret);
    return ret;
  }

  private JavaClass loadClass(String className) {
    try {
      JavaClass clazz = mRepository.findClass(className);

      if (clazz == null) {
        return mRepository.loadClass(className);
      } else {
        return clazz;
      }
    } catch (ClassNotFoundException ex) { 
      return null; 
    }
  }

  /**
   * Gets the immediate dependencies of the class
   * 
   * @param ignore
   *          a flag indicating whether to ignore system classes
   * @return a Collection of class names of the dependencies.
   */
  public Collection getImmediateDependencies(boolean ignore) {
    Collection ret;
    if (!isPrimitiveArray(getClassName())) {
      if (ignore) {
        ret = filterSystemClasses(getDependencies(
            cleanClassName(getClassName()), true));
      } else {
        ret = getDependencies(cleanClassName(getClassName()), false);
      }
    } else {
      // System.out.println("gid - " + getClassName() + " is primitive");
      ret = new HashSet();
    }
    //integrity();
    return ret;
  }

  /**
   * Gets all of the dependencies of the class
   * 
   * @param ignore
   *          a flag indicating whether to ignore system classes
   * @return a Collection of class names of the dependencies.
   */
  public Collection getAllDependencies(String rootClass, boolean ignore) {
    setClassName(rootClass);
    Stack fringe = new Stack();
    HashSet ret = new HashSet();

    if (isPrimitiveArray(getClassName())) {
      // System.out.println(getClassName() + " primitive array");
      return ret;
    }

    fringe.addAll(getImmediateDependencies(ignore));

    while (!fringe.isEmpty()) {
      String cur = (String) fringe.pop();
      // System.out.println(cur);
      if (!isPrimitiveArray(cur)) {
        cur = cleanClassName(cur);
        // System.out.println(cur);
        if (!ret.contains(cur)) {
          ret.add(cur);
          fringe.addAll(getDependencies(cur, ignore));
        }
      }
    }

    if (ret.contains(getClassName())) {
      ret.remove(getClassName());
    }
    if (ignore) {
      ret = new HashSet(filterSystemClasses(ret));
    }

    //integrity();

    return ret;
  }

  /**
   * Filters the ignored classes from the collection and creates a new
   * collection.
   * @param c the collection to filter
   * @return the filtered collection
   */
  private Collection filterSystemClasses(Collection c) {
    ArrayList ret = new ArrayList();
    Iterator it = c.iterator();

    while (it.hasNext()) {
      String cur = (String) it.next();
      Iterator packages = getIgnoredPackages().iterator();
      boolean allowed = true;
      while (packages.hasNext()) {
        String pack = (String) packages.next();
        if (cur.startsWith(pack + ".")) {
          allowed = false;
        }
      }
      if (allowed) {
        ret.add(cur);
      }
    }
    return ret;
  }

  /**
   * Gets a Set of packages (as strings) ignored by the dependency extractor.
   * All subpackages are ignored also
   * 
   * @return the ignored packages.
   */
  public Set getIgnoredPackages() {
    return mIgnoredPackages;
  }

  /**
   * Sets the set of packages to ignore. All subpackages are ignored alse
   * 
   * @param newIgnore
   *          mew ignore set.
   */
  public void setIgnoredPackages(Set newIgnore) {
    mIgnoredPackages = newIgnore;
  }

  public static String cleanClassName(String className) {
    while (className.startsWith("[")) {
      className = className.substring(1);
    }

    if (className.endsWith(";") && className.startsWith("L")) {
      className = className.substring(1);
      className = className.substring(0, className.length() - 1);
    }

    return className;
  }

  /**
   * Determines whether the string <CODE>s</CODE> is the internal
   * representation of a primitive type.
   * 
   * @param s
   *          the string to check
   * @return true is <CODE>s</CODE> represents a primitive type
   */
  public static boolean isPrimitiveArray(String s) {
    if (s.startsWith("[")) {
      s = cleanClassName(s);
      return s.equals("B") || s.equals("C") || s.equals("D") || s.equals("F")
          || s.equals("I") || s.equals("J") || s.equals("S") || s.equals("Z")
          || s.startsWith("L");
    }
    return false;
  }

//  public void integrity() {
//    // Class must exist
//    try {
//      Class.forName(mClassName);
//    } catch (ClassNotFoundException e) {
//      assert false;
//    }
//  }

  public Set getStringTypes(Collection c) {
    Set s = new HashSet();

    Iterator it = c.iterator();

    while (it.hasNext()) {
      Type t = (Type) it.next();

      while (t instanceof ArrayType) {
        t = ((ArrayType) t).getBasicType();
      }

      if (t == Type.BOOLEAN) {
        continue;
      } else if (t == Type.BYTE) {
        continue;
      } else if (t == Type.CHAR) {
        continue;
      } else if (t == Type.DOUBLE) {
        continue;
      } else if (t == Type.FLOAT) {
        continue;
      } else if (t == Type.INT) {
        continue;
      } else if (t == Type.LONG) {
        continue;
      } else if (t == Type.SHORT) {
        continue;
      } else if (t == Type.VOID) {
        continue;
      } else {
        s.add(t.toString());
      }
    }
    return s;
  }
  
  public void clearCache() {
    mCache.clear();
  }
}
