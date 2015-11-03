package com.mozu.qbintegration.service;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;

import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAdd;
import com.mozu.qbintegration.model.qbmodel.allgen.CustomerAddRqType;
import com.mozu.qbintegration.model.qbmodel.allgen.QBXML;

@Service
public class XMLService {

	private static final String QBXML_PREFIX = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
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
    	escapeStrings(qbxml);
		String qbXMLStr = null;
	
		StringWriter writer = new StringWriter();
		marshaller.marshal(qbxml, writer);
		qbXMLStr = QBXML_PREFIX + writer.toString();
		writer.close();
	
		return qbXMLStr;
	}

	public QBXML getUnmarshalledValue(String respFromQB) throws Exception {
		QBXML umValue = null;
		try {
			Reader r = new StringReader(respFromQB);
			umValue = (QBXML)unmarshaller.unmarshal(r);
			unescapeStrings(umValue);
		} catch (Exception e) {
			throw e;
		}

		return umValue;
	}
	
	
	/**
	 * Begin handling non-ascii characters with html entity encoding.
	 * @param qbxml
	 */
	private void escapeStrings(QBXML qbxml) {
        if (qbxml.getQBXMLMsgsRq() != null) {
            if (qbxml.getQBXMLMsgsRq().getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq() != null) {
                for(Object obj : qbxml.getQBXMLMsgsRq().getHostQueryRqOrCompanyQueryRqOrCompanyActivityQueryRq()) {
                    if (obj instanceof CustomerAddRqType){
                        if (((CustomerAddRqType)obj).getCustomerAdd() != null) {
                            CustomerAdd customerAdd = ((CustomerAddRqType)obj).getCustomerAdd();
                            customerAdd.setFirstName(StringEscapeUtils.escapeHtml(customerAdd.getFirstName()));
                            customerAdd.setLastName(StringEscapeUtils.escapeHtml(customerAdd.getLastName()));
                            customerAdd.setEmail(StringEscapeUtils.escapeHtml(customerAdd.getEmail()));
                        }
                    }
                }
            }
        }
	}

	/**
	 * Begin handling non-ascii characters with html entity decoding.
	 * @param qbxml
	 */
	private void unescapeStrings(QBXML qbxml) {
	
	}
}
