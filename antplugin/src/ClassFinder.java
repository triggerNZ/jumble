import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import util.MultiRepository;
import util.ClassToBeMutated;
import org.apache.bcel.classfile.*;
import org.apache.tools.ant.DirectoryScanner;
import com.reeltwo.jumble.fast.FastRunner;
import com.reeltwo.jumble.ui.JumbleListener;
import com.reeltwo.jumble.ui.JumbleScorePrinterListener;

/**
 * 
 * @author Jay Huang
 * 
 */

/**
* 
* This class finds all classes in a given directory or all classes in all sub-directory in the given directory, then
* run jumble on these classes if every one of them has at least one valid test class. 
*
*/
public class ClassFinder {

  private static final String pathsep = System.getProperty("path.separator");
  private static final String dirsep = System.getProperty("file.separator");
  private static final String sysClasspath = System.getProperty("java.class.path");

  /** Output file names */
  private static final String outFileName = "jumble_output.txt";
  private static final String errFileName = "jumble_errput.txt";

  /** Whether to recursively scan all classes under the base directory */
  private boolean recurScan = false;

  /** Base directory of all classes to be scanned, and the class path */
  private String basedir, cp;

  /** Whether to output jumble results to files */
  private boolean ouputResultsToFile;

  /** Whether to mutate constants */
  private boolean mInlineConstants = true;

  /** Whether to mutate return values */
  private boolean mReturnVals = true;

  /** Whether to mutate stores */
  private boolean mStores = false;

  /** Whether to mutate increments */
  private boolean mIncrements = true;

  /** Whether to mutate constant pool */
  private boolean mCPool = true;

  /** Whether to mutate switches */
  private boolean mSwitches = true;

  private boolean mOrdered = true;

  private boolean mVerbose = false;

  /**
   * Construtor
   * 
   * @param basedir base directory
   * @param cp classpath
   */
  public ClassFinder(String basedir, String cp) {
    this.basedir = basedir.length() > 0 ? basedir : System.getProperty("user.dir");
    this.cp = cp;
    ouputResultsToFile = false;

  }

  /** Set whether to output to files */
  public void setOuputResultsToFile(boolean val) {
    ouputResultsToFile = val;
  }

  /** Set verbose mode */
  public void setVerbose(boolean val) {
    mVerbose = val;
  }

  /** Set mutate inline constants */
  public void setInlineConstants(boolean val) {
    mInlineConstants = val;
  }

  /** Set mutate return values */
  public void setReturnVals(boolean val) {
    mReturnVals = val;
  }

  /** Set mutate stores */
  public void setStores(boolean val) {
    mStores = val;
  }

  /** Set mutate increments */
  public void setIncrements(boolean val) {
    mIncrements = val;
  }

  /** Set mutate constant pool */
  public void setCPool(boolean val) {
    mCPool = val;
  }

  /** Set mutate switches */
  public void setSwitches(boolean val) {
    mSwitches = val;
  }

  public void setOrdered(boolean val) {
    mOrdered = val;
  }

  public void setRecurScan(boolean val) {
    recurScan = val;
  }

  /**
   * Run jumble on all classes inside the base directory, including classes
   * within sub-directories.
   * 
   * @see scanClasses, runJumbleInDirecotry
   */
  public void runJumbleOnAllClasses() {

    PrintStream orgStream = null;
    PrintStream orgErr = null;

    PrintStream outFileStream = null;
    PrintStream errFileStream = null;

    // Set the base directory with default being the user directory if empty 
    // directory is parsed into the constructor
    String path = basedir;
    File dir = new File(path).getAbsoluteFile();
    path = dir.getAbsolutePath();
    System.out.println("Base Dir is " + path);

    // Set system class path.
    System.setProperty("java.class.path", sysClasspath + pathsep + path + pathsep + cp);

    DirectoryScanner ds = new DirectoryScanner();

    try {

      orgStream = System.out;
      orgErr = System.err;

      if (ouputResultsToFile) {
        File out = new File("outFileName");
        File err = new File("errFileName");

        try {
          out.delete();
          err.delete();
        } finally {
          out = new File("outFileName");
          err = new File("errFileName");
        }

        outFileStream = new PrintStream(new FileOutputStream(out, true));
        errFileStream = new PrintStream(new FileOutputStream(err, true));

        // Redirecting console output to file
        System.setOut(outFileStream);
        // Redirecting console output to file
        System.setErr(errFileStream);
      }

      //Scan classes in base directory and run jumble on these classes
      if (!recurScan) {
        try {
          runJumbleInDirectory(path, "", ".class", " ");
        }

        catch (Exception e) {
          e.printStackTrace();
        } finally {
          return;
        }
      }

      //Recursively scan all classes within the base directory
      String[] includes = { "**\\*.class" };
      ds.setBasedir(path);
      ds.setIncludes(includes);
      ds.scan();
      String[] directories = ds.getNotIncludedDirectories();

      //For each scanned directory, run jumble on valid classes inside it. 
      for (String relativePath : directories) {
        runJumbleInDirectory(path, relativePath, ".class", " ");

      }
    }

    catch (Exception e) {

      e.printStackTrace();

    }

    finally {
      // Restoring back to console
      System.setOut(orgStream);
      System.setErr(orgErr);
    }

  }

  /**
   * Scan all classes within a given directory and assign test classes to
   * corresponding classes
   * 
   * @param directory the directory to be scanned
   * @param included included filename pattern
   * @param excluded excluded filename pattern
   * @return classes in the directory whose names match the patterns
   */
  public List<ClassToBeMutated> scanClasses(final String directory, final String included, final String excluded) {

    List<ClassToBeMutated> mutatingClass = new ArrayList<ClassToBeMutated>();

    // Look up classes.
    JavaClass[] claz = MultiRepository.lookUpClasses(directory, included, excluded);
    if (claz.length == 0) {
      return mutatingClass;
    }

    //  Match classes to corresponding test classes, by the naming convention.
    //  e.g if a class' name is xyz.class, then every class in the directory with
    // name xyzTest.class will be regarded as a test class of xyz.class and
    // added to the test class list.
    for (int i = 0; i < claz.length; i++) {

      ClassToBeMutated mclazz = new ClassToBeMutated(claz[i]);

      for (int j = 0; j < claz.length; j++) {
        if (claz[j].getClassName().equals(claz[i].getClassName() + "Test")) {

          mclazz.addTest(claz[j]);
          break;
        }
      }
      mutatingClass.add(mclazz);
    }

    return mutatingClass;

  }

  /**
   * Run jumble on classes in the given directory
   * 
   * @param dir the directory
   * @param mutatingClass the classes to be mutated
   * @param listener jumble listener to print results
   * 
   */
  private void runJumble(String dir, List<ClassToBeMutated> mutatingClass, JumbleListener listener) {

    FastRunner runner = new FastRunner();

    //Set jumble flags
    runner.setInlineConstants(mInlineConstants);
    runner.setReturnVals(mReturnVals);
    runner.setStores(mStores);
    runner.setIncrements(mIncrements);
    runner.setCPool(mCPool);
    runner.setSwitch(mSwitches);
    runner.setOrdered(mOrdered);
    runner.setVerbose(mVerbose);

    //Set jumble classpath
    String origClassPath = runner.getClassPath();
    runner.setClassPath(origClassPath + pathsep + dir);

    if (mutatingClass.size() > 0) {
      System.out.println("Current directory :  " + dir);
    } else {
      return;
    }

    //Run jumble on classes
    for (ClassToBeMutated c : mutatingClass) {
      List<String> testNames = new ArrayList<String>();
      for (JavaClass jc : c.getTests()) {
        System.out.println(c.getName() + "    TestClass :   " + jc.getClassName());
        testNames.add(jc.getClassName());
      }

      try {

        if (testNames.size() == 0) {
          continue;
        }
        runner.runJumble(c.getName(), testNames, listener);
        System.out.println("\n");

      } catch (Exception e) {
        e.getStackTrace();
      }
    }
  }

  /**
   * Run jumble on classes in a given directory
   * 
   * @param baseDir base directory
   * @param directory relative path to the base directory
   * @param included included filename pattern
   * @param excluded excluded filename pattern
   * @see scanClasses
   */
  public void runJumbleInDirectory(String baseDir, String directory, String included, String excluded) {

    List<ClassToBeMutated> mutatingClass = scanClasses(baseDir + dirsep + directory, included, excluded);

    runJumble(baseDir + dirsep + directory, mutatingClass, new JumbleScorePrinterListener());

  }

}
