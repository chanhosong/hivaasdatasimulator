package com.hhi.vaas.platform.mappingtool.ui;

public class DocState {
	public static final int DOC_STATE_RESET = 0;
	public static final int DOC_STATE_MODIFIED = 1;
	
	private int currentState;
	
	DocState() {
		currentState = DOC_STATE_RESET;
	}
	
	public int getDocState() {
		return currentState;
	}
	
	public void setDocState(int docState) {
		this.currentState = docState;
	}
	
	public void resetDocState() {
		currentState = DOC_STATE_RESET;
	}
}
