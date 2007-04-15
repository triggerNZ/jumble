package com.reeltwo.jumble.dependency;

/**
 * Used in DependencyExtractorTest
 * @author Tin Pavlinic
 * @version $Revision:496 $
 */
public class DT2 {
  public void method() {
    DT1 dt1 = new DT1();
    //DT2 dt2 = null;
    DT3[] dt3 = new DT3[0];
    new StringBuffer().append(dt1).append(dt3[0]);
  }
}
