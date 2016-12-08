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

import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.Bundle;

import com.hhi.vaas.platform.datasimulator.IDefineID;
import com.hhi.vaas.platform.datasimulator.IStatisticsCallback;
import com.hhi.vaas.platform.datasimulator.common.LoggerSingletone;
import com.hhi.vaas.platform.datasimulator.common.ViewMapSingleton;
import com.hhi.vaas.platform.datasimulator.provider.StatisticsData;
import com.hhi.vaas.platform.datasimulator.provider.UpdateStatistics;

public class StatisticsView extends ViewPart implements IStatisticsCallback {
	private TableViewer statisticsTableViewer;
	private UpdateStatistics updateStatistis;
	private IStatisticsCallback statisticsCallback;
	private int stateCode;
	private ViewMapSingleton map;
	private Image processing;
	private LoggerSingletone LOGGER;

	private Action startingProcess = new Action("Start Process") {
		public void run() {
			if (stateCode == IDefineID.STATE_INIT) {
				updateStatistis.start();
				startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
						IDefineID.IMG_PAUSE_PROCESS));
			} else if (updateStatistis.getStateCode() == IDefineID.STATE_STARTED) {
				updateStatistis.suspend();
				startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
						IDefineID.IMG_START_PROCESS));
			} else if (updateStatistis.getStateCode() == IDefineID.STATE_SUSPENDED) {
				updateStatistis.resume();
				startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
						IDefineID.IMG_PAUSE_PROCESS));
			} else if (updateStatistis.getStateCode() == IDefineID.STATE_STOPPED) {
				updateStatistis.start();
				startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
						IDefineID.IMG_PAUSE_PROCESS));
			}
			stateCode = updateStatistis.getStateCode();
		}
	};

	public StatisticsView() {
		LOGGER = LoggerSingletone.getInstance();
		LOGGER.setLogger(LOGGER.getLogger().getLogger(StatisticsView.class.getName()));
		LOGGER.getLogger().info("Create a statistics view");
		
		statisticsCallback = this;
		updateStatistis = new UpdateStatistics(statisticsCallback);
		stateCode = IDefineID.STATE_INIT;
		map = ViewMapSingleton.getInstance();
		Bundle bundle = Platform.getBundle(IDefineID.PLUGIN_ID);
		@SuppressWarnings("restriction")
		URL fullPathString = BundleUtility.find(bundle, IDefineID.IMG_STATISTICS_PROCESSING);
		processing = ImageDescriptor.createFromURL(fullPathString).createImage();
	}

	public void createPartControl(Composite parent) {
		createStatisticsView(parent);
		addToolBarAction();
		createTableViewer(parent);
		addListener(parent);
	}

	private void createStatisticsView(Composite parent) {
	}

	private void addToolBarAction() {
		getViewSite().getActionBars().getToolBarManager().add(startingProcess);
		startingProcess.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(IDefineID.PLUGIN_ID,
				IDefineID.IMG_START_PROCESS));
	}

	private void createTableViewer(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		statisticsTableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		createColumns(parent, statisticsTableViewer);
		Table table = statisticsTableViewer.getTable();
		GridData gdTable = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTable.widthHint = 54;
		table.setLayoutData(gdTable);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		statisticsTableViewer.setContentProvider(new ArrayContentProvider());
		// statisticsTableViewer.setInput(ModelProvider.INSTANCE.getStatisticsData());

		statisticsTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
			}
		});
		getSite().setSelectionProvider(statisticsTableViewer);

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		statisticsTableViewer.getControl().setLayoutData(gridData);
	}

	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "", "View Title", "Line Speed", "Send Speed", "Transferred Size", "Period" };
		int[] bounds = { 25, 250, 100, 100, 150, 50 };
		TableViewerColumn col;

		col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return null;
			}

			public Image getImage(Object element) {
				if (((StatisticsData) element).isProcessing()) {
					return processing;
				} else {
					return null;
				}
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				StatisticsData p = (StatisticsData) element;
				return p.getViewTitle();
			}
		});

		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				StatisticsData p = (StatisticsData) element;
				return String.format("%d", (int) p.getLinesPerSec());
				//return String.valueOf(p.getLinesPerSec());
			}
		});

		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				StatisticsData p = (StatisticsData) element;
				String bytesPerSec = null;
				
				if(1024 > p.getBytesPerSec()) {
					bytesPerSec = String.format("%.2f B/sec", (double) p.getBytesPerSec());
				} else if(1073741824 <= p.getBytesPerSec()) {
					bytesPerSec = String.format("%.2f GB/sec", (double) p.getBytesPerSec() / 1073741824.0);
				} else if(1048576 <= p.getBytesPerSec()) {
					bytesPerSec = String.format("%.2f MB/sec", (double) p.getBytesPerSec() / 1048576.0);
				} else if(1024 <= p.getBytesPerSec()) {
					bytesPerSec = String.format("%.2f KB/sec", (double) p.getBytesPerSec() / 1024.0);
				}
				
				return bytesPerSec;
			}
		});

		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				StatisticsData p = (StatisticsData) element;
				String bytesTransferred = null;
				
				if(1024 > p.getBytesTransferred()) {
					bytesTransferred = String.format("%.2f B", (double) p.getBytesTransferred());
				} else if(1073741824 <= p.getBytesTransferred()) {
					bytesTransferred = String.format("%.2f GB", (double) p.getBytesTransferred() / 1073741824.0);
				} else if(1048576 <= p.getBytesTransferred()) {
					bytesTransferred = String.format("%.2f MB", (double) p.getBytesTransferred() / 1048576.0);
				} else if(1024 <= p.getBytesTransferred()) {
					bytesTransferred = String.format("%.2f KB", (double) p.getBytesTransferred() / 1024.0);
				}
				
				return bytesTransferred;
				//return String.valueOf(p.getBytesTransferred());
			}
		});

		col = createTableViewerColumn(titles[5], bounds[5], 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				StatisticsData p = (StatisticsData) element;
				return String.valueOf(p.getPeriodTime());
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		TableViewerColumn viewerColumn = new TableViewerColumn(statisticsTableViewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();

		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);

		return viewerColumn;
	}

	public void addListener(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				/*if (stateCode == IDefineID.STATE_STARTED || stateCode == IDefineID.STATE_SUSPENDED) {
					// updateStatistis.stop();
				}*/
				statisticsCallback = null;
				LOGGER.setLogger(LOGGER.getLogger().getLogger(StatisticsView.class.getName()));
				LOGGER.getLogger().info("Good bye Statistics.");
			}
		});
	}

	public void setFocus() {
	}

	public TableViewer getStatisticsTableViewer() {
		return statisticsTableViewer;
	}

	// callback implement
	public void displaySpeedOfTransfer(final List<StatisticsData> statisticsData) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (!map.getMap().isEmpty()) {
					if (statisticsData != null) {
						statisticsTableViewer.setInput(statisticsData);
					}
				}
			}
		});
	}
	// callback implement
}
//end of StatisticsView.java