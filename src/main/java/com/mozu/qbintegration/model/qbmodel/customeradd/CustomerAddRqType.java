//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.04 at 01:39:25 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.customeradd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerAddRqType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerAddRqType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CustomerAdd" type="{}CustomerAddType"/>
 *         &lt;element name="IncludeRetElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAddRqType", propOrder = {
    "customerAdd",
    "includeRetElement"
})
public class CustomerAddRqType {

    @XmlElement(name = "CustomerAdd", required = true)
    protected CustomerAddType customerAdd;
    @XmlElement(name = "IncludeRetElement", required = true)
    protected String includeRetElement;

    /**
     * Gets the value of the customerAdd property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAddType }
     *     
     */
    public CustomerAddType getCustomerAdd() {
        return customerAdd;
    }

    /**
     * Sets the value of the customerAdd property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAddType }
     *     
     */
    public void setCustomerAdd(CustomerAddType value) {
        this.customerAdd = value;
    }

    /**
     * Gets the value of the includeRetElement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeRetElement() {
        return includeRetElement;
    }

    /**
     * Sets the value of the includeRetElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeRetElement(String value) {
        this.includeRetElement = value;
    }

}
