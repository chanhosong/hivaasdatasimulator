/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hhi.vaas.platform.vdm.parser.config.DefaultConfigAlarmParser;
import com.hhi.vaas.platform.vdm.parser.kv.KVParser;
import com.hhi.vaas.platform.vdm.parser.nmea.DefaultNMEAParserProxy;
import com.hhi.vaas.platform.vdm.parser.nmea.NMEAAISParserProxy;

/**
 * @author BongJin Kwon
 *
 */
public class DataParserFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataParserFactory.class);
	
	private static DataParserFactory dataParserFactory;
	
	private List<VDMParser> parserList = new ArrayList<VDMParser>();
	
	
	/**
	 * Selects a parser to process the data and return.
	 * @param rawData
	 * @return
	 */
	public static VDMParser getParser(String rawData){
		
		if(dataParserFactory == null){
			initDataParserFactory();
		}
		
		for (VDMParser vdmParser : dataParserFactory.parserList) {
			
			if(vdmParser.supports(rawData)){
				
				LOGGER.debug("Selected Parser : {}", vdmParser.getClass().getName());
				
				return vdmParser;
			}
		}
		
		throw new RuntimeException("parser not found.");
		
	}
	
	/**
	 * All parsers are registered here.
	 */
	private static void initDataParserFactory(){
		dataParserFactory = new DataParserFactory();
		//dataParserFactory.parserList.add(new NMEAAISParserProxy());
		dataParserFactory.parserList.add(new DefaultNMEAParserProxy());
		dataParserFactory.parserList.add(new KVParser());
		dataParserFactory.parserList.add(new DefaultConfigAlarmParser());
	}
	

}
