package com.reeltwo.jumble.util;

import java.util.Properties;

/**
 * TODO
 *
 * @author Tin
 * @version $Revision$
 */
public class DisplayEnvironment {

  public static void main(String[] args) {
    Properties props = System.getProperties();
        
    System.out.println("java.home " + props.getProperty("java.home"));
    System.out.println("java.class.path " + props.getProperty("java.class.path"));
  }
}
