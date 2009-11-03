import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class AllTests extends TestSuite {

	public static Test suite() {
		final TestSuite suite = new TestSuite();
		suite.addTest(util.AllTests.suite());
		return suite;
	}

	public static void main(final String[] args) {
		TestRunner.run(suite());
	}

}
