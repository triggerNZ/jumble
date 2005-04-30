
package jumble.util;

import java.util.Properties;

import java.io.IOException;

public class JavaRunner {
    private String mClassName;
    private String [] mArgs;
    
    public JavaRunner(String className) {
        this(className, new String[0]);
    }
    
    public JavaRunner(String className, String [] arguments) {
        mClassName = className;
        mArgs = arguments;
    }
    
    public String getClassName() {
        return mClassName;
    }
    
    public void setClassName(String newName) {
        mClassName = newName;
    }
    public String [] getArguments() {
        return mArgs;
    }
    public void setArguments(String [] args) {
        mArgs = args;
    }
    
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
