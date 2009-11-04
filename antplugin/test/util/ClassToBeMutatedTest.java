package util;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.SyntheticRepository;
import org.apache.bcel.util.ClassPath;

/**
 * 
 * @author Jay Huang
 *
 */
public class ClassToBeMutatedTest extends TestCase {

  private static final String dirsep = System.getProperty("file.separator");
  private static final String classpath = System.getProperty("user.dir") + dirsep + "example";
  private static final SyntheticRepository rep = SyntheticRepository.getInstance(new ClassPath(classpath));

  private ClassToBeMutated mClass;
  private JavaClass claz;

  private List<JavaClass> tests;

  public ClassToBeMutatedTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(ClassToBeMutatedTest.class);

  }

  @Override
  public void setUp() {

    tests = new ArrayList<JavaClass>();

    try {
      claz = rep.loadClass("Mover");
      mClass = new ClassToBeMutated(claz);
      tests.add(rep.loadClass("MoverTest"));
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void testGetName() {

    assertEquals("Mover", mClass.getName());
    mClass = null;

    try {
      assertNull(mClass.getName());
      fail();
    } catch (Exception e) {

    }

  }

  public void testAddTestGetTests() throws ClassNotFoundException {

    JavaClass testClass = rep.loadClass("MoverTest");
    mClass.addTest(testClass);
    assertEquals(tests, mClass.getTests());

    testClass = rep.loadClass("Mover2Test");

    mClass.addTest(testClass);
    tests.add(testClass);
    assertEquals(tests, mClass.getTests());

    mClass.addTest(testClass);
    assertEquals(2, mClass.getTests().size());
    assertEquals(tests, mClass.getTests());

  }

  public void testGetJavaClass() {
    assertEquals(claz, mClass.getJavaClass());
    mClass = null;

    try {
      assertNull(mClass.getJavaClass());
      fail();
    } catch (Exception e) {
    }

  }

}
