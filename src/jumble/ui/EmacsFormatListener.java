package jumble.ui;


import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import jumble.fast.MutationResult;
import org.apache.bcel.util.ClassPath.ClassFile;
import org.apache.bcel.util.ClassPath;

/**
 * Prints the results of a Jumble run in <code>Emacs</code> compatible format.
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

  private boolean mInitialTestsPassed;

  private ClassPath mClassPath;

  private String mBaseDir = System.getProperty("user.dir");

  public EmacsFormatListener(String classPath) {
    this(classPath, System.out);
  }

  public EmacsFormatListener(String classPath, PrintStream output) {
    mStream = output;
    mClassPath = new ClassPath(classPath);
  }

  public void jumbleRunEnded() {
    if (mInitialTestsPassed) {
      if (mMutationCount == 0) {
        mStream.println("Score: 100");
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
      String sourceName = res.getClassName().replace('.', '/') + ".java";
      try {
        // Try to resolve the class name relative to the classpath
        ClassFile cf = mClassPath.getClassFile(res.getClassName());
        sourceName = cf.getPath();
        sourceName = sourceName.replaceAll("\\.class$", ".java");
        if (sourceName.startsWith(mBaseDir)) {
          sourceName = sourceName.substring(mBaseDir.length() + 1);
        }
      } catch (IOException e) {
        // Use the sourceName that we have.
      }
      // Convert inner class to the outer source file
      sourceName = sourceName.replaceAll("\\$[^.]+\\.", ".");
      mStream.println(sourceName + description);
    } else {
      mCovered++;
    }
  }

  public void jumbleRunStarted(String className, List testClasses) {
    mClassName = className;
  }

  public void performedInitialTest(int status, int mutationCount, long timeout) {
    assert status >= 0 && status < 4;
    mInitialTestsPassed = status == InitialTestStatus.OK;
    mInitialStatus = status;
    mMutationCount = mutationCount;
    mStream.print("Mutating " + mClassName);

    if (mInitialStatus == InitialTestStatus.INTERFACE) {
      mStream.println(" (Interface)");
      mStream.println("Score: 100");
    } else {
      mStream.println(" (" + mMutationCount + " mutation points)");
      if (mInitialStatus == InitialTestStatus.NO_TEST) {
        mStream.println(mClassName + ":0: No test class");
        mStream.println("Score: 0");
      } else if (mInitialStatus == InitialTestStatus.FAILED) {
        mStream.println(mClassName + ":0: Test class is broken");
        mStream.println("Score: 0");
      }
    }
  }

  public void error(String errorMsg) {
    mStream.println("ERROR: " + errorMsg);
  }
}
