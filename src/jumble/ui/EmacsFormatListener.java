package jumble.ui;


import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import jumble.fast.JumbleResult;
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

  private String findSourceName(String className) {
    String sourceName = className.replace('.', '/') + ".java";
    try {
      // Try to resolve the class name relative to the classpath
      ClassFile cf = mClassPath.getClassFile(className);
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
    return sourceName;
  }

  public void finishedMutation(MutationResult res) {
    if (res.isFailed()) {
      String description = res.getDescription();
      description = description.substring(description.indexOf(":"));
      String sourceName = findSourceName(res.getClassName());
      mStream.println(sourceName + description);
    } else {
      mCovered++;
    }
  }

  public void jumbleRunStarted(String className, List testClasses) {
    mClassName = className;
  }

  public void performedInitialTest(JumbleResult result, int mutationCount) {
    mInitialTestsPassed = result.initialTestsPassed();
    mMutationCount = mutationCount;
    String sourceName = findSourceName(mClassName);
    mStream.print("Mutating " + sourceName);

    if (result.isInterface()) {
      mStream.println(" (Interface)");
      mStream.println("Score: 100");
    } else {
      mStream.println(" (" + mMutationCount + " mutation points)");
      if (result.isMissingTestClass()) {
        mStream.println(sourceName + ":0: No test class" + toString(result.getTestClasses()));
        mStream.println("Score: 0");
      } else if (!mInitialTestsPassed) {
        mStream.println(sourceName + ":0: Test class is broken" + toString(result.getTestClasses()));
        mStream.println("Score: 0");
      }
    }
  }
  
  private String toString(String[] names) {
    if (names == null || names.length == 0) {
      return "";
    }
    if (names.length == 1) {
      return " " + names[0];
    } else {
      StringBuffer sb = new StringBuffer(" ");
      sb.append('[');
      for (String name : names) {
        sb.append(name).append(' ');
      }
      sb.append(']');
      return sb.toString();
    }
  }

  public void error(String errorMsg) {
    mStream.println("ERROR: " + errorMsg);
  }
}
