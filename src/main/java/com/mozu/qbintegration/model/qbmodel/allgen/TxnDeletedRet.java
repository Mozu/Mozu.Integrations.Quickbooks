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
 *         &lt;element ref="{}TxnDelType" minOccurs="0"/>
 *         &lt;element ref="{}TxnID" minOccurs="0"/>
 *         &lt;element ref="{}TimeCreated" minOccurs="0"/>
 *         &lt;element ref="{}TimeDeleted" minOccurs="0"/>
 *         &lt;element ref="{}RefNumber" minOccurs="0"/>
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
    "txnDelType",
    "txnID",
    "timeCreated",
    "timeDeleted",
    "refNumber"
})
@XmlRootElement(name = "TxnDeletedRet")
public class TxnDeletedRet {

    @XmlElement(name = "TxnDelType")
    protected String txnDelType;
    @XmlElement(name = "TxnID")
    protected String txnID;
    @XmlElement(name = "TimeCreated")
    protected String timeCreated;
    @XmlElement(name = "TimeDeleted")
    protected String timeDeleted;
    @XmlElement(name = "RefNumber")
    protected String refNumber;

    /**
     * Gets the value of the txnDelType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnDelType() {
        return txnDelType;
    }

    /**
     * Sets the value of the txnDelType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnDelType(String value) {
        this.txnDelType = value;
    }

    /**
     * Gets the value of the txnID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnID() {
        return txnID;
    }

    /**
     * Sets the value of the txnID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnID(String value) {
        this.txnID = value;
    }

    /**
     * Gets the value of the timeCreated property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeCreated() {
        return timeCreated;
    }

    /**
     * Sets the value of the timeCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeCreated(String value) {
        this.timeCreated = value;
    }

    /**
     * Gets the value of the timeDeleted property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeDeleted() {
        return timeDeleted;
    }

    /**
     * Sets the value of the timeDeleted property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeDeleted(String value) {
        this.timeDeleted = value;
    }

    /**
     * Gets the value of the refNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefNumber() {
        return refNumber;
    }

    /**
     * Sets the value of the refNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefNumber(String value) {
        this.refNumber = value;
    }

}
