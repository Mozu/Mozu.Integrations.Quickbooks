package com.mozu.qbintegration.utils;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;

public class XMLHelper {

	private static final String QBXML_PREFIX = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<?qbxml version=\"13.0\"?>";
	// One time as well
	static JAXBContext contextObj = null;
	
	static ArrayBlockingQueue<Marshaller> marshallerPool = new ArrayBlockingQueue<>(10);
	static ArrayBlockingQueue<Unmarshaller> unmarshallerPool = new ArrayBlockingQueue<>(10);
	
	public static String getMarshalledValue(QBXML qbxml) throws Exception {
		String qbXMLStr = null;
		Marshaller marshallerObj = null;
		try {
			getMarshallerObj();
			StringWriter writer = new StringWriter();
			marshallerObj = marshallerPool.poll();
			marshallerObj.marshal(qbxml, writer);
			qbXMLStr = QBXML_PREFIX + writer.toString();
		} catch (Exception e) {
			throw e;
		} finally {
			marshallerPool.add(marshallerObj);
		}
		return qbXMLStr;
	}

	public static Object getUnmarshalledValue(String respFromQB) throws Exception {
		Object umValue = null;
		Unmarshaller unmarshallerObj = null;
		try {
			getMarshallerObj();
			unmarshallerObj = unmarshallerPool.poll();
			Reader r = new StringReader(respFromQB);
			umValue = unmarshallerObj.unmarshal(r);
		} catch (Exception e) {
			throw e;
		} finally {
			unmarshallerPool.add(unmarshallerObj);
		}

		return umValue;
	}
	
	private static void getMarshallerObj() throws Exception {
		if (contextObj == null) {
			 contextObj = JAXBContext.newInstance(QBXML.class);
			 Marshaller marshaller = null;
			 for(int i = 0; i < 10; i++) {
				 marshaller = contextObj.createMarshaller();
				 marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
				 marshallerPool.add(marshaller);
				 unmarshallerPool.add(contextObj.createUnmarshaller());
			 }
		}
	}
}
