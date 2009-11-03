package util;

import junit.framework.TestSuite;
import junit.framework.Test;

public class AllTests extends TestSuite {
	public static Test suite() {
		final TestSuite suite = new TestSuite();
		suite.addTest(ClassToBeMutatedTest.suite());
		suite.addTest(JavaFilenameFilterTest.suite());
		suite.addTest(MultiRepositoryTest.suite());

		return suite;
	}

}
