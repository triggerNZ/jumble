package jumble.util;

import java.lang.reflect.Constructor;

import junit.framework.TestSuite;
import java.lang.reflect.InvocationTargetException;
/**
 * A test suite which is capable of being loaded in a new class loader.
 * @author Tin Pavlinic
 * @version $Revision$
 *
 */
public class ClassLoaderChangeableTestSuite extends TestSuite implements ClassLoaderChanger {

  public ClassLoaderChangeableTestSuite(String className) throws ClassNotFoundException {
    super(Class.forName(className));
  }
  
  public Object clone(ClassLoader cl) throws ClassNotFoundException {
    
    try {
      Class clazz = cl.loadClass("jumble.util.ClassLoaderChangeableTestSuite");
      Constructor con = clazz.getConstructor(new Class[] {String.class});

      return con.newInstance(new Object[] {getName()});
    } catch (InvocationTargetException e) {
      throw new ClassNotFoundException(e.getMessage());
    } catch (InstantiationException e) {
      throw new ClassNotFoundException(e.getMessage());
    } catch (IllegalAccessException e) {
      throw new ClassNotFoundException(e.getMessage());
    } catch (NoSuchMethodException e) {
      throw new ClassNotFoundException(e.getMessage());
    }
  }
}
