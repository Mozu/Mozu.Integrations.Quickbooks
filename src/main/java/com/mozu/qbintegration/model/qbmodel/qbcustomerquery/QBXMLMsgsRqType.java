//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.04 at 01:59:17 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.qbcustomerquery;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QBXMLMsgsRqType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QBXMLMsgsRqType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CustomerQueryRq" type="{}CustomerQueryRqType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="onError" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QBXMLMsgsRqType", propOrder = {
    "customerQueryRq"
})
public class QBXMLMsgsRqType {

    @XmlElement(name = "CustomerQueryRq", required = true)
    protected CustomerQueryRqType customerQueryRq;
    @XmlAttribute(name = "onError")
    protected String onError;

    /**
     * Gets the value of the customerQueryRq property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerQueryRqType }
     *     
     */
    public CustomerQueryRqType getCustomerQueryRq() {
        return customerQueryRq;
    }

    /**
     * Sets the value of the customerQueryRq property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerQueryRqType }
     *     
     */
    public void setCustomerQueryRq(CustomerQueryRqType value) {
        this.customerQueryRq = value;
    }

    /**
     * Gets the value of the onError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnError() {
        return onError;
    }

    /**
     * Sets the value of the onError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnError(String value) {
        this.onError = value;
    }

}
