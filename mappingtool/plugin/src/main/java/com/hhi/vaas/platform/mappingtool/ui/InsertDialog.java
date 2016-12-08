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
 * hsbae			2015. 4. 21.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.hhi.vaas.platform.mappingtool.nmea.NMEA0183Parser;


public class InsertDialog extends Dialog {
	private Shell parentShell = null;
	
	// return values;
	private String protocolName;
	private String groupName;
	private List<String> recordList = new ArrayList<String>();
	
	
	private Tree treePreview;
	private Text textSampleSentence;
	private Text textGroupName;
	private Text textRecordCount;
	private Text text_3;
	private Text text_4;
	private Text text_5;
	
	private static final Logger LOGGER = Logger.getLogger(InsertDialog.class);


	
	public InsertDialog(Shell parentShell) {
		super(parentShell);
		
		// TODO Auto-generated constructor stub
		this.parentShell = parentShell;
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
 
		Composite composite = (Composite) super.createDialogArea(parent);
 
		composite.getShell().setText("Insert Group");
 
		
		composite.setLayout(new GridLayout(1, false));
		
		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmNmea = new TabItem(tabFolder, SWT.NONE);
		tbtmNmea.setText("NMEA");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmNmea.setControl(composite_1);
		composite_1.setLayout(new GridLayout(5, false));
		
		Label lblSampleSentence = new Label(composite_1, SWT.NONE);
		lblSampleSentence.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSampleSentence.setText("Sample Sentence : ");
		
		textSampleSentence = new Text(composite_1, SWT.BORDER);
		textSampleSentence.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Button btnGenBySample = new Button(composite_1, SWT.NONE);
		btnGenBySample.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				insertBySample(e);
				
			}
		});
		btnGenBySample.setText("Generate");
		
		Label lblGroupName = new Label(composite_1, SWT.NONE);
		lblGroupName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGroupName.setText("Group Name :");
		
		textGroupName = new Text(composite_1, SWT.BORDER);
		textGroupName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Record Count :");
		
		textRecordCount = new Text(composite_1, SWT.BORDER);
		textRecordCount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnGenByInfo = new Button(composite_1, SWT.NONE);
		btnGenByInfo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				insertByInfo(e);
			}
		});
		btnGenByInfo.setText("Generate");
		
		Label label = new Label(composite_1, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		
		treePreview = new Tree(composite_1, SWT.BORDER);
		treePreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 2));
		
		/*
		treePreview.addListener(SWT.MouseDoubleClick, new Listener() {
		      public void handleEvent(Event event) {
		        if (event.detail == SWT.MouseDoubleClick) {
		        	LOGGER.debug("double clicked");
		          //text.setText(event.item + " was checked.");
		        } else {
		        	LOGGER.debug("double clicked -- ");
		          //text.setText(event.item + " was selected");
		        }
		      }
		    });
		*/
		
		TabItem tbtmKv_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmKv_1.setText("KV");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmKv_1.setControl(composite_2);
		composite_2.setLayout(new GridLayout(5, false));
		
		Label label_1 = new Label(composite_2, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("Sample Sentence : ");
		
		text_3 = new Text(composite_2, SWT.BORDER);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Button button_1 = new Button(composite_2, SWT.NONE);
		button_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		button_1.setText("Generate");
		
		Label label_2 = new Label(composite_2, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("Group Name :");
		
		text_4 = new Text(composite_2, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_3 = new Label(composite_2, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("Record Count :");
		
		text_5 = new Text(composite_2, SWT.BORDER);
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button = new Button(composite_2, SWT.NONE);
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		button.setText("Generate");
		
		Label label_4 = new Label(composite_2, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
		
		Tree tree_1 = new Tree(composite_2, SWT.BORDER);
		tree_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 3));
 
		return composite;
	}
 

	

	private void insertBySample(SelectionEvent e) {
		// check text field
		String nmeaData = textSampleSentence.getText();
		nmeaData.trim();
		try {
			NMEA0183Parser parser = new NMEA0183Parser(nmeaData);
			String groupName = parser.getSentenceId();
			int recordCount = parser.getItemCount();
		
			LOGGER.debug("group = " + groupName + "count = " + recordCount);
			
			if ("".equals(groupName) || recordCount < 1) {
				throw new Exception("invalid data");
			}
			
			treePreview.clearAll(true);
			
			TreeItem treeItem0 = new TreeItem(treePreview, SWT.NONE);
			treeItem0.setText("NMEA");
			
			TreeItem treeItem1 = new TreeItem(treeItem0, 0);
			treeItem1.setText(groupName);
			for (int i = 0; i < recordCount; i++) {
				TreeItem treeItem = new TreeItem(treeItem1, SWT.NONE);
				String strFieldNo = String.format("field:%02d", i + 1);
				treeItem.setText(strFieldNo);
			}
			
			// expendAll
			treePreview.getItem(0).setExpanded(true);
			treePreview.getItem(0).getItem(0).setExpanded(true);
			
			
		} catch (Exception e1) {
			MessageDialog.openWarning(parentShell,  "Warning", "Invalid data");
			return;
		}
	}
	
	private void insertByInfo(SelectionEvent e) {
		// check text field
		String groupName = textGroupName.getText();
		int recordCount = 0;
		
		try {
			recordCount = Integer.parseInt(textRecordCount.getText());
			
			LOGGER.debug("group = " + groupName + "count = " + recordCount);
			
			if ("".equals(groupName) || recordCount < 1) {
				throw new Exception("invalid data");
			}
		} catch (Exception e1) {
			MessageDialog.openWarning(parentShell,  "Warning", "Invalid data");
			return;
		}

		treePreview.clearAll(true);
		
		TreeItem treeItem0 = new TreeItem(treePreview, SWT.NONE);
		treeItem0.setText("NMEA");
		
		TreeItem treeItem1 = new TreeItem(treeItem0, 0);
		treeItem1.setText(groupName);
		for (int i = 0; i < recordCount; i++) {
			TreeItem treeItem = new TreeItem(treeItem1, SWT.NONE);
			String strFieldNo = String.format("field:%02d", i + 1);
			treeItem.setText(strFieldNo);
		}
		
		// expendAll
		treePreview.getItem(0).setExpanded(true);
		treePreview.getItem(0).getItem(0).setExpanded(true);
		
	}
	
	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		try {
			protocolName = treePreview.getItem(0).getText();
			groupName = treePreview.getItem(0).getItem(0).getText();
			int itemCount = treePreview.getItem(0).getItem(0).getItemCount();
			for (int i = 0; i < itemCount; i++) {
				recordList.add(treePreview.getItem(0).getItem(0).getItem(i).getText());
			}
		} catch (Exception e) {
			protocolName = "";
			groupName = "";
			recordList.clear();
		}
       super.okPressed();
	}

	// for return values
	public String getProtocolName() {
		return protocolName;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public List<String> getRecordItems() {
		return recordList;
	}
	
	
	
}
//end of MyDialog.java