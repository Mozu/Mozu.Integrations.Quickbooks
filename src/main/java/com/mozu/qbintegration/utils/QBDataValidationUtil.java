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
	
	/**
	 * Populate multiple address lines based on mozu address line 1 length.
	 * 
	 * @param qbXMLBillAddressType
	 * @param mozuAddrLine1
	 */
	public static void populateQBBillToAddrFromMozuAddr(BillAddress qbXMLBillAddressType,
			String mozuAddrLine1) {
		if(!StringUtils.isEmpty(mozuAddrLine1)) {
			//usual scenario
			qbXMLBillAddressType.setAddr1(getAddressSlice(
					mozuAddrLine1, 0, QB_ADDR_FIELD_SIZE));
			qbXMLBillAddressType.setAddr2(getAddressSlice(
					mozuAddrLine1, QB_ADDR_FIELD_SIZE, QB_ADDR_FIELD_SIZE * 2));
			qbXMLBillAddressType.setAddr3(getAddressSlice(
					mozuAddrLine1, QB_ADDR_FIELD_SIZE * 2, QB_ADDR_FIELD_SIZE * 3));
			qbXMLBillAddressType.setAddr4(getAddressSlice(
					mozuAddrLine1, QB_ADDR_FIELD_SIZE * 3, QB_ADDR_FIELD_SIZE * 4));
			qbXMLBillAddressType.setAddr5(getAddressSlice(
					mozuAddrLine1, QB_ADDR_FIELD_SIZE * 4, QB_ADDR_FIELD_SIZE * 5));
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
			//usual scenario
			qbXMLShipAddressType.setAddr1(getAddressSlice(
					mozuAddrLine1, 0, QB_ADDR_FIELD_SIZE));
			qbXMLShipAddressType.setAddr2(getAddressSlice(
					mozuAddrLine1, QB_ADDR_FIELD_SIZE, QB_ADDR_FIELD_SIZE * 2));
			qbXMLShipAddressType.setAddr3(getAddressSlice(
					mozuAddrLine1, QB_ADDR_FIELD_SIZE * 2, QB_ADDR_FIELD_SIZE * 3));
			qbXMLShipAddressType.setAddr4(getAddressSlice(
					mozuAddrLine1, QB_ADDR_FIELD_SIZE * 3, QB_ADDR_FIELD_SIZE * 4));
			qbXMLShipAddressType.setAddr5(getAddressSlice(
					mozuAddrLine1, QB_ADDR_FIELD_SIZE * 4, QB_ADDR_FIELD_SIZE * 5));
		}
	}

	private static String getAddressSlice(String mozuAddrLine1,
			Integer startIndex, Integer endIndex) {
		if (mozuAddrLine1.length() <= startIndex) {
			return null;
		} else {
			Integer mozuAddrLength = mozuAddrLine1.length();
			return mozuAddrLine1.substring(startIndex, 
					mozuAddrLength < endIndex ? mozuAddrLength : endIndex);
		}
	}

}
