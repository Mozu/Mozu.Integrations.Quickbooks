package com.mozu.qbintegration.utils;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;

public class XMLHelper {

	private static final String QBXML_PREFIX = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<?qbxml version=\"13.0\"?>";
	// One time as well
	static Marshaller marshallerObj = null;
	static JAXBContext contextObj = null;
	public static String getMarshalledValue(QBXML qbxml) throws Exception {
		String qbXMLStr = null;
		try {
			getMarshallerObj();
			StringWriter writer = new StringWriter();
			marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshallerObj.marshal(qbxml, writer);
			qbXMLStr = QBXML_PREFIX + writer.toString();
		} catch (Exception e) {
			throw e;
		}
		return qbXMLStr;
	}

	public static Object getUnmarshalledValue(String respFromQB) throws Exception {
		Object umValue = null;
		try {
			getMarshallerObj();
			Unmarshaller unmarshallerObj = contextObj.createUnmarshaller();
			Reader r = new StringReader(respFromQB);
			umValue = unmarshallerObj.unmarshal(r);
		} catch (Exception e) {
			throw e;
		}

		return umValue;
	}
	
	private static void getMarshallerObj() throws Exception {
		if (marshallerObj == null) {
			 contextObj = JAXBContext.newInstance(QBXML.class);
			marshallerObj = contextObj.createMarshaller();
			marshallerObj.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		}
	}
}
