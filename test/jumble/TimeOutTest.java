/*
 * Created on Apr 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;

import java.math.BigInteger;

import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * @author Tin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TimeOutTest extends TestCase {
	private TimeOut mShort;
	private TimeOut mLong;
	private Method mMethod;
	private Object [] mParam = new Object [] {new BigInteger("30")};
	public TimeOutTest() throws Exception {
		mMethod = getClass().getMethod("slowFibonacci", new Class[] {BigInteger.class});
	}
	
	protected void setUp() throws Exception {
		mShort = new TimeOut(mMethod, null, mParam, 1000);
		mLong = new TimeOut(mMethod, null, mParam, 10000);
	}


	public void testShortRun() throws Exception {
		mShort.run();
		System.out.println(mShort.getReturnValue());
		assertTrue(mShort.getReturnValue() == null);
		
	}
	
	public void testLongRun() throws Exception {
		mLong.run();
		System.out.println(mLong.getReturnValue());
		assertTrue(mLong.getReturnValue() != null);
	}

	public static BigInteger slowFibonacci(BigInteger i) {
		BigInteger TWO = new BigInteger("2");
		if(i.equals(TWO) || i.equals(BigInteger.ONE))
			return BigInteger.ONE;
		else return slowFibonacci(i.subtract(BigInteger.ONE))
			.add(slowFibonacci(i.subtract(TWO)));
	}
}
