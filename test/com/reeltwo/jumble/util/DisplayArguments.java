package com.reeltwo.jumble.util;

/**
 * Class which simply prints out its command line arguments.
 * Used for the testing of JavaRunner
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class DisplayArguments {

  public static void main(String[] args) {
    for (int i = 0; i < args.length; i++) {
      System.out.println(args[i]);
    }
  }
}
