//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.07 at 08:01:35 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.allgen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}FromReportDate" minOccurs="0"/>
 *         &lt;element ref="{}ToReportDate" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fromReportDate",
    "toReportDate"
})
@XmlRootElement(name = "ReportPeriod")
public class ReportPeriod {

    @XmlElement(name = "FromReportDate")
    protected String fromReportDate;
    @XmlElement(name = "ToReportDate")
    protected String toReportDate;

    /**
     * Gets the value of the fromReportDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromReportDate() {
        return fromReportDate;
    }

    /**
     * Sets the value of the fromReportDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromReportDate(String value) {
        this.fromReportDate = value;
    }

    /**
     * Gets the value of the toReportDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToReportDate() {
        return toReportDate;
    }

    /**
     * Sets the value of the toReportDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToReportDate(String value) {
        this.toReportDate = value;
    }

}
