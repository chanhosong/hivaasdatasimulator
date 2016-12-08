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
package com.hhi.vaas.platform.mappingtool;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public final class EquipmentExcelReadTest {
	public static final String EquipmentDataExceltest = "/VDR_JRC.xlsx";
	
	private static final Logger LOGGER = Logger.getLogger(EquipmentExcelReadTest.class);
	
	private EquipmentExcelReadTest() {
		
	}
	
	
	public static void main(String[] args) {
		try {
			//FileInputStream file = new FileInputStream(new File("equipment_VDR.xlsx"));
			
			InputStream is = EQDXMLHandlerTest.class.getResourceAsStream(EquipmentDataExceltest);
			
			// Create workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(is);
			is.close();
			
			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);
			
			LOGGER.debug("Sheet name = " + sheet.getSheetName());
			
			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			
			Row rowHeaderInfo = rowIterator.next();
			LOGGER.debug(rowHeaderInfo.getCell(0).getStringCellValue() 
					+ "\t" + rowHeaderInfo.getCell(1).getStringCellValue() 
					+ "\t" + rowHeaderInfo.getCell(2).getStringCellValue() 
					+ "\t" + rowHeaderInfo.getCell(3).getStringCellValue());
			
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					
					//Check the cell type and format accordingly
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						int fieldNum = (int) cell.getNumericCellValue();
						System.out.print(fieldNum + "\t");
						//String filed = String.format("field:%02d", fieldNum);
						//System.out.print(filed + "\t");
						break;
					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + "\t");
						break;
					case Cell.CELL_TYPE_BLANK:
						System.out.print("\t");
						break;
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
//end of EquipmentExcelRead.java