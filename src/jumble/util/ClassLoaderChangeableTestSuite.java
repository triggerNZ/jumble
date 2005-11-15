package jumble.util;

import java.lang.reflect.Constructor;

import junit.framework.TestSuite;
/**
 * A test suite which is capable of being loaded in a new class loader.
 * @author Tin Pavlinic
 * @version $Revision$
 *
 */
public class ClassLoaderChangeableTestSuite extends TestSuite implements
    ClassLoaderChanger {
  public ClassLoaderChangeableTestSuite(String className) throws ClassNotFoundException {
    super(Class.forName(className));
  }
  
  public Object changeClassLoader(ClassLoader cl) throws ClassNotFoundException {
    
    try {
      Class clazz = cl.loadClass("jumble.util.ClassLoaderChangeableTestSuite");
      Constructor con = clazz.getConstructor(new Class[] { String.class });

      return con.newInstance(new Object[] { getName()});
    } catch (Exception e) {
      throw new ClassNotFoundException(e.getMessage());
    }
  }
}
