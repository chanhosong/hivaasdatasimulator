package com.hhi.vaas.platform.mappingtool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


public final class SimpleMessageBox {
	public static final int MSG_TYPE_TEXT = 0;
	public static final int MSG_TYPE_WARNING = 1;
	public static final int MSG_TYPE_ERROR = 2;
	public static final int MSG_TYPE_INFO = 3;
	
	private SimpleMessageBox() {
		// won't be called.
	}
	
	private static String getMessageType(int msgType) {
		String msgString = "";
		
		switch (msgType) {
		case MSG_TYPE_TEXT:
			msgString = "Message";
			break;
		case MSG_TYPE_WARNING:
			msgString = "Warning";
			break;
		case MSG_TYPE_ERROR:
			msgString = "Error";
			break;
		case MSG_TYPE_INFO:
			msgString = "Info";
			break;
		default:
			break;
		}
		
		return msgString;
	}
	
	public static void show(Shell shell, String message) {
		//e.printStackTrace();
		show(shell, message, MSG_TYPE_WARNING, SWT.ICON_WARNING | SWT.OK);
	}
	
	public static int show(Shell shell, String message, int msgType) {
		
		return show(shell, message, msgType, SWT.ICON_WARNING | SWT.OK);
	}
	
	public static int show(Shell shell, String message, int msgType, int style) {
		MessageBox messageBox = new MessageBox(shell, style);
		messageBox.setText(getMessageType(msgType));
		
		messageBox.setMessage(message);
		return messageBox.open();
	}
}
