
package jumble.util;

import java.util.Properties;

import java.io.IOException;
/** Class to run a java process with the same JRE and classpath
 * as this JRE.
 * @author Tin Pavlinic
 */
public class JavaRunner {
    private String mClassName;
    private String [] mArgs;
    /** Constructor.
     * @param className The name of the class to run
     */
    public JavaRunner(String className) {
        this(className, new String[0]);
    }
    /** Constructor
     * @param className of the class to run
     * @param arguments the arguments to pass to the main method.
     */
    public JavaRunner(String className, String [] arguments) {
        mClassName = className;
        mArgs = arguments;
    }
    /** Gets the name of the class to run
     * @return the class name
     */
    public String getClassName() {
        return mClassName;
    }
    /** Sets the class to run
     * @param newName name of the new class to run. Must contain a main
     * method.
     */
    public void setClassName(String newName) {
        mClassName = newName;
    }
    /** Gets the arguments to pass to the process
     * @return the arguments
     */
    public String [] getArguments() {
        return mArgs;
    }
    /** Sets the arguments to pass to the main method
     * @param args the new arguments.
     */
    public void setArguments(String [] args) {
        mArgs = args;
    }
    
    /** Starts the java process.
     * @return the running java process
     * @throws IOException if something goes wrong.
     */
    public Process start() throws IOException {

         //get the properties
         Properties props = System.getProperties();
         final String LS = props.getProperty("file.separator");
         final String JAVAHOME = props.getProperty("java.home");
         final String CLASSPATH = props.getProperty("java.class.path");
            
         //create the java command
         StringBuffer command = new StringBuffer();
         command.append(JAVAHOME + LS + "bin" + LS + "java ");
         command.append("-cp " + CLASSPATH + " ");
         command.append(getClassName());
	     
         for(int i = 0; i < getArguments().length; i++) {
	             command.append(" " + getArguments()[i]);
	     }
         
         return Runtime.getRuntime().exec(command.toString());

    }

}
