/* Copyright (C) 2015~ Hyundai Heavy Industries. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Hyundai Heavy Industries
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with Hyundai Heavy Industries.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * sehwan			2015. 6. 18.				First Draft.
 */

package com.hhi.vaas.platform.datasimulator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimElement;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

import com.hhi.vaas.platform.datasimulator.actions.GeneratorViewRestore;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 600));
        configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
        configurer.setTitle("Data Simulator"); //$NON-NLS-1$
        hideQuickAccess();
    }

	@Override
	public void postWindowOpen() {
		IWorkbenchWindow window = getWindowConfigurer().getWindow();
		GeneratorViewRestore restroeView = new GeneratorViewRestore(window);
        
		restroeView.restore();
		super.postWindowOpen();
	}

	private void hideQuickAccess() {
		UIJob job = new UIJob("Hide quick access") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
					MTrimBar topTrim = ((WorkbenchWindow) window).getTopTrim();
					for (MTrimElement element : topTrim.getChildren()) {
						if ("SearchField".equals(element.getElementId())) {
							element.setVisible(false);
							element.setToBeRendered(false);
							;
							break;
						}
					}
				}
				return Status.OK_STATUS;
			}
		};

		job.schedule();
	}
}