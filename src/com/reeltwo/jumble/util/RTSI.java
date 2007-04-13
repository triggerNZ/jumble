package com.reeltwo.jumble.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The Run Time Subclass Identification class is used to determine all
 * subclasses of a given class in a set of given packages.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class RTSI {
  private static final String CLASSPATH = System.getProperty("java.class.path");

  private static final String PS = System.getProperty("path.separator");

  private static final String FS = System.getProperty("file.separator");

  private RTSI() { }

  /**
   * Display all the classes inheriting or implementing a given class in a given
   * package.
   * 
   * @param pckname
   *          the fully qualified name of the package
   * @param tosubclassname
   *          the name of the class to inherit from
   */
  public static Collection find(String pckname, String tosubclassname)
    throws ClassNotFoundException {
    Class c = Class.forName(tosubclassname);
    return find(pckname, c);
  }

  /**
   * Get all the classes inheriting or implementing a given class in a given
   * package.
   * 
   * @param pckgname
   *          the fully qualified name of the package
   * @param tosubclass
   *          the Class object to inherit from
   * @return a Collection containing the classes
   */
  public static Collection find(String pckgname, Class tosubclass) {

    StringTokenizer tokens = new StringTokenizer(CLASSPATH, PS);
    ArrayList dirs = new ArrayList();
    ArrayList jars = new ArrayList();

    while (tokens.hasMoreTokens()) {
      File f = new File(tokens.nextToken());
      if (f.isDirectory()) {
        dirs.add(f);
      } else if (f.isFile() && f.getName().endsWith(".jar")) {
        jars.add(f);
      }
      // ignore all other files
    }

    ArrayList ret = new ArrayList();

    ret.addAll(getClassesFromDirs(dirs, pckgname, tosubclass));
    ret.addAll(getClassesFromJars(jars, pckgname, tosubclass));
    return ret;
  }

  private static Collection getClassesFromDirs(Collection dirs,
                                               String packageName, Class baseClass) {
    Collection ret = new HashSet();
    // System.out.println(packageName);
    Iterator dirIterator = dirs.iterator();

    while (dirIterator.hasNext()) {
      File currentDir = (File) dirIterator.next();

      // Need to check this
      File packageDir = new File(currentDir.getAbsolutePath() + FS
                                 + packageName.replace('.', FS.charAt(0)));

      File[] classes = packageDir.listFiles(new FileFilter() {
          public boolean accept(File f) {
            return f.getName().endsWith(".class");
          }
        });
      if (classes == null) {
        continue;
      }
      for (int i = 0; i < classes.length; i++) {
        // [package name] + filename -".class"
        String className = packageName
          + "."
          + classes[i].getName().substring(0,
                                           classes[i].getName().length() - 6);
        Class clazz = null;
        try {
          clazz = Class.forName(className);
        } catch (NoClassDefFoundError e) {
          ; // This is no problem, sometimes happens
        } catch (Throwable e) {
          System.err.println("Error getting " + className);
          e.printStackTrace();
          System.err.println("Attempting to continue...");
          // All kinds of things can happen here
          // try to report and ignore the error
        }

        if (isSubClass(clazz, baseClass)) {
          // System.out.println(className);
          ret.add(className);
        }
      }
    }
    return ret;
  }

  private static boolean isSubClass(Class clazz, Class base) {
    if (base.isInterface()) {
      Class[] interfaces = clazz.getInterfaces();
      for (int i = 0; i < interfaces.length; i++) {
        if (interfaces[i] == base) {
          return true;
        }
      }
      return false;
    } else {
      while (clazz != null) {
        if (clazz == base) {
          return true;
        }
        clazz = clazz.getSuperclass();
      }
      return false;
    }
  }

  private static Collection getClassesFromJars(Collection jars,
                                               String packageName, Class baseClass) {
    Collection ret = new HashSet();

    Iterator jarsIt = jars.iterator();
    while (jarsIt.hasNext()) {
      try {
        JarFile jar = new JarFile((File) jarsIt.next());
        Enumeration entries = jar.entries();

        while (entries.hasMoreElements()) {
          JarEntry curEntry = (JarEntry) entries.nextElement();
          // System.out.println(curEntry);
          // only look at actual class files
          if (!curEntry.isDirectory() && curEntry.toString().endsWith(".class")) {
            String className = curEntry.toString().substring(0,
                                                             curEntry.toString().length() - 6).replace('/', '.');
            String pack = "";
            if (className.indexOf('.') > 0) {
              pack = className.substring(0, className.lastIndexOf("."));
            }
            if (pack.equals(packageName)) {
              try {
                Class c = Class.forName(className);

                if (isSubClass(c, baseClass)) {
                  ret.add(className);
                }
              } catch (NoClassDefFoundError e) {
                ; // this can sometimes happen, no worries
              } catch (Throwable e) {
                System.err.println("Error getting " + className);
                e.printStackTrace();
                System.err.println("Attempting to continue...");
                // All kinds of things can happen here
                // try to report and ignore the error
              }
            }

          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return ret;
  }

  /**
   * Goes through the class path and finds all visible packages.
   * 
   * @return a <CODE>Collection</CODE> of all visible packages.
   */
  public static Collection getAllVisiblePackages() {
    StringTokenizer tokens = new StringTokenizer(CLASSPATH, PS);
    ArrayList dirs = new ArrayList();
    ArrayList jars = new ArrayList();

    while (tokens.hasMoreTokens()) {
      File f = new File(tokens.nextToken());
      if (f.isDirectory()) {
        dirs.add(f.getAbsolutePath());
      } else if (f.isFile() && f.getName().endsWith(".jar")) {
        jars.add(f.getAbsolutePath());
      }

      // ignore all other files
    }

    ArrayList ret = new ArrayList();

    ret.addAll(getPackagesFromDirs(dirs));
    ret.addAll(getPackagesFromJars(jars));
    ret.add("");
    return ret;
  }

  private static Collection getPackagesFromDirs(Collection dirs) {
    Collection ret = new ArrayList();
    Iterator it = dirs.iterator();

    while (it.hasNext()) {
      String dir = (String) it.next();
      ret.addAll(getPackages(dir, null));
    }
    return ret;
  }

  private static Collection getPackages(String dir, String prefix) {
    Collection ret = new ArrayList();
    File f = new File(dir);

    assert f.exists();
    assert f.isDirectory();

    File[] contents = f.listFiles();
    assert contents != null;

    for (int i = 0; i < contents.length; i++) {
      if (contents[i].isDirectory()) {
        String dirName = contents[i].getName();
        String packageName = (prefix == null ? "" : prefix + ".") + dirName;
        ret.add(packageName);
        ret.addAll(getPackages(contents[i].getAbsolutePath(), packageName));
      }
    }
    return ret;
  }

  private static Collection getPackagesFromJars(Collection jars) {
    Collection ret = new ArrayList();
    Iterator it = jars.iterator();

    while (it.hasNext()) {
      String jarfile = (String) it.next();
      ret.addAll(getJarPackages(jarfile));
    }
    return ret;
  }

  private static Collection getJarPackages(String jarfile) {
    Collection ret = new ArrayList();
    try {
      JarFile jar = new JarFile(jarfile);
      Enumeration currentEntries = jar.entries();

      while (currentEntries.hasMoreElements()) {
        JarEntry entry = (JarEntry) currentEntries.nextElement();

        if (entry.isDirectory()) {
          ret.add(entry.getName().replace('/', '.').substring(0,
                                                              entry.getName().length() - 1));
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return ret;
  }

  public static void main(String[] args) {
    try {
      System.out.println(find(args[0], args[1]));
    } catch (Exception e) {
      System.out.println("Usage: java RTSI [<package>] <subclass>");

    }
  }
}
