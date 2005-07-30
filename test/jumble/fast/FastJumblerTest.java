package jumble.fast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import jumble.Jumbler;
import jumble.Mutater;
import jumble.util.JavaRunner;
import junit.framework.TestCase;
import junit.framework.TestResult;

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
import org.apache.bcel.generic.Type;

import experiments.JumblerExperimentTest;

/**
 * Tests the corresponding class.
 * 
 * @author Tin Pavlinic
 *  
 */
public class FastJumblerTest extends TestCase {
  private String mFileName;

  public final void setUp() throws Exception {
    //Unique filename
    mFileName = "tmpTest" + System.currentTimeMillis() + ".dat";

    TimingTestSuite suite = new TimingTestSuite(new Class[] {
        JumblerExperimentTest.class });
    suite.run(new TestResult());
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
        mFileName));
    out.writeObject(suite.getOrder());
    out.close();
  }

  public final void tearDown() {
    assertTrue(new File(mFileName).delete());
  }

  public void testMain() throws Exception {
    JavaRunner runner = new JavaRunner("jumble.fast.FastJumbler", 
        new String[] { "experiments.JumblerExperiment", "0",
          mFileName, "-r", "-k", "-i" });
    Process p = runner.start();
    
    BufferedReader reader = new BufferedReader(new InputStreamReader
        (p.getInputStream()));
    String line = reader.readLine();
    assertEquals("START", line);
    line = reader.readLine();
    assertTrue("Unexpected output: " + line, line.startsWith("PASS"));
    reader.close();
    p.destroy();
  }

  /**
   * Test for a bug that was found - it seems that the classes are getting
   * corrupted.
   */
  public final void testRunMethodExistence() throws Exception {
    try {
      final ClassLoader loader = new FastJumbler("experiments.JumblerExperiment",
          new Mutater(0));
      final Class clazz = loader.loadClass("jumble.fast.JumbleTestSuite");
      clazz.getMethod("run", new Class[] { loader.loadClass("jumble.fast.TestOrder"),
          String.class, String.class, boolean.class, boolean.class});
    } catch (NoSuchMethodException e) {
      fail();
    }
  }
  
  /**
   * Test whether the jumble.fast.FastJumbler produces the same
   * results as jumble.Jumbler
   */
  public final void testJumblerCompatability() throws Exception {
    JavaClass original = new ClassParser("c:/eclipse/workspace/jumblesrc/" 
        + "experiments/JumblerExperiment.class").parse();
    
    for (int i = 0; i < 5; i++) {
      Jumbler jumbler  
        = new Jumbler("experiments.JumblerExperiment", 
            new Mutater(i));
      FastJumbler fastJumbler 
        = new FastJumbler("experiments.JumblerExperiment",
          new Mutater(i));
      
      JavaClass a = jumbler.modifyClass(original);
      JavaClass b = fastJumbler.modifyClass(original);
      assertFalse(a == b);
      compareJavaClasses(a, b);     
    }
  }
  
  /**
   * Asserts that the classes a and b are equal
   * 
   * @param a first array
   * @param b second array
   */
  private void compareJavaClasses(JavaClass a, JavaClass b) {
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
    
    compareMethodArrays(meth1, meth2);
    
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
   * @param a1 first array.
   * @param a2 second array.
   */
  private void compareClassArrays(JavaClass[] a1, 
      JavaClass[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareJavaClasses(a1[i], a2[i]);
    }
  }

  /**
   * Asserts that the two arrays are equal.
   * @param a1 first array.
   * @param a2 second array.
   */
  private void compareAttributeArrays(Attribute[] a1, 
      Attribute[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareAttributes(a1[i], a2[i]);
    }
  }
  
  /**
   * Compares attributes
   * 
   * @param a first attribute
   * @param b second attribute
   */
  private void compareAttributes(Attribute a, 
      Attribute b) {
    assertEquals(a.getLength(), b.getLength());
    assertEquals(a.getNameIndex(), b.getNameIndex());
    assertEquals(a.getTag(), b.getTag());
    
    compareConstantPools(a.getConstantPool(),
        b.getConstantPool());
  }
  
  /**
   * Compares constant pools.
   * 
   * @param a first pool
   * @param b second pool
   */
  private void compareConstantPools(ConstantPool a,
      ConstantPool b) {
    assertEquals(a.getLength(), b.getLength());
    
    for (int i = 0; i < a.getLength(); i++) {
        if (a.getConstant(i) != null) {
        //Probably shouldn't use toString but equals was not defined properly
        assertEquals(a.getConstant(i).toString(), b.getConstant(i).toString());
        assertEquals(a.getConstant(i).getClass(), b.getConstant(i).getClass());
       } else {
         assertEquals(null, b.getConstant(i));
       }
    }
  }
  
  /**
   * Asserts that the two arrays are equal.
   * @param a1 first array.
   * @param a2 second array.
   */
  private void compareFieldArrays(Field[] a1, 
      Field[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareFields(a1[i], a2[i]);
    }
  }
  
  /**
   * Asserts two fields the same.
   * 
   * @param a first field
   * @param b second field
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
   * @param a first
   * @param b second
   */
  private void compareTypes(Type a, Type b) {
    assertEquals(a.getSignature(), b.getSignature());
    assertEquals(a.getSize(), b.getSize());
    assertEquals(a.getType(), b.getType());
    assertEquals(a.toString(), b.toString());
  }
  
  /**
   * Asserts that the two arrays are equal.
   * @param a1 first array.
   * @param a2 second array.
   */
  private void compareIntArrays(int[] a1, 
      int[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      assertEquals(a1[i], a2[i]);
    }
  }
  
  /**
   * Asserts that the two arrays are equal.
   * @param a1 first array.
   * @param a2 second array.
   */
  private void compareMethodArrays(Method[] a1, 
      Method[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareMethods(a1[i], a2[i]);
    }
  }
  
  private void compareMethods(Method a, Method b) {
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
      compareLineNumberArray(a.getLineNumberTable().getLineNumberTable(),
          b.getLineNumberTable().getLineNumberTable());
    }
    
    if (a.getLocalVariableTable() == null) {
      assertEquals(null, b.getLocalVariableTable());
    } else {
      compareLocalVariableArray(a.getLocalVariableTable().getLocalVariableTable(), 
          b.getLocalVariableTable().getLocalVariableTable());
    }
    compareTypes(a.getReturnType(), b.getReturnType());
  }
  
  /**
   * Asserts that the two arrays are equal.
   * @param a1 first array.
   * @param a2 second array.
   */
  private void compareTypeArrays(Type[] a1, 
      Type[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareTypes(a1[i], a2[i]);
    }
  }
  
  /**
   * Compares exception tables.
   * @param a first
   * @param b second
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
   * @param a first array.
   * @param b second array.
   */
  private void compareLineNumberArray(LineNumber[] a,
      LineNumber[] b) {
    assertEquals(a.length, a.length);
    for (int i = 0; i < a.length; i++) {
      compareLineNumber(a[i], b[i]);
    }
  }
  
  /**
   * Compares line numbers.
   * @param a first
   * @param b second
   */
  private void compareLineNumber(LineNumber a, LineNumber b) {
    assertEquals(a.getLineNumber(), b.getLineNumber());
    assertEquals(a.getStartPC(), b.getStartPC());
  }
  /**
   * Asserts that the two arrays are equal.
   * @param a1 first array.
   * @param a2 second array.
   */
  private void compareStringArrays(String[] a1, 
      String[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      assertEquals(a1[i], a2[i]);
    }
  }
  
  /**
   * Asserts that the two arrays are equal.
   * @param a1 first array.
   * @param a2 second array.
   */
  private void compareLocalVariableArray(LocalVariable[] a1, 
      LocalVariable[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      compareLocalVariables(a1[i], a2[i]);
    }
  }
  
  private void compareLocalVariables(LocalVariable a,
      LocalVariable b) {
    assertEquals(a.getIndex(), b.getIndex());
    assertEquals(a.getName(), b.getName());
    assertEquals(a.getLength(), b.getLength());
    assertEquals(a.getSignature(), b.getSignature());
    assertEquals(a.getStartPC(), b.getStartPC());
  }
  
  private void compareCode(Code a, Code b) {
    if (a == null) {
      assertEquals(null, b);
      return;
    }
    compareAttributeArrays(a.getAttributes(), b.getAttributes());
    compareByteArrays(a.getCode(), b.getCode());
  }
  
  /**
   * Asserts that the two arrays are equal.
   * @param a1 first array.
   * @param a2 second array.
   */
  private void compareByteArrays(byte[] a1, 
      byte[] a2) {
    assertEquals(a1.length, a2.length);
    for (int i = 0; i < a1.length; i++) {
      assertEquals(i + ": " + byteToHex(a1[i]) + " != " 
          + byteToHex(a2[i]), a1[i] + 128, a2[i] + 128);
    }
  }
  
  private static String byteToHex(byte b) {
    String hex = Integer.toHexString(b + 128);
    return "0x" + hex;
  }
}
