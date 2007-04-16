package com.reeltwo.jumble.fast;


import junit.framework.TestCase;

/**
 * Abstract test to test whether <CODE>JumbleResult</CODE>s. Mainly used to
 * make sure that tests initially failing mean that nothing gets jumbled. To run
 * this test, subclass it and override the <CODE>getJumbleResult()</CODE>
 * method and return an array of concrete instances to be checked.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public abstract class AbstractJumbleResultTest extends TestCase {
  public abstract JumbleResult[] getJumbleResults();

  public final void testFailedTest() {
    JumbleResult[] res = getJumbleResults();

    for (int i = 0; i < res.length; i++) {
      if (res[i].initialTestsPassed()) {
        assertNotSame(null, res[i].getCovered());
        assertNotSame(null, res[i].getMissed());
        assertNotSame(null, res[i].getTimeouts());
      } else {
        assertEquals(null, res[i].getCovered());
        assertEquals(null, res[i].getMissed());
        assertEquals(null, res[i].getTimeouts());
        assertEquals(0, res[i].getTimeoutLength());
      }
    }
  }
}
