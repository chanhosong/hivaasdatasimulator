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
 * hsbae			2015. 4. 23.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;

public class EQDSetDeviceInfoDialog extends Dialog {
	private Text textDeviceName;
	private Text textDeviceVendor;
	private Text textDeviceProtocol;
	private Text textDocumentName;
	
	
	private String strDocName;
	private String strDocVersion;
	private String strDocRevision;
	private String strDevName;
	private String strDevVendor;
	private String strDevProtocol;
	

	protected EQDSetDeviceInfoDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = (Composite) super.createDialogArea(parent);
		 
		composite.getShell().setText("New Document");
 
		
		composite.setLayout(new GridLayout(1, false));
		
		Group group = new Group(composite, SWT.NONE);
		group.setText("< Equipment Information >");
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		group.setLayout(new GridLayout(4, false));
		
		Label lblNewLabel_4 = new Label(group, SWT.NONE);
		lblNewLabel_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setAlignment(SWT.RIGHT);
		lblNewLabel_4.setText("Equipment ID : ");
		
		textDocumentName = new Text(group, SWT.BORDER);
		GridData gd_textDocumentName = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_textDocumentName.widthHint = 313;
		textDocumentName.setLayoutData(gd_textDocumentName);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Equipment Type : ");
		
		textDeviceName = new Text(group, SWT.BORDER);
		textDeviceName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Vendor : ");
		
		textDeviceVendor = new Text(group, SWT.BORDER);
		textDeviceVendor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("Protocol : ");
		
		textDeviceProtocol = new Text(group, SWT.BORDER);
		textDeviceProtocol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		strDocName = textDocumentName.getText();
		strDocVersion = "1";
		strDocRevision = "0";
		strDevName = textDeviceName.getText();
		strDevVendor = textDeviceVendor.getText();
		strDevProtocol = textDeviceProtocol.getText();
		
		super.okPressed();
	}
	

	public String getStrDocName() {
		return strDocName;
	}

	public String getStrDocVersion() {
		return strDocVersion;
	}

	public String getStrDocRevision() {
		return strDocRevision;
	}

	public String getStrDevName() {
		return strDevName;
	}

	public String getStrDevVendor() {
		return strDevVendor;
	}

	public String getStrDevProtocol() {
		return strDevProtocol;
	}

}
//end of EQDSetDeviceInfoDialog.java