package com.hhi.vaas.platform.datasimulator;

public interface IActionCallback {
	void displayTxtFiledescriptor(String message);
	void playViewToolbarChange(Boolean stateCode);
	void setThreadStateCode(int stateCode);
	void displaySpeedOfSendingMsg(double linePerSec, double bytesPerSec, double bytesTransferred);
	void setSpeedOfSendingMsg(double linePerSec, double bytesPerSec, double bytesTransferred);
	void displayUpdate(String message, double linePerSec, double bytesPerSec, double bytesTransferred);
}
