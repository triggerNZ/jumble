package jumble.ui;

import java.io.PrintStream;

import jumble.fast.JumbleResult;

/**
 * Result printer used for debugging
 * 
 * @author Tin Pavlinic
 * @version $Revision$
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
