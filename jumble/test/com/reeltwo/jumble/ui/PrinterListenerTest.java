package com.reeltwo.jumble.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.reeltwo.jumble.fast.InterfaceResult;
import com.reeltwo.jumble.fast.JumbleResult;
import com.reeltwo.jumble.fast.MutationResult;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests the corresponding class.
 * 
 * @author Tin Pavlinic
 * @version $Revision 1.0 $
 */
public class PrinterListenerTest extends TestCase {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public void testLineWrapping() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrinterListener listener = new PrinterListener(new PrintStream(baos));
		StringBuffer expected = new StringBuffer();

		for (int i = 0; i < 50; i++) {
			expected.append(".");
			listener.finishedMutation(new MutationResult(MutationResult.PASS, "dummyClass", i, "dummy description"));
		}
		expected.append(LINE_SEPARATOR);
		for (int i = 0; i < 25; i++) {
			expected.append("T");
			listener.finishedMutation(new MutationResult(MutationResult.TIMEOUT, "dummyClass", i + 50, "dummy description"));
		}

		assertEquals(baos.toString(), expected.toString());
	}

	public void testInterfacesReportZeroMutationPoints() {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrinterListener printerListener = new PrinterListener(new PrintStream(byteArrayOutputStream));

		printerListener.performedInitialTest(new InterfaceResult("Dummy"), 0);
		StringTokenizer stringTokenizer = new StringTokenizer(byteArrayOutputStream.toString(), LINE_SEPARATOR);
		assertEquals(3, stringTokenizer.countTokens());
		stringTokenizer.nextToken();
		stringTokenizer.nextToken();
		assertEquals("Mutation points = 0", stringTokenizer.nextToken());
	}

	public static Test suite() {
		return new TestSuite(PrinterListenerTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
}
