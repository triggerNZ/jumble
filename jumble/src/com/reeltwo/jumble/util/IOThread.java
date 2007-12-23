package com.reeltwo.jumble.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Class which constantly polls an InputStream and allows the user
 * to see if any data is available.
 *
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class IOThread extends Thread {

  private static final String LS = System.getProperty("line.separator");

  public static final boolean DEBUG = false;
  private BufferedReader mOut;
  private LinkedList mBuffer;

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
  @Override
public void run() {
    String curLine;
    try {
      while ((curLine = mOut.readLine()) != null) {
        synchronized (this) {
          mBuffer.addLast(curLine);
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
      return (String) mBuffer.removeFirst();
    }
  }


  /**
   * Returns all the lines of text that are available. Otherwise
   * returns null.
   * @return the next line of text or null
   */
  public String getAvailable() {
    synchronized (this) {
      if (mBuffer.size() == 0) {
        return null;
      }
      StringBuffer sb = new StringBuffer();
      while (mBuffer.size() > 0) {
        sb.append(mBuffer.removeFirst()).append(LS);
      }
      return sb.toString();
    }

  }
}
