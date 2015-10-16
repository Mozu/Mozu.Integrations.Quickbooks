//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.07 at 08:01:35 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.allgen;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransferQueryRqType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransferQueryRqType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}TransferTxnQueryFilter"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requestID" type="{}STRTYPE" />
 *       &lt;attribute name="metaData" default="NoMetaData">
 *         &lt;simpleType>
 *           &lt;restriction base="{}STRTYPE">
 *             &lt;enumeration value="NoMetaData"/>
 *             &lt;enumeration value="MetaDataOnly"/>
 *             &lt;enumeration value="MetaDataAndResponseData"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="iterator">
 *         &lt;simpleType>
 *           &lt;restriction base="{}STRTYPE">
 *             &lt;enumeration value="Start"/>
 *             &lt;enumeration value="Continue"/>
 *             &lt;enumeration value="Stop"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="iteratorID" type="{}STRTYPE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransferQueryRqType", propOrder = {
    "txnID",
    "maxReturned",
    "modifiedDateRangeFilter",
    "txnDateRangeFilter",
    "includeRetElement"
})
public class TransferQueryRqType {

    @XmlElement(name = "TxnID")
    protected List<String> txnID;
    @XmlElement(name = "MaxReturned")
    protected BigInteger maxReturned;
    @XmlElement(name = "ModifiedDateRangeFilter")
    protected ModifiedDateRangeFilter modifiedDateRangeFilter;
    @XmlElement(name = "TxnDateRangeFilter")
    protected TxnDateRangeFilter txnDateRangeFilter;
    @XmlElement(name = "IncludeRetElement")
    protected List<String> includeRetElement;
    @XmlAttribute(name = "requestID")
    protected String requestID;
    @XmlAttribute(name = "metaData")
    protected String metaData;
    @XmlAttribute(name = "iterator")
    protected String iterator;
    @XmlAttribute(name = "iteratorID")
    protected String iteratorID;

    /**
     * Gets the value of the txnID property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the txnID property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTxnID().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTxnID() {
        if (txnID == null) {
            txnID = new ArrayList<String>();
        }
        return this.txnID;
    }

    /**
     * Gets the value of the maxReturned property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxReturned() {
        return maxReturned;
    }

    /**
     * Sets the value of the maxReturned property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxReturned(BigInteger value) {
        this.maxReturned = value;
    }

    /**
     * Gets the value of the modifiedDateRangeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ModifiedDateRangeFilter }
     *     
     */
    public ModifiedDateRangeFilter getModifiedDateRangeFilter() {
        return modifiedDateRangeFilter;
    }

    /**
     * Sets the value of the modifiedDateRangeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModifiedDateRangeFilter }
     *     
     */
    public void setModifiedDateRangeFilter(ModifiedDateRangeFilter value) {
        this.modifiedDateRangeFilter = value;
    }

    /**
     * Gets the value of the txnDateRangeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link TxnDateRangeFilter }
     *     
     */
    public TxnDateRangeFilter getTxnDateRangeFilter() {
        return txnDateRangeFilter;
    }

    /**
     * Sets the value of the txnDateRangeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link TxnDateRangeFilter }
     *     
     */
    public void setTxnDateRangeFilter(TxnDateRangeFilter value) {
        this.txnDateRangeFilter = value;
    }

    /**
     * Gets the value of the includeRetElement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includeRetElement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludeRetElement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIncludeRetElement() {
        if (includeRetElement == null) {
            includeRetElement = new ArrayList<String>();
        }
        return this.includeRetElement;
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
     * Gets the value of the metaData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetaData() {
        if (metaData == null) {
            return "NoMetaData";
        } else {
            return metaData;
        }
    }

    /**
     * Sets the value of the metaData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetaData(String value) {
        this.metaData = value;
    }

    /**
     * Gets the value of the iterator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIterator() {
        return iterator;
    }

    /**
     * Sets the value of the iterator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIterator(String value) {
        this.iterator = value;
    }

    /**
     * Gets the value of the iteratorID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIteratorID() {
        return iteratorID;
    }

    /**
     * Sets the value of the iteratorID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIteratorID(String value) {
        this.iteratorID = value;
    }

}
