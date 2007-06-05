package com.reeltwo.jumble.ui;



import com.reeltwo.jumble.fast.JumbleResult;
import com.reeltwo.jumble.fast.MutationResult;
import java.io.PrintStream;
import java.util.List;

/**
 * Prints the results of a Jumble run to a <code>PrintStream</code>, this
 * will usually be <code>System.out</code>.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class PrinterListener implements JumbleListener {
  private static final int DOTS_PER_LINE = 50;
  
  private PrintStream mStream;

  private int mCovered = 0;

  private int mMutationCount;

  private String mClassName;

  private List mTestNames;

  private boolean mInitialTestsPassed;
  
  private int mDotCount = 0;

  public PrinterListener() {
    this(System.out);
  }

  public PrinterListener(PrintStream output) {
    mStream = output;
  }

  public void jumbleRunEnded() {
    if (mInitialTestsPassed) {
      mStream.println();

      if (mMutationCount == 0) {
        mStream.println("Score: 100% (NO MUTATIONS POSSIBLE)");
      } else {
        mStream.println("Score: " + (mCovered) * 100 / mMutationCount + "%");
      }
    }
    mStream.close();
  }

  public void finishedMutation(MutationResult res) {
    if (res.isPassed()) {
      mStream.print(".");
      mCovered++;
      newDot();
    } else if (res.isTimedOut()) {
      mStream.print("T");
      mCovered++;
      newDot();
    } else {
      mStream.println("M FAIL: " + res.getDescription());
      mDotCount = 0;
    }
  }

  private void newDot() {
    mDotCount++;
    if (mDotCount == DOTS_PER_LINE) {
      mDotCount = 0;
      mStream.println();
    }
  }
  
  public void jumbleRunStarted(String className, List testClasses) {
    //System.err.println("class: " + className + " tests: " + testClasses);
    mClassName = className;
    mTestNames = testClasses;
  }

  public void performedInitialTest(JumbleResult result, int mutationCount) {
    mInitialTestsPassed = result.initialTestsPassed();
    mMutationCount = mutationCount;
    mStream.println("Mutating " + mClassName);

    if (result.isInterface()) {
      mStream.println("Score: 100% (INTERFACE)");
      return;
    }

    mStream.print("Tests:");
    for (int i = 0; i < mTestNames.size(); i++) {
      mStream.print(" " + mTestNames.get(i));
    }
    mStream.println();

    if (result.isMissingTestClass()) {
      mStream.println("Score: 0% (NO TEST CLASS)");
      mStream.println("Mutation points = " + mMutationCount);
      return;
    }

    if (!mInitialTestsPassed) {
      mStream.println("Score: 0% (TEST CLASS IS BROKEN)");
      mStream.println("Mutation points = " + mMutationCount);
      return;
    }

    mStream.print("Mutation points = " + mMutationCount);
    mStream.println(", unit test time limit " + (double) result.getTimeoutLength() / 1000 + "s");
  }
  
  public void error(String errorMsg) {
    mStream.println("ERROR: " + errorMsg);
  }
}
