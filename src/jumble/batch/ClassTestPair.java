package jumble.batch;

/** Class representing a class-test association
 * @author Tin Pavlinic
 */
public class ClassTestPair {
    private String mClass;
    private String mTest;
    
    public ClassTestPair(String className, String testName) {
        mClass = className;
        mTest = testName;
    }
    public String getClassName() {
        return mClass;
    }
    public String getTestName() {
        return mTest;
    }
    public boolean isValid() {
        try {
            Class.forName(mClass);
            Class.forName(mTest);
            return true;
        } catch(ClassNotFoundException e) {
            return false;
        }
    }
    
    public boolean equals(Object o) {
        if(o instanceof ClassTestPair) {
            ClassTestPair p = (ClassTestPair)o;
            return p.getClassName().equals(getClassName()) &&
            	p.getTestName().equals(getTestName());
        } else
            return false;
    }
    public int hashCode() {
        return getClassName().hashCode() ^ getTestName().hashCode();
    }
    public String toString() {
        return "[" + getClassName() + ", " + getTestName() + "]";
    }
}
