import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.bcel.classfile.*;
import org.apache.tools.ant.DirectoryScanner;
import com.reeltwo.jumble.fast.FastRunner;

import com.reeltwo.jumble.ui.JumbleListener;
import com.reeltwo.jumble.ui.JumbleScorePrinterListener;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Search .class files in a given directory For every non testing class, assign
 * corresponding test classes.
 * 
 * @author Jay Huang
 */

public class ClassFinderAnt extends Task {
	Vector<Path> paths = new Vector<Path>();

	private String classpath, path;

	public void addPath(Path p) {
		paths.add(p);

	}

	public void setPath(String p) {
		path = p;

	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the path of the directory
	 */

	public void execute() {

		String cp = "";
		String pathsep = System.getProperty("path.separator");
		for (Path p : paths) {
			cp = cp + p.toString() + pathsep;
		}

		ClassFinder cf = new ClassFinder(path, cp);
		cf.setOuputResultsToFile(true);
		cf.runJumbleOnAllClasses();

	}

}