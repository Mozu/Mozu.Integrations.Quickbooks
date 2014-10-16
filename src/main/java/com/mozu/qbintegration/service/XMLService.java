package com.mozu.qbintegration.service;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Service;

import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;

@Service
public class XMLService {

	private static final String QBXML_PREFIX = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<?qbxml version=\"13.0\"?>";

	protected JAXBContext contextObj = null;
	
	protected Marshaller marshaller = null;
	protected Unmarshaller unmarshaller = null;
    
    @PostConstruct
    protected void getMarshallerObj() throws Exception {
        if (contextObj == null) {
             contextObj = JAXBContext.newInstance(QBXML.class);
             marshaller = contextObj.createMarshaller();
             marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
             unmarshaller = contextObj.createUnmarshaller();
        }
    }

    public String getMarshalledValue(QBXML qbxml) throws Exception {
		String qbXMLStr = null;
		try {
			StringWriter writer = new StringWriter();
			marshaller.marshal(qbxml, writer);
			qbXMLStr = QBXML_PREFIX + writer.toString();
		} catch (Exception e) {
			throw e;
		} 
		return qbXMLStr;
	}

	public Object getUnmarshalledValue(String respFromQB) throws Exception {
		Object umValue = null;
		try {
			Reader r = new StringReader(respFromQB);
			umValue = unmarshaller.unmarshal(r);
		} catch (Exception e) {
			throw e;
		}

		return umValue;
	}
}
