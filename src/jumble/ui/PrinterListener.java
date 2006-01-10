package jumble.ui;

import java.io.PrintStream;
import java.util.List;

import jumble.fast.MutationResult;

import com.reeltwo.util.Debug;

/**
 * Prints the results of a Jumble run to a <code>PrintStream</code>, this
 * will usually be <code>System.out</code>.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class PrinterListener implements JumbleListener {
  private PrintStream mStream;

  private int mCovered = 0;

  private int mMutationCount;

  private int mInitialStatus;

  private String mClassName;

  private List mTestNames;

  private boolean mInitialTestsPassed;

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
        mStream.println("Score: 100 (NO MUTATIONS POSSIBLE)");
      } else {
        mStream.println("Score: " + (mCovered) * 100 / mMutationCount);
      }
    }
    mStream.close();
  }

  public void finishedMutation(MutationResult res) {
    if (res.isPassed()) {
      mStream.print(".");
      mCovered++;
    } else if (res.isTimedOut()) {
      mStream.print("T");
      mCovered++;
    } else {
      mStream.println("M FAIL: " + res.getDescription());
    }
  }

  public void jumbleRunStarted(String className, List testClasses) {
    assert Debug.println("class: " + className + " tests: " + testClasses);
    mClassName = className;
    mTestNames = testClasses;
  }

  public void performedInitialTest(int status, int mutationCount, long timeout) {
    assert status >= 0 && status < 4;
    mInitialTestsPassed = status == InitialTestStatus.OK;
    mInitialStatus = status;
    mMutationCount = mutationCount;
    mStream.println("Mutating " + mClassName);

    if (mInitialStatus == InitialTestStatus.INTERFACE) {
      mStream.println("Score: 100 (INTERFACE)");
      return;
    }

    mStream.print("Tests:");
    for (int i = 0; i < mTestNames.size(); i++) {
      mStream.print(" " + mTestNames.get(i));
    }
    mStream.println();

    if (mInitialStatus == InitialTestStatus.NO_TEST) {
      mStream.println("Score: 0 (NO TEST CLASS)");
      mStream.println("Mutation points = " + mMutationCount);
      return;
    }

    if (mInitialStatus == InitialTestStatus.FAILED) {
      mStream.println("Score: 0 (TEST CLASS IS BROKEN)");
      mStream.println("Mutation points = " + mMutationCount);
      return;
    }

    mStream.print("Mutation points = " + mMutationCount);
    mStream.println(", unit test time limit " + (double) timeout / 1000 + "s");
  }
}
