package jumble.util;

/**
 * Interface for classes which can be dynamically reloaded and cloned in a new
 * class loader.
 * 
 * @author Tin Pavlinic
 */
public interface ClassLoaderChanger {
  /**
   * Reloads the class in the specified class loader and clones the object.
   * 
   * @param loader
   *          the new class loader to load in
   * @return a clone of the object in the new class loader
   * @throws ClassNotFoundException
   *           if <CODE>loader</CODE> could not load the class or if the class
   *           cannot be cloned in the new loader.
   */
  public Object changeClassLoader(ClassLoader loader)
      throws ClassNotFoundException;
}
