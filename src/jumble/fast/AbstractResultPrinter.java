package jumble.fast;

import java.io.PrintStream;

/**
 * Abstract class defining a result printer to a stream. Use <CODE>getStream()
 * </CODE> to get the output stream
 * 
 * @author Tin Pavlinic
 */
public abstract class AbstractResultPrinter implements JumbleResultPrinter {
  /** Output stream */
  private PrintStream mStream;

  /**
   * Constructor. Constructs a result printer with the specified print stream.
   * 
   * @param p
   *          the stream to output to
   */
  public AbstractResultPrinter(PrintStream p) {
    mStream = p;
  }

  /**
   * Gets the output stream associated with this result printer.
   * 
   * @return a <CODE>PrintStream</CODE> object
   */
  public PrintStream getStream() {
    return mStream;
  }

  /**
   * Override to implement custom output format. Write to the stream specified
   * by <CODE>getStream()</CODE>
   * 
   * @param res the result to print
   * @throws Exception if something goes wrong
   */
  public abstract void printResult(JumbleResult res) throws Exception;
}
