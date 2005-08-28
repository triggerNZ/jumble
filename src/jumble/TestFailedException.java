/*
 * Created on Apr 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;

public class TestFailedException extends RuntimeException {
  /**
   * Number used for serialization
   */
  private static final long serialVersionUID = 1331153312422968104L;

  public TestFailedException(String s) {
    super(s);
  }

  public TestFailedException() {
    super();
  }

}
