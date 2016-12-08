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
 * hsbae			2015. 4. 9.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.xml.sax.SAXException;

import com.hhi.vaas.platform.mappingtool.mapdatamodel.MapDataHandler;
import com.hhi.vaas.platform.mappingtool.mapdatamodel.MapDataItem;
import com.hhi.vaas.platform.mappingtool.mapdatamodel.MapDataXMLModel;
import com.hhi.vaas.platform.mappingtool.toolbaractions.FileEditAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.FileLoadAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.FileNewAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.FileReloadAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.FileSaveAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IFileEditActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IFileLoadActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IFileNewActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IFileReloadActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IFileSaveActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IItemDeleteActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.ItemDeleteAction;

/**
 * MappingTableView Class
 *   - load/save/display the mapping rule xml file.
 * @author hsbae
 *
 */
/**
 * @author hsbae
 *
 */
public class MappingTableView 
		extends ViewPart 
		implements IFileLoadActionCallback, IFileSaveActionCallback, IFileNewActionCallback, IFileReloadActionCallback, 
					IItemDeleteActionCallback, IFileEditActionCallback {

	public static final String ID = "com.hhi.vaas.platform.mappingtool.mappingtableview";
	
	public static final Logger LOGGER = Logger.getLogger(MappingTableView.class);
	
	private DocState docState = new DocState();
	
	//TreeViewer v;
	private TableViewer v = null;
	private MapDataXMLModel mapXml = null;
	private String pathMapDataFile = null;
	private String systemName = null;
	
	private Set<String> vdmPathList = new HashSet<String>();
	
	private double vdmFileVersion = 0.0;
	private double eqdFileVersion = 0.0;
	
	public MappingTableView() {
		// TODO Auto-generated constructor stub
		v = null;
		mapXml = null;
		pathMapDataFile = null;
		systemName = null;
		
		vdmFileVersion = 0.0;
		eqdFileVersion = 0.0;
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		
		// add toolbar icons
		addToolbarActions(); 
		
		v = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, v);
		
		final Table table = v.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		v.setContentProvider(ArrayContentProvider.getInstance());
		
		// register key event handler
		addKeyEventListeners();
				
		//v.setInput(sampleContents());
		doFileNewAction();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	private void docStateChange(int newState) {
		if (docState.getDocState() != newState) {
			if (docState.DOC_STATE_MODIFIED == newState) {
				docState.setDocState(newState);
				this.setPartName("* Mapping Table View");
			} else if (docState.DOC_STATE_RESET == newState) {
				docState.resetDocState();
				this.setPartName("Mapping Table View");
			}
		}
	}
	
	private void addKeyEventListeners() {
		v.getTable().addKeyListener(new KeyAdapter() {
			
			private Shell shell = getViewSite().getShell();
			
			@Override
			public void keyReleased(final KeyEvent e) {
				
				if (e.keyCode == SWT.DEL) {
					doItemDeleteAction();
				}
			}
		});
	}
	
	
	/**
	 * 
	 * @param title
	 * @param bounds
	 * @param colNumber
	 * @return
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bounds, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(v, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		
		column.setText(title);
		column.setWidth(bounds);
		column.setResizable(true);
		column.setMoveable(true);
		
		return viewerColumn;
	}
	
	/**
	 * 
	 * @param parent
	 * @param viewer
	 */
	private void createColumns(Composite parent, final TableViewer viewer) {
		String [] titles = { "Protocol", "Function", "From", "Sub field", "param2", "To" };
		int[] bounds = {100, 0, 150, 100, 0, 500};
		
		
		
		TableViewerColumn col;
		
		col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MapDataItem) {
					return ((MapDataItem) element).getProtocol();
				}
				return "";
			}
		});
		
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MapDataItem) {
					return ((MapDataItem) element).getFunction();
				}
				return "";
			}
		});
		
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MapDataItem) {
					return ((MapDataItem) element).getFrom();
				}
				return "";
			}
		});
		
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MapDataItem) {
					return ((MapDataItem) element).getParam1();
				}
				return "";
			}
		});
		
		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MapDataItem) {
					return ((MapDataItem) element).getParam2();
				}
				return "";
			}
		});
		
		col = createTableViewerColumn(titles[5], bounds[5], 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MapDataItem) {
					return ((MapDataItem) element).getTo();
				}
				return "";
			}
		});
	}
	
	/**
	 * add toolbar icons to the view
	 */
	private void addToolbarActions() {
		// add new toolbar
		FileNewAction fileNewAction = new FileNewAction(this);
		getViewSite().getActionBars().getToolBarManager().add(fileNewAction);
		

		// add edit toolbar
		FileEditAction fileEditAction = new FileEditAction(this);
		fileEditAction.setText("XML Edit");
		getViewSite().getActionBars().getToolBarManager().add(fileEditAction);
				
		// add fileload toolbar 
		FileLoadAction fileLoadAction = new FileLoadAction(this);
		fileLoadAction.setText("Load mapping rules");
		getViewSite().getActionBars().getToolBarManager().add(fileLoadAction);
		
		// add filesave toolbar 
		FileSaveAction fileSaveAction = new FileSaveAction(this);
		fileSaveAction.setText("Save mapping rules");
		getViewSite().getActionBars().getToolBarManager().add(fileSaveAction);
		
		// add item delete toolbar
		ItemDeleteAction deleteAction = new ItemDeleteAction(this);
		deleteAction.setText("Delete the mapping rule");
		getViewSite().getActionBars().getToolBarManager().add(deleteAction);
		
		// add filereload toolbar
		FileReloadAction fileReloadAction = new FileReloadAction(this);
		fileReloadAction.setText("Reload mapping rules");
		getViewSite().getActionBars().getToolBarManager().add(fileReloadAction);
		
	}
	

	@Override
	public void doFileNewAction() {
		// TODO Auto-generated method stub
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(EquipmentDataView.ID);
		EquipmentDataView eqdView = (EquipmentDataView) view;
		
		IViewPart view2 = page.findView(VesselDataModelView.ID);
		VesselDataModelView vdmView = (VesselDataModelView) view2;
		
		mapXml = new MapDataXMLModel();
		try {
			v.setInput(mapXml.getMapDataList());
			pathMapDataFile = null;
			docStateChange(docState.DOC_STATE_RESET);
			
			eqdView.clearAllLinks();
			vdmView.clearAllLinks();
			
		} catch (Exception e) {
			Shell shell = getViewSite().getShell();
			SimpleMessageBox.show(shell, e.toString());
		}
	}
	
	
	public void syncWithEQDView() {
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(EquipmentDataView.ID);
		EquipmentDataView eqdView = (EquipmentDataView) view;
		
		Display display = Display.getCurrent();
		
		TableItem[] tableItems = v.getTable().getItems();
		
		eqdView.clearAllLinks();
		
		try {
			eqdFileVersion = eqdView.getFullVersion();
		} catch (Exception e) {
			eqdFileVersion = 0.0;
		}
		
		LOGGER.debug("==> syncWithEQDView()");
		
		MapDataItem mapItem = null;
		
		for (TableItem item : tableItems) {
			mapItem = (MapDataItem) item.getData();
			boolean bFound = false;
			
			try {
				bFound = eqdView.searchAndUpdateByMapItem(mapItem);
				if (bFound == false) {
					LOGGER.warn("[MAP] Not Found at EQD : " + mapItem);
					
					item.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
				} else {
					item.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
					LOGGER.trace("[MAP] item found at EQD : " + mapItem);
				}
					
			} catch (Exception e) {
				LOGGER.warn("[MAP] eqd sync error : " + mapItem);
			}
		}
		
		eqdView.refreshContents();
		LOGGER.debug("<== syncWithEQDView()");
	}
	
	public void syncWithVDMView() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(VesselDataModelView.ID);
		VesselDataModelView vdmView = (VesselDataModelView) view;
		
		Display display = Display.getCurrent();
		
		vdmFileVersion = vdmView.getFullVersion();
		
		TableItem[] tableItems = v.getTable().getItems();
		
		//vdmView.clearAllLinks();
		
		LOGGER.debug("==> syncWithVDMView()");
		LOGGER.debug("systemName = " + systemName);
		
		if ("".equals(systemName) || systemName == null) {
			systemName = vdmView.getCurrentSystemName();
			
			if ("".equals(systemName)) {
				Shell shell = this.getViewSite().getShell();
				SimpleMessageBox.show(shell, "WARN : Invalid equipmentId");
			}
		}
		
		
		
		MapDataItem mapItem = null;
		
		for (TableItem item : tableItems) {
			mapItem = (MapDataItem) item.getData();
			boolean bFound = false;
			
			try {
				bFound = vdmView.searchAndUpdateByMapItem(systemName, mapItem);
				if (bFound == true) {
					LOGGER.trace("[MAP] item found at VDM : " + mapItem);
					item.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				} else {
					LOGGER.debug("[MAP] Not found at VDM : " + mapItem);
					item.setForeground(display.getSystemColor(SWT.COLOR_RED));
				}
			} catch (Exception e) {
				LOGGER.warn("[MAP] vdm sync error : " + mapItem);
			}
		}
		vdmView.refreshContents();
		LOGGER.debug("<== syncWithVDMView()");
	}
	
	@Override
	public void doFileLoadAction(String strFileName) {
		MapDataXMLModel mapXmlLocal = null;
		
		Shell shell = this.getViewSite().getShell();
		
		try {
			// clear link information fields of equipmentDataView
			
			mapXmlLocal = MapDataHandler.load(strFileName);
			
			v.setInput(mapXmlLocal.getMapDataList());
			pathMapDataFile = strFileName;
			systemName = mapXmlLocal.getMapDataHeader().getEquipmentId();
			mapXml = mapXmlLocal;
			
			
			docStateChange(docState.DOC_STATE_RESET);
			
		} catch (ParserConfigurationException | SAXException | IOException | MPTException e2) {
			SimpleMessageBox.show(shell, e2.getMessage());
		} catch (Exception e) {
			
			//SimpleMessageBox.show(shell, e.getMessage());
			SimpleMessageBox.show(shell, e.toString());
			return;
		}
		
		// sync with equipment data view
		
		try {
			syncWithEQDView();
		} catch (Exception e) {
			LOGGER.debug("[MAP] eqd sync exception");
		}
		
		// sync with vdm view
		try {
			syncWithVDMView();
		} catch (Exception e) {
			LOGGER.debug("[MAP] vdm sync exception");
		}
	}
	
	@Override
	public void doFileReloadAction() {
		// TODO Auto-generated method stub
		
		doFileLoadAction(pathMapDataFile);
	}
	
	@Override
	public boolean doPrecheckNewCondition() {
		// TODO Auto-generated method stub
		Shell shell = this.getViewSite().getShell();
		
		if (docState.getDocState() == docState.DOC_STATE_MODIFIED) {
			int ret = SimpleMessageBox.show(shell, "Save current mapping table before make a new file?", 
					SimpleMessageBox.MSG_TYPE_WARNING, 
					SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			
			if (ret == SWT.CANCEL) {
				return false;
			} else if (ret == SWT.YES) {
				String result;
				String [] fileFilters = {"*.xml"};
				
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
				
				fd.setFilterExtensions(fileFilters);
				result = fd.open();
				if (result != null) {
					String filePath = fd.getFilterPath();
					String fileName = fd.getFileName();
					String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					String fileFullPath = filePath + "/" + fileName;

					LOGGER.debug("Selected file = " + fileFullPath);
					
					doFileSaveAction(fileFullPath);
				}
				return true;
			} else {
				// nothing
				return true;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean doPrecheckLoadCondition() {
		// TODO Auto-generated method stub
		Shell shell = this.getViewSite().getShell();
		
		if (docState.getDocState() == docState.DOC_STATE_MODIFIED) {
			int ret = SimpleMessageBox.show(shell, "Save current mapping table before loading?", 
					SimpleMessageBox.MSG_TYPE_WARNING, 
					SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			
			if (ret == SWT.CANCEL) {
				return false;
			} else if (ret == SWT.YES) {
				doFileSaveAction(pathMapDataFile);
				SimpleMessageBox.show(shell, "Saved at \n" + pathMapDataFile);
				return true;
			} else {
				// nothing
				return true;
			}
		}
		
		return true;
	}

	@Override
	public boolean doPrecheckSaveCondition() {
		//LOGGER.debug("doPrecheckSaveCondition()");
		
		return true;
	}
	
	@Override
	public void doFileSaveAction(String fileName) {
		
		//MapDataXMLModel mapXml = (MapDataXMLModel) v.getInput();
		Shell shell = getViewSite().getShell();
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		IViewPart view = page.findView(VesselDataModelView.ID);
		VesselDataModelView vdmView = (VesselDataModelView) view;
		
		if ("".equals(systemName) || systemName == null) {
			systemName = vdmView.getCurrentSystemName();
		}
		
		try {
			//MapDataHandler.save(mapXml,  fileName);
			MapDataHandler.save(mapXml,  fileName, vdmFileVersion, eqdFileVersion, systemName);
			pathMapDataFile = fileName;
			
			docStateChange(docState.DOC_STATE_RESET);
			
			
		} catch (MPTException me) {
			SimpleMessageBox.show(shell, me.getMessage());
		} catch (Exception e) {
			SimpleMessageBox.show(shell, e.toString());
		}
		
	}
	


	@Override
	public void doItemDeleteAction() {
		// TODO Auto-generated method stub
		if (!v.getSelection().isEmpty()) {
			Shell shell = getViewSite().getShell();
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			
			messageBox.setText("Warning");
			messageBox.setMessage("Delete this item?");
			
			int ret = messageBox.open();
			if (ret == SWT.OK) {
				handleDelete();
			}
		}
	}

	@Override
	public void doFileEditAction() {
		// TODO Auto-generated method stub
		IWorkbenchPage page = getViewSite().getPage();
		
		
		try {
			
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(pathMapDataFile));
			FileStoreEditorInput editorInput = new FileStoreEditorInput(fileStore);
			
			page.openEditor(editorInput, "projection_test.editors.XMLEditor");
		} catch (Exception e) {
			Shell shell = getViewSite().getShell();
			SimpleMessageBox.show(shell, e.toString());
		}
	
	}
	
	public MapDataXMLModel getMapXmlModel() {
		return mapXml;
	}
	
	/**
	 * 
	 */
	
	private void handleDelete() {
		
		//int selIndex = v.getTable().getSelectionIndex();
		int[] selIndices = v.getTable().getSelectionIndices();
		int itemCount = v.getTable().getItemCount();

		//StructuredSelection selection = (StructuredSelection) v.getSelection();
		
		if (selIndices.length <= 0) {
			return;
		}
		
		if (selIndices[selIndices.length - 1] < itemCount - 1) {
			v.setSelection(new StructuredSelection(v.getElementAt(selIndices[selIndices.length - 1] + 1)), true);	
		}
		
		for (int i = selIndices.length - 1; i >= 0; i--) {
			itemCount = v.getTable().getItemCount();
			if (itemCount > 0 && selIndices[i] != -1) {
				
				//mapXml.getMapDataList().remove(selIndex);
				boolean result = mapXml.removeMapDataByIndex(selIndices[i]);
			}
		}
		
		docStateChange(docState.DOC_STATE_MODIFIED);
		
		v.refresh();
		
		syncWithEQDView();
		syncWithVDMView();
	}
	
	public void handleAppend(MapDataItem newItem) {
		//mapXml.getMapDataList().add(newItem);
		boolean result = mapXml.addMapData(newItem);
		
		if (result == true) {
			docStateChange(docState.DOC_STATE_MODIFIED);
			
			v.refresh();
		}
	}
	
	public boolean checkVdmPathIsExist(String vdmPath) {
		return mapXml.checkVdmPath(vdmPath);
	}

	
	public boolean onClose() {
		Shell shell = this.getViewSite().getShell();
		
		if (docState.getDocState() == docState.DOC_STATE_MODIFIED) {
			int ret = SimpleMessageBox.show(shell, "Save current mapping table before close?", 
					SimpleMessageBox.MSG_TYPE_WARNING, 
					SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			
			if (ret == SWT.CANCEL) {
				return false;
			} else if (ret == SWT.YES) {
				String result;
				String [] fileFilters = {"*.xml"};
				
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
				
				fd.setFilterExtensions(fileFilters);
				result = fd.open();
				if (result != null) {
					String filePath = fd.getFilterPath();
					String fileName = fd.getFileName();
					String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
					String fileFullPath = filePath + "/" + fileName;

					LOGGER.debug("Selected file = " + fileFullPath);
					
					doFileSaveAction(fileFullPath);
				}
				return true;
			} else {
				// nothing
				return true;
			}
		}
		
		return true;
	}
	
}
//end of MappingTableView.java