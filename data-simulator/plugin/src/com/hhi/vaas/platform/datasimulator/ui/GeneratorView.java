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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.hhi.vaas.platform.datasimulator.IActionCallback;
import com.hhi.vaas.platform.datasimulator.IDefineID;
import com.hhi.vaas.platform.datasimulator.actions.ActionsExecution;
import com.hhi.vaas.platform.datasimulator.common.CommonMethodes;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.common.ViewMapSingleton;

public class GeneratorView extends ViewPart implements IActionCallback {
	private Text txtFileName;
	private Text txtFileDescription;
	private Text txtManualCommand;
	private Text txtPeriod;
	private Label lblLinesSec01;
	private Label lblBytesSec01;
	private Label lblBytesSec02;
	private Label lblBytesTrans01;
	private Label lblBytesTrans02;
	private Button btnSend;
	
	private IWorkbenchWindow window;
	private ActionsExecution actionExecution;
	private IActionCallback actionCallBack;
	private ViewMapSingleton map;
	private LoggerSingletone LOGGER;
	
	private File loadedFile;
	private String filePath;
	private int stateCode, functionCode;
	private String viewName;
	private String ipAdress;
	private String  device;
	private String dataType;
	private int portNumber = -1, typeofProtocol = -1;
	private boolean settingOk, loadfileOk, reloadOK, startOK;
	private double linesPerSec, bytesPerSec, bytesTransferred;
	private int thisIntanceID;
	private long periodTime;
	private final long time;
	private NetworkInterface nic = null;
	private boolean checkTimeStamp = false;

	public GeneratorView() {
		LOGGER = LoggerSingletone.getInstance();
		
		window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		this.actionExecution = new ActionsExecution();
		actionCallBack = this;
		stateCode = IDefineID.STATE_NO_EXSIST;
		functionCode = IDefineID.FUNCTION_NO_REPEAT;
		settingOk = false;
		loadfileOk = false;
		startOK = false;
		linesPerSec = 0;
		bytesPerSec = 0;
		bytesTransferred = 0;
		map = ViewMapSingleton.getInstance();
		thisIntanceID = map.getGeneratorInstanceCount();
		map.getMap().put(thisIntanceID, this);
		LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
		LOGGER.getLogger().info(String.format("Create a generator view %d", thisIntanceID));
		viewName = "";
		periodTime = 0;
		ipAdress = "";
		device = "";
		dataType = "";
		filePath = "";
		time = 500;
	}

	public void setPartName(String partName) {
		super.setPartName(partName);
	}

	private Action fileLoadAction = new Action("Open File") {
		public void run() {
			FileDialog dlgOpen = new FileDialog(window.getShell(), SWT.OPEN);

			if (IDefineID.STATE_STARTED == stateCode) {
				actionExecution.getFileReading().suspend();
				stateCode = actionExecution.getFileReading().getStateCode();
			}

			filePath = dlgOpen.open();

			fileLoad(filePath);
		}
	};

	public void fileLoad(String filePath) {
		if (filePath != null) {
			loadedFile = new File(filePath);
			if (loadedFile.exists()) {
				txtFileName.setText(loadedFile.getName());
				loadfileOk = true;

				if (stateCode == IDefineID.STATE_STARTED || stateCode == IDefineID.STATE_SUSPENDED) {
					actionExecution.getFileReading().stop();
					stateCode = IDefineID.STATE_INIT;
				} else if (stateCode == IDefineID.STATE_STOPPED) {
					actionExecution.getFileReading().setStateCode(IDefineID.STATE_INIT);
					stateCode = IDefineID.STATE_INIT;
				} else if (stateCode == IDefineID.STATE_NO_EXSIST) {
					stateCode = IDefineID.STATE_INIT;
				}

				if (settingOk) {
					startingProcess.setEnabled(true);
					//setting.setEnabled(false);
					startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
							IDefineID.IMG_START_PROCESS));
				}
			} else {
				CommonMethodes.warningMessageBox("Wrong file.", getViewSite().getShell());
				startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
						IDefineID.IMG_PAUSE_PROCESS));

				if (stateCode == IDefineID.STATE_SUSPENDED) {
					actionExecution.getFileReading().resume();
					stateCode = actionExecution.getFileReading().getStateCode();
				}
			}
		} else {
			if (stateCode == IDefineID.STATE_SUSPENDED) {
				actionExecution.getFileReading().resume();
				stateCode = actionExecution.getFileReading().getStateCode();
			}
		}
	}

	private Action startingProcess = new Action("Start Process") {
		public void run() {
			Shell shell = getViewSite().getShell();

			if (!loadedFile.exists()) {
				CommonMethodes.warningMessageBox("No seleted file", shell);
				return;
			}
			if (Integer.valueOf(txtPeriod.getText()) < 0) {
				CommonMethodes.warningMessageBox("Input period, 3 or over.", shell);
				return;
			}

			if (!settingOk) {
				CommonMethodes.warningMessageBox("Before starting process, you should set up UDP Net.", shell);
				return;
			}

			if (stateCode == IDefineID.STATE_NO_EXSIST || stateCode == IDefineID.STATE_INIT) {
				try {
					txtFileDescription.setText("");
					actionExecution.startProcess(loadedFile, actionCallBack, checkTimeStamp);
					setStateOfStartingProcess(true);
					actionExecution.getFileReading().start();
					reloadingProcess.setEnabled(true);
					repeatingProcess.setEnabled(true);
					startOK = true;
					if (periodTime <= time) {
						txtFileDescription.append("High-speed sending...\r\n");
					}
				} catch (FileNotFoundException e) {
					//e.printStackTrace();
					LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
					LOGGER.getLogger().error(String.format("Error code : 0x%04x Property file not found. ", IDefineID.ERROR_FILE_NOT_FOUND));
				} catch (InterruptedException e) {
					//e.printStackTrace();
					LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
					LOGGER.getLogger().error(String.format("Error code : 0x%04x Interrupted exception. ", IDefineID.ERROR_THREAD_INTERRUPTED_EXCEPTION));
				}
			} else if (actionExecution.getFileReading().getStateCode() == IDefineID.STATE_STARTED) {
				setStateOfStartingProcess(false);
				actionExecution.getFileReading().suspend();
			} else if (actionExecution.getFileReading().getStateCode() == IDefineID.STATE_SUSPENDED) {
				setStateOfStartingProcess(true);
				actionExecution.getFileReading().resume();
				if (periodTime <= time) {
					txtFileDescription.append("High-speed sending...\r\n");
				}

			} else if (actionExecution.getFileReading().getStateCode() == IDefineID.STATE_STOPPED) {
				txtFileDescription.setText("");
				actionExecution.getFileReading().setStateCode(IDefineID.STATE_INIT);
				actionExecution.getFileReading().start();
				if (periodTime <= time) {
					txtFileDescription.append("High-speed sending...\r\n");
				}
			}
			stateCode = actionExecution.getFileReading().getStateCode();
			functionCode = actionExecution.getFileReading().getFunctionCode();
		}
	};

	private Action reloadingProcess = new Action("Reload Process") {
		public void run() {
			reloadOK = true;

			if (actionExecution.getFileReading().getStateCode() == IDefineID.STATE_STARTED
					|| actionExecution.getFileReading().getStateCode() == IDefineID.STATE_SUSPENDED) {
				actionExecution.getFileReading().stop();
			}

			try {
				txtFileDescription.setText("");
				actionExecution.startProcess(loadedFile, actionCallBack, checkTimeStamp);
				setStateOfStartingProcess(true);
				actionExecution.getFileReading().start();
				if (periodTime <= time) {
					txtFileDescription.append("High-speed sending...\r\n");
				}
				stateCode = actionExecution.getFileReading().getStateCode();
			} catch (FileNotFoundException e) {
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Property file not found. ", IDefineID.ERROR_FILE_NOT_FOUND));
			} catch (InterruptedException e) {
				//e.printStackTrace();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
				LOGGER.getLogger().error(String.format("Error code : 0x%04x Interrupted exception. ", IDefineID.ERROR_THREAD_INTERRUPTED_EXCEPTION));
			}
		}
	};

	private Action repeatingProcess = new Action("Repeat Process") {
		public void run() {
			repeat();
		}
	};

	public void repeat() {
		if (stateCode == IDefineID.STATE_STARTED || stateCode == IDefineID.STATE_SUSPENDED) {
			if (actionExecution.getFileReading().getFunctionCode() == IDefineID.FUNCTION_REPEAT) {
				actionExecution.getFileReading().setFunctionCode(IDefineID.FUNCTION_NO_REPEAT);
				functionCode = IDefineID.FUNCTION_NO_REPEAT;
				repeatingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
						IDefineID.IMG_REPEAT_PROCESS));
			} else {
				actionExecution.getFileReading().setFunctionCode(IDefineID.FUNCTION_REPEAT);
				functionCode = IDefineID.FUNCTION_REPEAT;
				repeatingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
						IDefineID.IMG_REPEAT_PROCESS_FALSE));
			}
		} else {
			if (functionCode == IDefineID.FUNCTION_NO_REPEAT) {
				functionCode = IDefineID.FUNCTION_REPEAT;
				repeatingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
						IDefineID.IMG_REPEAT_PROCESS_FALSE));
			} else {
				functionCode = IDefineID.FUNCTION_NO_REPEAT;
				repeatingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
						IDefineID.IMG_REPEAT_PROCESS));
			}
		}
	}

	private Action setting = new Action("Setting") {
		public void run() {			
			boolean settingOkTmp = settingOk;
			
			if(stateCode == IDefineID.STATE_STARTED) {
				actionExecution.getFileReading().suspend();
				stateCode = IDefineID.STATE_SUSPENDED;
			}
			
			settingOk = actionExecution.setting(window, viewName, device, dataType, ipAdress, portNumber, typeofProtocol, nic);
			
			if (settingOk) {
				/*if(stateCode == IDefineID.STATE_STARTED || stateCode == IDefineID.STATE_SUSPENDED
						|| stateCode == IDefineID.STATE_STOPPED) {
					actionExecution.closingSocket();
				}*/
				
				if(stateCode == IDefineID.STATE_STARTED || stateCode == IDefineID.STATE_SUSPENDED) {
					actionExecution.getFileReading().stop();
					stateCode = IDefineID.STATE_STOPPED;
				}
				
				viewName = actionExecution.getStrViewTitle();
				device = actionExecution.getStrDeviceName();
				dataType = actionExecution.getStrDataTypde();
				ipAdress = actionExecution.getStrIpAdress();
				portNumber = actionExecution.getPortNumber();
				typeofProtocol = actionExecution.getTypeofProtocol();
				nic = actionExecution.getNic();
				btnSend.setEnabled(true);
				
				if (loadfileOk) {
					startingProcess.setEnabled(true);
					//setting.setEnabled(false);
					startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
							IDefineID.IMG_START_PROCESS));
				}
				setPartName(viewName);
			} else {
				settingOk = settingOkTmp;
				
				if(stateCode == IDefineID.STATE_SUSPENDED) {
					actionExecution.getFileReading().resume();
					stateCode = IDefineID.STATE_STARTED;
				}
			}
		}
	};

	private Action showMessage = new Action("Show Message") {
		public void run() {
			int stateCodeShowMsg = actionExecution.getFileReading().getStateCodeShowMsg();
			
			if(IDefineID.STATE_SHOW_MESSAGE == stateCodeShowMsg) {
				actionExecution.getFileReading().setStateCodeShowMsg(IDefineID.STATE_HIDE_MESSAGE);
				showMessage.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID, IDefineID.IMG_HIDE_MESSAGE));
			} else {
				actionExecution.getFileReading().setStateCodeShowMsg(IDefineID.STATE_SHOW_MESSAGE);
				showMessage.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID, IDefineID.IMG_SHOW_MESSAGE));
			}
			
		}
	};

	public void createPartControl(Composite parent) {
		createGeneratorview(parent);
		addToolBarAction();
		addListener(parent);
	}

	public void addListener(Composite parent) {
		txtPeriod.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				txtPeriod.setText("");
			}

			public void mouseDoubleClick(MouseEvent e) {
				txtPeriod.setText("");
			}
		});

		txtPeriod.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				if (!CommonMethodes.isLongValue(txtPeriod.getText())) {
					MessageBox dialog = new MessageBox(getViewSite().getShell(), SWT.OK | SWT.ICON_WARNING);
					dialog.setText("Warning!");
					dialog.setMessage("Input number");

					if (dialog.open() == SWT.OK) {
						txtPeriod.setText("");
					}
				}
			}

			public void keyPressed(KeyEvent e) {
			}
		});

		txtManualCommand.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				txtManualCommand.setText("");
			}

			public void mouseDoubleClick(MouseEvent e) {
				txtManualCommand.setText("");
			}
		});

		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (stateCode == IDefineID.STATE_STARTED || stateCode == IDefineID.STATE_SUSPENDED) {
					actionExecution.getFileReading().stop();
					// actionExecution.getFileReading().getSending().getSocket().close();
				}
				actionCallBack = null;
				map.getMap().remove(thisIntanceID);
				map.discountGeneratorInstanceMax();
				LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
				LOGGER.getLogger().info(String.format("Good bye generator view %d", thisIntanceID));
			}
		});
	}

	private void createGeneratorview(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		Composite composite01 = new Composite(parent, SWT.NONE);
		composite01.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout glComposite01 = new GridLayout(9, false);
		glComposite01.horizontalSpacing = 1;
		composite01.setLayout(glComposite01);

		Label lblFileName = new Label(composite01, SWT.NONE);
		lblFileName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFileName.setText("File Name :");

		txtFileName = new Text(composite01, SWT.BORDER);
		GridData gdTxtFileName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtFileName.widthHint = 347;
		txtFileName.setLayoutData(gdTxtFileName);
		txtFileName.setText("File Name");
		
		Label label_1 = new Label(composite01, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_1.heightHint = 20;
		label_1.setLayoutData(gd_label_1);
		
		Button btnCheckTimeStamp = new Button(composite01, SWT.CHECK);
		btnCheckTimeStamp.setText("Time stamp");
		
		btnCheckTimeStamp.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				Button btnTmp = (Button) e.widget;				
				checkTimeStamp = btnTmp.getSelection();
				if(IDefineID.STATE_INIT == stateCode || IDefineID.STATE_STARTED == stateCode || IDefineID.STATE_STOPPED == stateCode 
						|| IDefineID.STATE_SUSPENDED == stateCode) {
					actionExecution.getFileReading().setCheckTimeStamp(checkTimeStamp);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		Label label = new Label(composite01, SWT.SEPARATOR | SWT.CENTER);
		GridData gd_label = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_label.heightHint = 20;
		label.setLayoutData(gd_label);
		label.setAlignment(SWT.CENTER);

		Label lblPeriod = new Label(composite01, SWT.NONE);
		lblPeriod.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPeriod.setText("Period :");

		txtPeriod = new Text(composite01, SWT.BORDER | SWT.RIGHT);
		GridData gdTxtPeriod = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gdTxtPeriod.widthHint = 30;
		txtPeriod.setLayoutData(gdTxtPeriod);
		txtPeriod.setText("1000");
		periodTime = Long.valueOf(txtPeriod.getText());

		Label lblMs = new Label(composite01, SWT.NONE);
		lblMs.setText("ms");

		Button btnPeroidApply = new Button(composite01, SWT.NONE);
		btnPeroidApply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				periodTime = Long.valueOf(txtPeriod.getText());
				if (stateCode != IDefineID.STATE_NO_EXSIST && startOK == true) {
					actionExecution.getFileReading().setPeriodTime(periodTime);
				}

				if (periodTime <= time) {
					txtFileDescription.setText("High-speed sending...\r\n");
				}
			}
		});
		btnPeroidApply.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnPeroidApply.setText("Apply");

		Composite composite02 = new Composite(parent, SWT.NONE);
		GridData gdComposite02 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gdComposite02.widthHint = 286;
		composite02.setLayoutData(gdComposite02);
		composite02.setLayout(new GridLayout(1, false));

		txtFileDescription = new Text(composite02, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CANCEL | SWT.MULTI);
		GridData gdTxtFileDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gdTxtFileDescription.widthHint = 200;
		txtFileDescription.setLayoutData(gdTxtFileDescription);
		txtFileDescription.setBounds(0, 0, 73, 21);

		Composite composite03 = new Composite(parent, SWT.NONE);
		composite03.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite03.setLayout(new GridLayout(2, false));

		txtManualCommand = new Text(composite03, SWT.BORDER);
		GridData gdTxtManualCommand = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtManualCommand.widthHint = 330;
		txtManualCommand.setLayoutData(gdTxtManualCommand);
		txtManualCommand.setText("Manual Command");
		txtManualCommand.setBounds(0, 0, 73, 21);

		btnSend = new Button(composite03, SWT.NONE);
		btnSend.setEnabled(false);
		btnSend.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					manualSignal();
				} catch (IOException e1) {
					//e1.printStackTrace();
					LOGGER.setLogger(LOGGER.getLogger().getLogger(GeneratorView.class.getName()));
					LOGGER.getLogger().error(String.format("Error code : 0x%04x Property IO exception. ", IDefineID.ERROR_IO_EXCEPTION));
				}
			}
		});
		btnSend.setText("Send");

		Composite composite04 = new Composite(parent, SWT.NONE);
		GridData gdComposite04 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdComposite04.widthHint = 404;
		composite04.setLayoutData(gdComposite04);
		composite04.setLayout(new GridLayout(7, false));

		lblLinesSec01 = new Label(composite04, SWT.RIGHT);
		GridData gdLblLinesSec01 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gdLblLinesSec01.widthHint = 50;
		lblLinesSec01.setLayoutData(gdLblLinesSec01);
		lblLinesSec01.setText("0");

		Label lblLinesSec02 = new Label(composite04, SWT.NONE);
		lblLinesSec02.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblLinesSec02.setText("Lines/sec");

		lblBytesSec01 = new Label(composite04, SWT.RIGHT);
		GridData gdLblBytesSec01 = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gdLblBytesSec01.widthHint = 70;
		lblBytesSec01.setLayoutData(gdLblBytesSec01);
		lblBytesSec01.setText("0");

		lblBytesSec02 = new Label(composite04, SWT.NONE);
		lblBytesSec02.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblBytesSec02.setText("B/sec");

		lblBytesTrans01 = new Label(composite04, SWT.RIGHT);
		GridData gdLblBytesTrans01 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdLblBytesTrans01.widthHint = 240;
		lblBytesTrans01.setLayoutData(gdLblBytesTrans01);
		lblBytesTrans01.setText("0");

		lblBytesTrans02 = new Label(composite04, SWT.NONE);
		lblBytesTrans02.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblBytesTrans02.setAlignment(SWT.RIGHT);
		lblBytesTrans02.setText("B transferred");
	}

	private void manualSignal() throws IOException {
		if (IDefineID.STATE_STARTED == stateCode) {
			actionExecution.getFileReading().suspend();
			startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
					IDefineID.IMG_START_PROCESS));

			if (IDefineID.UDP_UNICAST == typeofProtocol || IDefineID.UDP_BROADCAST == typeofProtocol) {
				actionExecution.getFileReading().getUdpSending().sendMsgUnicast(txtManualCommand.getText());
			} else if(IDefineID.UDP_MULTICAST == typeofProtocol ) {
				actionExecution.getFileReading().getUdpSending().sendMsgMulticast(txtManualCommand.getText());
			} else if(IDefineID.ZEROMQ_PUSH == typeofProtocol) {
				actionExecution.getFileReading().getZeroMqSending().sendingMessage(txtManualCommand.getText());
			}
		} else if (IDefineID.STATE_SUSPENDED == stateCode) {
			startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
					IDefineID.IMG_START_PROCESS));

			if (IDefineID.UDP_UNICAST == typeofProtocol || IDefineID.UDP_BROADCAST == typeofProtocol) {
				actionExecution.getFileReading().getUdpSending().sendMsgUnicast(txtManualCommand.getText());
			} else if(IDefineID.UDP_MULTICAST == typeofProtocol ) {
				actionExecution.getFileReading().getUdpSending().sendMsgMulticast(txtManualCommand.getText());
			} else if(IDefineID.ZEROMQ_PUSH == typeofProtocol) {
				actionExecution.getFileReading().getZeroMqSending().sendingMessage(txtManualCommand.getText());
			}
		} else if (IDefineID.STATE_STOPPED == stateCode || IDefineID.STATE_INIT == stateCode
				|| IDefineID.STATE_NO_EXSIST == stateCode) {
			startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
					IDefineID.IMG_START_PROCESS));
			actionExecution.manualSignal();

			if (IDefineID.UDP_UNICAST == typeofProtocol || IDefineID.UDP_BROADCAST == typeofProtocol) {
				actionExecution.getUdpSending().sendMsgUnicast(txtManualCommand.getText());
			} else if (typeofProtocol == IDefineID.UDP_MULTICAST) {
				actionExecution.getUdpSending().sendMsgMulticast(txtManualCommand.getText());
			} else if(IDefineID.ZEROMQ_PUSH == typeofProtocol) {
				actionExecution.getZMQSending().sendingMessage(txtManualCommand.getText());
			}

			actionExecution.deleteSendMsg();
		}

		String strSiganl = txtManualCommand.getText() + "\r\n";
		txtFileDescription.append(strSiganl);
	}

	private void addToolBarAction() {
		IActionBars menuActionBars = getViewSite().getActionBars();

		menuActionBars.setGlobalActionHandler("FileLoadAction", fileLoadAction);

		getViewSite().getActionBars().getToolBarManager().add(fileLoadAction);
		fileLoadAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID, IDefineID.IMG_FILE_LOAD));
		getViewSite().getActionBars().getToolBarManager().add(startingProcess);
		startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID, IDefineID.IMG_START_PROCESS));
		startingProcess.setEnabled(false);
		getViewSite().getActionBars().getToolBarManager().add(reloadingProcess);
		reloadingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID, IDefineID.IMG_RELOAD_PROCESS));
		reloadingProcess.setEnabled(false);
		getViewSite().getActionBars().getToolBarManager().add(repeatingProcess);
		repeatingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID, IDefineID.IMG_REPEAT_PROCESS));
		getViewSite().getActionBars().getToolBarManager().add(setting);
		setting.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID, IDefineID.IMG_SETTING));
		getViewSite().getActionBars().getToolBarManager().add(showMessage);
		showMessage.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID, IDefineID.IMG_SHOW_MESSAGE));
	}

	public void setStateOfStartingProcess(boolean starting) {
		actionExecution.getFileReading().setFunctionCode(functionCode);
		// periodTime = Long.valueOf(txt_Period.getText());
		actionExecution.getFileReading().setPeriodTime(periodTime);
		if (starting) {
			startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
					IDefineID.IMG_PAUSE_PROCESS));
		} else {
			startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
					IDefineID.IMG_START_PROCESS));
		}
	}

	/*
	 * @Override public void init(IViewSite site, IMemento memento) throws
	 * PartInitException { super.init(site, memento); }
	 * 
	 * @Override public void saveState(IMemento memento) {
	 * super.saveState(memento); }
	 */

	// callback implement
	public void playViewToolbarChange(Boolean stateCode) {
		if (!reloadOK) {
			startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
					IDefineID.IMG_START_PROCESS));
		}
		reloadOK = false;
	}

	public void displayTxtFiledescriptor(final String message) {

		Display display = null;

		if (!txtFileDescription.isDisposed()) {
			display = txtFileDescription.getDisplay();

			display.asyncExec(new Runnable() {
				public void run() {
					if (!txtFileDescription.isDisposed()) {
						if (periodTime > time) {
							txtFileDescription.append(message + "\r\n");
						}
					}
				}
			});
		}
	}

	public void setThreadStateCode(int stateCode) {
		this.stateCode = stateCode;
	}

	public void displaySpeedOfSendingMsg(final double linePerSec, final double bytesPerSec, final double bytesTransferred) {
		this.linesPerSec = linePerSec;
		this.bytesPerSec = bytesPerSec;
		this.bytesTransferred = bytesTransferred;
		
		Display.getDefault().asyncExec(new Runnable() {
				public void run() {
				if (!lblLinesSec01.isDisposed() && !lblBytesSec01.isDisposed() && !lblBytesTrans01.isDisposed()) {

					lblLinesSec01.setText(String.format("%d", (int) linePerSec));
					//lblLinesSec01.setText(String.valueOf(linePerSec));

					if(1024 > bytesPerSec) {
						lblBytesSec01.setText(String.valueOf(bytesPerSec));
						lblBytesSec02.setText("B/sec");
					} else if(1073741824 <= bytesPerSec) {
						lblBytesSec01.setText(String.format("%.2f", (double) bytesPerSec / 1073741824.0));
						lblBytesSec02.setText("GB/sec");
					} else if(1048576 <= bytesPerSec) {
						lblBytesSec01.setText(String.format("%.2f", (double) bytesPerSec / 1048576.0));
						lblBytesSec02.setText("MB/sec");
					} else if(1024 <= bytesPerSec) {
						lblBytesSec01.setText(String.format("%.2f", (double) bytesPerSec / 1024.0));
						lblBytesSec02.setText("KB/sec");
					}

					if (1024 > bytesTransferred) {
						lblBytesTrans01.setText(String.valueOf(bytesTransferred));
						lblBytesTrans02.setText("B transferred");
					} else if(1073741824 <= bytesTransferred) {
						lblBytesTrans01.setText(String.format("%.2f", (double) bytesTransferred / 1073741824.0));
						lblBytesTrans02.setText("GB transferred");
					} else if(1048576 <= bytesTransferred) {
						lblBytesTrans01.setText(String.format("%.2f", (double) bytesTransferred / 1048576.0));
						lblBytesTrans02.setText("MB transferred");
					} else if(1024 <= bytesTransferred ) {
						lblBytesTrans01.setText(String.format("%.2f", (double) bytesTransferred / 1024.0));
						lblBytesTrans02.setText("KB transferred");
					}
				}
		}
		});
	}

	public void setSpeedOfSendingMsg(double linePerSec, double bytesPerSec, double bytesTransferred) {
		this.linesPerSec = linePerSec;
		this.bytesPerSec = bytesPerSec;
		this.bytesTransferred = bytesTransferred;
	}

	public void displayUpdate(final String message, final double linePerSec, final double bytesPerSec,
			final double bytesTransferred) {
		this.linesPerSec = linePerSec;
		this.bytesPerSec = bytesPerSec;
		this.bytesTransferred = bytesTransferred;

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!lblLinesSec01.isDisposed() && !lblBytesSec01.isDisposed() && !lblBytesTrans01.isDisposed()
						&& !txtFileDescription.isDisposed()) {
					lblLinesSec01.setText(String.valueOf(linePerSec));
					if(921600 <= bytesPerSec) {
						lblBytesSec01.setText(String.valueOf(bytesPerSec / 1024));
						lblBytesSec02.setText("KB/sec");
					} else {
					lblBytesSec01.setText(String.valueOf(bytesPerSec));
						lblBytesSec02.setText("B/sec");
					}
					if(921600 <= bytesTransferred ) {
						lblBytesTrans01.setText(String.valueOf(bytesTransferred / 1024));
						lblBytesTrans02.setText("KB/sec");
					} else {
						lblBytesTrans01.setText(String.valueOf(bytesTransferred));
						lblBytesTrans02.setText("B/sec");
					}
					lblBytesTrans01.setText(String.valueOf(bytesTransferred));

					if (periodTime > time) {
						txtFileDescription.append(message + "\r\n");
					}
				}
			}
		});
	}

	// callback implement

	// getter & setter
	public Label getLblLinesSec01() {
		return lblLinesSec01;
	}

	public Label getLblBytesSec01() {
		return lblBytesSec01;
	}

	public Label getLblBytesTrans01() {
		return lblBytesTrans01;
	}

	public double getLinesPerSec() {
		return linesPerSec;
	}

	public double getBytesPerSec() {
		return bytesPerSec;
	}

	public double getBytesTransferred() {
		return bytesTransferred;
	}

	public Button getBtnSend() {
		return btnSend;
	}

	public Action getStartingProcess() {
		return startingProcess;
	}

	public Action getSetting() {
		return setting;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public void setTypeofProtocol(int typeofProtocol) {
		this.typeofProtocol = typeofProtocol;
	}

	public void setSettingOk(boolean settingOk) {
		this.settingOk = settingOk;
	}

	public void setLoadfileOk(boolean loadfileOk) {
		this.loadfileOk = loadfileOk;
	}

	public void setReloadOK(boolean reloadOK) {
		this.reloadOK = reloadOK;
	}

	public void setIpAdress(String ipAdress) {
		this.ipAdress = ipAdress;
	}

	public boolean isLoadfileOk() {
		return loadfileOk;
	}

	public ActionsExecution getActionExecution() {
		return actionExecution;
	}

	public int getStateCode() {
		return stateCode;
	}

	public long getPeriodTime() {
		return periodTime;
	}

	public Text getTxtPeriod() {
		return txtPeriod;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setTxtFileName(Text txtFileName) {
		this.txtFileName = txtFileName;
	}

	public void setTxtPeriod(Text txtPeriod) {
		this.txtPeriod = txtPeriod;
	}

	public void setFunctionCode(int functionCode) {
		this.functionCode = functionCode;
	}

	public void setPeriodTime(long periodTime) {
		this.periodTime = periodTime;
	}

	public void setLoadedFile(File loadedFile) {
		this.loadedFile = loadedFile;
	}

	public Text getTxtFileName() {
		return txtFileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public int getThisIntanceID() {
		return thisIntanceID;
	}

	public int getFunctionCode() {
		return functionCode;
	}

	public String getViewName() {
		return viewName;
	}

	public String getIpAdress() {
		return ipAdress;
	}

	public int getTypeofProtocol() {
		return typeofProtocol;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public boolean isSettingOk() {
		return settingOk;
	}
	
	public NetworkInterface getNic() {
		return nic;
	}
	
	public void setNic(NetworkInterface nic) {
		this.nic = nic;
	}
	
	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	// getter & setter

	public void setFocus() {
	}
}