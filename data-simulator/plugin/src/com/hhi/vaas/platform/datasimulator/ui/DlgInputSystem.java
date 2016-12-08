
/* Copyright (C) eMarine Co. Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of eMarine Co. Ltd.
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with eMarine Co. Ltd.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * ddong			2015. 9. 4.				First Draft.
 */
package com.hhi.vaas.platform.datasimulator.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class DlgInputSystem extends Dialog {
	private Text textInput;
	
	private String strInput;

	protected DlgInputSystem(Shell parentShell) {
		super(parentShell);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Setting");
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Insert :");
		
		textInput = new Text(composite, SWT.BORDER);
		GridData gd_textInput = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textInput.widthHint = 200;
		textInput.setLayoutData(gd_textInput);
		
		return composite;
	}
	
	protected void okPressed() {
		strInput = textInput.getText();
		
		super.okPressed();
	}
	
	public String getStrInput() {
		return this.strInput;
	}
}
//end of UserSystem.java