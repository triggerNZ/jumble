package jumble.util;


import com.reeltwo.util.Debug;
import java.io.IOException;
import java.util.Properties;

/**
 * Class to run a java process with the same settings as this JRE.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class JavaRunner {

  private final String mJvmBin;

  private String mClassName;

  private String[] mArgs;

  private String[] mJvmArgs;

  /**
   * Constructor.
   * 
   * @param className The name of the class to run
   */
  public JavaRunner(String className) {
    this(className, new String[0]);
  }

  /**
   * Constructor
   * 
   * @param className of the class to run
   * @param arguments the arguments to pass to the main method.
   */
  public JavaRunner(String className, String[] arguments) {
    mClassName = className;
    mArgs = arguments;

    Properties props = System.getProperties();
    String ls = props.getProperty("file.separator");
    mJvmBin = props.getProperty("java.home") + ls + "bin" + ls + "java";

    mJvmArgs = new String[2];
    mJvmArgs[0] = "-cp";
    mJvmArgs[1] = props.getProperty("java.class.path");
  }

  /**
   * Gets the name of the class to run
   * 
   * @return the class name
   */
  public String getClassName() {
    return mClassName;
  }

  /**
   * Sets the class to run
   * 
   * @param newName name of the new class to run. Must contain a main method.
   */
  public void setClassName(String newName) {
    mClassName = newName;
  }

  /**
   * Gets the arguments to pass to the JVM.
   * 
   * @return the arguments
   */
  public String[] getJvmArguments() {
    return mJvmArgs;
  }

  /**
   * Sets the arguments to pass to the JVM.  The default JVM arguments
   * includes the classpath for the JVM to use, so if you supply new
   * JVM arguments, you should probably include classpath settings.
   * 
   * @param args the new arguments.
   */
  public void setJvmArguments(String[] args) {
    mJvmArgs = args;
  }

  /**
   * Gets the arguments to pass to the main method
   * 
   * @return the arguments
   */
  public String[] getArguments() {
    return mArgs;
  }

  /**
   * Sets the arguments to pass to the main method
   * 
   * @param args the new arguments.
   */
  public void setArguments(String[] args) {
    mArgs = args;
  }

  /**
   * Starts the java process.
   * 
   * @return the running java process
   * @throws IOException if something goes wrong.
   */
  public Process start() throws IOException {
    String[] command = getExecArgs();
    assert Debug.println(toString(command));
    return Runtime.getRuntime().exec(command);
  }

  /**
   * Creates the actual arguments used to start the process.  This
   * incorporates the user-supplied arguments plus the explicit
   * location of the JVM, and the classpath to supply.
   *
   * @return a <code>String[]</code> value
   */
  private String[] getExecArgs() {
    final int baseArgs = 2 + (mJvmArgs == null ? 0 : mJvmArgs.length);

    //create the java command
    String[] command = new String[baseArgs + getArguments().length];
    int idx = 0;
    command[idx++] = mJvmBin;
    if (mJvmArgs != null) {
      for (int i = 0; i < mJvmArgs.length; i++) {
        command[idx++] = mJvmArgs[i];
      }
    }
    command[idx++] = getClassName();
    for (int i = 0; i < getArguments().length; i++) {
      command[idx++] = getArguments()[i];
    }
    return command;
  }

  private String toString(String[] command) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < command.length; i++) {
      if (i != 0) {
        sb.append(' ');
      }
      sb.append(command[i]);
    }
    return sb.toString();
  }

  public String toString() {
    return toString(getExecArgs());
  }
}
