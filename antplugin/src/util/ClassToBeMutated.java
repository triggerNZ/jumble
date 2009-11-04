package util;

import java.util.ArrayList;
import java.util.List;
import org.apache.bcel.classfile.JavaClass;

/**
 * This class represents a class to be mutated.
 * 
 * @author Jay Huang
 * 
 */

public class ClassToBeMutated {

  // A list to store corresponding test classes
  private List<JavaClass> tests;

  // The mutated class, used internally as a JavaClass object.
  private JavaClass claz;

  //Constructor
  public ClassToBeMutated(JavaClass c) {
    claz = c;

    tests = new ArrayList<JavaClass>();
  }

  /** @return the name of this class */
  public String getName() {
    return claz.getClassName();
  }

  /*
   * Add a test class to the list.
   * 
   * @param test the class to be added.
   */

  public void addTest(JavaClass test) {

    //If the class is not already in the list, add class.
    if (!tests.contains(test))

      tests.add(test);

  }

  /** @return the test classes list. */
  public List<JavaClass> getTests() {
    return tests;
  }

  /** @return the JavaClass of this class object. */
  public JavaClass getJavaClass() {
    return claz;
  }

}
