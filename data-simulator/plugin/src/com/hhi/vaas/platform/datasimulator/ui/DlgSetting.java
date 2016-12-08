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

package com.hhi.vaas.platform.datasimulator.ui;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

import com.hhi.vaas.platform.datasimulator.IDefineID;
import com.hhi.vaas.platform.datasimulator.common.CommonMethodes;
import com.hhi.vaas.platform.datasimulator.udp.NicBinding;

public class DlgSetting extends Dialog {
	private Text txtIPAdress;
	private Text txtPortNumber;

	private String iPAdress;
	private int portNumbr;
	private String deviceName;
	private String deviceOptionName;
	private String protocol;
	private int typeofProtocol;

	private Combo comboDevice;
	private Combo comboDeviceOption;
	private Combo comboProtocol;
	private Combo comboNicList;
	private Label lblSettingInfo;

	private String viewName;

	private NetworkInterface nic;
	private List<NetworkInterface> nicList;
	private Label lblDataTyep;
	
	private Shell shell;

	public int getTypeofProtocol() {
		return typeofProtocol;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getDeviceOptionName() {
		return deviceOptionName;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getIPAdress() {
		return iPAdress;
	}

	public int getPortNumbr() {
		return portNumbr;
	}

	public NetworkInterface getNic() {
		return nic;
	}

	public DlgSetting(Shell parentShell, String viewName, String device, String dataType,
			String ipAdress, int portNumber, int typeofProtocol, NetworkInterface nic) {
		super(parentShell);
		this.viewName = viewName;
		this.shell = parentShell;
		this.deviceName = device;
		this.deviceOptionName = dataType;
		this.iPAdress = ipAdress;
		this.portNumbr = portNumber;
		this.typeofProtocol = typeofProtocol;
		this.nic = nic;
	}
	
	private void initSetting() {
		if(!deviceName.isEmpty() && deviceName != null) {
			String strDeviceNameList[] = comboDevice.getItems();
			
			for(int i = 0; i < strDeviceNameList.length; i++) {
				if(strDeviceNameList[i].contains(deviceName)) {
					comboDevice.select(i);
				} else {
					if(i == strDeviceNameList.length - 1) {
						comboDevice.add(deviceName);
						comboDevice.select(i+1);
					}
				}
			}
		}
		
		if(!deviceOptionName.isEmpty() && deviceOptionName != null) {
			String strDeviceOptionName[] = comboDeviceOption.getItems();
			
			for(int i = 0; i < strDeviceOptionName.length; i++) {
				if(strDeviceOptionName[i].contains(deviceOptionName)) {
					comboDeviceOption.select(i);
				} else {
					if(i == strDeviceOptionName.length - 1) {
						comboDeviceOption.add(deviceOptionName);
						comboDeviceOption.select(i+1);
					}
				}
			}
		}
		
		if(!iPAdress.isEmpty() && iPAdress != null) {
			txtIPAdress.setText(iPAdress);
		}
		
		if(portNumbr > -1) {
			txtPortNumber.setText(String.valueOf(portNumbr));
		}
		
		if(typeofProtocol == IDefineID.UDP_UNICAST) {
			comboProtocol.select(0);
		} else if(typeofProtocol == IDefineID.UDP_MULTICAST) {
			comboProtocol.select(1);			
			comboNicList.setEnabled(true);
			
			if(nic != null) {
				Enumeration<InetAddress> nicAddresses = nic.getInetAddresses();
				String ipAddr = nicAddresses.nextElement().toString();	
				ipAddr = ipAddr.substring(1);				
				String strNicList[] = comboNicList.getItems();
				
				for(int i = 0; i < strNicList.length; i++) {
					if(strNicList[i].contains(ipAddr)) {
						comboNicList.select(i);
					}
				}
			}
		} else if(typeofProtocol == IDefineID.UDP_BROADCAST) {
			comboProtocol.select(2);
		} else if(typeofProtocol == IDefineID.ZEROMQ_PUSH) {
			comboProtocol.select(3);
		}		
	}

	protected void configureShell(Shell newGenerator) {
		super.configureShell(newGenerator);

		newGenerator.setText("Setting");

		NicBinding nicListBinding = new NicBinding();
		nicList = new ArrayList<NetworkInterface>();
		nicList = nicListBinding.findNetworkInterface();
	}

	protected Control createDialogArea(final Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout glComposite = new GridLayout(5, false);
		composite.setLayout(glComposite);

		lblSettingInfo = new Label(composite, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 5, 1);
		gd_lblNewLabel.widthHint = 431;
		lblSettingInfo.setLayoutData(gd_lblNewLabel);
		// lblSettingInfo.setText("New Label");
		lblSettingInfo.setText(viewName);

		Label lblDevice = new Label(composite, SWT.NONE);
		lblDevice.setSize(43, 15);
		lblDevice.setText("&Device :");

		comboDevice = new Combo(composite, SWT.NONE);
		comboDevice.setItems(new String[] { "ACONIS", "VDR", "Loading Computer"});
		comboDevice.setVisibleItemCount(10);
		GridData gdComDevice = new GridData(SWT.LEFT, SWT.CENTER, false, false,
				1, 1);
		gdComDevice.widthHint = 140;
		comboDevice.setLayoutData(gdComDevice);
		comboDevice.setBounds(0, 0, 88, 23);
		comboDevice.select(0);		
		/*comboDevice.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (comboDevice.getSelectionIndex() == comboDevice
						.getItemCount() - 1) {
					String strTemp = setUserInput(parent.getShell());
					comboDevice.add(strTemp, comboDevice.getItemCount() - 1);
					comboDevice.select(comboDevice.getItemCount() - 2);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});*/

		lblDataTyep = new Label(composite, SWT.NONE);
		lblDataTyep.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblDataTyep.setText("Data Type :");

		comboDeviceOption = new Combo(composite, SWT.NONE);
		comboDeviceOption.setVisibleItemCount(10);
		comboDeviceOption.setItems(new String[] { "Sensor", "Alarm", "Config"});
		GridData gd_comboDeviceOption = new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1);
		gd_comboDeviceOption.widthHint = 140;
		comboDeviceOption.setLayoutData(gd_comboDeviceOption);
		comboDeviceOption.setBounds(0, 0, 88, 23);
		comboDeviceOption.select(0);		
		/*comboDeviceOption.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (comboDeviceOption.getSelectionIndex() == comboDeviceOption
						.getItemCount() - 1) {
					String strTemp = setUserInput(parent.getShell());
					comboDeviceOption.add(strTemp,
							comboDeviceOption.getItemCount() - 1);
					comboDeviceOption.select(comboDeviceOption.getItemCount() - 2);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) { }
		});*/
		new Label(composite, SWT.NONE);

		Label lbliPAdress = new Label(composite, SWT.NONE);
		lbliPAdress.setText("&IP :");

		txtIPAdress = new Text(composite, SWT.BORDER);
		GridData gdTxtIPAdress = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gdTxtIPAdress.widthHint = 156;
		txtIPAdress.setLayoutData(gdTxtIPAdress);
		txtIPAdress.setText("127.0.0.1");

		Label lblPortNumber = new Label(composite, SWT.NONE);
		lblPortNumber.setText("&Port :");

		txtPortNumber = new Text(composite, SWT.BORDER);
		GridData gdTxtPortNumber = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1);
		gdTxtPortNumber.widthHint = 156;
		txtPortNumber.setLayoutData(gdTxtPortNumber);
		txtPortNumber.setText("Port Number");

		txtPortNumber.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
			}

			@Override
			public void mouseDown(MouseEvent e) {
				txtPortNumber.setText("");
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				txtPortNumber.setText("");
			}
		});

		/*txtPortNumber.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (!CommonMethodes.isLongValue(txtPortNumber.getText())) {
					MessageBox dialog = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_WARNING);
					dialog.setText("Warning!");
					dialog.setMessage("Input number");

					if (dialog.open() == SWT.OK) {
						txtPortNumber.setText("");
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});*/

		Label lblProtocol = new Label(composite, SWT.NONE);
		lblProtocol.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblProtocol.setText("Protocol :");

		comboProtocol = new Combo(composite, SWT.READ_ONLY);
		comboProtocol.setVisibleItemCount(4);
		comboProtocol.setItems(new String[] { "UDP : Unicast",
				"UDP : Multicast", "UDP : Broadcast", "ZeroMQ : PUB" });
		GridData gd_comboProtocol = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_comboProtocol.widthHint = 140;
		comboProtocol.setLayoutData(gd_comboProtocol);
		comboProtocol.select(0);

		comboProtocol.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (comboProtocol.getSelectionIndex() == 1) {
					comboNicList.setEnabled(true);
				} else {
					comboNicList.setEnabled(false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label lblNic = new Label(composite, SWT.NONE);
		lblNic.setText("NIC :");

		comboNicList = new Combo(composite, SWT.READ_ONLY);
		GridData gd_comboNicList = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		gd_comboNicList.widthHint = 140;
		comboNicList.setLayoutData(gd_comboNicList);
		comboNicList.setEnabled(false);
		new Label(composite, SWT.NONE);

		for (int i = 0; i < nicList.size(); i++) {
			Enumeration<InetAddress> nicAddresses = nicList.get(i).getInetAddresses();
			String ipAddr = nicAddresses.nextElement().toString();
			
			String tmp = nicList.get(i).getName() + " : "
					+ nicList.get(i).getDisplayName() + " - " + ipAddr.substring(1);
			comboNicList.add(tmp);
		}
		
		initSetting();

		return composite;
	}

	protected void okPressed() {
		boolean checkIpAddress, checkMultiIpAddress = false, checkLoopbakcIpAddress = false, checkPortNumber, checkNic = false;
		InetAddress ipAddress = null;
		
		try {
			ipAddress = InetAddress.getByName(txtIPAdress.getText());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		checkIpAddress = CommonMethodes.checkIpAddress(trimString(txtIPAdress.getText()));
		
		if(checkIpAddress) {
			checkMultiIpAddress = ipAddress.isMulticastAddress();
			
			if(ipAddress.getHostAddress().equals("127.0.0.1")) {
				checkLoopbakcIpAddress = true;
			}
		}
		
		checkPortNumber = CommonMethodes.checkPortNbr(trimString(txtPortNumber.getText()));
				
		if (comboProtocol.getSelectionIndex() == 1) {
			if(comboNicList.getSelectionIndex() < 0) {
				checkNic = false;
			} else {
				checkNic = true;
			}
		} else {
			checkNic = true;
		}

		if(!checkIpAddress) {
			CommonMethodes.warningMessageBox("Wrong IP Address!!", shell);
		} else if(!checkPortNumber) {
			CommonMethodes.warningMessageBox("Wrong Port Number!!", shell);
		} else if(!checkNic) {
			CommonMethodes.warningMessageBox("Select the NIC", shell);
		} else if (comboProtocol.getSelectionIndex() == 1 && !checkMultiIpAddress) {
			if (!checkLoopbakcIpAddress) {
				CommonMethodes.warningMessageBox("Wrong Multicast IP Address!!", shell);
				checkIpAddress = false;
			}
		}
				
		if(checkIpAddress && checkPortNumber && checkNic) {
			/*deviceName = comboDevice.getItem(comboDevice.getSelectionIndex());
			deviceOptionName = comboDeviceOption.getItem(comboDeviceOption
					.getSelectionIndex());*/
			deviceName = comboDevice.getText();
			deviceOptionName = comboDeviceOption.getText();
			protocol = comboProtocol.getItem(comboProtocol.getSelectionIndex());
			iPAdress = txtIPAdress.getText();
			portNumbr = Integer.valueOf(txtPortNumber.getText());

			if (comboProtocol.getSelectionIndex() == 0) {
				typeofProtocol = IDefineID.UDP_UNICAST;
			} else if (comboProtocol.getSelectionIndex() == 1) {
				if(checkLoopbakcIpAddress) {
					typeofProtocol = IDefineID.UDP_UNICAST;
				} else {
					typeofProtocol = IDefineID.UDP_MULTICAST;
				}
			} else if (comboProtocol.getSelectionIndex() == 2) {
				typeofProtocol = IDefineID.UDP_BROADCAST;
			} else if (comboProtocol.getSelectionIndex() == 3) {
				typeofProtocol = IDefineID.ZEROMQ_PUSH;
			}

			if (comboNicList.getSelectionIndex() < 0) {
				nic = null;
			} else {
				nic = nicList.get(comboNicList.getSelectionIndex());
			}

			super.okPressed();
		}
	}
	
	private String trimString(String ipAddress) {
		String trimIpAddress;
		
		// 모든 공백 제거
		// tmp = ipAddress.replaceAll("\\p{Z}", ipAddress.replaceAll(" ", ""));
		// 문자열 앞뒤 공백 제거
		trimIpAddress = ipAddress.trim();
		
		return trimIpAddress;
	}

	/*private String setUserInput(Shell shell) {
		DlgInputSystem dlg = new DlgInputSystem(shell.getShell());
		String inputStr = "";

		if (Window.OK == dlg.open()) {
			inputStr = dlg.getStrInput();
		}

		return inputStr;
	}*/
}
