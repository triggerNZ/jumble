package jumble.ui;

/**
 * Class full of constants describing the result of an initial test run.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class InitialTestStatus {
  //Prevent instantiation
  private InitialTestStatus() {  
  }
  /** Tests ran successfilly */
  public static final int OK = 0;
  
  /** Tests failed */
  public static final int FAILED = 1;
  
  /** Abstract class being mutated */
  public static final int INTERFACE = 2;
  
  /** No test class */
  public static final int NO_TEST = 3;
}
