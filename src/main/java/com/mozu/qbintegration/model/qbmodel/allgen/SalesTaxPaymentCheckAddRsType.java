//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.07 at 08:01:35 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.allgen;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SalesTaxPaymentCheckAddRsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SalesTaxPaymentCheckAddRsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}SalesTaxPaymentCheckRet" minOccurs="0"/>
 *         &lt;element ref="{}ErrorRecovery" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requestID" type="{}STRTYPE" />
 *       &lt;attribute name="statusCode" use="required" type="{}INTTYPE" />
 *       &lt;attribute name="statusSeverity" use="required" type="{}STRTYPE" />
 *       &lt;attribute name="statusMessage" type="{}STRTYPE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SalesTaxPaymentCheckAddRsType", propOrder = {
    "salesTaxPaymentCheckRet",
    "errorRecovery"
})
public class SalesTaxPaymentCheckAddRsType {

    @XmlElement(name = "SalesTaxPaymentCheckRet")
    protected SalesTaxPaymentCheckRet salesTaxPaymentCheckRet;
    @XmlElement(name = "ErrorRecovery")
    protected ErrorRecovery errorRecovery;
    @XmlAttribute(name = "requestID")
    protected String requestID;
    @XmlAttribute(name = "statusCode", required = true)
    protected BigInteger statusCode;
    @XmlAttribute(name = "statusSeverity", required = true)
    protected String statusSeverity;
    @XmlAttribute(name = "statusMessage")
    protected String statusMessage;

    /**
     * Gets the value of the salesTaxPaymentCheckRet property.
     * 
     * @return
     *     possible object is
     *     {@link SalesTaxPaymentCheckRet }
     *     
     */
    public SalesTaxPaymentCheckRet getSalesTaxPaymentCheckRet() {
        return salesTaxPaymentCheckRet;
    }

    /**
     * Sets the value of the salesTaxPaymentCheckRet property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesTaxPaymentCheckRet }
     *     
     */
    public void setSalesTaxPaymentCheckRet(SalesTaxPaymentCheckRet value) {
        this.salesTaxPaymentCheckRet = value;
    }

    /**
     * Gets the value of the errorRecovery property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorRecovery }
     *     
     */
    public ErrorRecovery getErrorRecovery() {
        return errorRecovery;
    }

    /**
     * Sets the value of the errorRecovery property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorRecovery }
     *     
     */
    public void setErrorRecovery(ErrorRecovery value) {
        this.errorRecovery = value;
    }

    /**
     * Gets the value of the requestID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets the value of the requestID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestID(String value) {
        this.requestID = value;
    }

    /**
     * Gets the value of the statusCode property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the value of the statusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStatusCode(BigInteger value) {
        this.statusCode = value;
    }

    /**
     * Gets the value of the statusSeverity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusSeverity() {
        return statusSeverity;
    }

    /**
     * Sets the value of the statusSeverity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusSeverity(String value) {
        this.statusSeverity = value;
    }

    /**
     * Gets the value of the statusMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the value of the statusMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusMessage(String value) {
        this.statusMessage = value;
    }

}
