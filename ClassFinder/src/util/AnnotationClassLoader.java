package util;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.JavaClass;

import com.reeltwo.jumble.annotations.TestClass;

public class AnnotationClassLoader extends ClassLoader{
	
	public AnnotationClassLoader(){
	}
	
	public  List<String> getTestClassByAnnotation(JavaClass clazz){
		//MutatedClass clazz = new MutatedClass(claz);
		 List<String> testClassNames=null;
		 
	   try{
		   System.out.println( System.getProperty("java.class.path"));
		   Class <?> claz =this.getClass().getClassLoader().loadClass(clazz.getClassName());
		   
		   System.out.println( System.getProperty("pass"));
		   TestClass testClass = claz.getAnnotation(TestClass.class);
		     testClassNames = new ArrayList<String>();
		     if (testClass != null) {
		    String[] testClasses = testClass.value();
		     for (String testClassName : testClasses) {
		          testClassNames.add(testClassName);
		        }
		      }
	   
		 }
		      
	   
	catch(Exception e){System.out.println(e);}
	return testClassNames;
	   }
}
