import java.util.Vector;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Ant task main class
 * 
 * @author Jay Huang
 */

public class ClassFinderAnt extends Task {

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

  /** Whether to output results to files */
  private boolean outputToFile = false;

  /** Whether to recursively scan all classes under the base directory */
  private boolean recurScan = true;

  private String resultPath = System.getProperty("user.dir");

  Vector<Path> paths = new Vector<Path>();

  /** Classpath and the base directory */
  private String classpath, path;

  /**
   * Add classpath
   * 
   * @param p path to be added
   */
  public void addPath(Path p) {
    paths.add(p);

  }

  /**
   * Set base directory
   * 
   * @param p base directory
   */
  public void setPath(String p) {
    path = p;

  }

  /** Set where to store jumble results */
  public void setResultPath(String val) {
    resultPath = val;

  }

  public void setVerbose(boolean val) {
    mVerbose = val;
  }

  public void setInlineConstants(boolean val) {
    mInlineConstants = val;
  }

  public void setReturnVals(boolean val) {
    mReturnVals = val;
  }

  public void setStores(boolean val) {
    mStores = val;
  }

  public void setIncrements(boolean val) {
    mIncrements = val;
  }

  public void setCPool(boolean val) {
    mCPool = val;
  }

  public void setSwitches(boolean val) {
    mSwitches = val;
  }

  public void setOrdered(boolean val) {
    mOrdered = val;
  }

  public void setOutputToFile(boolean val) {
    outputToFile = val;
  }

  public void setRecurScan(boolean val) {
    recurScan = val;

  }

  /**
   * The main method.
   * 
   * @param args the path of the directory
   */

  public void execute() {

    String cp = "";
    String pathsep = System.getProperty("path.separator");
    for (Path p : paths) {
      cp = cp + p.toString() + pathsep;
    }

    ClassFinder cf = new ClassFinder(path, cp);

    cf.setInlineConstants(mInlineConstants);
    cf.setReturnVals(mReturnVals);
    cf.setStores(mStores);
    cf.setIncrements(mIncrements);
    cf.setCPool(mCPool);
    cf.setSwitches(mSwitches);
    cf.setOrdered(mOrdered);
    cf.setVerbose(mVerbose);
    cf.setOuputResultsToFile(outputToFile);
    cf.setResultPath(resultPath);
    cf.setRecurScan(recurScan);
    cf.runJumbleOnAllClasses();

  }

}