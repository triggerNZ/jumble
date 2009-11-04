package util;

import java.util.HashSet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.bcel.util.ClassPath;
import util.MultiRepository;

/**
 * Test of </code>MultiRepository<code> class
 * 
 * @author Jay Huang
 * 
 */

public class MultiRepositoryTest extends TestCase {

  private static final String dirsep = System.getProperty("file.separator");
  private static final String classpath = System.getProperty("user.dir") + dirsep + "example";
  private static final SyntheticRepository rep = SyntheticRepository.getInstance(new ClassPath(classpath));

  HashSet<JavaClass> classes;

  public MultiRepositoryTest(String name) {
    super(name);

  }

  public static Test suite() {
    return new TestSuite(MultiRepositoryTest.class);

  }

  @Override
  public void setUp() {
    System.out.println(classpath);
    classes = new HashSet<JavaClass>();

    try {
      classes.add(rep.loadClass("Mover"));
      classes.add(rep.loadClass("MoverTest"));
      classes.add(rep.loadClass("Mover2"));
      classes.add(rep.loadClass("Mover2Test"));
      classes.add(rep.loadClass("Mover3"));
      classes.add(rep.loadClass("Mover3Test"));

    } catch (ClassNotFoundException e) {

      e.printStackTrace();
    }

  }

  public void testLookupClasses() {

    JavaClass[] lookedupClasses = MultiRepository.lookUpClasses(classpath, ".class", " ");

    // Compare size
    assertEquals(classes.size(), lookedupClasses.length);

    for (JavaClass claz : lookedupClasses) {
      assertTrue(classes.contains(claz));
    }

    lookedupClasses = MultiRepository.lookUpClasses(classpath, ".class", "Test.class");

    assertEquals(3, lookedupClasses.length);
    for (JavaClass claz : lookedupClasses) {
      assertTrue(classes.contains(claz));
      assertFalse(claz.getClassName().endsWith("Test"));
    }

    lookedupClasses = MultiRepository.lookUpClasses(classpath, ".sdadasdas", " ");
    assertEquals(0, lookedupClasses.length);

    lookedupClasses = MultiRepository.lookUpClasses(classpath, "Mover.class", " ");
    assertEquals(1, lookedupClasses.length);
    assertEquals("Mover", lookedupClasses[0].getClassName());

  }
}
