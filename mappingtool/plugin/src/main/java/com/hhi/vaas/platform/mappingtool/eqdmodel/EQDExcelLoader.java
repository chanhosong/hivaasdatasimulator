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
 * hsbae			2015. 4. 15.		First Draft.
 */
package com.hhi.vaas.platform.mappingtool.eqdmodel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EQDExcelLoader {

	public static final Logger LOGGER = Logger.getLogger(EQDExcelLoader.class);
	
	public EQDExcelLoader() {
		LOGGER.info("[EQD] >> EQDExcelLoader");
	}
	
	
	//public EquipmentDataModel load(InputStream is, String deviceName, String protocolName)  {
	public EquipmentDataModel load(InputStream is)  {
		
		XSSFWorkbook workbook = null;
		
		XSSFSheet sheet;
		
		String deviceName = "NewDevice";
		String protocolName = "";
		
		try {
			// Create workbook instance holding reference to .xlsx file
			workbook = new XSSFWorkbook(is);
			
			// Get first/desired sheet from the workbook
			//sheet = workbook.getSheetAt(0);
			int sheetIdx = workbook.getSheetIndex("NMEA");
			
			if (sheetIdx == -1) {
				sheetIdx = workbook.getSheetIndex("KV");
			}
			
			if (sheetIdx >= 0) {
				sheet = workbook.getSheetAt(sheetIdx);
			
				protocolName = sheet.getSheetName();
			
				LOGGER.debug("Protocol name = " + protocolName);
				
			} else {
				workbook.close();
				throw new EQDExcelException("Invalid EXCEL file");
			}
			
		} catch (Exception e) {
			
			throw new EQDExcelException("Invalid EXCEL file");
			
		} finally {
			if (workbook != null) {
	
				try {
					workbook.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		
		EquipmentDataModel eqdRoot = new EquipmentDataModel(null, deviceName);

		EquipmentDataModel eqdProtocol = new EquipmentDataModel(eqdRoot, protocolName);
		eqdRoot.child.add(eqdProtocol);
		
		
		try {
			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			
			Row rowHeaderInfo = rowIterator.next();
			checkHeaderRow(rowHeaderInfo);
			
			EquipmentDataModel eqdGroup = null;
			
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				EquipmentDataModel eqdRecord = null;
				
				String strGroupName = getCellStringValue(row.getCell(0));
				if (!"".equals(strGroupName)) {
					eqdGroup = parseGroup(eqdProtocol, row);
				} else if (eqdGroup != null) {
					if (eqdProtocol.getName().equals("NMEA")) {
						eqdRecord = parseNMEARecord(eqdGroup, row);
					} else {
						eqdRecord = parseOtherRecord(eqdGroup, row);
					}
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return eqdRoot;
	}
	
	private boolean checkHeaderRow(Row headerRow) {
		
		String titleGroup = "group";
		String titleRecord = "record";
		String titleDescription = "description";
		String titleType = "type";
		
		try {	
			String col0 = headerRow.getCell(0).getStringCellValue().toLowerCase();
			String col1 = headerRow.getCell(1).getStringCellValue().toLowerCase();
			String col2 = headerRow.getCell(2).getStringCellValue().toLowerCase();
			String col3 = headerRow.getCell(3).getStringCellValue().toLowerCase();
			
			if (!titleGroup.equals(col0) || !titleRecord.equals(col1) 
					|| !titleDescription.equals(col2) || !titleType.equals(col3)) {
				throw new EQDExcelException("Invalid excel format");
			}
			
		} catch (Exception e) {
			throw new EQDExcelException("Invalid excel format");
		}
		
		return true;
	}
	
	private EquipmentDataModel parseGroup(EquipmentDataModel parent, Row row) {
		// For each row, iterate through all the columns
		EquipmentDataModel eqdGroup;
		
		Iterator<Cell> cellIterator = row.cellIterator();
		
		
		String [] columns = {"", "", "", ""};
		
		for (int i = 0; i < 4; i++) {
			if (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				columns[cell.getColumnIndex()] = getCellStringValue(cell);
			} else {
				break;
			}
		}
		
		if ("".equals(columns[0])) {
			//throw new EQDExcelException("invalid row : " + columns);
			return null;
		}
		
		eqdGroup = new EquipmentDataModel(parent, columns[0], columns[2], columns[3]);
		parent.child.add(eqdGroup);
		return eqdGroup;
	}
	
	private EquipmentDataModel parseNMEARecord(EquipmentDataModel parent, Row row) {
		// For each row, iterate through all the columns
		EquipmentDataModel eqdRecord;
		
		Iterator<Cell> cellIterator = row.cellIterator();
		
		String [] columns = {"", "", "", ""};
		
		for (int i = 0; i < 4; i++) {
			if (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				columns[i] = getCellStringValue(cell);
			}	
		}
		
		if (!"".equals(columns[0])) {
			throw new EQDExcelException("this is not recrod row : " 
					+ columns[0] + "," + columns[1] + "," + columns[2] + "," + columns[3]);
		}
		
		try {
			int fieldNum = Integer.parseInt(columns[1]);
			columns[1] = String.format("field:%02d",  fieldNum);
		} catch (Exception e)	{
			throw new EQDExcelException("record fields are not number : " 
					+ columns[0] + "," + columns[1] + "," + columns[2] + "," + columns[3]);
		}
		
		eqdRecord = new EquipmentDataModel(parent, columns[1], columns[2], columns[3]);
		parent.child.add(eqdRecord);
		return eqdRecord;
	}
	
	private EquipmentDataModel parseOtherRecord(EquipmentDataModel parent, Row row) {
		// For each row, iterate through all the columns
		EquipmentDataModel eqdRecord;
		
		Iterator<Cell> cellIterator = row.cellIterator();
		
		String [] columns = {"", "", "", ""};
		
		for (int i = 0; i < 4; i++) {
			if (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				columns[cell.getColumnIndex()] = getCellStringValue(cell);
			}	
		}
		
		if (!"".equals(columns[0])) {
			throw new EQDExcelException("this is not recrod row : " 
					+ columns[0] + "," + columns[1] + "," + columns[2] + "," + columns[3]);
		}
		
		eqdRecord = new EquipmentDataModel(parent, columns[1], columns[2], columns[3]);
		parent.child.add(eqdRecord);
			return eqdRecord;
	}
	
	
	private String getCellStringValue(Cell cell) {
		String retValue = "";
		
		if (cell == null) {
			return "";
		}
		
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			int value = (int) cell.getNumericCellValue();
			retValue = "" + value;
			break;
		case Cell.CELL_TYPE_STRING:
			retValue = cell.getStringCellValue();
			break;
		default:
			retValue = "";
			break;
		}
	
		
		return retValue;
	}
	
	
}
//end of EQDExcelLoader.java