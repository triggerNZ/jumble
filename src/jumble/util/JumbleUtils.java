package jumble.util;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;

/**
 * Class containing several utility methods useful to Jumble.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class JumbleUtils {
  private JumbleUtils() {
  }

  /**
   * Determines whether a given class is a test class.
   * 
   * @param clazz
   *          the class to check.
   * @return true if the class is a test class, false otherwise.
   */
  public static boolean isTestClass(Class clazz) {
    Set interfaceSet = new HashSet();
    Class tmp = clazz;

    while (tmp != Object.class) {
      Class[] intfc = tmp.getInterfaces();
      for (int i = 0; i < intfc.length; i++) {
        interfaceSet.add(intfc[i]);
      }
      tmp = tmp.getSuperclass();
    }

    Class[] interfaces = (Class[]) interfaceSet.toArray(new Class[interfaceSet.size()]);

    for (int i = 0; i < interfaces.length; i++) {
      if (interfaces[i] == Test.class) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets whether assertions are currently enabled.
   * 
   * @return whether assertions are currently enabled
   * 
   */
  public static boolean isAssertionsEnabled() {
    boolean assertionsEnabled = false;
    assert (assertionsEnabled = true) == true;
    return assertionsEnabled;
  }
}
