package com.hhi.vaas.platform.mappingtool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.hhi.vaas.platform.mappingtool.ui.EquipmentDataView;
import com.hhi.vaas.platform.mappingtool.ui.MappingTableView;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1024, 768));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		
		
		// remove search menu
		//IWorkbenchWindow workbenchWindow = configurer.getWindow();
		//workbenchWindow.getActivePage().hideActionSet("org.eclipse.search.searchActionSet");
		//IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		//page.hideActionSet("org.eclipse.ui.actionSet.openFiles");
		
		
		
	}
	
	
	private void removedUnwantedMenus(IWorkbenchPage page) {
		
		// hide generic 'File' commands
        page.hideActionSet("org.eclipse.ui.actionSet.openFiles");

        // hide 'Convert Line Delimiters To...'
        page.hideActionSet("org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo");

        // hide 'Search' commands
        page.hideActionSet("org.eclipse.search.searchActionSet");

        // hide 'Annotation' commands
        page.hideActionSet("org.eclipse.ui.edit.text.actionSet.annotationNavigation");

        // hide 'Forward/Back' type navigation commands
        page.hideActionSet("org.eclipse.ui.edit.text.actionSet.navigation");
        
        // hide 'Key assist' type navigation commands
        page.hideActionSet("org.eclipse.ui.actionSet.keyBindings");
        
        
        
	} 
	
	
	public void postWindowOpen() {
		
		// remove unwanted UI contributions that eclipse makes by default
	    IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();

	    for (int i = 0; i < windows.length; ++i) {
	        IWorkbenchPage page = windows[i].getActivePage();
	        if (page != null) {
	        	removedUnwantedMenus(page);
	        }
	    }
	}

	@Override
	public boolean preWindowShellClose() {
		// TODO Auto-generated method stub
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		IViewPart view2 = page.findView(EquipmentDataView.ID);
		EquipmentDataView eqdView = (EquipmentDataView) view2;
		
		IViewPart view = page.findView(MappingTableView.ID);
		MappingTableView mapView = (MappingTableView) view;
		
		// equipment data view close()
		if (eqdView.onClose() == false) {
			return false;
		}
		
		if (mapView.onClose() == false) {
			return false;
		}
		
		return super.preWindowShellClose();
	}
	
	
}
