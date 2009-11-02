import util.*;



import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;   
import java.io.File;
import java.io.FileOutputStream;   
import java.io.PrintStream;   

import org.apache.bcel.classfile.*;
import org.apache.tools.ant.DirectoryScanner;
import com.reeltwo.jumble.fast.FastRunner;

import com.reeltwo.jumble.ui.JumbleListener;
import com.reeltwo.jumble.ui.JumbleScorePrinterListener;
//import com.reeltwo.jumble.ui.PrinterListener;


/**
 * Search .class files in a given directory For every non testing class, assign
 * corresponding test classes.
 * 
 * @author Jay Huang
 */

public class ClassFinderCmdLine {

	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the path of the directory
	 */
	
	

	public static void main(String[] args) {
		ClassFinder cf = new ClassFinder((args.length > 0)?args[0]:"",null);
		cf.setOuputResultsToFile(true);
		cf.runJumbleOnAllClasses();

	}
		
}