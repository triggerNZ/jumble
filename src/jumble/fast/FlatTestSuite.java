package jumble.fast;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A test suite containing other tests, not test suites. If test suites are
 * added, they are flattened. This is useful for ordering tests in Jumble.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class FlatTestSuite extends TestSuite {
  /**
   * Constructs a new FlatTestSuite. Just calls the parent constructor.
   */
  public FlatTestSuite() {
    super();
  }

  /**
   * Constructs a new FlatTestSuite.
   * 
   * @param theClass
   *          the class to construct the test suite from.
   */
  public FlatTestSuite(final Class theClass) {
    super(theClass);
  }

  /**
   * Constructs a new FlatTestSuite. Just calls the parent constructor.
   * 
   * @param theClass
   *          the class to construct the test suite from.
   * @param name
   *          the name of the test suite
   */
  public FlatTestSuite(final Class theClass, final String name) {
    super(theClass, name);
  }

  /**
   * Constructs a new FlatTestSuite. Just calls the parent constructor.
   * 
   * @param name
   *          the neame of the test suite
   */
  public FlatTestSuite(final String name) {
    super(name);
  }

  /**
   * Since this class is used mainly for timing tests, the suite hierarchy
   * becomes meaningless, so we want to break up the hierarchy and only get the
   * leaf tests.
   */
  public void addTest(final Test t) {
    if (t instanceof TestSuite) {
      TestSuite suite = (TestSuite) t;

      for (int i = 0; i < suite.testCount(); i++) {
        addTest(suite.testAt(i));
      }
    } else {
      super.addTest(t);
    }
  }

  /**
   * Creates a new <code>FlatTestSuite</code>
   * 
   * If the class contains a static <code>suite()</code> method, then it
   * flattens that and uses it. Otherwise it just calls the parent constructor.
   */
  public static FlatTestSuite newFlatTestSuite(Class testClass) {
    //TODO: do this
    return null;
  }
}
