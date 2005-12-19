package jumble.mutation;


import java.util.HashMap;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoader;

/**
 * A <code>ClassLoader</code> which embeds a <code>Mutater</code> so
 * that applications can be run with a <code>Mutater</code> in place.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class MutatingClassLoader extends ClassLoader {

  /** Used to perform the actual mutation */
  private Mutater mMutater;

  /** The name of the class being mutated */
  private String mTarget;

  /** The cache of fresh classes */
  private HashMap mCache;

  public MutatingClassLoader(final String target, final Mutater mutater) {
    // Add these ignored classes to work around jakarta commons logging stupidity with class loaders.
    super(new String[] {"org.apache", "org.xml", "org.w3c"});
    mTarget = target;
    mMutater = mutater;
    mCache = new HashMap();
  }

  /**
   * Gets a string description of the modification produced.
   * 
   * @return the modification
   */
  public String getModification() {
    return mMutater.getModification();
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
      try {
        if (mCache.containsKey(clazz.getClassName())) {
          clazz = (JavaClass) mCache.get(clazz.getClassName());
        } else {
          mCache.put(clazz.getClassName(), clazz);
        }
        JavaClass ret = mMutater.jumbler(clazz);
        return ret;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return clazz;
  }
}
