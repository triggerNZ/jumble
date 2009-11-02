import java.io.File;
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

public class ClassFinder {

	private String basedir, cp;
	private boolean ouputResultsToFile,verbose;

	private static final String pathsep = System.getProperty("path.separator");
	private static final String dirsep = System.getProperty("file.separator");
	private static final String classpath = System
			.getProperty("java.class.path");

	public ClassFinder(String basedir, String cp) {
		this.basedir = basedir;
		this.cp = cp;
		ouputResultsToFile = false;
		verbose = false;
	}

	public void setOuputResultsToFile(boolean val) {
		ouputResultsToFile = val;
	}
	
	public void setVervos(boolean val){verbose = val;}

	public void runJumbleOnAllClasses() {

		PrintStream orgStream = null;
		PrintStream outFileStream = null;
		PrintStream orgErr = null;
		PrintStream errFileStream = null;

		// A list to store non testing classes to be mutated.

		// Current class path

		// directory path
		String path = " ";

		DirectoryScanner ds = new DirectoryScanner();

		try {

			orgStream = System.out;
			orgErr = System.err;

			if (ouputResultsToFile) {
				File out = new File("output.txt");
				File err = new File("error.txt");
				
				try {
					out.delete();
					err.delete();
				} finally {
					out = new File("output.txt");
					err = new File("error.txt");
				}
				
				outFileStream = new PrintStream(new FileOutputStream(out, true));
				errFileStream = new PrintStream(new FileOutputStream(err, true));
				// Redirecting console output to file
				System.setOut(outFileStream);
				// Redirecting console output to file
				System.setErr(errFileStream);
			}
			// If no argument is specify, the default path is the working
			// directory
			path = basedir.length() > 0 ? basedir : System
					.getProperty("user.dir");
			File dir = new File(path).getAbsoluteFile();
			path = dir.getAbsolutePath();
			System.out.println("Base Dir is " + path);

			String[] includes = { "**\\*.class" };
			ds.setBasedir(path);
			ds.setIncludes(includes);
			ds.scan();
			String[] directories = ds.getNotIncludedDirectories();

			for (String sds : directories) {
				System.out.println(sds);

			}

			// Set system class path.
			System.setProperty("java.class.path", classpath + pathsep + path
					+ pathsep + cp);

			for (String relativePath : directories) {
				String abPath = (relativePath.length() == 0) ? path : path
						+ dirsep + relativePath;
				System.out.println("Current Directory is : " + abPath);
				runJumbleInDirectory(path, relativePath, ".class", " ");
				System.out
						.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
								+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			}
		}

		catch (Exception e) {

			e.printStackTrace();

		}

		finally {
			// Restoring back to console
			System.setOut(orgStream);
			System.setErr(orgStream);
		}

	}

	public List<ClassToBeMutated> scanClasses(final String directory,
			final String included, final String excluded) {

		List<ClassToBeMutated> mutatingClass = new ArrayList<ClassToBeMutated>();

		// Look up classes.
		JavaClass[] claz = MultiRepository.lookUpClasses(directory, included,
				excluded);
		System.out.println("scanClasses in " + directory + " finds "
				+ claz.length);
		for (int c = 0; c < claz.length; c++) {
			System.out.println("    " + c + ": " + claz[c].getClassName());
		}
		if (claz.length == 0) {
			return mutatingClass;
		}

		/*
		 * Match classes to corresponding test classes, by the naming
		 * convention. e.g if a class' name is xyz.class, then every class in
		 * the directory with name xyzTest.class will be regarded as a test
		 * class of xyz.class and added to the test class list.
		 */

		for (int i = 0; i < claz.length; i++) {

			ClassToBeMutated mclazz = new ClassToBeMutated(claz[i]);

			for (int j = 0; j < claz.length; j++) {
				if (claz[j].getClassName().equals(
						claz[i].getClassName() + "Test")) {
					System.out.println("Adding class " + claz[i].getClassName()
							+ " with test " + claz[j].getClassName());
					mclazz.addTest(claz[j]);
					break;
				}
			}
			mutatingClass.add(mclazz);
		}

		return mutatingClass;

	}

	private void runJumble(String classpath,
			List<ClassToBeMutated> mutatingClass, JumbleListener listener) {

		FastRunner runner = new FastRunner();
		runner.setVerbose(verbose);
		String origClassPath = runner.getClassPath();
		runner.setClassPath(origClassPath + pathsep + classpath);
		// Print classes and test classes.
		for (ClassToBeMutated c : mutatingClass) {
			List<String> testNames = new ArrayList<String>();
			for (JavaClass jc : c.getTests()) {
				System.out.print(c.getName() + "    TestClass :   "
						+ jc.getClassName() + "   ");
				testNames.add(jc.getClassName());
			}

			System.out.println();
			try {

				if (testNames.size() > 0) {
					System.out.println("---- JUMBLE class path is "
							+ runner.getClassPath());
					runner.runJumble(c.getName(), testNames, listener);
					System.out.println("---- FINISHED JUMBLING " + c.getName()
							+ "\n");
				}

			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	}

	public void runJumbleInDirectory(String baseDir, String directory,
			String included, String excluded) {

		List<ClassToBeMutated> mutatingClass = scanClasses(baseDir + dirsep
				+ directory, included, excluded);

		runJumble(baseDir, mutatingClass, new JumbleScorePrinterListener());

	}

}
