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
 *         &lt;element ref="{}TxnEventType"/>
 *         &lt;element ref="{}TxnEventOperation"/>
 *         &lt;element ref="{}TxnID"/>
 *         &lt;element name="RefNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="20"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
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
    "txnEventType",
    "txnEventOperation",
    "txnID",
    "refNumber"
})
@XmlRootElement(name = "TxnEvent")
public class TxnEvent {

    @XmlElement(name = "TxnEventType", required = true)
    protected String txnEventType;
    @XmlElement(name = "TxnEventOperation", required = true)
    protected String txnEventOperation;
    @XmlElement(name = "TxnID", required = true)
    protected String txnID;
    @XmlElement(name = "RefNumber")
    protected String refNumber;

    /**
     * Gets the value of the txnEventType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnEventType() {
        return txnEventType;
    }

    /**
     * Sets the value of the txnEventType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnEventType(String value) {
        this.txnEventType = value;
    }

    /**
     * Gets the value of the txnEventOperation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnEventOperation() {
        return txnEventOperation;
    }

    /**
     * Sets the value of the txnEventOperation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnEventOperation(String value) {
        this.txnEventOperation = value;
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
