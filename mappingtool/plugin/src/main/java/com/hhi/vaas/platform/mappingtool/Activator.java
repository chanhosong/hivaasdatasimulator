package com.hhi.vaas.platform.mappingtool;



import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;



/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.hhi.vaas.platform.mappingtool"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);
	private static final Logger LOGGER = Logger.getLogger(Activator.class);
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public static void log(IStatus status) {
		//LOGGER.debug(status.toString());
	}
	
	public static void log(Exception e) {
		//log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		initLog4j(context);
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	private void initLog4j(BundleContext context) throws IOException {

		// setup logger
		final URL confURL = getBundle().getEntry("log4j.properties");
		
		PropertyConfigurator.configure(FileLocator.toFileURL(confURL).getFile());
		
		LOGGER.debug("Log4j initialized with " + FileLocator.toFileURL(confURL).getFile());
		
		LOGGER.info("Log file location : " + System.getProperty("user.dir") + "/log/");
	}
}
