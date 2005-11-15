package jumble;

/**
 * Runtime exception for a test failure.
 *
 * @author Tin
 * @version $Revision$
 */
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
