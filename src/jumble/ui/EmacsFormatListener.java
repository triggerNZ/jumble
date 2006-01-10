package jumble.ui;

import java.io.PrintStream;
import java.util.List;

import jumble.fast.MutationResult;

import com.reeltwo.util.Debug;

/**
 * Prints the results of a Jumble run in <code>Emacs</code> compatible
 * format.
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision$
 */
public class EmacsFormatListener implements JumbleListener {
  private PrintStream mStream;

  private int mCovered = 0;

  private int mMutationCount;

  private int mInitialStatus;

  private String mClassName;

  private List mTestNames;

  private boolean mInitialTestsPassed;

  public EmacsFormatListener() {
    this(System.out);
  }

  public EmacsFormatListener(PrintStream output) {
    mStream = output;
  }

  public void jumbleRunEnded() {
    if (mInitialTestsPassed) {
      if (mMutationCount == 0) {
        mStream.println("Score: 100 (NO MUTATIONS POSSIBLE)");
      } else {
        mStream.println("Score: " + (mCovered) * 100 / mMutationCount);
      }
    }
    mStream.close();
  }

  public void finishedMutation(MutationResult res) {
    if (res.isFailed()) {
      String description = res.getDescription();
      description = description.substring(description.indexOf(":"));
      String sourceName = res.getClassName().replace(".", "/") + ".java";
      mStream.println(sourceName + description);
    } else {
      mCovered++;
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
    mStream.println("Mutating " + mClassName + " (" + mMutationCount + " mutation points)");

    if (mInitialStatus == InitialTestStatus.INTERFACE) {
      mStream.println("Score: 100 (INTERFACE)");
    } else {
      if (mInitialStatus == InitialTestStatus.NO_TEST) {
        mStream.println(mClassName + ":0: (NO TEST CLASS)");
        mStream.println("Score: 0 (NO TEST CLASS)");
      } else if (mInitialStatus == InitialTestStatus.FAILED) {
        mStream.println(mClassName + ":0: (TEST CLASS IS BROKEN)");
        mStream.println("Score: 0 (TEST CLASS IS BROKEN)");
      }
    }
  }
}
