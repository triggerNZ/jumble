package util;
import java.io.*;

/**
 * This Class is used to filter file names in a directory so that
 * only files with name ending with the specified phrase are  
 * identified. It is used implicitly when </code>list()</code> method of 
 * File class is called.
 * 
 * @author Jay Huang
*/

public class JavaFilenameFilter implements FilenameFilter {
 
String name,rejectname;
	
/* Constructor
 * @param filename specify the name that filter should look for
 * @param reject   specify the name that filter should ignore
*/

public JavaFilenameFilter(String filename, String reject){
	
	this.name = filename;
	
	this.rejectname = reject;
	
}

public boolean accept(File dir,String filename){
	
	return !filename.endsWith ( this.rejectname ) && 
	        filename.endsWith ( this.name ) ;
		}
}
