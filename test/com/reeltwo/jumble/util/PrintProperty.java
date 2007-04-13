package com.reeltwo.jumble.util;
/**
 * Class used for Jumble testing
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class PrintProperty {
  public static void main(String[] args) {
    String prop = System.getProperty("jumbleTestProperty");
    
    System.out.println(prop);
  }
}
