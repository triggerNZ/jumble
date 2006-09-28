package jumble.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.reeltwo.util.Debug;

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
   * @param className
   *          The name of the class to run
   */
  public JavaRunner(String className) {
    this(className, new String[0]);
  }

  /**
   * Constructor
   * 
   * @param className
   *          of the class to run
   * @param arguments
   *          the arguments to pass to the main method.
   */
  public JavaRunner(String className, String[] arguments) {
    mClassName = className;
    mArgs = arguments;

    Properties props = System.getProperties();
    String ls = props.getProperty("file.separator");
    mJvmBin = props.getProperty("java.home") + ls + "bin" + ls + "java";

    String[] systemProperties = getProps();
    final int initialLength = (JumbleUtils.isAssertionsEnabled() ? 3 : 2);
    
    
    mJvmArgs = new String[initialLength + systemProperties.length];
    mJvmArgs[0] = "-cp";
    mJvmArgs[1] = props.getProperty("java.class.path");
    if (JumbleUtils.isAssertionsEnabled()) {
      mJvmArgs[2] = "-ea";
    }
    System.arraycopy(systemProperties, 0, mJvmArgs, initialLength, systemProperties.length);
  }

  private static String[] getProps() {
    Map props = (Map) System.getProperties().clone();
    cleanProps(props);
    String[] ret = new String[props.size()];
    Iterator it = props.entrySet().iterator();
    
    for (int i = 0; i < ret.length; i++) {
      Map.Entry entry = (Map.Entry) it.next();
      ret[i] = "-D" + entry.getKey() + "=\"" + entry.getValue() + "\"";
    }
    return ret;
  }
  
  private static void cleanProps(Map props) {
    props.remove("java.runtime.name");
    props.remove("sun.boot.library.path");
    props.remove("java.vendor");
    props.remove("java.vendor.url.bug");
    props.remove("java.vm.version");
    props.remove("java.vendor.url");
    props.remove("java.vm.vendor");
    props.remove("path.separator");
    props.remove("line.separator");
    props.remove("file.separator");
    props.remove("java.vm.name");
    props.remove("java.home");
    props.remove("sun.os.patch.level");
    props.remove("java.vm.specification.name");
    props.remove("java.vm.specification.version");   
    props.remove("file.encoding.pkg");
    props.remove("user.country");
    props.remove("user.dir");
    props.remove("java.runtime.version");
    props.remove("java.awt.graphicsenv");
    props.remove("java.endorsed.dirs");
    props.remove("os.arch");
    props.remove("java.io.tmpdir");
    props.remove("java.vm.specification.vendor");
    props.remove("user.variant");
    props.remove("os.name");
    props.remove("sun.jnu.encoding");
    props.remove("java.library.path");
    props.remove("java.class.path");
    props.remove("java.specification.name");
    props.remove("java.specification.vendor");
    props.remove("java.specification.version");
    props.remove("java.class.version");
    props.remove("sun.management.compiler");
    props.remove("os.version");
    props.remove("user.home");
    props.remove("user.name");
    props.remove("user.timezone");
    props.remove("java.awt.printerjob");
    props.remove("file.encoding");
    props.remove("sun.arch.data.model");
    props.remove("user.language");
    props.remove("awt.toolkit");
    props.remove("java.vm.info");
    props.remove("java.version");
    props.remove("java.ext.dirs");
    props.remove("sun.boot.class.path");
    props.remove("sun.io.unicode.encoding");
    props.remove("sun.cpu.endian");
    props.remove("sun.desktop");
    props.remove("sun.cpu.isalist");
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
   * @param newName
   *          name of the new class to run. Must contain a main method.
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
   * Sets the arguments to pass to the JVM. The default JVM arguments includes
   * the classpath for the JVM to use, so if you supply new JVM arguments, you
   * should probably include classpath settings.
   * 
   * @param args
   *          the new arguments.
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
   * @param args
   *          the new arguments.
   */
  public void setArguments(String[] args) {
    mArgs = args;
  }

  /**
   * Starts the java process.
   * 
   * @return the running java process
   * @throws IOException
   *           if something goes wrong.
   */
  public Process start() throws IOException {
    String[] command = getExecArgs();
    assert Debug.println(toString(command));
    return Runtime.getRuntime().exec(command);
  }

  /**
   * Creates the actual arguments used to start the process. This incorporates
   * the user-supplied arguments plus the explicit location of the JVM, and the
   * classpath to supply.
   * 
   * @return a <code>String[]</code> value
   */
  private String[] getExecArgs() {
    final int baseArgs = 2 + (mJvmArgs == null ? 0 : mJvmArgs.length);

    // create the java command
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
