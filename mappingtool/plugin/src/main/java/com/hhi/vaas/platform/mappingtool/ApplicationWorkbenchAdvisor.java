package com.hhi.vaas.platform.mappingtool;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

//import com.hhi.vaas.platform.mappingtool.p2update.utils.P2Util;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "com.hhi.vaas.platform.mappingtool.perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	
	@Override
	public IAdaptable getDefaultPageInput() {
		// TODO Auto-generated method stub
		return super.getDefaultPageInput();
	}
	
	@Override
	public void preStartup() {
		//P2Util.checkForUpdates();
	}

}
