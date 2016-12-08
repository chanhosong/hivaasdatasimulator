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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.ViewPart;

import com.hhi.vaas.platform.mappingtool.mapdatamodel.MapDataItem;
import com.hhi.vaas.platform.mappingtool.toolbaractions.FileEditAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.FileLoadAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.FileReloadAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.FileSaveAction;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IFileEditActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IFileLoadActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IFileReloadActionCallback;
import com.hhi.vaas.platform.mappingtool.toolbaractions.IFileSaveActionCallback;
import com.hhi.vaas.platform.mappingtool.vesseldatamodel.VDMHandlerInterface;
import com.hhi.vaas.platform.vdm.handler.VDMConstants;
import com.hhi.vaas.platform.vdm.handler.VDMNode;


/**
 * VesselDataModelView class
 *   - Load / Display VDM Tree
 * @author hsbae
 *
 */
public class VesselDataModelView extends ViewPart 
		implements IFileLoadActionCallback, IFileSaveActionCallback, IFileEditActionCallback, 
					IFileReloadActionCallback  {
	
	public static final String ID = "com.hhi.vaas.platform.mappingtool.vesseldatamodelview";
	
	private static final Logger LOGGER = Logger.getLogger(VesselDataModelView.class);
	
	private TreeViewer treeViewer;
	private Combo combo;
	private String curFileName;
	
	private VDMHandlerInterface vdmh;
	
	private Display display = Display.getCurrent();
	
	public VesselDataModelView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		Label lblSystem = new Label(parent, SWT.NONE);
		lblSystem.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSystem.setText("System :");
		
		combo = new Combo(parent, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleSystemSelection(e);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSystemSelection(e);
				//LOGGER.debug(combo.getText());
		      }
			
		});
		
		treeViewer = new TreeViewer(parent, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		
		tree.addListener(SWT.Expand, new Listener() {

			@Override
			public void handleEvent(Event e) {
				updateTreeItemColors(((TreeItem) e.item).getItems());
			}

		});
		
		
		
		//tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		// TODO Auto-generated method stub
		
		// addActions
		addToolbarActions();
		
		addKeyEventHandler();
		
		addOtherEventHandler();
		
		treeViewer.setContentProvider(new VdmTreeContentProvider());
		//treeViewer.setLabelProvider(new VdmTreeLabelProvider());
		
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] {TextTransfer.getInstance()};
		treeViewer.addDragSupport(operations, transferTypes, new VDMDragListener(treeViewer));
		
		// set comparer
//		
//		treeViewer.setComparer(new IElementComparer() {
//		    // we need this to keep refresh() working while having custom
//		    // equals() in PushOperationResult
//			
//			@Override
//			public int hashCode(Object element) {
//				final int prime = 31;
//				int result = 1;
//				result = prime * result
//						+ ((toString() == null) ? 0 : toString().hashCode());
//				return result;
//			}
//
//
//
//			@Override
//			public boolean equals(Object obj) {
//				if (this == obj)
//					return true;
//				if (obj == null)
//					return false;
//				if (getClass() != obj.getClass())
//					return false;
//				VDMNode other = (VDMNode) obj;
//				if (toString() == null) {
//					if (other.toString() != null)
//						return false;
//				} else if (!toString().equals(other.toString()))
//					return false;
//				return true;
//			}
//
//
//
//			@Override
//			public boolean equals(Object a, Object b) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//
//
//			@Override
//			public int hashCode(Object element) {
//				// TODO Auto-generated method stub
//				return 0;
//			}
//		 });
//		 
		
		// add columns
		createColumns(parent, treeViewer);
		
		treeViewer.setInput(null);
		//treeViewer.expandAll();
		
		//addColumns();
		
	}

	private void createColumns(Composite parent, TreeViewer tv) {
		// TODO Auto-generated method stub
		String [] titles = {"Name", "Type", "Description"};
		int [] bounds = {230, 70, 400};
		
		TreeViewerColumn col = null;
		
		// vdmpath column
		col = createTreeViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				// TODO Auto-generated method stub
				Object element = cell.getElement();
				String label = "";
				if (element instanceof VDMNode) {
					VDMNode vdmNode = (VDMNode) element;
					String nodeName = vdmNode.getNodeName(); 
					if (VDMConstants.NODE_NAME_LDEVICE.equals(nodeName)) {
						label = vdmNode.getAttribute("inst");
					} else if (VDMConstants.NODE_NAME_LN.equals(nodeName)) {
						label = vdmNode.getAttribute("prefix") + vdmNode.getAttribute("lnClass") 
								+ vdmNode.getAttribute("inst");
					} else if (VDMConstants.NODE_NAME_DO.equals(nodeName) 
							|| VDMConstants.NODE_NAME_SDO.equals(nodeName)) {
						label = vdmNode.getAttribute("name");
					} else if (VDMConstants.NODE_NAME_DA.equals(nodeName) 
							|| VDMConstants.NODE_NAME_BDA.equals(nodeName)) {
						label = vdmNode.getAttribute("name");
					} else {
						label = element.toString();
					}
				}
				cell.setText(label);
				
			}
		});
		
		// type column
		col = createTreeViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				// TODO Auto-generated method stub
				Object element = cell.getElement();
				String label = "";
				if (element instanceof VDMNode) {
					VDMNode vdmNode = (VDMNode) element;
					label = vdmNode.getNodeName(); // will be changed vdmNode.getNodeDesc();
					//vdmNode.
				}
				cell.setText(label);
			}
		});
		
		// type column
		col = createTreeViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				// TODO Auto-generated method stub
				Object element = cell.getElement();
				String label = "";
				if (element instanceof VDMNode) {
					VDMNode vdmNode = (VDMNode) element;
					label = vdmNode.getDescription(); // will be changed vdmNode.getNodeDesc();
				}
				cell.setText(label);
			}
		});
		
	}

	private TreeViewerColumn createTreeViewerColumn(String title, int bounds, final int colNumber) {
		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
		
		column.getColumn().setText(title);
		column.getColumn().setWidth(bounds);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		
		return column;
	}
	
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	private void addToolbarActions() {
		
		// add edit toolbar
		FileEditAction fileEditAction = new FileEditAction(this);
		fileEditAction.setText("XML Edit");
		getViewSite().getActionBars().getToolBarManager().add(fileEditAction);
		
		// add filelaodaction to view toolbar.
		FileLoadAction fileLoadAction = new FileLoadAction(this);
		//fileLoadAction.setText("Load equipment data");
		String [] loadfilters = {"*.vcd"};
		fileLoadAction.setFileFilters(loadfilters);
		getViewSite().getActionBars().getToolBarManager().add(fileLoadAction);
		
		// add filesaveaction to view toolbar.
		FileSaveAction fileSaveAction = new FileSaveAction(this);
		String [] savefilters = {"*.xml"};
		//fileSaveAction.setText("Save equipment data");
		fileSaveAction.setFileFilters(savefilters);
		getViewSite().getActionBars().getToolBarManager().add(fileSaveAction);
		
		// add filereloadaction
		FileReloadAction fileReloadAction = new FileReloadAction(this);
		fileReloadAction.setText("VCD File Reload");
		getViewSite().getActionBars().getToolBarManager().add(fileReloadAction);
		
	}
	
	/*
	private void addColumns() {
		TreeViewerFocusCellManager focusCellManager 
				= new TreeViewerFocusCellManager(v, new FocusCellOwnerDrawHighlighter(v));
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(v) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
						return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
								|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
								|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
								|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
					}
		};
			
		int feature = ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL
				| ColumnViewerEditor.KEYBOARD_ACTIVATION;
		
		TreeViewerEditor.create(v,  focusCellManager,  actSupport, feature);
		final TextCellEditor textCellEditor = new TextCellEditor(v.getTree());
		
		String [] columLabels = { "Name", "Description", "VDMPath"};
		
		for(int i = 0; i < columLabels.length; i++) {
			TreeViewerColumn column = null;
			switch(i) {
			case 0:
				column = new TreeViewerColumn(v, SWT.NONE );
				column.getColumn().setWidth(100);
				break;
			case 1:
				column = new TreeViewerColumn(v, SWT.NONE );
				column.getColumn().setWidth(200);
				break;
			case 2:
				column = new TreeViewerColumn(v, SWT.NONE );
				column.getColumn().setWidth(300);
				break;

			}
			
			column.getColumn().setMoveable(true);
			column.getColumn().setText(columLabels[i]);
			//column.setLabelProvider(createColumnLabelProvider(i));
			//column.setEditingSupport(createEditingSupportFor(v, textCellEditor, i));
			
		}
	}
	*/
	
	private void addKeyEventHandler() {
	}
	
	private void addOtherEventHandler() {
		
	}

	public void handleSystemSelection(SelectionEvent e) {
		String systemTitle = combo.getText();
		
		VDMNode root = vdmh.getSystemRoot(systemTitle);
		treeViewer.setInput(root);
		
		syncWithMappingTable();
		
	}
	
	
	public String getCurrentSystemName() {
		//VDMNode root = (VDMNode) treeViewer.getInput();
		//String systemName = root.getNodeName();
		
		String systemName = combo.getText();
		
		return systemName;
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

	@Override
	public boolean doPrecheckSaveCondition() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void doFileSaveAction(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean doPrecheckLoadCondition() {
		// TODO Auto-generated method stub
		Shell shell = this.getViewSite().getShell();
		
//		if (docState.getDocState() == docState.DOC_STATE_MODIFIED) {
//			int ret = SimpleMessageBox.show(shell, "Save current mapping table before loading?", 
//					SimpleMessageBox.MSG_TYPE_WARNING, 
//					SWT.ICON_WARNING | SWT.OK | SWT.NO | SWT.CANCEL);
//			
//			if (ret == SWT.CANCEL) {
//				return false;
//			} else if (ret == SWT.OK) {
//				doFileSaveAction(pathMapDataFile);
//				SimpleMessageBox.show(shell, "Saved at \n" + pathMapDataFile);
//				return true;
//			} else {
//				// nothing
//				return true;
//			}
//		}
		
		return true;
	}
	

	@Override
	public void doFileLoadAction(String fileName) {
		// TODO Auto-generated method stub
		Shell shell = getViewSite().getShell();
		
		
		vdmh = new VDMHandlerInterface();
		
		InputStream is;
		try {
			is = new FileInputStream(fileName);
			LOGGER.debug("==> vdmh.load()");
			vdmh.load(is);
			LOGGER.debug("<== vdmh.load()");
			
			List<String> systemList = vdmh.getSystemList();
			combo.setItems((String[]) systemList.toArray(new String[systemList.size()]));
			combo.select(0);
			
			VDMNode root = vdmh.getSystemRoot(combo.getItem(0));
			treeViewer.setInput(root);
			//treeViewer.expandAll();
			treeViewer.expandToLevel(2);
			curFileName = fileName;
			LOGGER.debug("<== treeViewer.expandToLevel()");
			// sync with mapping rule viewer
			try {
				syncWithMappingTable();
			} catch (Exception e) {
				LOGGER.debug("[VDM] WRN : sync error. mapping table is not ready");
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			SimpleMessageBox.show(shell, e.getMessage());
			
		} catch (Exception e) {
			SimpleMessageBox.show(shell, e.toString());
		}
	}

	@Override
	public void doFileReloadAction() {
		// TODO Auto-generated method stub
		doFileLoadAction(curFileName);
		
	}
	
	public double getFullVersion() {
		String version = "";
		double dVersion = 0.0;
		
		version = vdmh.getFullVersion();
		
		version = version.substring(0, version.lastIndexOf(".")); 	// trim revision field
		
		try {
			dVersion =  Double.parseDouble(version);
		} catch (Exception e) {
			LOGGER.debug("[VDM] version read error, set to 0.0 !! ");
			dVersion = 0.0;
		}
		
		return dVersion;
	}
	
	private void syncWithMappingTable() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(MappingTableView.ID);
		MappingTableView mView = (MappingTableView) view;
		
		LOGGER.debug("[VDM] INFO : sync with mapping table");
		
		mView.syncWithVDMView();
		//refreshContents();
	}
	
	public boolean searchAndUpdateByMapItem(String systemName, MapDataItem mapItem) {
		String key;
		String destination;
		
		boolean bFound = false;
	
		String vdmPath = mapItem.getTo();
		
		vdmPath = vdmPath.replace("VDMPath:", "");
		
		//LOGGER.debug("[VDM] vdmPath = " + vdmPath);
		
		bFound = vdmh.checkVdmPath(systemName, vdmPath);
		
		return bFound;
	}
	
	public void refreshContents() {
		
		TreeItem[] treeItems = treeViewer.getTree().getItems();
		updateTreeItemColors(treeItems);
		
		//printItems(treeViewer.getTree().getItems());

		//treeViewer.refresh();
		//refreshTree();
	}
	
	public void updateTreeItemColors(TreeItem[] items) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(MappingTableView.ID);
		MappingTableView mView = (MappingTableView) view;
		
		LOGGER.trace("[VDM] ==> updateTreeItemColors()");
		
		for (TreeItem item : items) {
			
			VDMNode vdmItem = (VDMNode) item.getData();
			
			String vdmPath = vdmItem.getVdmpath();
			//LOGGER.debug(vdmItem.getVdmpath());
			
			if (mView.checkVdmPathIsExist(vdmPath)) {
				item.setForeground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
			} else {
				item.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				LOGGER.trace("[VDM] not found item : " + vdmPath);
			}
			
			if (!item.getExpanded()) {
				continue;
			}
			updateTreeItemColors(item.getItems());
		}
		LOGGER.trace("[VDM] <== updateTreeItemColors()");
	}
	
	public void clearAllLinks() {
		
		TreeItem[] treeItems = treeViewer.getTree().getItems();
		
		clearDestinations(treeItems);
		
		//treeViewer.refresh();
		//refreshTree();
	}
	
	public void refreshTree() {
//		Object[] expandedElements = treeViewer.getExpandedElements(); 
//		treeViewer.refresh(); 
//		for (Object element : expandedElements) { 
//			treeViewer.setExpandedState(element, true); 
//		} 

		Object[] elements = treeViewer.getExpandedElements(); 
		TreePath[] treePaths = treeViewer.getExpandedTreePaths(); 
		treeViewer.refresh(); 
		treeViewer.setExpandedElements(elements); 
		treeViewer.setExpandedTreePaths(treePaths); 
	}
	

	private boolean clearDestinations(TreeItem[] items) {
		
		for (TreeItem item: items) {
			
			VDMNode vdmitem = (VDMNode) item.getData();
			vdmitem.setMapped(false);
			
			item.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			
			if (!item.getExpanded()) {
				continue;
			}
			clearDestinations(item.getItems());
		}
		
		return true;
	}
}


//end of VesselDataModelView.java