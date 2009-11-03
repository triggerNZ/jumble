package util;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
public class JavaFilenameFilterTest extends TestCase{
	
private JavaFilenameFilter filter;
public static  Test suite(){
	return new TestSuite(JavaFilenameFilterTest.class);
	
}

public void testAccept(){
	
	filter = new JavaFilenameFilter(".class","Test.class");
	
	assertFalse(filter.accept(null,"SomeClass.clas"));
	assertFalse(filter.accept(null,"SomeClassTest.class"));
	
	assertTrue(filter.accept(null,"SomeClass.class"));
	
	
    filter = new JavaFilenameFilter(".java"," ");
	
	assertTrue(filter.accept(null,"SomeClassTest.java"));
	
}
}
