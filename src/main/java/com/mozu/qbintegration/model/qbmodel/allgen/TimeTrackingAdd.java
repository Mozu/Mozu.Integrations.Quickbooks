//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.07 at 08:01:35 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.allgen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element ref="{}TxnDate" minOccurs="0"/>
 *         &lt;element ref="{}EntityRef"/>
 *         &lt;element ref="{}CustomerRef" minOccurs="0"/>
 *         &lt;element ref="{}ItemServiceRef" minOccurs="0"/>
 *         &lt;element ref="{}Rate" minOccurs="0"/>
 *         &lt;element ref="{}Duration"/>
 *         &lt;element ref="{}ClassRef" minOccurs="0"/>
 *         &lt;element ref="{}PayrollItemWageRef" minOccurs="0"/>
 *         &lt;element name="Notes" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="4095"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}BillableStatus" minOccurs="0"/>
 *         &lt;element ref="{}ExternalGUID" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="defMacro" type="{}MACROTYPE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "txnDate",
    "entityRef",
    "customerRef",
    "itemServiceRef",
    "rate",
    "duration",
    "classRef",
    "payrollItemWageRef",
    "notes",
    "billableStatus",
    "externalGUID"
})
@XmlRootElement(name = "TimeTrackingAdd")
public class TimeTrackingAdd {

    @XmlElement(name = "TxnDate")
    protected String txnDate;
    @XmlElement(name = "EntityRef", required = true)
    protected EntityRef entityRef;
    @XmlElement(name = "CustomerRef")
    protected CustomerRef customerRef;
    @XmlElement(name = "ItemServiceRef")
    protected ItemServiceRef itemServiceRef;
    @XmlElement(name = "Rate")
    protected String rate;
    @XmlElement(name = "Duration", required = true)
    protected String duration;
    @XmlElement(name = "ClassRef")
    protected ClassRef classRef;
    @XmlElement(name = "PayrollItemWageRef")
    protected PayrollItemWageRef payrollItemWageRef;
    @XmlElement(name = "Notes")
    protected String notes;
    @XmlElement(name = "BillableStatus")
    protected String billableStatus;
    @XmlElement(name = "ExternalGUID")
    protected String externalGUID;
    @XmlAttribute(name = "defMacro")
    protected String defMacro;

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
     * Gets the value of the entityRef property.
     * 
     * @return
     *     possible object is
     *     {@link EntityRef }
     *     
     */
    public EntityRef getEntityRef() {
        return entityRef;
    }

    /**
     * Sets the value of the entityRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityRef }
     *     
     */
    public void setEntityRef(EntityRef value) {
        this.entityRef = value;
    }

    /**
     * Gets the value of the customerRef property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerRef }
     *     
     */
    public CustomerRef getCustomerRef() {
        return customerRef;
    }

    /**
     * Sets the value of the customerRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerRef }
     *     
     */
    public void setCustomerRef(CustomerRef value) {
        this.customerRef = value;
    }

    /**
     * Gets the value of the itemServiceRef property.
     * 
     * @return
     *     possible object is
     *     {@link ItemServiceRef }
     *     
     */
    public ItemServiceRef getItemServiceRef() {
        return itemServiceRef;
    }

    /**
     * Sets the value of the itemServiceRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemServiceRef }
     *     
     */
    public void setItemServiceRef(ItemServiceRef value) {
        this.itemServiceRef = value;
    }

    /**
     * Gets the value of the rate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRate() {
        return rate;
    }

    /**
     * Sets the value of the rate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRate(String value) {
        this.rate = value;
    }

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDuration(String value) {
        this.duration = value;
    }

    /**
     * Gets the value of the classRef property.
     * 
     * @return
     *     possible object is
     *     {@link ClassRef }
     *     
     */
    public ClassRef getClassRef() {
        return classRef;
    }

    /**
     * Sets the value of the classRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassRef }
     *     
     */
    public void setClassRef(ClassRef value) {
        this.classRef = value;
    }

    /**
     * Gets the value of the payrollItemWageRef property.
     * 
     * @return
     *     possible object is
     *     {@link PayrollItemWageRef }
     *     
     */
    public PayrollItemWageRef getPayrollItemWageRef() {
        return payrollItemWageRef;
    }

    /**
     * Sets the value of the payrollItemWageRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link PayrollItemWageRef }
     *     
     */
    public void setPayrollItemWageRef(PayrollItemWageRef value) {
        this.payrollItemWageRef = value;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the billableStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillableStatus() {
        return billableStatus;
    }

    /**
     * Sets the value of the billableStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillableStatus(String value) {
        this.billableStatus = value;
    }

    /**
     * Gets the value of the externalGUID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalGUID() {
        return externalGUID;
    }

    /**
     * Sets the value of the externalGUID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalGUID(String value) {
        this.externalGUID = value;
    }

    /**
     * Gets the value of the defMacro property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefMacro() {
        return defMacro;
    }

    /**
     * Sets the value of the defMacro property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefMacro(String value) {
        this.defMacro = value;
    }

}
