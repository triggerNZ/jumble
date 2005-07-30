package jumble.fast;

/**
 * Interface representing a class which prints the results of a jumble run.
 * 
 * @author Tin Pavlinic
 *  
 */
public interface JumbleResultPrinter {
  /**
   * Displays the result.
   * 
   * @param result
   *          the result to display
   * @throws Exception
   *           if something goes wrong
   */
  public void printResult(JumbleResult result) throws Exception;
}
