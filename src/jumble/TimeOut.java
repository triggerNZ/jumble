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
import java.util.Timer;
import java.util.TimerTask;

public class TimeOut {
	private Method mMethod;
	private Object mTarget;
	private Object [] mArgs;
	private long mTimeOut;
	private Object mReturnVal;
	private Thread mMethodThread;
	
	public TimeOut(Method method, Object target, Object [] args, long timeout) {
		mMethod = method;
		mTarget = target;
		mArgs = args;
		mTimeOut = timeout;
		mMethodThread = new MethodThread();
	}
	
	public void run() throws Exception {
		mMethodThread.start();
		
		new Timer().schedule(new TimerTask() {
			public void run() {
				System.out.println("Timer!");
				if(mMethodThread.isAlive())
					mMethodThread.stop();
			}
		}, mTimeOut);
		
		while(mMethodThread.isAlive()) {
			Thread.sleep(100);
		}
	}
	
	public synchronized Object getReturnValue() {
		return mReturnVal;
	}
	public synchronized void setReturnValue(Object o) {
		mReturnVal = o;
	}
	
	private class MethodThread extends Thread {
		public void run() {
			try {
				setReturnValue(mMethod.invoke(mTarget, mArgs));
			} catch(Exception e) {
				//This happens when the thread is stopped
			}
		}
	}

}
