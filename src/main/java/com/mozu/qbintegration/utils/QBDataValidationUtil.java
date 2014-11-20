/**
 * 
 */
package com.mozu.qbintegration.utils;

import org.apache.commons.lang.StringUtils;

import com.mozu.qbintegration.model.qbmodel.allgen.BillAddress;
import com.mozu.qbintegration.model.qbmodel.allgen.ShipAddress;

/**
 * Util class for any current or future data related trimming/slicing
 * operations between mozu and quickbooks. 
 * @author Akshay
 *
 */
public final class QBDataValidationUtil {
	
	/**
	 * QB has upper limit of 41 to the addresses - in QBXML13
	 */
	public static final Integer QB_ADDR_FIELD_SIZE = 41;
	
	public static final String SPACE_SEPARATOR = " ";

	private static final String DELIMITER = "- -";
	
	/**
	 * Populate multiple address lines based on mozu address line 1 length.
	 * 
	 * @param qbXMLBillAddressType
	 * @param mozuAddrLine1
	 */
	public static void populateQBBillToAddrFromMozuAddr(BillAddress qbXMLBillAddressType,
			String mozuAddrLine1) {
		if(!StringUtils.isEmpty(mozuAddrLine1)) {
			
			String readyToUseStr = getAddressSplitOnSpace(mozuAddrLine1, QB_ADDR_FIELD_SIZE);
			String[] lineSplit = readyToUseStr.split(DELIMITER);
			
			//usual scenario
			if(lineSplit.length > 0) {
				qbXMLBillAddressType.setAddr1(lineSplit[0]);
			}
			
			if(lineSplit.length > 1) {
				qbXMLBillAddressType.setAddr2(lineSplit[1]);
			}
			
			if(lineSplit.length > 2) {
				qbXMLBillAddressType.setAddr3(lineSplit[2]);
			}
			
			if(lineSplit.length > 3) {
				qbXMLBillAddressType.setAddr4(lineSplit[3]);
			}
			
			if(lineSplit.length > 4) {
				qbXMLBillAddressType.setAddr5(lineSplit[4]);
			}
			
		}
	}
	
	/**
	 * Populate multiple Ship to address lines based on mozu address line 1 length.
	 * 
	 * @param qbXMLBillAddressType
	 * @param mozuAddrLine1
	 */
	public static void populateQBShipToAddrFromMozuAddr(ShipAddress qbXMLShipAddressType,
			String mozuAddrLine1) {
		if(!StringUtils.isEmpty(mozuAddrLine1)) {
			
			String readyToUseStr = getAddressSplitOnSpace(mozuAddrLine1, QB_ADDR_FIELD_SIZE);
			String[] lineSplit = readyToUseStr.split(DELIMITER);
			
			//usual scenario
			if(lineSplit.length > 0) { //Akshay - so would like to use reflection and make this in a loop
				qbXMLShipAddressType.setAddr1(lineSplit[0]);
			}
			
			if(lineSplit.length > 1) {
				qbXMLShipAddressType.setAddr2(lineSplit[1]);
			}
			
			if(lineSplit.length > 2) {
				qbXMLShipAddressType.setAddr3(lineSplit[2]);
			}
			
			if(lineSplit.length > 3) {
				qbXMLShipAddressType.setAddr4(lineSplit[3]);
			}
			
			if(lineSplit.length > 4) {
				qbXMLShipAddressType.setAddr5(lineSplit[4]);
			}
			
		}
	}

	public static String getAddressSplitOnSpace(String input, int maxLineLength) {
	    String[] tokens = input.split(SPACE_SEPARATOR);
	    StringBuilder output = new StringBuilder(input.length());
	    int lineLen = 0;
	    for (int i = 0; i < tokens.length; i++) {
	        String word = tokens[i];

	        if (lineLen + (SPACE_SEPARATOR + word).length() > maxLineLength) {
	            if (i > 0) {
	                output.append(DELIMITER); //don't think anyone uses double cap in address.
	            }
	            lineLen = 0;
	        }
	        if (i < tokens.length - 1 && (lineLen + (word + SPACE_SEPARATOR).length() + tokens[i + 1].length() <=
	                maxLineLength)) {
	            word += SPACE_SEPARATOR;
	        }
	        output.append(word);
	        lineLen += word.length();
	    }
	    return output.toString();
	}

}
