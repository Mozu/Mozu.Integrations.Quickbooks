//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.04 at 02:45:08 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.qborderquery;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RefNumberRangeFilterType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RefNumberRangeFilterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FromRefNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ToRefNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RefNumberRangeFilterType", propOrder = {
    "fromRefNumber",
    "toRefNumber"
})
public class RefNumberRangeFilterType {

    @XmlElement(name = "FromRefNumber", required = true)
    protected String fromRefNumber;
    @XmlElement(name = "ToRefNumber", required = true)
    protected String toRefNumber;

    /**
     * Gets the value of the fromRefNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromRefNumber() {
        return fromRefNumber;
    }

    /**
     * Sets the value of the fromRefNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromRefNumber(String value) {
        this.fromRefNumber = value;
    }

    /**
     * Gets the value of the toRefNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToRefNumber() {
        return toRefNumber;
    }

    /**
     * Sets the value of the toRefNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToRefNumber(String value) {
        this.toRefNumber = value;
    }

}
