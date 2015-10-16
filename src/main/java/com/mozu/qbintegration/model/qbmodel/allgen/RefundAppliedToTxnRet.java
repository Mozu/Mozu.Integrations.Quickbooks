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
 *         &lt;element ref="{}TxnID"/>
 *         &lt;element ref="{}TxnType"/>
 *         &lt;element ref="{}TxnDate"/>
 *         &lt;element name="RefNumber" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="20"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}CreditRemaining"/>
 *         &lt;element ref="{}RefundAmount"/>
 *         &lt;element ref="{}CreditRemainingInHomeCurrency" minOccurs="0"/>
 *         &lt;element ref="{}RefundAmountInHomeCurrency" minOccurs="0"/>
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
    "txnID",
    "txnType",
    "txnDate",
    "refNumber",
    "creditRemaining",
    "refundAmount",
    "creditRemainingInHomeCurrency",
    "refundAmountInHomeCurrency"
})
@XmlRootElement(name = "RefundAppliedToTxnRet")
public class RefundAppliedToTxnRet {

    @XmlElement(name = "TxnID", required = true)
    protected String txnID;
    @XmlElement(name = "TxnType", required = true)
    protected String txnType;
    @XmlElement(name = "TxnDate", required = true)
    protected String txnDate;
    @XmlElement(name = "RefNumber")
    protected String refNumber;
    @XmlElement(name = "CreditRemaining", required = true)
    protected String creditRemaining;
    @XmlElement(name = "RefundAmount", required = true)
    protected String refundAmount;
    @XmlElement(name = "CreditRemainingInHomeCurrency")
    protected String creditRemainingInHomeCurrency;
    @XmlElement(name = "RefundAmountInHomeCurrency")
    protected String refundAmountInHomeCurrency;

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
     * Gets the value of the txnType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnType() {
        return txnType;
    }

    /**
     * Sets the value of the txnType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnType(String value) {
        this.txnType = value;
    }

    /**
     * Gets the value of the txnDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnDate() {
        return txnDate;
    }

    /**
     * Sets the value of the txnDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnDate(String value) {
        this.txnDate = value;
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

    /**
     * Gets the value of the creditRemaining property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditRemaining() {
        return creditRemaining;
    }

    /**
     * Sets the value of the creditRemaining property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditRemaining(String value) {
        this.creditRemaining = value;
    }

    /**
     * Gets the value of the refundAmount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefundAmount() {
        return refundAmount;
    }

    /**
     * Sets the value of the refundAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefundAmount(String value) {
        this.refundAmount = value;
    }

    /**
     * Gets the value of the creditRemainingInHomeCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreditRemainingInHomeCurrency() {
        return creditRemainingInHomeCurrency;
    }

    /**
     * Sets the value of the creditRemainingInHomeCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreditRemainingInHomeCurrency(String value) {
        this.creditRemainingInHomeCurrency = value;
    }

    /**
     * Gets the value of the refundAmountInHomeCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefundAmountInHomeCurrency() {
        return refundAmountInHomeCurrency;
    }

    /**
     * Sets the value of the refundAmountInHomeCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefundAmountInHomeCurrency(String value) {
        this.refundAmountInHomeCurrency = value;
    }

}
