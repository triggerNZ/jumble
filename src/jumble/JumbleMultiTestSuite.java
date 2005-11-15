/*
 * Created on 18/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;

/**
 * @author Tin
 * @version $Revision$
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JumbleMultiTestSuite extends JumbleTestSuite {
    public JumbleMultiTestSuite(Class [] testClasses) {
        super(testClasses[0]);
        
        for(int i = 1; i < testClasses.length; i++) {
            addTestSuite(testClasses[i]);
        }
        
    }
    public static String run(String [] tests) {
        String curTest = null;
        try {
            Class [] classes = new Class[tests.length];
            for(int i = 0; i < classes.length; i++) {
                curTest = tests[i];
                classes[i] = Class.forName(curTest);
            }
            
            return new JumbleMultiTestSuite(classes).run();
          } catch (ClassNotFoundException e) {
            return "FAIL: No test class: " + curTest;
          }
    }

}
