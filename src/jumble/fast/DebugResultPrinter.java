/*
 * Created on 7/07/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble.fast;

import java.io.PrintStream;

/**
 * Result printer used for debugging
 * 
 * @author Tin Pavlinic
 */
public class DebugResultPrinter extends AbstractResultPrinter {
  /**
   * Constructor.
   * 
   * @param p the stream to output to
   */
  public DebugResultPrinter(PrintStream p) {
    super(p);
  }

  /** 
   * Displays the result in a very verbose way
   * @param res
   * @throws Exception
   */
  public void printResult(JumbleResult res) throws Exception {
    for (int i = 0; i < res.getAllMutations().length; i++) {
      getStream().println(res.getAllMutations()[i]);
    }

  }

}
