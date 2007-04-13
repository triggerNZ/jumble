package com.reeltwo.jumble.util;

/**
 * Interface for objects that can be dynamically cloned under a new
 * class loader.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public interface ClassLoaderCloneable {

  /**
   * Returns a clone of the object as loaded by the specified class
   * loader.
   * 
   * @param loader the new class loader to load in
   * @return a clone of the object in the new class loader
   * @throws ClassNotFoundException if <CODE>loader</CODE> could not
   * load the class or if the class cannot be cloned in the new
   * loader.
   */
  Object clone(ClassLoader loader) throws ClassNotFoundException;
}
