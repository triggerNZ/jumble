/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jumble;

/** A class for stopping methods if they take too long.
 * @author Tin Pavlinic
 */

import java.lang.reflect.Method;

public class TimeOut {
	private Method mMethod;
	private Object mTarget;
	private Object [] mArgs;
	private long mTimeOut;
	
	public TimeOut(Method method, Object target, Object [] args, long timeout) {
		mMethod = method;
		mTarget = target;
		mArgs = args;
		mTimeOut = timeout;
	}
}
