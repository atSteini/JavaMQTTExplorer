package pojo;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

import app.App;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class Logger {

	private static boolean 
			ENABLE_ERROR_LOG = true,
			ENABLE_INFO_LOG = true,
			ENABLE_KEY_LOG = false,
			ENABLE_BUTTON_LOG = false,
			ENABLE_STATUSBAR_LOG = true,
			ENABLE_STATE_CHANGE_LOG = false,
			ENABLE_NO_STATUS_LOG = true,
			ENABLE_SELECTION_CHANGE_LOG = false;
	
	public static String 
			inlineInfo = "INFO: ",
			inlineWarning = "WARNING: ",
			inlineError = "ERROR: ",
			inlineKey = "KEY: ",
			inlineButton = "BUTTON: ",
			inlineStateChanged = "STATE CHANGED: ",
			inlineNoStatus = "NO STATUS: ",
			inlineSelectionChanged = "SELECTION CHANGED: ";
	
	private static void inlineInfo() {
		System.out.print(inlineInfo);
	}
	
	private static void inlineNoStatus() {
		System.out.print(inlineNoStatus);
	}
	
	private static void inlineKey() {
		System.out.print(inlineKey);
	}
	
	private static void inlineButton() {
		System.out.print(inlineButton);
	}
	
	private static void inlineStateChanged() {
		System.out.print(inlineStateChanged);
	}
	
	private static void inlineSelectionChanged() {
		System.out.print(inlineSelectionChanged);
	}
	
	private static void inlineError() {
		System.err.print(inlineError);
	}

	public static void statusLog(Object o) {
		if (!ENABLE_STATUSBAR_LOG) { return; }
		
		App.setStatusBar(o);
	}
	
	public static void selectionChangedLog(ListSelectionEvent e) {
		if (!ENABLE_SELECTION_CHANGE_LOG) { return; }
		
		inlineSelectionChanged();
		System.out.println(e.getFirstIndex() + " | " + e);
	}
	
	public static void buttonLog(ActionEvent e) {
		if (!ENABLE_BUTTON_LOG) { return; }
		
		inlineButton();
		System.out.println(e);
	}
	
	public static void stateChangeLog(ChangeEvent e) {
		if (!ENABLE_STATE_CHANGE_LOG) { return; }
		
		inlineStateChanged();
		System.out.println(e);
	}
	
	public static void keyLog(KeyEvent e) {
		if (!ENABLE_KEY_LOG) { return; }
		
		inlineKey();
		System.out.println(e);
	}
	
	public static void infoLog(Object o) {
		if (!ENABLE_INFO_LOG) { return; }
		statusLog(o);
		
		inlineInfo();
		System.out.println(o);
	}

	public static void noStatusLog(Object o) {
		if (!ENABLE_NO_STATUS_LOG) { return; }
		
		inlineNoStatus();
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

	public static void exitSystem(int status) {
		errorLog("System exit with Status " + status);
		
		System.exit(status);
	}
	
	public static boolean isENABLE_ERROR_LOG() {
		return ENABLE_ERROR_LOG;
	}

	public static void setENABLE_ERROR_LOG(boolean eNABLE_ERROR_LOG) {
		ENABLE_ERROR_LOG = eNABLE_ERROR_LOG;
	}

	public static boolean isENABLE_CONSOLE_LOG() {
		return ENABLE_INFO_LOG;
	}

	public static void setENABLE_CONSOLE_LOG(boolean eNABLE_CONSOLE_LOG) {
		ENABLE_INFO_LOG = eNABLE_CONSOLE_LOG;
	}

	public static boolean isENABLE_STATUSBAR() {
		return ENABLE_STATUSBAR_LOG;
	}

	public static void setENABLE_STATUSBAR(boolean nENABLE_STATUSBAR) {
		ENABLE_STATUSBAR_LOG = nENABLE_STATUSBAR;
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
