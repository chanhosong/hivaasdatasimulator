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
 * Bongjin Kwon	2015. 3. 25.		First Draft.
 */
/**
 * 
 */
package com.hhi.vaas.platform.agent.update;

import com.hhi.vaas.platform.middleware.common.util.PropertyService;
import com.hhi.vaas.platform.middleware.common.util.XMLUtil;
import com.hhi.vaas.platform.vdm.handler.VDMLoader;
import com.hhi.vaas.platform.vdm.handler.VesselDataModel;
import com.hhi.vaas.platform.vdm.handler.crypto.CryptoException;
import com.hhi.vaas.platform.vdm.handler.crypto.CryptoUtils;
import com.hhi.vaas.platform.vdm.parser.DataConverter;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.taskdefs.optional.ReplaceRegExp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 * 
 * updating agent
 *  - updating mapping xml
 * </pre>
 * @author BongJin Kwon
 *
 */
public class UpdateHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateHandler.class);

	private String configFilePath = "./conf/context.properties";
	private DataConverter dataConverter;
	private PropertyService props;
	
	public UpdateHandler(DataConverter dataConverter, PropertyService props) {
		this.dataConverter = dataConverter;
		this.props = props;
	}
	
	public UpdateHandler(DataConverter dataConverter) {
		this.dataConverter = dataConverter;
	}
	
	public void setProps(PropertyService props) {
		this.props = props;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}
	
	public UpdateResultModel updateVCD(String vcdContents){
		UpdateResultModel updateResult = new UpdateResultModel();
		
		try {
			vcdContents = XMLUtil.removeUTF8BOM(vcdContents);
			
			ByteArrayInputStream is = new ByteArrayInputStream(vcdContents.getBytes("UTF-8"));
			VesselDataModel vdm = VDMLoader.load(is);
			dataConverter.updateVDM(vdm);
			
			String savedFileName = saveToVCDFile(vcdContents);
			
			updateVCDConfig(savedFileName);
			
			updateResult.setSuccess(true);
			updateResult.setErrorMessage(null);
			LOGGER.info("vcd update success.");
			
		} catch (Exception e) {
			LOGGER.error("vcd update fail.", e);
			
			updateResult.setSuccess(false);
			updateResult.setErrorMessage(getUpdateException(e));
		}
		
		return updateResult;
	}
	
	/**
	 * updating mapping xml
	 * @param mappingXMLStr
	 * @return
	 */
	public UpdateResultModel updateMapping(String mappingXMLStr) {
		
		UpdateResultModel updateResult = new UpdateResultModel();
		
		try {
			dataConverter.updateMapping(mappingXMLStr);
			
			String savedFileName = saveToFile(mappingXMLStr);
			
			updateConfig(savedFileName);
			
			updateResult.setSuccess(true);
			updateResult.setErrorMessage(null);
			LOGGER.info("mapping xml update success.");
			
		} catch(Exception e) {
			LOGGER.error("mapping xml update fail.", e);
			
			updateResult.setSuccess(false);
			updateResult.setErrorMessage(getUpdateException(e));
		}
		
		return updateResult;
		
	}
	
	/**
	 * get update exception message.
	 * @param e
	 * @return exception stack trace message.
	 */
	private String getUpdateException(Exception e) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		PrintWriter pw = new PrintWriter(baos);
		
		e.printStackTrace(pw);
		
		return baos.toString();
	}
	
	private String saveToFile(String mappingXMLStr) throws IOException {
		
		String savedFileName = "Mapping_" + getNowDate() + ".xml";
		
		File saveFile = new File("./conf/" + savedFileName);
		
		FileUtils.write(saveFile, mappingXMLStr);
		
		LOGGER.info("saved conf/{}", savedFileName);
		
		return savedFileName;
	}
	
	private String saveToVCDFile(String vcdStr) throws IOException, CryptoException {
		
		String savedFileName = "VCD_" + getNowDate() + ".vcd";
		
		File saveFile = new File("./conf/" + savedFileName);
		
		byte[] encBytes = CryptoUtils.encrypt(vcdStr, "UTF-8");
		
		FileUtils.writeByteArrayToFile(saveFile, encBytes);
		
		LOGGER.info("saved conf/{}", savedFileName);
		
		return savedFileName;
	}
	
	private void updateConfig(String savedFileName) {
		
		File configFile = new File(configFilePath);
		
		ReplaceRegExp task = new ReplaceRegExp();
		
		task.setFile(configFile);
		task.setFlags("gs");
		task.setByLine(true);
		task.setMatch("mapping.xml.path=/Mapping(.*).xml");
		task.setReplace("mapping.xml.path=/" + savedFileName);
		
		task.execute();
		
		this.props.put("mapping.xml.path", "/"+savedFileName);
		
		LOGGER.info("updated config. {}", configFile.getAbsolutePath());
	}
	
	private void updateVCDConfig(String savedFileName) {
		
		File configFile = new File(configFilePath);
		
		ReplaceRegExp task = new ReplaceRegExp();
		
		task.setFile(configFile);
		task.setFlags("gs");
		task.setByLine(true);
		task.setMatch("vaas.vcd.path=/VCD(.*).vcd");
		task.setReplace("vaas.vcd.path=/" + savedFileName);
		
		task.execute();
		
		this.props.put("vaas.vcd.path", "/"+savedFileName);
		
		LOGGER.info("updated config. {}", configFile.getAbsolutePath());
	}
	
	private String getNowDate() {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(new Date(System.currentTimeMillis()));
	}

}
//end of UpdateHandler.java