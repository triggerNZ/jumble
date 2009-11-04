package util;

import java.io.*;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.bcel.util.ClassPath;

/**
 * This class enables to look up java classes in a given directory
 * 
 * @author Jay Huang
 */
public class MultiRepository extends Repository {

  //Constructor	
  public MultiRepository() {

    super();
  }

  /**
   * Look up desired classes in a directory.The default directory is the
   * 
   * @param path the path of directory where classes are looked up.
   * @param filter string that indicates what class names should be looked for.
   * @param reject string that indicates what class names should be omitted.
   * @return a list of matched JavaClass objects
   */

  public static JavaClass[] lookUpClasses(String path, String filter, String reject) {

    // array to store the matched classes.
    JavaClass[] classes = null;

    try {

      // the file to be looked up for classes from.
      File file = new File(path);

      // filefilter to look for files that match the specified name 	
      JavaFilenameFilter classfilter = new JavaFilenameFilter(filter, reject);

      // Repository indicates where to look for classes from by assigning the
      // class path to be the file directory.
      SyntheticRepository rep = SyntheticRepository.getInstance(new ClassPath(path));

      // Array to store the names of matched files.
      String[] classnames = file.list(classfilter);

      classes = new JavaClass[classnames.length];

      // Load classes from the repository and store to the result list.
      for (int i = 0; i < classnames.length; i++) {

        classes[i] = rep.loadClass(classnames[i].substring(0, classnames[i].length() - 6));
      }
    }

    catch (Exception e) {
      e.printStackTrace();
    }

    return classes;
  }
}
