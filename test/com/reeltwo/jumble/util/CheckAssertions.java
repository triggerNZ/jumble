package com.reeltwo.jumble.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Class for testing assertions.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class CheckAssertions {
  public static void main(String[] args) throws IOException {
    JavaRunner runner = new JavaRunner("com.reeltwo.jumble.util.CheckAssertions2");
    Process p = runner.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

    String str = reader.readLine();
    if (str == null) {
      System.out.println("Assertions off");
    } else {
      System.out.println(str);
    }
    reader.close();
  }
}
