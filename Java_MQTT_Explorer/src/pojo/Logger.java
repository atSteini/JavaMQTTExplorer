package pojo;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import app.App;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class Logger {

	private static boolean 
			ENABLE_ERROR_LOG = true,
			ENABLE_CONSOLE_LOG = true,
			ENABLE_KEY_LOG = false,
			ENABLE_BUTTON_LOG = false,
			ENABLE_STATUSBAR = true;
	
	public static String 
			inlineInfo = "INFO: ",
			inlineWarning = "WARNING: ",
			inlineError = "ERROR: ",
			inlineKey = "KEY: ",
			inlineButton = "BUTTON: ";
	
	private static void inlineInfo() {
		System.out.print(inlineInfo);
	}
	
	private static void inlineKey() {
		System.out.print(inlineKey);
	}
	
	private static void inlineButton() {
		System.out.print(inlineButton);
	}
	
	private static void inlineError() {
		System.err.print(inlineError);
	}

	public static void statusLog(Object o) {
		if (!ENABLE_STATUSBAR) { return; }
		
		App.setStatusBar(o);
	}
	
	public static void buttonLog(ActionEvent e) {
		if (!ENABLE_BUTTON_LOG) { return; }
		statusLog(e);
		
		inlineButton();
		System.out.println(e);
	}
	
	public static void keyLog(KeyEvent e) {
		if (!ENABLE_KEY_LOG) { return; }
		statusLog(e);
		
		inlineKey();
		System.out.println(e);
	}
	
	public static void consoleLog(Object o) {
		if (!ENABLE_CONSOLE_LOG) { return; }
		statusLog(o);
		
		inlineInfo();
		System.out.println(o);
	}

	public static void errorLog(Object o) {
		if (!ENABLE_ERROR_LOG) { return; }
		statusLog(o);
		
		inlineError();
		System.err.println(o);
	}

	public static void errorLog(Exception e) {
		if (!ENABLE_ERROR_LOG) { return; }
		statusLog(e);
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		inlineError();
		System.err.println(sw.toString());
	}
	
	public static void errorLog(Throwable t) {
		if (!ENABLE_ERROR_LOG) { return; }
		statusLog(t);
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		
		inlineError();
		System.err.println(sw.toString());
	}

	public static boolean isENABLE_ERROR_LOG() {
		return ENABLE_ERROR_LOG;
	}

	public static void setENABLE_ERROR_LOG(boolean eNABLE_ERROR_LOG) {
		ENABLE_ERROR_LOG = eNABLE_ERROR_LOG;
	}

	public static boolean isENABLE_CONSOLE_LOG() {
		return ENABLE_CONSOLE_LOG;
	}

	public static void setENABLE_CONSOLE_LOG(boolean eNABLE_CONSOLE_LOG) {
		ENABLE_CONSOLE_LOG = eNABLE_CONSOLE_LOG;
	}

	public static boolean isENABLE_STATUSBAR() {
		return ENABLE_STATUSBAR;
	}

	public static void setENABLE_STATUSBAR(boolean nENABLE_STATUSBAR) {
		ENABLE_STATUSBAR = nENABLE_STATUSBAR;
	}

	public static String getInlineInfo() {
		return inlineInfo;
	}

	public static String getInlineWarning() {
		return inlineWarning;
	}

	public static String getInlineError() {
		return inlineError;
	}

	public static void setInlineInfo(String inlineInfo) {
		Logger.inlineInfo = inlineInfo;
	}

	public static void setInlineWarning(String inlineWarning) {
		Logger.inlineWarning = inlineWarning;
	}

	public static void setInlineEROR(String inlineError) {
		Logger.inlineError = inlineError;
	}
}
