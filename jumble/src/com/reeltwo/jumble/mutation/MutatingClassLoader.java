package com.reeltwo.jumble.mutation;



import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.Repository;
import org.apache.bcel.util.SyntheticRepository;


/**
 * A <code>ClassLoader</code> which embeds a <code>Mutater</code> so
 * that applications can be run with a single class undergoing
 * mutation.
 *
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class MutatingClassLoader extends ClassLoader {

  private static final String[] DEFERRED_PREFIXES = new String[] {
    "java.",
    "junit.",
    "org.junit.",
    "sun.reflect.",
    /*
      "com.sun.management.OperatingSystemMXBean"
      "javax.",  // Too general to have deferred
      "com.sun", // Too general to have deferred
    */
  };
    
  private static final String[] LOADED_PREFIXES = new String[] {
    /*
      "javax.transaction.",        
      "javax.persistence.",
      "com.sun.xml.messaging.",
      "com.sun.facelets",
      "com.sun.org.apache.xerces",
     */
  };

  /** Used to perform the actual mutation */
  private final Mutater mMutater;

  /** The name of the class being mutated */
  private final String mTarget;

  private final Hashtable<String, Class<?>> mClasses = new Hashtable<String, Class<?>>();
  private final ClassLoader mDeferTo = ClassLoader.getSystemClassLoader();
  private final Repository mRepository;

  private final ClassPath mClassPath;

  /** List of prefixes of classes to defer to parent classloader. */
  private String[] mDeferPrefixes = DEFERRED_PREFIXES;

  /** Exceptions to the above list. */
  private String[] mDontDeferPrefixes = LOADED_PREFIXES;

  /** Textual description of the modification made. */
  private String mModification;


  /**
   * Creates a new <code>MutatingClassLoader</code> instance.
   *
   * @param target the class name to be mutated.  Other classes will
   * not be mutated.
   * @param mutater a <code>Mutater</code> value that will carry out
   * mutations.
   * @param classpath a <code>String</code> value supplying the
   * classes visible to the classloader.
   */
  public MutatingClassLoader(final String target, final Mutater mutater, final String classpath) {
    mTarget = target;
    mMutater = mutater;
    mClassPath = new ClassPath(classpath);
    //System.err.println("Using classpath" + mClassPath);
    mRepository = SyntheticRepository.getInstance(mClassPath);
    mMutater.setRepository(mRepository);
  }

  public void addDeferredPrefixes(Collection<String> prefixes) {
    addDeferredPrefixes(prefixes.toArray(new String[prefixes.size()]));
  }

  public void addDeferredPrefixes(String[] prefixes) {
    mDeferPrefixes = new String[DEFERRED_PREFIXES.length + prefixes.length];
    System.arraycopy(DEFERRED_PREFIXES, 0, mDeferPrefixes, 0, DEFERRED_PREFIXES.length);
    System.arraycopy(prefixes, 0, mDeferPrefixes, DEFERRED_PREFIXES.length, prefixes.length);
  }

  /**
   * Gets a string description of the modification produced.
   *
   * @return the modification
   */
  public String getModification() {
    return mModification;
  }

  public int countMutationPoints(String className) throws ClassNotFoundException {
    loadClass(className);
    return mMutater.countMutationPoints(className);
  }

  @Override
  protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
    Class cl = null;
    if ((cl = mClasses.get(className)) == null) {
      // Classes we're forcing to be loaded by mDeferTo
      if (isLoadedByDeferredClassLoader(className)) {
        //System.err.println("Parent loading of class: " + className);
        cl = mDeferTo.loadClass(className);
      }
      
      if (cl == null) {
        JavaClass clazz = null;

        // Try loading from our repository
        try {
          if ((clazz = mRepository.loadClass(className)) != null) {
            clazz = modifyClass(clazz);
          }
        } catch (ClassNotFoundException e) {
          ; // OK, because we'll let Class.forName handle it
        }

        if (clazz != null) {
          //System.err.println("MCL loading class: " + className);
          byte[] bytes  = clazz.getBytes();
          cl = defineClass(className, bytes, 0, bytes.length);
        } else {
          //System.err.println("Parent loading of class: " + className);
          //cl = Class.forName(className);
          cl = mDeferTo.loadClass(className);
        }
      }

      if (resolve) {
        resolveClass(cl);
      }
    }

    mClasses.put(className, cl);

    return cl;
  }

  boolean isLoadedByDeferredClassLoader(String className) {
    for (int i = 0; i < mDontDeferPrefixes.length; i++) {
      if (className.startsWith(mDontDeferPrefixes[i])) {
        return false;
      }
    }    
    for (int i = 0; i < mDeferPrefixes.length; i++) {
      if (className.startsWith(mDeferPrefixes[i])) {
        return true;
      }
    }    
    return false;
  }

  /**
   * If the class matches the target then it is mutated, otherwise the class if
   * returned unmodified. Overrides the corresponding method in the superclass.
   * Classes are cached so that we always load a fresh version.
   *
   * This method is public so we can test it
   *
   * @param clazz modification target
   * @return possibly modified class
   */
  public JavaClass modifyClass(JavaClass clazz) {
    if (clazz.getClassName().equals(mTarget)) {
      synchronized (mMutater) {
        clazz = mMutater.jumbler(clazz);
        mModification = mMutater.getModification();
      }
    }
    return clazz;
  }

  @Override
@SuppressWarnings("unchecked")
  public Enumeration<URL> getResources(String name) throws IOException {
    Enumeration<URL> resources = mClassPath.getResources(name);
    if (!resources.hasMoreElements()) {
      resources = mDeferTo.getResources(name);
    }
    return resources;
  }

  @Override
public URL getResource(String name) {
    URL resource = mClassPath.getResource(name);
    if (resource == null) {
      resource = mDeferTo.getResource(name);
    }
    return resource;
  }

  @Override
public InputStream getResourceAsStream(String name) {
    InputStream resource = mClassPath.getResourceAsStream(name);
    if (resource == null) {
      resource = mDeferTo.getResourceAsStream(name);
    }
    return resource;
  }
}
