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
 * hsbae			2015. 4. 8.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.ui;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.ViewPart;

import com.hhi.vaas.platform.mappingtool.eqdmodel.EQDExcelException;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EQDExcelLoader;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EQDXMLException;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EQDXMLHandler;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EQDXMLModel;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EquipmentDataHeader;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EquipmentDataMappingTemplate;
import com.hhi.vaas.platform.mappingtool.eqdmodel.EquipmentDataModel;
import com.hhi.vaas.platform.mappingtool.eqdmodel.TemplateItem;
import com.hhi.vaas.platform.mappingtool.mapdatamodel.MapDataItem;
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
import com.hhi.vaas.platform.mappingtool.toolbaractions.IItemInsertActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.ItemDeleteAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.ItemInsertAction;

/**
 *  EquipmentDataView Class
 *    - load/save/display equipment data 
 *  @author hsbae
 */

public class EquipmentDataView 
		extends ViewPart 
		implements IFileLoadActionCallback, IFileSaveActionCallback, IFileNewActionCallback, IFileEditActionCallback, 
					IFileReloadActionCallback, 
					IItemInsertActionCallback, IItemDeleteActionCallback {
	
	public static final String ID = "com.hhi.vaas.platform.mappingtool.equipmentdataview";
	
	private static final Logger LOGGER = Logger.getLogger(EquipmentDataView.class);
	
	private TreeViewer v;
	private String curFileName = null;
	private EquipmentDataHeader equipmentDataHeader = null;
	//public EquipmentDataMappingTemplate equipmentDataTemplate = null;
	public Map<String, TemplateItem> templateList = null;
	
	private DocState docState = new DocState();
	
	private Display display;
	
	public EquipmentDataView() {
		 display = Display.getCurrent();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		/*
		Button btnSave = new Button(parent, SWT.NONE);
		btnSave.setText("Save");
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveEquipmentDataXML();
			}
		});
		*/
		
		// create fileLoad toolbar
		
		// addActions
		//removedUnwantedMenus();
		
		addToolbarActions();
		
		v = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI); // 
		v.getTree().setLinesVisible(true);
		v.getTree().setHeaderVisible(true);
		
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferType = new Transfer[] {TextTransfer.getInstance()};
		v.addDropSupport(operations, transferType, new EQDDropListener(v));
		
		
		addKeyEventHandler();
		
		addOtherEventHandler();
		
		
		TreeViewerFocusCellManager focusCellManager 
					= new TreeViewerFocusCellManager(v, new FocusCellOwnerDrawHighlighter(v));
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(v) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
						return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
								|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
								|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED 
										&& event.keyCode == SWT.CR)
								|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
					}
		};
			
		int feature = ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL
				| ColumnViewerEditor.KEYBOARD_ACTIVATION;
		
		TreeViewerEditor.create(v,  focusCellManager,  actSupport, feature);
		final TextCellEditor textCellEditor = new TextCellEditor(v.getTree());
		
		String [] columLabels = { "Name", "Description", "Type", "LN", "Destinations" };
		int [] bounds = {180, 300, 60, 30, 300};
		
		for (int i = 0; i < columLabels.length; i++) {
			TreeViewerColumn column = null;
			
			column = new TreeViewerColumn(v, SWT.NONE);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setMoveable(true);
			column.getColumn().setText(columLabels[i]);
			column.setLabelProvider(createColumnLabelProvider(i));
			column.setEditingSupport(createEditingSupportFor(v, textCellEditor, i));
			
		}
		
		v.setContentProvider(new EquipmentDataTreeContentProvider());
		//v.setInput(createModel());
		v.setInput(createEquipmentDataModel());
		//v.setInput(createEquipmentDataModelbyExcel());
		
		v.expandAll();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	
	private void docStateChange(int newState) {
		if (docState.getDocState() != newState) {
			if (docState.DOC_STATE_MODIFIED == newState) {
				docState.setDocState(newState);
				this.setPartName("* Equipment Data View");
			} else if (docState.DOC_STATE_RESET == newState) {
				docState.resetDocState();
				this.setPartName("Equipment Data View");
			}
		}
	}
	
	private void addKeyEventHandler() {
		v.getTree().addKeyListener(new KeyAdapter() {

			
			@Override
			public void keyReleased(final KeyEvent e) {
				
				if(e.keyCode == SWT.DEL) {
					doItemDeleteAction();
				}
				/*
				else if(e.keyCode == SWT.INSERT) {
					// insert item
					TreeSelection selection = (TreeSelection) v.getSelection();
					//IStructuredSelection selection = (IStructuredSelection) v.getSelection();
					if(selection.isEmpty()) {
						return;
					}
					
					handleInsert();
				}
				*/
			}
			
			
		});
	}
	
	
	private void addOtherEventHandler() {

		v.getTree().addSelectionListener(new SelectionAdapter() {
			
		});
	}
	
	private void handleInsert() {
		
		TreeSelection selection = (TreeSelection) v.getSelection();
		TreeItem[] items = v.getTree().getSelection();
		EquipmentDataModel eqdSelected = (EquipmentDataModel) selection.getFirstElement();
		
		int curIndex = eqdSelected.parent.child.indexOf(eqdSelected);
		
		EquipmentDataModel eqdItem = new EquipmentDataModel(eqdSelected.parent, "field:");
		eqdSelected.parent.child.add(curIndex, eqdItem);
		
		LOGGER.debug(selection);
		
		v.refresh();
		
	}
	
	private void handleDelete() {
		
		TreeSelection selection = (TreeSelection) v.getSelection();
		
		TreeItem[] items = v.getTree().getSelection();
		TreeItem item = items[0];
			
		LOGGER.debug(selection);
		
		EquipmentDataModel eqdItem = (EquipmentDataModel) selection.getFirstElement();
		
		String deviceName = ((EquipmentDataModel) v.getInput()).getName();
		
		// do not remove root node
		if (deviceName.equals(eqdItem.getName())) {
			return;
		}
		
		/*
		int index = eqdItem.parent.child.indexOf(eqdItem);
		TreeItem selItem;
		
		if(index == 0) {
			selItem = item.getParentItem();
		}
		else
		{
			selItem = item.getParentItem().getItem(index - 1);
		}
		
		v.getTree().setSelection(selItem);
		*/
		
		eqdItem.parent.child.remove(eqdItem);
		docStateChange(docState.DOC_STATE_MODIFIED);
		
		v.refresh();
	}
	
	private void addToolbarActions() {
		// add new toolbar
		FileNewAction fileNewAction = new FileNewAction(this);
		getViewSite().getActionBars().getToolBarManager().add(fileNewAction);
		
		// add edit toolbar
		FileEditAction fileEditAction = new FileEditAction(this);
		fileEditAction.setText("XML Edit");
		getViewSite().getActionBars().getToolBarManager().add(fileEditAction);
		
		
		// add filelaodaction to view toolbar.
		FileLoadAction fileLoadAction = new FileLoadAction(this);
		//fileLoadAction.setText("Load equipment data");
		String [] loadfilters = {"*.xml;*.xlsx;*.xls"};
		fileLoadAction.setFileFilters(loadfilters);
		getViewSite().getActionBars().getToolBarManager().add(fileLoadAction);
		
		// add filesaveaction to view toolbar.
		FileSaveAction fileSaveAction = new FileSaveAction(this);
		String [] savefilters = {"*.xml"};
		//fileSaveAction.setText("Save equipment data");
		fileSaveAction.setFileFilters(savefilters);
		getViewSite().getActionBars().getToolBarManager().add(fileSaveAction);
		
		// add + toolbar
		ItemInsertAction itemInsertAction = new ItemInsertAction(this);
		getViewSite().getActionBars().getToolBarManager().add(itemInsertAction);
		
		// add - toolbar
		ItemDeleteAction itemDeleteAction = new ItemDeleteAction(this);
		getViewSite().getActionBars().getToolBarManager().add(itemDeleteAction);
		
		// add reload toolbar
		FileReloadAction fileReloadAction = new FileReloadAction(this);
		fileReloadAction.setText("EQD reload");
		getViewSite().getActionBars().getToolBarManager().add(fileReloadAction);
	}
	
	@Override
	public void doFileNewAction() {
		// TODO Auto-generated method stub
		// save condition check -> save previous work.
		// user input the equipment information (model name, vendor, protocol)
		// set title with device name and vendor
		// make a new tree with protocol name
		// show the tree
		
		Shell shell = getViewSite().getShell();
		EQDSetDeviceInfoDialog infoDlg = new EQDSetDeviceInfoDialog(shell);
		
		if (Window.OK == infoDlg.open()) {
			try {
				String strDocName = infoDlg.getStrDocName();
				String strDocVersion = infoDlg.getStrDocVersion();
				String strDocRevision = infoDlg.getStrDocRevision();
				String strDevName = infoDlg.getStrDevName();
				String strDevVendor = infoDlg.getStrDevVendor();
				String strDevProtocol = infoDlg.getStrDevProtocol(); 
				
				
				LOGGER.debug(strDocName);
				LOGGER.debug(strDocVersion);
				LOGGER.debug(strDocRevision);
				LOGGER.debug(strDevName);
				LOGGER.debug(strDevVendor);
				LOGGER.debug(strDevProtocol);
				
				
				if ("".equals(strDocName) || "".equals(strDevName) || "".equals(strDevProtocol)) {
					throw new Exception("invalid data");
				}
				
				EquipmentDataHeader eqdHeader = new EquipmentDataHeader();
				eqdHeader.setId(strDocName);
				eqdHeader.setVersion(strDocVersion);
				eqdHeader.setRevision(strDocRevision);
				
				EquipmentDataModel eqdRoot = new EquipmentDataModel(null, strDevName);
				EquipmentDataModel eqdProtocol = new EquipmentDataModel(eqdRoot, strDevProtocol);
				eqdRoot.child.add(eqdProtocol);
				
				equipmentDataHeader = eqdHeader;
				v.setInput(eqdRoot);
				v.refresh();
				
				docStateChange(docState.DOC_STATE_RESET);
		
			} catch (Exception e) {
				SimpleMessageBox.show(shell, e.getMessage());
			}
		}
	}
	

	@Override
	public void doFileEditAction() {
		// TODO Auto-generated method stub
		
		IWorkbenchPage page = getViewSite().getPage();
		Shell shell = getViewSite().getShell();
		
		try {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(curFileName));
			FileStoreEditorInput editorInput = new FileStoreEditorInput(fileStore);
			
			page.openEditor(editorInput, "projection_test.editors.XMLEditor");
		} catch (Exception e) {
			SimpleMessageBox.show(shell, "No file selected.");
		}
		
	}
	
	private void syncWithMappingTable() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(MappingTableView.ID);
		MappingTableView mView = (MappingTableView) view;
		
		LOGGER.debug("[EQD] INFO : sync with mapping table");
		v.refresh();
		
		mView.syncWithEQDView();
	}
	
	@Override
	public boolean doPrecheckNewCondition() {
		// TODO Auto-generated method stub
		Shell shell = this.getViewSite().getShell();
		
		if (docState.getDocState() == docState.DOC_STATE_MODIFIED) {
			int ret = SimpleMessageBox.show(shell, "Save current equipment data before make a new file?", 
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
			int ret = SimpleMessageBox.show(shell, "Save current equipment data before loading?", 
					SimpleMessageBox.MSG_TYPE_WARNING, 
					SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			
			if (ret == SWT.CANCEL) {
				return false;
			} else if (ret == SWT.YES) {
				doFileSaveAction(curFileName);
				SimpleMessageBox.show(shell, "Saved at \n" + curFileName);
				return true;
			} else {
				// nothing
				return true;
			}
		}
		
		return true;
	}
	
	public void doFileLoadAction(String strFileName) {
		Shell shell = this.getViewSite().getShell();
		
		// check file name
		
		// check validation
		
		// load file
		InputStream is;
		EquipmentDataModel eqdRoot = null;
		EquipmentDataHeader eqdHeader = null;
		EquipmentDataMappingTemplate eqdTemplate = null;
		
		try {
			is = new FileInputStream(new File(strFileName));
			String fileExt = strFileName.substring(strFileName.lastIndexOf(".") + 1, strFileName.length());
			
			if ("xls".equals(fileExt) || "xlsx".equals(fileExt)) {
				EQDExcelLoader eqdLoader = new EQDExcelLoader();
				eqdRoot = eqdLoader.load(is); //, "JRC_VDR", "NMEA");
				is.close();
				
				eqdHeader = new EquipmentDataHeader("NewDevice", "1", "0");
				eqdTemplate = new EquipmentDataMappingTemplate();
			} else if ("xml".equals(fileExt)) {
				EQDXMLModel eqdXmlModel = EQDXMLHandler.load(is);
				is.close();
				
				eqdRoot = eqdXmlModel.parse(); 
				eqdHeader = eqdXmlModel.getEqdHeader();
				eqdTemplate = eqdXmlModel.getTemplateInfo();
						
			} else {
				is.close();
				return;
			}
			
			curFileName = strFileName;
			equipmentDataHeader = eqdHeader;
			templateList = eqdTemplate.getTemplateList();
			
			docStateChange(docState.DOC_STATE_RESET);
			
			v.setInput(eqdRoot);
			v.expandAll();
			
			// sync with mapping rule viewer
			try {
				syncWithMappingTable();
			} catch (Exception e) {
				LOGGER.debug("[EQD] WRN : mapping table is not ready");
			}
			
		} catch (EQDXMLException  | EQDExcelException e) {
		
			eqdRoot = new EquipmentDataModel(null, "root");
			v.setInput(eqdRoot);
			
			SimpleMessageBox.show(shell, e.getMessage());
		} catch (Exception e2) {
			eqdRoot = new EquipmentDataModel(null, "root");
			v.setInput(eqdRoot);
			
			SimpleMessageBox.show(shell, e2.toString());
		}
	}
	
	@Override
	public void doFileReloadAction() {
		// TODO Auto-generated method stub
		doFileLoadAction(curFileName);
	}
	
	public boolean doPrecheckSaveCondition() { 
		//LOGGER.debug("doPrecheckSaveCondition()");
		return true;
	}
	
	public void doFileSaveAction(String fileName) {
		LOGGER.debug("doFileSaveAction() - " + fileName);
		
		EquipmentDataModel eqdRoot = (EquipmentDataModel) v.getInput();
		EquipmentDataHeader eqdHeader = equipmentDataHeader;
		Shell shell = getViewSite().getShell();
		
		OutputStream os;
		try {
			os = new FileOutputStream(fileName);
			EQDXMLHandler.save(os, equipmentDataHeader, eqdRoot);
			os.close();
			
			curFileName = fileName;
			docStateChange(docState.DOC_STATE_RESET);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
			SimpleMessageBox.show(shell, e.getMessage());
		} catch (Exception e) {
			SimpleMessageBox.show(shell, e.toString());
		}
		
	}
	
	public void doItemInsertAction() {
		Shell shell = this.getViewSite().getShell();
		
		EquipmentDataModel eqdRoot = (EquipmentDataModel) v.getInput();
		if (eqdRoot == null) {
			// show new dialog
			return;
		}
		
		LOGGER.debug("item insert");
		
		EquipmentDataModel eqdProtocol = eqdRoot.child.get(0);
		String eqdProtocolName = eqdProtocol.getName();
		
		InsertDialog insertDlg = new InsertDialog(shell);
		int returnCode = insertDlg.open(); 
		if (Dialog.OK == returnCode) {
			LOGGER.debug("OK");
			String protocolName = insertDlg.getProtocolName();
			String groupName = insertDlg.getGroupName();
			List<String> recordItems = insertDlg.getRecordItems();
			
			LOGGER.debug(protocolName);
			LOGGER.debug(groupName);
			LOGGER.debug(recordItems);
			
			if (eqdProtocolName.equals(protocolName)) {
				EquipmentDataModel newGroup = new EquipmentDataModel(eqdProtocol, groupName);
				eqdProtocol.child.add(newGroup);
				
				int itemCount = recordItems.size();
				for (int i = 0; i < itemCount; i++) {
					EquipmentDataModel newItem = new EquipmentDataModel(newGroup, recordItems.get(i));
					newGroup.child.add(newItem);
				}
				docStateChange(docState.DOC_STATE_MODIFIED);
			
				syncWithMappingTable();
				
				v.expandAll();
				
				
			} else {
				LOGGER.debug("WRN : Protocol is not matching");
			}
			
			
			
		}
	}
	
	private EquipmentDataModel createEquipmentDataModel() {
		
		// start with blank tree
		EquipmentDataModel root = new EquipmentDataModel(null, "root");
		
		return root;

	}
	
	//private EquipmentDataModel create
	
	private ColumnLabelProvider createColumnLabelProvider(final int columnNum) {
		return new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				
				if (element instanceof EquipmentDataModel) {
					switch (columnNum) {
					case 0:
						return ((EquipmentDataModel) element).getName();
					case 1:
						return ((EquipmentDataModel) element).getDesc();
					case 2:
						return ((EquipmentDataModel) element).getType();
					case 3:
						return ((EquipmentDataModel) element).getLinkCount();
					case 4:
						return ((EquipmentDataModel) element).getDestination();
					default:
						break;
					}
				}
				return null;
			}
		};
	}
	
	private EditingSupport createEditingSupportFor(final TreeViewer viewer, final TextCellEditor textCellEditor, 
			final int columnNum) {
		return new EditingSupport(viewer) {
			
			@Override
			protected boolean canEdit(Object element) {
				return !(columnNum == 3 || columnNum == 4);
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return textCellEditor;
			}
			
			@Override
			protected Object getValue(Object element) {
				//return ((EquipmentDataModel) element).name;
				if (element instanceof EquipmentDataModel) {
					
					switch (columnNum) {
					case 0:
						return ((EquipmentDataModel) element).getName();
					case 1:
						return ((EquipmentDataModel) element).getDesc();
					case 2:
						return ((EquipmentDataModel) element).getType();
					default:
						break;
					}
				}
				return null;
			}
			
			@Override
			protected void setValue(Object element, Object value) {
					if (element instanceof EquipmentDataModel) {
					
						switch (columnNum) {
						case 0:
							((EquipmentDataModel) element).setName((String) value);
							docStateChange(docState.DOC_STATE_MODIFIED);
							break;
						case 1:
							((EquipmentDataModel) element).setDesc((String) value);
							docStateChange(docState.DOC_STATE_MODIFIED);
							break;
						case 2:
							((EquipmentDataModel) element).setType((String) value);
							docStateChange(docState.DOC_STATE_MODIFIED);
							break;
						default:
							break;
					}
				}
				
				viewer.update(element,  null);
			}
		};
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

	/*
	// create sampledata
	private EquipmentDataModel createModel() {
		EquipmentDataModel root = new EquipmentDataModel (null, "root");
		EquipmentDataModel tmp;
		
		tmp = new EquipmentDataModel(root, "GLL", "GPS Position");
		root.child.add(tmp);
		tmp.child.add(new EquipmentDataModel(tmp, "01", "Latitude value", 	"degree"));
		tmp.child.add(new EquipmentDataModel(tmp, "02", "Latitude dir", 	"N/S"));
		tmp.child.add(new EquipmentDataModel(tmp, "03", "Longitude value", 	"degree"));
		tmp.child.add(new EquipmentDataModel(tmp, "04", "Longitude dir", 	"E/W"));
		tmp.child.add(new EquipmentDataModel(tmp, "05", "UTC of Position", 	"hhmmmss.ss"));
		tmp.child.add(new EquipmentDataModel(tmp, "06", "Satus", 			"A/V"));
		
		tmp = new EquipmentDataModel(root, "ZDA", "Time & Date" );
		root.child.add(tmp);
		tmp.child.add(new EquipmentDataModel(tmp, "01", "UTC", 		""));
		tmp.child.add(new EquipmentDataModel(tmp, "02", "Day", 		"integer 1~31"));
		tmp.child.add(new EquipmentDataModel(tmp, "03", "Month", 	"integer 1~12"));
		tmp.child.add(new EquipmentDataModel(tmp, "04", "Year",		"yyyy"));
		
		return root;
	}
	*/
	
	
	
	public void clearAllLinks() {
		
		TreeItem[] treeItems = v.getTree().getItems();
		
		clearDestinations(treeItems);
		
		v.refresh();
	}
	
	private boolean clearDestinations(TreeItem[] items) {
		
		Display display = Display.getCurrent();
		
		for (TreeItem item: items) {
			
			EquipmentDataModel eqditem = (EquipmentDataModel) item.getData();
			eqditem.clearDestination();
			
			item.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			
			if (!item.getExpanded()) {
				continue;
			}
			clearDestinations(item.getItems());
		}
		
		return true;
	}
	
	private boolean searchAndMarkForKV(List<EquipmentDataModel> items, String key, String destination, boolean bFound) {

		boolean bLocalFound = false;
		
		//LOGGER.debug("key = " + key + ", bFound = " + bFound);
		
		if (bFound) {
			return true;
		}

		for (EquipmentDataModel eqdItem : items) {
			
			if (key.equals(eqdItem.getName())) {
				eqdItem.setDestination(destination);
				bLocalFound = true;
				
				break;
			}
			
			bLocalFound = searchAndMarkForKV(eqdItem.getChildren(), key, destination, bFound);
			if (bLocalFound == true) {
				break;
			}
		}
		
		return bLocalFound;
	}
	
	public EquipmentDataModel searchEQD(EquipmentDataModel parent, String name) {
		
		for (EquipmentDataModel eqdItem : parent.getChildren()) {
			if (name.equals(eqdItem.getName())) {
				return eqdItem;
			}
		}
		
		return null;
	}
	
	private boolean searchAndMarkForNMEA(List<EquipmentDataModel> items, String key, String destination, boolean bFound) {

		boolean bLocalFound = false;
		
		String protocol = "NMEA";
		
		int idxOfDelimiter = key.indexOf('_'); 
		
		String groupName = key.substring(0, idxOfDelimiter);
		String recordName = key.substring(idxOfDelimiter + 1);
		
		LOGGER.trace("group = " + groupName + ", record = " + recordName);
		
		if (idxOfDelimiter < 1) {
			LOGGER.debug("[EQD] invalid key value : " + key);
			return false;
		}
		
		if (bFound) {
			return true;
		}

		for (EquipmentDataModel eqdItem : items) {
			if (bLocalFound == true) {
				break;
			}
				
			if (groupName.equals(eqdItem.getName())) {
				List<EquipmentDataModel> records = eqdItem.getChildren();
				
				for (EquipmentDataModel eqdrecord : records) {
					if (recordName.equals(eqdrecord.getName())) {
						bLocalFound = true;
						eqdrecord.setDestination(destination);
						break;
					}
				}
			}
		}
		
		return bLocalFound;
	}
	
	private boolean searchAndUpdateForKV(TreeItem[] items, String key, String destination, boolean bFound) {
		// �����ؾ���. 
		
		boolean bLocalFound = false;
		
		//LOGGER.debug("key = " + key + ", bFound = " + bFound);
		
		if (bFound) {
			return true;
		}

		for (TreeItem item : items) {
			
			EquipmentDataModel eqdItem = (EquipmentDataModel) item.getData();
			if (key.equals(eqdItem.getName())) {
				eqdItem.setDestination(destination);
				bLocalFound = true;
				
				item.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
				
				break;
			}
			
			if (!item.getExpanded()) {
				continue;
			}
			
			bLocalFound = searchAndUpdateForKV(item.getItems(), key, destination, bFound);
			if (bLocalFound == true) {
				break;
			}
		}
		
		return bLocalFound;
	}
	
	private boolean searchAndUpdateForNMEA(TreeItem[] items, String key, String destination, boolean bFound) {
		// �����ؾ���. 
		
		boolean bLocalFound = false;
		
		String protocol = "NMEA";
		
		int idxOfDelimiter = key.indexOf('_'); 
		
		String groupName = key.substring(0, idxOfDelimiter);
		String recordName = key.substring(idxOfDelimiter + 1);
		
		LOGGER.trace("group = " + groupName + ", record = " + recordName);
		
		if (idxOfDelimiter < 1) {
			LOGGER.debug("[EQD] invalid key value : " + key);
			return false;
		}
		
		for (TreeItem item : items) {
			if (bLocalFound == true) {
				break;
			}
			
			EquipmentDataModel eqditem = (EquipmentDataModel) item.getData();
			if (eqditem.getName().equals(groupName)) {
				TreeItem[] records = item.getItems();
				for (TreeItem record : records) {
					EquipmentDataModel eqdrecord = (EquipmentDataModel) record.getData();
					if (eqdrecord.getName().equals(recordName)) {
						bLocalFound = true;
						eqdrecord.setDestination(destination);
						record.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
						break;
					}
				}
			}
		}
		
		
		return bLocalFound;
	}
	
	public void printItems(TreeItem[] items) {
		for (TreeItem item: items) {
			
			EquipmentDataModel eqditem = (EquipmentDataModel) item.getData();
			LOGGER.debug(eqditem.getName());
			LOGGER.debug(eqditem.getDesc());
			
			if (!item.getExpanded()) {
				continue;
			}
			printItems(item.getItems());
		}
	}
	
	private String getKeyFromMapItem(MapDataItem mapItem) {
		String protocol = mapItem.getProtocol();
		String from = mapItem.getFrom();
		String param1 = mapItem.getParam1();
		String key = "";
		
		if (protocol.equals("NMEA")) {
			 // ���� ó�� 
			key = from + "_" + param1;
		} else if (protocol.equals("KV")) {
			key = from;
		} else {
			key = "";
		}
		
		return key;

	}
	
	public void refreshContents() {
		
		updateTreeItemColors(v.getTree().getItems());
		
		v.refresh();
	}
	
	public void updateTreeItemColors(TreeItem[] items) {
		
		for (TreeItem item : items) {
			EquipmentDataModel eqdItem = (EquipmentDataModel) item.getData();
			
			if (eqdItem.isMapped()) {
				item.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
			}
			
			if (!item.getExpanded()) {
				continue;
			}
			updateTreeItemColors(item.getItems());
		}
	}
	
	public boolean searchAndUpdateByMapItem(MapDataItem mapItem) {
		String key;
		String destination;
		
		boolean bFound = false;
	
		String protocol = "";
		
		EquipmentDataModel eqdRoot = (EquipmentDataModel) v.getInput();
		EquipmentDataModel eqdProtocol = eqdRoot.child.get(0);
		
		// protocol check
		try {
			
			
			protocol = eqdProtocol.getName();
			
			if (!protocol.equals(mapItem.getProtocol())) {
				LOGGER.debug("[EQD] WRN : sync. protocol matching fail. Eqd : " + protocol + "Map : " + mapItem.getProtocol());
				
				return false;
			}
			
		} catch (Exception e) {
			LOGGER.debug("[EQD] WRN : sync. equipment data is not ready");
			return false;
		}
		
		// get key
		key = getKeyFromMapItem(mapItem);
		if (key == null || key.equals("")) {
			LOGGER.debug("[EQD] WRN : sync. key == null : " + mapItem);
			return false;
		}
		
		// get destination.
		destination = mapItem.getTo();
		if (destination == null || destination.equals("")) {
			LOGGER.debug("[EQD] WRN : sync. destination == null : " + mapItem);
			return false;
		}
		
		if ("KV".equals(protocol)) {
			bFound = searchAndMarkForKV(eqdProtocol.getChildren(), key, destination, false);
		} else if ("NMEA".equals(protocol)) {
			bFound = searchAndMarkForNMEA(eqdProtocol.getChildren(), key, destination, false);;
		}
		
		
		return bFound;
	}

	public double getFullVersion() {
		return equipmentDataHeader.getFullVersion();
	}
	
	/*
	public void updateLinks(List<MapDataItem> mapDataList) {
		String key;
		String destination;
		boolean bFound = false;
		
		TreeItem[] treeItems = v.getTree().getItems();
		
		for (MapDataItem mapItem : mapDataList ) {
			bFound = false;
			
			key = getKeyFromMapItem(mapItem);
			if (key.equals("") || key == null) {
				LOGGER.debug("[EQD] WRN : sync. key == null : " + mapItem);
				continue;
			}
			
			// protocol check
			try {
				EquipmentDataModel eqdRoot = (EquipmentDataModel) v.getInput();
				EquipmentDataModel eqdProtocol = eqdRoot.child.get(0);
				String eqdProtocolName = eqdProtocol.getName();
				
				if(!eqdProtocolName.equals(mapItem.getProtocol())) {
					System.out.printf("[EQD] WRN : sync. protocol matching fail. Eqd : %s, Map : %s\n", 
							eqdProtocolName, mapItem.getProtocol());
					return;
				}
				
			} catch (Exception e) {
				LOGGER.debug("[EQD] WRN : sync. equipment data is not ready");
				return;
			}
			
			// get destination.
			destination = mapItem.getTo();
			if (destination.equals("") || destination == null) {
				LOGGER.debug("[EQD] WRN : sync. destination == null : " + mapItem);
				continue;
			}
			
			// 
			bFound = searchAndUpdate_KV(treeItems, key, destination, false);
			if(bFound != true) {
				LOGGER.debug("[EQD] WRN : sync. could not found : " + mapItem );
			}
			
		}
		
		v.refresh();
		
	}
	*/
	
	public boolean onClose() {
		Shell shell = this.getViewSite().getShell();
		
		if (docState.getDocState() == docState.DOC_STATE_MODIFIED) {
			int ret = SimpleMessageBox.show(shell, "Save current equipment data before close?", 
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
//end of EquipmentDataView.java