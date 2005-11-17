package jumble.util;

import java.util.List;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Class which constantly polls an InputStream and allows the user
 * to see if any data is available.
 *
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class IOThread extends Thread {

  public final static boolean DEBUG = false;
  private BufferedReader mOut;
  private List mBuffer;

  /**
   * A new thread.
   *
   * @param out stream
   */
  public IOThread(InputStream out) {
    mOut = new BufferedReader(new InputStreamReader(out));
    mBuffer = new LinkedList();
  }

  /** Loops while the stream exists.*/
  public void run() {
    String curLine;
    try {
      while ((curLine = mOut.readLine()) != null) {  
        synchronized (this) {
          mBuffer.add(0, curLine);
        }
      }
      if (DEBUG) {
        System.out.println("F");
      }
    } catch (IOException e) {
      //e.printStackTrace();
      return;
    }
  }

  /**
   * Returns the next line of text if available. Otherwise
   * returns null.
   * @return the next line of text or null
   */
  public String getNext() {
    synchronized (this) {
      if (mBuffer.size() == 0) {
        return null;
      }
      String ret = (String) mBuffer.get(mBuffer.size() - 1);
      mBuffer.remove(mBuffer.size() - 1);
      return ret;
    }
  }
}
