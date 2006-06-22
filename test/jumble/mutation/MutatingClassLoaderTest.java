package jumble.mutation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.IDIV;
import org.apache.bcel.generic.IMUL;
import org.apache.bcel.generic.IOR;
import org.apache.bcel.generic.ISHL;
import org.apache.bcel.generic.ISHR;
import org.apache.bcel.generic.ISUB;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionComparator;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ByteSequence;

/**
 * Tests the corresponding class.
 * 
 * @author Tin Pavlinic
 * @version $Revision$
 */
public class MutatingClassLoaderTest extends TestCase {

  static final String CLASSPATH = System.getProperty("java.class.path");

  public void testNoMutation() throws Exception {
    JavaClass original = new ClassParser(getClass().getClassLoader()
        .getResourceAsStream("experiments/JumblerExperiment.class"),
        "JumblerExperiment.class").parse();

    MutatingClassLoader j = new MutatingClassLoader("experiments.JumblerExperiment", new Mutater(-1), CLASSPATH);
    JavaClass a = j.modifyClass(original);
    compareJavaClasses(original, a);
    JavaClass b = j.modifyClass(original);
    compareJavaClasses(b, a);
    compareJavaClasses(b, original);
    JavaClass c = j.modifyClass(a);
    compareJavaClasses(c, b);
  }

  public void testClassLoading() throws Exception {
    MutatingClassLoader j = new MutatingClassLoader("experiments.JumblerExperiment", new Mutater(-1), CLASSPATH);
    Class clazz = j.loadClass("java.lang.Object");
    assertNotNull(clazz);
    clazz = j.loadClass("java.util.ArrayList");
    assertNotNull(clazz);
    clazz = j.loadClass("jumble.fast.TimingTestSuite");
    assertNotNull(clazz);

    j = new MutatingClassLoader("experiments.JumblerExperiment", new Mutater(-1), "");
    clazz = j.loadClass("java.lang.Object");
    assertNotNull(clazz);
    clazz = j.loadClass("java.util.ArrayList");
    assertNotNull(clazz);
    // Jumble classes are also available, because we always load them from the parent classloader
    clazz = j.loadClass("jumble.fast.TimingTestSuite");
    assertNotNull(clazz);

    // Should have a test here where we load from a new explicit
    // classpath. Perhaps copy a class file to a temporary directory
    // and use that dir as the classpath.

//     try {
//       clazz = j.loadClass("jumble.fast.TimingTestSuite");
//       fail("Expected ClassNotFoundException");
//     } catch (ClassNotFoundException e) {
//       ; // Expected
//     }
  }

  public void testListAllModifications() throws ClassNotFoundException {
    String className = "experiments.JumblerExperiment";
    Mutater mutater = new Mutater(-1);
    mutater.setMutateIncrements(true);
    mutater.setMutateInlineConstants(true);
    mutater.setMutateReturnValues(true);
    //System.err.println(CLASSPATH);

    MutatingClassLoader jumbler = new MutatingClassLoader(className, mutater, CLASSPATH);
    final int mutationCount = jumbler.countMutationPoints(className);
    assertEquals(12, mutationCount);

    // First list things when not performing any modifications
    for (int i = 0; i < mutationCount; i++) {
      mutater.setMutationPoint(i);
      String methodName = mutater.getMutatedMethodName(className);
      assertNotNull(methodName);
      int mutPoint = mutater.getMethodRelativeMutationPoint(className);
      assertTrue(mutPoint != -1);
      String modification = mutater.getModification();
      assertNull(modification);
      //System.err.println(methodName + "##" + mutPoint + "##" + modification);
    }    

    // Now list when performing modifications
    for (int i = 0; i < mutationCount; i++) {
      mutater.setMutationPoint(i);
      jumbler = new MutatingClassLoader(className, mutater, CLASSPATH);
      jumbler.loadClass(className);
      String methodName = mutater.getMutatedMethodName(className);
      assertNotNull(methodName);
      int mutPoint = mutater.getMethodRelativeMutationPoint(className);
      assertTrue(mutPoint != -1);
      String modification = mutater.getModification();
      assertNotNull(modification);
      //System.err.println(methodName + "##" + mutPoint + "##" + modification);
    }    
  }


  public final void testJumbler() throws Exception {
    JavaClass original = Repository.lookupClass("jumble.X2");

    MutatingClassLoader fj = new MutatingClassLoader("jumble.X2", new Mutater(0), CLASSPATH);

    JavaClass c1 = fj.modifyClass(original);
//     printClass(original);
//     System.out.println("-------------------");
//     printClass(c1);
    compareModification(original, c1, 3, new IDIV());

    fj = new MutatingClassLoader("jumble.X2", new Mutater(1), CLASSPATH);
    c1 = fj.modifyClass(original);
    compareModification(original, c1, 5, new IMUL());

    fj = new MutatingClassLoader("jumble.X2", new Mutater(2), CLASSPATH);
    c1 = fj.modifyClass(original);
    compareModification(original, c1, 6, new ISUB());

    fj = new MutatingClassLoader("jumble.X2", new Mutater(3), CLASSPATH);
    c1 = fj.modifyClass(original);
    compareModification(original, c1, 9, new IMUL());

    fj = new MutatingClassLoader("jumble.X2", new Mutater(4), CLASSPATH);
    c1 = fj.modifyClass(original);
    compareModification(original, c1, 11, new IMUL());

    fj = new MutatingClassLoader("jumble.X2", new Mutater(5), CLASSPATH);
    c1 = fj.modifyClass(original);
    compareModification(original, c1, 12, new IADD());

    fj = new MutatingClassLoader("jumble.X2", new Mutater(6), CLASSPATH);
    c1 = fj.modifyClass(original);
    compareModification(original, c1, 14, new ISHL());

    fj = new MutatingClassLoader("jumble.X2", new Mutater(7), CLASSPATH);
    c1 = fj.modifyClass(original);
    compareModification(original, c1, 16, new ISHR());

    fj = new MutatingClassLoader("jumble.X2", new Mutater(8), CLASSPATH);
    c1 = fj.modifyClass(original);
    compareModification(original, c1, 18, new IOR());

    fj = new MutatingClassLoader("jumble.X2", new Mutater(9), CLASSPATH);
    c1 = fj.modifyClass(original);
    compareModification(original, c1, 25, null);
  }

  private void compareModification(JavaClass orig, JavaClass mod,
                                   int mutationPoint, Instruction expected) throws Exception {
    int point = 0;

    InstructionComparator comp = Instruction.getComparator();
    Method[] methods = orig.getMethods();

    for (int i = 0; i < methods.length; i++) {
      ByteSequence origCode = new ByteSequence(methods[i].getCode().getCode());
      ByteSequence modCode = new ByteSequence(mod.getMethods()[i].getCode().getCode());

      while (origCode.available() > 0) {
        final Instruction i1 = Instruction.readInstruction(origCode);
        final Instruction i2 = Instruction.readInstruction(modCode);
        if (point == mutationPoint) {
          assertFalse(i1 + "==" + i2, comp.equals(i1, i2));
          assertTrue(i1 + "!=" + i2, comp.equals(i2, expected));
        } else {
          assertTrue(i1 + "!=" + i2, comp.equals(i1, i2));
        }
        point++;
      }
    }
  }


  /**
   * Asserts that the classes a and b are equal
   * 
   * @param a
   *          first array
   * @param b
   *          second array
   */
  private void compareJavaClasses(JavaClass a, JavaClass b) throws Exception {
    JavaClass[] i1 = a.getAllInterfaces();
    JavaClass[] i2 = b.getAllInterfaces();

    compareClassArrays(i1, i2);

    Attribute[] a1 = a.getAttributes();
    Attribute[] a2 = b.getAttributes();

    compareAttributeArrays(a1, a2);

    assertEquals(a.getAccessFlags(), b.getAccessFlags());
    assertEquals(a.getClassName(), b.getClassName());
    assertEquals(a.getClassNameIndex(), b.getClassNameIndex());

    compareConstantPools(a.getConstantPool(), b.getConstantPool());

    Field[] f1 = a.getFields();
    Field[] f2 = b.getFields();

    compareFieldArrays(f1, f2);

    int[] int1 = a.getInterfaceIndices();
    int[] int2 = b.getInterfaceIndices();
    compareIntArrays(int1, int2);

    assertEquals(a.getMajor(), b.getMajor());

    Method[] meth1 = a.getMethods();
    Method[] meth2 = b.getMethods();

    compareMethodArrays(meth1, meth2, a, b);

    assertEquals(a.getMinor(), b.getMinor());
    assertEquals(a.getPackageName(), b.getPackageName());
    assertEquals(a.getSource(), b.getSource());

    JavaClass[] sc1 = a.getSuperClasses();
    JavaClass[] sc2 = b.getSuperClasses();
    compareClassArrays(sc1, sc2);
    assertEquals(a.getSuperclassNameIndex(), b.getSuperclassNameIndex());
    assertEquals(a.isClass(), b.isClass());
    assertEquals(a.isSuper(), b.isSuper());
  }

  /**
   * Asserts that the two arrays are equal.
   * 
   * @param a1
   *          first array.
   * @param a2
   *          second array.
   */
  private void compareClassArrays(JavaClass[] a1, JavaClass[] a2)
      throws Exception {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareJavaClasses(a1[i], a2[i]);
    }
  }

  /**
   * Asserts that the two arrays are equal.
   * 
   * @param a1
   *          first array.
   * @param a2
   *          second array.
   */
  private void compareAttributeArrays(Attribute[] a1, Attribute[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareAttributes(a1[i], a2[i]);
    }
  }

  /**
   * Compares attributes
   * 
   * @param a
   *          first attribute
   * @param b
   *          second attribute
   */
  private void compareAttributes(Attribute a, Attribute b) {
    assertEquals(a.getLength(), b.getLength());
    assertEquals(a.getNameIndex(), b.getNameIndex());
    assertEquals(a.getTag(), b.getTag());

    compareConstantPools(a.getConstantPool(), b.getConstantPool());
  }

  /**
   * Compares constant pools.
   * 
   * @param a
   *          first pool
   * @param b
   *          second pool
   */
  private void compareConstantPools(ConstantPool a, ConstantPool b) {
    assertEquals(a.getLength(), b.getLength());

    for (int i = 0; i < a.getLength(); i++) {
      if (a.getConstant(i) != null) {
        // Probably shouldn't use toString but equals was not defined properly
        assertEquals(a.getConstant(i).toString(), b.getConstant(i).toString());
        assertEquals(a.getConstant(i).getClass(), b.getConstant(i).getClass());
      } else {
        assertEquals(null, b.getConstant(i));
      }
    }
  }

  /**
   * Asserts that the two arrays are equal.
   * 
   * @param a1
   *          first array.
   * @param a2
   *          second array.
   */
  private void compareFieldArrays(Field[] a1, Field[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareFields(a1[i], a2[i]);
    }
  }

  /**
   * Asserts two fields the same.
   * 
   * @param a
   *          first field
   * @param b
   *          second field
   */
  private void compareFields(Field a, Field b) {
    assertEquals(a.getAccessFlags(), b.getAccessFlags());
    assertEquals(a.getName(), b.getName());
    compareConstantPools(a.getConstantPool(), b.getConstantPool());
    assertEquals(a.toString(), b.toString());
    compareTypes(a.getType(), b.getType());
  }

  /**
   * Asserts two Type objects the same.
   * 
   * @param a
   *          first
   * @param b
   *          second
   */
  private void compareTypes(Type a, Type b) {
    assertEquals(a.getSignature(), b.getSignature());
    assertEquals(a.getSize(), b.getSize());
    assertEquals(a.getType(), b.getType());
    assertEquals(a.toString(), b.toString());
  }

  /**
   * Asserts that the two arrays are equal.
   * 
   * @param a1
   *          first array.
   * @param a2
   *          second array.
   */
  private void compareIntArrays(int[] a1, int[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      assertEquals(a1[i], a2[i]);
    }
  }

  /**
   * Asserts that the two arrays are equal.
   * 
   * @param a1
   *          first array.
   * @param a2
   *          second array.
   */
  private void compareMethodArrays(Method[] a1, Method[] a2, JavaClass classA,
      JavaClass classB) throws Exception {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareMethods(a1[i], a2[i], classA.getClassName(), new ConstantPoolGen(
          classA.getConstantPool()));
    }
  }

  private void compareMethods(Method a, Method b, String className,
      ConstantPoolGen cp) throws Exception {
    compareTypeArrays(a.getArgumentTypes(), b.getArgumentTypes());
    assertEquals(a.getName(), b.getName());
    assertEquals(a.getSignature(), b.getSignature());
    assertEquals(a.toString(), b.toString());
    assertEquals(a.getAccessFlags(), b.getAccessFlags());

    compareCode(a.getCode(), b.getCode());
    compareExceptionTable(a.getExceptionTable(), b.getExceptionTable());

    if (a.getLineNumberTable() == null) {
      assertEquals(null, b.getLineNumberTable());
    } else {
      compareLineNumberArray(a.getLineNumberTable().getLineNumberTable(), b
          .getLineNumberTable().getLineNumberTable());
    }

    if (a.getLocalVariableTable() == null) {
      assertEquals(null, b.getLocalVariableTable());
    } else {
      compareLocalVariableArray(a.getLocalVariableTable()
          .getLocalVariableTable(), b.getLocalVariableTable()
          .getLocalVariableTable());
    }
    compareTypes(a.getReturnType(), b.getReturnType());
  }

  /**
   * Asserts that the two arrays are equal.
   * 
   * @param a1
   *          first array.
   * @param a2
   *          second array.
   */
  private void compareTypeArrays(Type[] a1, Type[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareTypes(a1[i], a2[i]);
    }
  }

  /**
   * Compares exception tables.
   * 
   * @param a
   *          first
   * @param b
   *          second
   */
  private void compareExceptionTable(ExceptionTable a, ExceptionTable b) {
    if (a == null) {
      assertEquals(null, b);
      return;
    }
    compareIntArrays(a.getExceptionIndexTable(), b.getExceptionIndexTable());
    compareStringArrays(a.getExceptionNames(), b.getExceptionNames());
    assertEquals(a.getNumberOfExceptions(), b.getNumberOfExceptions());
    assertEquals(a.toString(), b.toString());
    compareAttributes(a, b);
  }

  /**
   * Asserts that the two arrays are equal.
   * 
   * @param a
   *          first array.
   * @param b
   *          second array.
   */
  private void compareLineNumberArray(LineNumber[] a, LineNumber[] b) {
    assertEquals(a.length, a.length);
    for (int i = 0; i < a.length; i++) {
      compareLineNumber(a[i], b[i]);
    }
  }

  /**
   * Compares line numbers.
   * 
   * @param a
   *          first
   * @param b
   *          second
   */
  private void compareLineNumber(LineNumber a, LineNumber b) {
    assertEquals(a.getLineNumber(), b.getLineNumber());
    assertEquals(a.getStartPC(), b.getStartPC());
  }

  /**
   * Asserts that the two arrays are equal.
   * 
   * @param a1
   *          first array.
   * @param a2
   *          second array.
   */
  private void compareStringArrays(String[] a1, String[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      assertEquals(a1[i], a2[i]);
    }
  }

  /**
   * Asserts that the two arrays are equal.
   * 
   * @param a1
   *          first array.
   * @param a2
   *          second array.
   */
  private void compareLocalVariableArray(LocalVariable[] a1, LocalVariable[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareLocalVariables(a1[i], a2[i]);
    }
  }

  private void compareLocalVariables(LocalVariable a, LocalVariable b) {
    assertEquals(a.getIndex(), b.getIndex());
    assertEquals(a.getName(), b.getName());
    assertEquals(a.getLength(), b.getLength());
    assertEquals(a.getSignature(), b.getSignature());
    assertEquals(a.getStartPC(), b.getStartPC());
  }

  private void compareCode(Code a, Code b) throws Exception {
    if (a == null) {
      assertEquals(null, b);
      return;
    }
    InstructionComparator comp = Instruction.getComparator();
    compareAttributeArrays(a.getAttributes(), b.getAttributes());

    ByteSequence bsa = new ByteSequence(a.getCode());
    ByteSequence bsb = new ByteSequence(b.getCode());

    assertEquals(bsa.available(), bsb.available());

    while (bsa.available() > 0) {
      Instruction ia = Instruction.readInstruction(bsa);
      Instruction ib = Instruction.readInstruction(bsb);
      assertEquals(bsa.available(), bsb.available());
      assertTrue("ia=" + ia + " ib=" + ib, comp.equals(ia, ib));
    }
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(MutatingClassLoaderTest.class);
    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }

  /**
   * Method used for debugging - prints out the bytecode instructions for each
   * method in the class.
   * 
   * @param c
   *          the class to display.
   * @throws Exception
   *           if something goes wrong
   */
  protected static void printClass(JavaClass c) throws Exception {
    Method[] m = c.getMethods();

    for (int i = 0; i < m.length; i++) {
      System.out.println(m[i].getName());

      ByteSequence code = new ByteSequence(m[i].getCode().getCode());

      while (code.available() > 0) {
        System.out.println("\t" + Instruction.readInstruction(code));
      }
    }
  }
}
