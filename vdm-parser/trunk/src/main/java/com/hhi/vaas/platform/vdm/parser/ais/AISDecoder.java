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
 * hsbae			2015-03-24			AIS Decoder
 */

package com.hhi.vaas.platform.vdm.parser.ais;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author : hsbae
 */
public class AISDecoder {

		HashMap<Character, Integer> SixBit_ASCII = new HashMap<>();
		List<String> fields = new ArrayList<String>();
		
		private boolean invalidMsg = false;
		private int messageId;
		private int repeatInd;
		private String userId;
		
		private byte[] bitStream;
		BitInputStream bitIn;
		
		/**
		 * @author : hsbae
		 */
		public void initTable() {
			SixBit_ASCII.put('0',	0x00);
			SixBit_ASCII.put('1',	0x01);
			SixBit_ASCII.put('2',	0x02);
			SixBit_ASCII.put('3',	0x03);
			SixBit_ASCII.put('4',	0x04);
			SixBit_ASCII.put('5',	0x05);
			SixBit_ASCII.put('6',	0x06);
			SixBit_ASCII.put('7',	0x07);
			SixBit_ASCII.put('8',	0x08);
			SixBit_ASCII.put('9',	0x09);
			SixBit_ASCII.put(':',	0x0a);
			SixBit_ASCII.put(';',	0x0b);
			SixBit_ASCII.put('<',	0x0c);
			SixBit_ASCII.put('=',	0x0d);
			SixBit_ASCII.put('>',	0x0e);
			SixBit_ASCII.put('?',	0x0f);
			SixBit_ASCII.put('@',	0x10);
			SixBit_ASCII.put('A',	0x11);
			SixBit_ASCII.put('B',	0x12);
			SixBit_ASCII.put('C',	0x13);
			SixBit_ASCII.put('D',	0x14);
			SixBit_ASCII.put('E',	0x15);
			SixBit_ASCII.put('F',	0x16);
			SixBit_ASCII.put('G',	0x17);
			SixBit_ASCII.put('H',	0x18);
			SixBit_ASCII.put('I',	0x19);
			SixBit_ASCII.put('J',	0x1a);
			SixBit_ASCII.put('K',	0x1b);
			SixBit_ASCII.put('L',	0x1c);
			SixBit_ASCII.put('M',	0x1d);
			SixBit_ASCII.put('N',	0x1e);
			SixBit_ASCII.put('O',	0x1f);
			SixBit_ASCII.put('P',	0x20);
			SixBit_ASCII.put('Q',	0x21);
			SixBit_ASCII.put('R',	0x22);
			SixBit_ASCII.put('S',	0x23);
			SixBit_ASCII.put('T',	0x24);
			SixBit_ASCII.put('U',	0x25);
			SixBit_ASCII.put('V',	0x26);
			SixBit_ASCII.put('W',	0x27);
			SixBit_ASCII.put('`',	0x28);
			SixBit_ASCII.put('a',	0x29);
			SixBit_ASCII.put('b',	0x2a);
			SixBit_ASCII.put('c',	0x2b);
			SixBit_ASCII.put('d',	0x2c);
			SixBit_ASCII.put('e',	0x2d);
			SixBit_ASCII.put('f',	0x2e);
			SixBit_ASCII.put('g',	0x2f);
			SixBit_ASCII.put('h',	0x30);
			SixBit_ASCII.put('i',	0x31);
			SixBit_ASCII.put('j',	0x32);
			SixBit_ASCII.put('k',	0x33);
			SixBit_ASCII.put('l',	0x34);
			SixBit_ASCII.put('m',	0x35);
			SixBit_ASCII.put('n',	0x36);
			SixBit_ASCII.put('o',	0x37);
			SixBit_ASCII.put('p',	0x38);
			SixBit_ASCII.put('q',	0x39);
			SixBit_ASCII.put('r',	0x3a);
			SixBit_ASCII.put('s',	0x3b);
			SixBit_ASCII.put('t',	0x3c);
			SixBit_ASCII.put('u',	0x3d);
			SixBit_ASCII.put('v',	0x3e);
			SixBit_ASCII.put('w',	0x3f);
		}
		
		private final char[] arrayBin2Ascii = {
								'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 
								'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 
								'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', 
								'^', '–', ' ', '!', '”', '#', '$', '%', '&', '`', 
								'(', ')', '*', '+', ',', '–', '.', '/',	'0', '1', 
								'2', '3', '4', '5', '6', '7', '8', '9',	':', ';', 
								'<', '=', '>', '?'};

		public AISDecoder()
		{
			initTable();
		}
		
		/**
		 * @author : hsbae
		 */
		public int getMessageId() {
			if (invalidMsg)
				return -1;
			
			return messageId;
		}
		
		/**
		 * @author : hsbae
		 * @param :
		 */
		public int getRepeatIndicator() {
			if (invalidMsg)
				return -1;
			
			return repeatInd;
		}
		
		/**
		 * @param :
		 */
		public String getUserId() {
			if (invalidMsg)
				return "";
			
			return userId;
		}
		
		/**
		 * @param :
		 */
		public int getFieldCount() {
			if (invalidMsg)
				return -1;
			
			return fields.size();
		}
		
		/**
		 * @param :
		 */
		public String getFiled(int index) {
			return "test";
		}
		
		/**
		 * @author : hsbae
		 * @param :
		 */
		private int decryptAscii2Bin(String strEncoded) {
			char[] data = strEncoded.toCharArray();
			int numberOfBits = 0;
			
			if(data.length == 0)
				return 0;
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedOutputStream bufOut = new BufferedOutputStream(out, 1024);
			BitOutputStream bitOut = new BitOutputStream(bufOut);
			
			for(int i=0; i<data.length; i++) {
				bitOut.write(6, SixBit_ASCII.get(data[i]));
				numberOfBits += 6;
				
				//String str = String.format("%6s", Integer.toBinaryString(SixBit_ASCII.get(data[i]))).replace(' ','0');
				//System.out.println(data[i] + "=" + str);		
			}
			
			bitOut.flush();
			bitStream = out.toByteArray();
			
			return numberOfBits;
		}
		
		/**
		 * @author : hsbae
		 * @param :
		 */
		public List<String> decode(String strEncoded) throws IOException {
			
			int totalNumberOfBits = 0;
			
			if(strEncoded == null) {
				System.out.println("null input");
				invalidMsg = true;
				return null;
			}
			
			fields.clear();
			
			//System.out.println(strEncoded);
			
			totalNumberOfBits = decryptAscii2Bin(strEncoded);
			
			if(bitStream.length == 0)
			{
				System.out.println("Decode fail");
				invalidMsg = true;
				return null;
			}
			
			ByteArrayInputStream in = new ByteArrayInputStream(bitStream);
			//BufferedInputStream bufIn = new BufferedInputStream(in);
			//bitIn = new BitInputStream(in);
			bitIn = new BitInputStream(in, totalNumberOfBits);
			
			int tempId = bitIn.read(6);
			
			if(tempId < 0) {
				invalidMsg = true;
				return null;
			}
			
			messageId = tempId;
			fields.add("" + messageId);
			
			
			
			int tempInd = bitIn.read(2);
			if(tempInd < 0) {
				invalidMsg = true;
				return null;
			}
			
			repeatInd = tempInd;
			fields.add("" + repeatInd);
			
			int tempUserId;
			tempUserId = bitIn.read(30);
			userId = String.format("%09d",  tempUserId);
			fields.add(userId);
			
			boolean result = false;
			
			switch(messageId) {
			case 1:
				result = parseAISMessage1();
				break;
			case 2:
				result = parseAISMessage2();
				break;
			case 3:
				result = parseAISMessage3();
				break;
			case 4:
				result = parseAISMessage4();
				break;
			case 5:
				result = parseAISMessage5();
				break;
			case 6:
				result = parseAISMessage6();
				break;
			case 7:
				result = parseAISMessage7();
				break;
			case 8:
				result = parseAISMessage8();
				break;
			case 9:
				result = parseAISMessage9();
				break;
			case 10:
				result = parseAISMessage10();
				break;
			case 11:
				result = parseAISMessage11();
				break;
			case 12:
				result = parseAISMessage12();
				break;
			case 13:
				result = parseAISMessage13();
				break;
			case 14:
				result = parseAISMessage14();
				break;
			case 15:
				result = parseAISMessage15();
				break;
			case 16:
				result = parseAISMessage16();
				break;
			case 17:
				result = parseAISMessage17();
				break;
			
			case 18:
				result = parseAISMessage18();
				break;
			case 19:
				result = parseAISMessage19();
				break;
			case 20:
				result = parseAISMessage20();
				break;
			
			case 21:
				result = parseAISMessage21();
				break;
			case 22:
				result = parseAISMessage22();
				break;
			case 23:
				result = parseAISMessage23();
				break;
			
			case 24:
				result = parseAISMessage24();
				break;
			case 25:
				result = parseAISMessage25();
				break;
			case 26:
				result = parseAISMessage26();
				break;
			case 27:
				result = parseAISMessage27();
				break;
			default:
				result = false;
				break;
			}
			
			if(result) {
				return fields;
			}
			else {
				fields = null;
				return null;
			}
			
			
			/*
			for(int i = 0; i<fields.size(); i++) {
				System.out.println("[" + i + "]= " + fields.get(i));
			}
			 */
			
			/*
			 while (true) {
				
				int inData = bitIn.read(8);
				if(inData < 0)
					break;
				
				String inStr = String.format("%8s",  Integer.toBinaryString(inData)).replace(' ','0');
				System.out.print(inStr + " ");
			}
			
			}*/
		}
		
		/**
		 * @author : hsbae
		 * @param :
		 */
		private String makeLon(int longitude) 
		{
			double fLongitude = 0;
			
			if( longitude >= 0x8000000 )
		    {
				longitude = 0x10000000 - longitude;
				longitude *= -1;
		    } 
			
			fLongitude = (longitude/10000.0)/60.0;
			
			if(fLongitude <= -181 || fLongitude >= 180 )
				return "";
			
			return String.format("%3.5f", fLongitude);
		}
		
		/**
		 * @author : hsbae
		 * @param :
		 */
		private String makeLat(int latitude) 
		{
			double fLatitude = 0;
			
			if( latitude >= 0x4000000 )
		    {
				latitude = 0x8000000 - latitude;
				latitude *= -1;
		    } 
			
			fLatitude = (latitude/10000.0)/60.0;
			
			if(fLatitude <= -91 || fLatitude >= 91 )
				return "";
			
			return String.format("%3.5f", fLatitude);
		}
		
		/**
		 * @author : hsbae
		 * @param :
		 */
		private String makeSixAscii(int numberOfBits) throws IOException 
		{
			String strAscii = "";
			for(int i=0; i < numberOfBits/6; i++) 
			{
				strAscii += arrayBin2Ascii[bitIn.read(6)];
			}
			
			strAscii = strAscii.trim();
			
			return strAscii;
		}
		
		/**
		 * Message type 1 : Position report
		 */
		private boolean parseAISMessage1() throws IOException {
			
			int navigationalStatus = bitIn.read(4);
			fields.add("" + navigationalStatus);
			
			int rot = bitIn.read(8);
			fields.add("" + rot);
			
			int sog = bitIn.read(10);
			fields.add("" + sog/10.);
			
			int posAccuracy = bitIn.read(1);
			fields.add("" + posAccuracy);
			
			int  longitude = bitIn.read(28);
			String strLon = makeLon(longitude); 
					
			fields.add(strLon);
			
			int latitude = bitIn.read(27);
			String strLat = makeLat(latitude);
			fields.add(strLat);
			
			int cog = bitIn.read(12);
			fields.add("" + cog/10.0);
			
			int trueHeading = bitIn.read(9);
			fields.add("" + trueHeading);
			
			int timeStamp = bitIn.read(6);
			fields.add("" + timeStamp);
			
			int specialNanoeuvreInd = bitIn.read(2);
			fields.add("" + specialNanoeuvreInd);
			
			int spare = bitIn.read(3);
			fields.add("" + spare);
			
			int raimFlag = bitIn.read(1);
			fields.add("" + raimFlag);
			
			int commState = bitIn.read(19);
			fields.add("" + commState);
			
			return true;
		}
		
		/**
		 * Message type 2 : Position report
		 */
		private boolean parseAISMessage2() throws IOException {
			// same as Message Type 1;
			return parseAISMessage1();
		}
		
		/**
		 * Message type 3 : Position report
		 */
		private boolean parseAISMessage3() throws IOException {
			// same as Message Type 1;
			return parseAISMessage1();
		}
		
		/**
		 * Message type 4 : Base station report
		 */
		private boolean parseAISMessage4() throws IOException {
			int utcYear = bitIn.read(14);
			fields.add("" + utcYear);
			
			int utcMonth = bitIn.read(4);
			fields.add("" + utcMonth);
			
			int utcDay = bitIn.read(5);
			fields.add("" + utcDay);
			
			int utcHour = bitIn.read(5);
			fields.add("" + utcHour);
			
			int utcMinute = bitIn.read(6);
			fields.add("" + utcMinute);
			
			int utcSecond = bitIn.read(6);
			fields.add("" + utcSecond);
			
			int positionAccuracy = bitIn.read(1);
			fields.add("" + positionAccuracy);
			
			String strLon = makeLon(bitIn.read(28));
			fields.add(strLon);
			
			String strLat = makeLat(bitIn.read(27));
			fields.add(strLat);
			
			int typePosDevice = bitIn.read(4);
			fields.add("" + typePosDevice);
			
			int txControlBroadcast = bitIn.read(1);
			fields.add("" + txControlBroadcast);
			
			int spare = bitIn.read(9);
			fields.add("" + spare);
			
			int raimFlag = bitIn.read(1);
			fields.add("" + raimFlag);
			
			int commState = bitIn.read(19);
			fields.add("" + commState);
			
			
			return true;
		}
		
		/**
		 * Message type 5 : Ship static and voyage related data
		 */
		private boolean parseAISMessage5() throws IOException {
			
			int aisVersion = bitIn.read(2);
			fields.add("" + aisVersion);
			
			int IMONumber = bitIn.read(30);
			fields.add("" + IMONumber);
			
			String callSign = makeSixAscii(42);
			fields.add("" + callSign);
			
			String name = makeSixAscii(120);
			fields.add("" + name);
			
			int shipType = bitIn.read(8);
			fields.add("" + shipType);
			
			// dimension A:B:C:D
			String dimension = "" + bitIn.read(9) + ':' + bitIn.read(9) + ':' + bitIn.read(6) + ':' + bitIn.read(6);
			fields.add("" + dimension);
			
			int typePosFix = bitIn.read(4);
			fields.add("" + typePosFix);
			
			// ETA month:day:hour:minute
			String ETA = "" + bitIn.read(4) + ':' + bitIn.read(5) + ':' + bitIn.read(5) + ':' + bitIn.read(6);
			fields.add("" + ETA);
			
			double maxDraught = bitIn.read(8) / 10.0;
			fields.add("" + maxDraught);
			
			String destination = makeSixAscii(120);
			fields.add("" + destination);
			
			int DTE = bitIn.read(1);
			fields.add("" + DTE);
			
			int spare = bitIn.read(1);
			fields.add("" + spare);
			
			return true;
		}
		
		/**
		 * Message type 6 : Addressed binary message
		 */
		private boolean parseAISMessage6() throws IOException {
			
			int seqNum = bitIn.read(2);
			fields.add("" + seqNum);
			
			int destId = bitIn.read(30);
			fields.add("" + destId);
			
			int retransFlag = bitIn.read(1);
			fields.add("" + retransFlag);
			
			int spare = bitIn.read(1);
			fields.add("" + spare);
			
			String binData = makeSixAscii(bitIn.available());
			fields.add("" + binData);
			
			return true;
		}
		
		/**
		 * Message type 7 : Binary acknowledge
		 */
		private boolean parseAISMessage7() throws IOException {
			
			int spare = bitIn.read(2);
			fields.add("" + spare);
			
			int destId1 = bitIn.read(30);
			fields.add("" + destId1);
			
			int seqNum1 = bitIn.read(2);
			fields.add("" + seqNum1);
			
			int destId2 = bitIn.read(30);
			fields.add("" + destId2);
			
			int seqNum2 = bitIn.read(2);
			fields.add("" + seqNum2);
			
			int destId3 = bitIn.read(30);
			fields.add("" + destId3);
			
			int seqNum3 = bitIn.read(2);
			fields.add("" + seqNum3);
			
			int destId4 = bitIn.read(30);
			fields.add("" + destId4);
			
			int seqNum4 = bitIn.read(2);
			fields.add("" + seqNum4);
						
			return true;
		}
		
		/**
		 * Message type 8 : Binary Broadcast Message
		 */
		private boolean parseAISMessage8() throws IOException {
			int spare = bitIn.read(2);
			fields.add("" + spare);
			
			String binData = makeSixAscii(bitIn.available());
			fields.add(binData);
			
			return true;
			
		}
		
		/**
		 * Message type 9 : Standard search and rescue aircraft position report
		 */
		private boolean parseAISMessage9() throws IOException {
			int altGNSS = bitIn.read(12);
			fields.add("" + altGNSS);
			
			int SOG = bitIn.read(10);
			fields.add("" + SOG);
			
			int posAccuracy = bitIn.read(1);
			fields.add("" + posAccuracy);
			
			String strLon = makeLon(bitIn.read(28));
			fields.add(strLon);
			
			String strLat = makeLat(bitIn.read(27));
			fields.add(strLat);
			
			int COG = bitIn.read(12);
			fields.add("" + COG/10.0);
			
			int timeStamp = bitIn.read(6);
			fields.add("" + timeStamp);
			
			int altSensor = bitIn.read(1);
			fields.add("" + altSensor);
			
			int spare = bitIn.read(7);
			fields.add("" + spare);
			
			int DTE = bitIn.read(1);
			fields.add("" + DTE);
			
			int spare2 = bitIn.read(3);
			fields.add("" + spare2);
			
			int assignedModeFlag = bitIn.read(1);
			fields.add("" + assignedModeFlag);
			
			int RAIMFlag = bitIn.read(1);
			fields.add("" + RAIMFlag);
			
			int communicationStateSelectorFlag = bitIn.read(1);
			fields.add("" + communicationStateSelectorFlag);
			
			int communicationState = bitIn.read(19);
			fields.add("" + communicationState);
			
			return true;
		}
		
		/**
		 * Message type 10 : Coordinated universal time and date inquiry
		 */
		private boolean parseAISMessage10() throws IOException {
			
			int spare = bitIn.read(2);
			fields.add("" + spare);
			
			int destId = bitIn.read(30);
			fields.add("" + destId);
			
			int spare3 = bitIn.read(2);
			fields.add("" + spare3);
			
			
			return true;
		}
		
		
		/**
		 * Message type 11 : Coordiated universal time/date response
		 */
		private boolean parseAISMessage11() throws IOException {
			// same as Message Type 4;
			return parseAISMessage4();
		}
		
		/**
		 * Message type 12 : Addressed safety related message
		 */
		private boolean parseAISMessage12() throws IOException {
			int sequenceNum = bitIn.read(2);
			fields.add("" + sequenceNum);
			
			int destId = bitIn.read(30);
			fields.add("" + destId);
			
			int retransFlag = bitIn.read(1);
			fields.add("" + retransFlag);
			
			int spare = bitIn.read(1);
			fields.add("" + spare);
			
			int remainBits = bitIn.available();
			String safetyMsg = makeSixAscii(remainBits);
			fields.add("" + safetyMsg);
			
			return true;
		}
		
		/**
		 * Message type 13 : Safety related acknowledge
		 */
		private boolean parseAISMessage13() throws IOException {
			// same as Message Type 7;
			return parseAISMessage7();
		}
		
		/**
		 * Message type 14 : Safety related broadcast message
		 */
		private boolean parseAISMessage14() throws IOException {
			
			int spare = bitIn.read(2);
			fields.add("" + spare);
			
			int remainBits = bitIn.available();
			String safetyMsg = makeSixAscii(remainBits);
			fields.add(safetyMsg);
			
			return true;
		}
		
		/**
		 * Message type 15 : Interrogation
		 */
		private boolean parseAISMessage15() throws IOException {
			
			int spare = bitIn.read(2);
			fields.add("" + spare);
			
			int destId1 = bitIn.read(30);
			fields.add("" + destId1);
			
			int messageId1_1 = bitIn.read(6);
			fields.add("" + messageId1_1);
			
			int slotOffset1_1 = bitIn.read(12);
			fields.add("" + slotOffset1_1);
			
			int spare2 = bitIn.read(2);
			fields.add("" + spare2);
			
			int messageId1_2 = bitIn.read(6);
			fields.add("" + messageId1_2);
			
			int slotOffset1_2 = bitIn.read(12);
			fields.add("" + slotOffset1_2);
			
			int spare3 = bitIn.read(2);
			fields.add("" + spare3);
			
			if(bitIn.available() >= 50)
			{
				int destId2 = bitIn.read(30);
				fields.add("" + destId2);
				
				int messageId2_1 = bitIn.read(6);
				fields.add("" + messageId2_1);
				
				int slotOffset2_1 = bitIn.read(12);
				fields.add("" + slotOffset2_1);

				int spare4 = bitIn.read(2);
				fields.add("" + spare4);
				
			}
			
			
			return true;
		}
		
		
		/**
		 * Message type 16 : Assigned mode command
		 */
		private boolean parseAISMessage16() throws IOException {
			
			int spare = bitIn.read(2);
			fields.add("" + spare);
						
			int destIdA = bitIn.read(30);
			fields.add("" + destIdA);
			
			int offsetA = bitIn.read(12);
			fields.add("" + offsetA);
			
			int incrementA = bitIn.read(10);
			fields.add("" + incrementA);
			
			
			// optional
			int destIdB = bitIn.read(30);
			fields.add("" + destIdB);
			
			int offsetB = bitIn.read(12);
			fields.add("" + offsetB);
			
			int incrementB = bitIn.read(10);
			fields.add("" + incrementB);
			
			int spare2 = bitIn.read(bitIn.available());
			fields.add("" + spare2);
			
			return true;
		}
		
		
		/**
		 * Message type 17 : Global navigation-satellite system broadcast binary message
		 */
		private boolean parseAISMessage17() throws IOException {
			
			int spare = bitIn.read(2);
			fields.add("" + spare);
			
			int longitude = bitIn.read(18);
			fields.add("" + longitude);
			
			int latitude = bitIn.read(17);
			fields.add("" + latitude);
			
			int spare2 = bitIn.read(5);
			fields.add("" + spare2);
			
			int data = bitIn.read(bitIn.available());
			fields.add("" + data);
			
			return true;
		}
		
		
		
		
		/**
		 * Message type 18 : Standard class B equipment position report
		 */
		private boolean parseAISMessage18() throws IOException {
			int spare = bitIn.read(8);
			fields.add("" + spare);
			
			int SOG = bitIn.read(10);
			fields.add("" + SOG);
			
			int posAccuracy = bitIn.read(1);
			fields.add("" + posAccuracy);
			
			String strLon = makeLon(bitIn.read(28));
			fields.add(strLon);
			
			String strLat = makeLat(bitIn.read(27));
			fields.add(strLat);
			
			int COG = bitIn.read(12);
			fields.add("" + COG/10.0);
			
			int trueHeading = bitIn.read(9);
			fields.add("" + trueHeading);
			
			int timeStamp = bitIn.read(6);
			fields.add("" + timeStamp);
			
			int spare2 = bitIn.read(2);
			fields.add("" + spare2);
			
			int classBUnitFlag = bitIn.read(1);
			fields.add("" + classBUnitFlag);
			
			int classBDisplayFlag = bitIn.read(1);
			fields.add("" + classBDisplayFlag);
			
			int classBDSCFlag = bitIn.read(1);
			fields.add("" + classBDSCFlag);
			
			int classBBandFlag = bitIn.read(1);
			fields.add("" + classBBandFlag);
			
			int classBMessage22Flag = bitIn.read(1);
			fields.add("" + classBMessage22Flag);
			
			int classModeFlag = bitIn.read(1);
			fields.add("" + classModeFlag);
			
			int RAIMFlag = bitIn.read(1);
			fields.add("" + RAIMFlag);
			
			int communicationStateSelectorFlag = bitIn.read(1);
			fields.add("" + communicationStateSelectorFlag);
			
			int communicationState = bitIn.read(19);
			fields.add("" + communicationState);
			
			return true;
		}
		
		/**
		 * Message type 19 : Extended class B equipment position report
		 */
		private boolean parseAISMessage19() throws IOException {
			int spare = bitIn.read(8);
			fields.add("" + spare);
			
			int SOG = bitIn.read(10);
			fields.add("" + SOG);
			
			int posAccuracy = bitIn.read(1);
			fields.add("" + posAccuracy);
			
			String strLon = makeLon(bitIn.read(28));
			fields.add(strLon);
			
			String strLat = makeLat(bitIn.read(27));
			fields.add(strLat);
			
			int COG = bitIn.read(12);
			fields.add("" + COG/10.0);
			
			int trueHeading = bitIn.read(9);
			fields.add("" + trueHeading);
			
			int timeStamp = bitIn.read(6);
			fields.add("" + timeStamp);
			
			int spare2 = bitIn.read(4);
			fields.add("" + spare2);
			
			String name = makeSixAscii(120);
			fields.add("" + name);
			
			int shipType = bitIn.read(8);
			fields.add("" + shipType);
			
			// dimension A:B:C:D (30)
			String dimension = "" + bitIn.read(9) + ':' + bitIn.read(9) + ':' + bitIn.read(6) + ':' + bitIn.read(6);
			fields.add("" + dimension);

			int typePosFix = bitIn.read(4);
			fields.add("" + typePosFix);
			
			int RAIMFlag = bitIn.read(1);
			fields.add("" + RAIMFlag);
			
			int DTE = bitIn.read(1);
			fields.add("" + DTE);
			
			int assignedModeFlag = bitIn.read(1);
			fields.add("" + assignedModeFlag);
			
			int spare3 = bitIn.read(4);
			fields.add("" + spare3);
			
			return true;
		}
		
		/**
		 * Message type 20 : Data link management message
		 */
		private boolean parseAISMessage20() throws IOException {
			
			// need to test
			int spare = bitIn.read(2);
			fields.add("" + spare);
			
			int offsetNumber1 = bitIn.read(12);
			fields.add("" + offsetNumber1);
			
			int numOfSlots1 = bitIn.read(4);
			fields.add("" + numOfSlots1);
			
			int timeOut1 = bitIn.read(3);
			fields.add("" + timeOut1);
			
			int increment1 = bitIn.read(11);
			fields.add("" + increment1);
			
			int offsetNumber2 = bitIn.read(12);
			fields.add("" + offsetNumber2);
			
			int numOfSlots2 = bitIn.read(4);
			fields.add("" + numOfSlots2);
			
			int timeOut2 = bitIn.read(3);
			fields.add("" + timeOut2);
			
			int increment2 = bitIn.read(11);
			fields.add("" + increment2);
			
			int offsetNumber3 = bitIn.read(12);
			fields.add("" + offsetNumber3);
			
			int numOfSlots3 = bitIn.read(4);
			fields.add("" + numOfSlots3);
			
			int timeOut3 = bitIn.read(3);
			fields.add("" + timeOut3);
			
			int increment3 = bitIn.read(11);
			fields.add("" + increment3);
			
			int offsetNumber4 = bitIn.read(12);
			fields.add("" + offsetNumber4);
			
			int numOfSlots4 = bitIn.read(4);
			fields.add("" + numOfSlots4);
			
			int timeOut4 = bitIn.read(3);
			fields.add("" + timeOut4);
			
			int increment4 = bitIn.read(11);
			fields.add("" + increment4);
			
			int spare2 = bitIn.read(bitIn.available());
			fields.add("" + spare2);
			
			return true;
		}
		
		/**
		 * Message type 21 : Aids-to-navigation report
		 */
		private boolean parseAISMessage21() throws IOException {
			
			// not complete. need to test and fix
			
			int typeAtoN = bitIn.read(5);
			fields.add("" + typeAtoN);
			
			String nameAtoN = makeSixAscii(120);
			fields.add("" + nameAtoN);
			
			int posAccuracy = bitIn.read(1);
			fields.add("" + posAccuracy);
			
			String strLon = makeLon(bitIn.read(28));
			fields.add(strLon);
			
			String strLat = makeLat(bitIn.read(27));
			fields.add(strLat);
			
			// dimension A:B:C:D (30)
			String dimension = "" + bitIn.read(9) + ':' + bitIn.read(9) + ':' + bitIn.read(6) + ':' + bitIn.read(6);
			fields.add("" + dimension);
			
			int typePosFix = bitIn.read(4);
			fields.add("" + typePosFix);
			
			int timeStamp = bitIn.read(6);
			fields.add("" + timeStamp);
			
			int offPosIndicator = bitIn.read(1);
			fields.add("" + offPosIndicator);
			
			int statusAtoN = bitIn.read(8);
			fields.add("" + statusAtoN);
			
			int RAIMFlag = bitIn.read(1);
			fields.add("" + RAIMFlag);
			
			int virtualAtoNFlag = bitIn.read(1);
			fields.add("" + virtualAtoNFlag);
			
			int assignedModeFlag = bitIn.read(1);
			fields.add("" + assignedModeFlag);
			
			int spare = bitIn.read(1);
			fields.add("" + spare);
			
			// name1 : name 2 : name3 ... // need to test
			String nameOfAtoNExtension = makeSixAscii(bitIn.available());
			fields.add("" + nameOfAtoNExtension);
			
			return true;
		}
		
		/**
		 * Message type 22 : Channel management
		 */
		private boolean parseAISMessage22() throws IOException {
			// need to test
			int spare = bitIn.read(2);
			fields.add("" + spare);
			
			int channelA = bitIn.read(11);
			fields.add("" + channelA);
			
			int channelB = bitIn.read(11);
			fields.add("" + channelB);
			
			int power = bitIn.read(1);
			fields.add("" + power);
			
			int longitude1 = bitIn.read(18);
			fields.add("" + longitude1);
			
			int latitude1 = bitIn.read(17);
			fields.add("" + latitude1);
			
			int longitude2 = bitIn.read(18);
			fields.add("" + longitude2);
			
			int latitude2 = bitIn.read(17);
			fields.add("" + latitude2);
			
			int messageIndicator = bitIn.read(1);
			fields.add("" + messageIndicator);
			
			int channelA_bandwidth = bitIn.read(1);
			fields.add("" + channelA_bandwidth);
			
			int channelB_bandwidth = bitIn.read(1);
			fields.add("" + channelB_bandwidth);
			
			int transitionalZoneSize = bitIn.read(3);
			fields.add("" + transitionalZoneSize);
			
			int spare2 = bitIn.read(23);
			fields.add("" + spare2);
			
			return true;
		}
		
		/**
		 * Message type 23 : Group assignment command
		 */
		private boolean parseAISMessage23() throws IOException {
			
			// need to test
			int spare = bitIn.read(2);
			fields.add("" + spare);
			
			int longitude1 = bitIn.read(18);
			fields.add("" + longitude1);
			
			int latitude1 = bitIn.read(17);
			fields.add("" + latitude1);
			
			int longitude2 = bitIn.read(18);
			fields.add("" + longitude2);
			
			int latitude2 = bitIn.read(17);
			fields.add("" + latitude2);
			
			int stationType = bitIn.read(4);
			fields.add("" + stationType);
			
			int shipType = bitIn.read(8);
			fields.add("" + shipType);
			
			int spare2 = bitIn.read(22);
			fields.add("" + spare2);
			
			int txrxMode = bitIn.read(2);
			fields.add("" + txrxMode);
			
			int reportingInterval = bitIn.read(4);
			fields.add("" + reportingInterval);
			
			int quietTime = bitIn.read(4);
			fields.add("" + quietTime);
			
			int spare3 = bitIn.read(6);
			fields.add("" + spare3);
			
			return true;
		}
		
		/**
		 * Message type 24 : Static data report
		 */
		private boolean parseAISMessage24() throws IOException {
			
			// not complete. need to test and fix
			
			int partNumber = bitIn.read(2);
			fields.add("" + partNumber);
			
			
			if(partNumber == 0) {			// Part A
				String name = makeSixAscii(120);
				fields.add("" + name);
			}
			else if(partNumber == 1) { 		// Part B
				
				int shipType = bitIn.read(8);
				fields.add("" + shipType);
				
				String vendorID = makeSixAscii(42);
				fields.add(vendorID);
				
				String callSign = makeSixAscii(42);
				fields.add(callSign);
				
				// dimension A:B:C:D
				String dimension = "" + bitIn.read(9) + ':' + bitIn.read(9) + ':' + bitIn.read(6) + ':' + bitIn.read(6);
				fields.add("" + dimension);

				int typePosFix = bitIn.read(4);
				fields.add("" + typePosFix);
				
				int spare = bitIn.read(2);
				fields.add("" + spare);
			}
			else
			{
				
			}
			
			return true;
		}
		
		/**
		 * Message type 25 : Single slot binary message
		 */
		private boolean parseAISMessage25() throws IOException {
			
			// need to test
			int destinationIndicator = bitIn.read(1);
			fields.add("" + destinationIndicator);
			
			
			int binaryDataFlag = bitIn.read(1);
			fields.add("" + binaryDataFlag);
			
			int destinationID = bitIn.read(30);
			fields.add("" + destinationID);
			
			int binaryData = bitIn.read(bitIn.available());
			fields.add("" + binaryData);
			
			return true;
		}
		
		/**
		 * Message type 26 : Multiple slot binary messsage with communications state
		 */
		private boolean parseAISMessage26() throws IOException {
			
			// not support
			return false;
		}
		
		/**
		 * Message type 27 : Data link management message
		 */
		private boolean parseAISMessage27() throws IOException {
			
			// need to test
			
			int posAccuracy = bitIn.read(1);
			fields.add("" + posAccuracy);
			
			int raimFlag = bitIn.read(1);
			fields.add("" + raimFlag);
			
			int navigationalStatus = bitIn.read(4);
			fields.add("" + navigationalStatus);
			
			int longitude = bitIn.read(18);
			fields.add("" + longitude);
			
			int latitude = bitIn.read(17);
			fields.add("" + latitude);
			
			int sog = bitIn.read(6);
			fields.add("" + sog);
			
			int cog = bitIn.read(9);
			fields.add("" + cog);
			
			int posLatency = bitIn.read(1);
			fields.add("" + posLatency);
			
			int spare = bitIn.read(1);
			fields.add("" + spare);
			
			return true;
		}
		
}
