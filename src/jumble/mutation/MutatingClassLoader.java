package jumble.mutation;


import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassLoader;

/**
 * A <code>ClassLoader</code> which embeds a <code>Mutater</code> so
 * that applications can be run with a single class undergoing
 * mutation.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class MutatingClassLoader extends ClassLoader {

  /** Used to perform the actual mutation */
  private Mutater mMutater;

  /** The name of the class being mutated */
  private String mTarget;

  /**
   * Creates a new <code>MutatingClassLoader</code> instance.
   *
   * @param target the class name to be mutated.  Other classes will
   * not be mutated.
   * @param mutater a <code>Mutater</code> value that will carry out
   * mutations.
   */
  public MutatingClassLoader(final String target, final Mutater mutater) {
    // Add these ignored classes to work around jakarta commons logging stupidity with class loaders.
    super(new String[] {"org.apache", "org.xml", "org.w3c"});
    mTarget = target;
    mMutater = mutater;
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
      return mMutater.jumbler(clazz);
    }
    return clazz;
  }
}
