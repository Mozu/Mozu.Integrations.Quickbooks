//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.05 at 06:41:40 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.productquery;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ItemDiscountRetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ItemDiscountRetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ListID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TimeCreated" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TimeModified" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EditSequence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FullName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BarCodeValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsActive" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ClassRef" type="{}ClassRefType"/>
 *         &lt;element name="ParentRef" type="{}ParentRefType"/>
 *         &lt;element name="Sublevel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ItemDesc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SalesTaxCodeRef" type="{}SalesTaxCodeRefType"/>
 *         &lt;element name="DiscountRate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DiscountRatePercent" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="AccountRef" type="{}AccountRefType"/>
 *         &lt;element name="ExternalGUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DataExtRet" type="{}DataExtRetType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemDiscountRetType", propOrder = {
    "listID",
    "timeCreated",
    "timeModified",
    "editSequence",
    "name",
    "fullName",
    "barCodeValue",
    "isActive",
    "classRef",
    "parentRef",
    "sublevel",
    "itemDesc",
    "salesTaxCodeRef",
    "discountRate",
    "discountRatePercent",
    "accountRef",
    "externalGUID",
    "dataExtRet"
})
public class ItemDiscountRetType {

    @XmlElement(name = "ListID", required = true)
    protected String listID;
    @XmlElement(name = "TimeCreated", required = true)
    protected String timeCreated;
    @XmlElement(name = "TimeModified", required = true)
    protected String timeModified;
    @XmlElement(name = "EditSequence", required = true)
    protected String editSequence;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "FullName", required = true)
    protected String fullName;
    @XmlElement(name = "BarCodeValue", required = true)
    protected String barCodeValue;
    @XmlElement(name = "IsActive", required = true)
    protected String isActive;
    @XmlElement(name = "ClassRef", required = true)
    protected ClassRefType classRef;
    @XmlElement(name = "ParentRef", required = true)
    protected ParentRefType parentRef;
    @XmlElement(name = "Sublevel", required = true)
    protected String sublevel;
    @XmlElement(name = "ItemDesc", required = true)
    protected String itemDesc;
    @XmlElement(name = "SalesTaxCodeRef", required = true)
    protected SalesTaxCodeRefType salesTaxCodeRef;
    @XmlElement(name = "DiscountRate", required = true)
    protected String discountRate;
    @XmlElement(name = "DiscountRatePercent", required = true)
    protected String discountRatePercent;
    @XmlElement(name = "AccountRef", required = true)
    protected AccountRefType accountRef;
    @XmlElement(name = "ExternalGUID", required = true)
    protected String externalGUID;
    @XmlElement(name = "DataExtRet", required = true)
    protected DataExtRetType dataExtRet;

    /**
     * Gets the value of the listID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListID() {
        return listID;
    }

    /**
     * Sets the value of the listID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListID(String value) {
        this.listID = value;
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
     * Gets the value of the timeModified property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeModified() {
        return timeModified;
    }

    /**
     * Sets the value of the timeModified property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeModified(String value) {
        this.timeModified = value;
    }

    /**
     * Gets the value of the editSequence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEditSequence() {
        return editSequence;
    }

    /**
     * Sets the value of the editSequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEditSequence(String value) {
        this.editSequence = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the barCodeValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBarCodeValue() {
        return barCodeValue;
    }

    /**
     * Sets the value of the barCodeValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBarCodeValue(String value) {
        this.barCodeValue = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsActive(String value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the classRef property.
     * 
     * @return
     *     possible object is
     *     {@link ClassRefType }
     *     
     */
    public ClassRefType getClassRef() {
        return classRef;
    }

    /**
     * Sets the value of the classRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassRefType }
     *     
     */
    public void setClassRef(ClassRefType value) {
        this.classRef = value;
    }

    /**
     * Gets the value of the parentRef property.
     * 
     * @return
     *     possible object is
     *     {@link ParentRefType }
     *     
     */
    public ParentRefType getParentRef() {
        return parentRef;
    }

    /**
     * Sets the value of the parentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParentRefType }
     *     
     */
    public void setParentRef(ParentRefType value) {
        this.parentRef = value;
    }

    /**
     * Gets the value of the sublevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSublevel() {
        return sublevel;
    }

    /**
     * Sets the value of the sublevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSublevel(String value) {
        this.sublevel = value;
    }

    /**
     * Gets the value of the itemDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemDesc() {
        return itemDesc;
    }

    /**
     * Sets the value of the itemDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemDesc(String value) {
        this.itemDesc = value;
    }

    /**
     * Gets the value of the salesTaxCodeRef property.
     * 
     * @return
     *     possible object is
     *     {@link SalesTaxCodeRefType }
     *     
     */
    public SalesTaxCodeRefType getSalesTaxCodeRef() {
        return salesTaxCodeRef;
    }

    /**
     * Sets the value of the salesTaxCodeRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesTaxCodeRefType }
     *     
     */
    public void setSalesTaxCodeRef(SalesTaxCodeRefType value) {
        this.salesTaxCodeRef = value;
    }

    /**
     * Gets the value of the discountRate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountRate() {
        return discountRate;
    }

    /**
     * Sets the value of the discountRate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountRate(String value) {
        this.discountRate = value;
    }

    /**
     * Gets the value of the discountRatePercent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountRatePercent() {
        return discountRatePercent;
    }

    /**
     * Sets the value of the discountRatePercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountRatePercent(String value) {
        this.discountRatePercent = value;
    }

    /**
     * Gets the value of the accountRef property.
     * 
     * @return
     *     possible object is
     *     {@link AccountRefType }
     *     
     */
    public AccountRefType getAccountRef() {
        return accountRef;
    }

    /**
     * Sets the value of the accountRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountRefType }
     *     
     */
    public void setAccountRef(AccountRefType value) {
        this.accountRef = value;
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
     * Gets the value of the dataExtRet property.
     * 
     * @return
     *     possible object is
     *     {@link DataExtRetType }
     *     
     */
    public DataExtRetType getDataExtRet() {
        return dataExtRet;
    }

    /**
     * Sets the value of the dataExtRet property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtRetType }
     *     
     */
    public void setDataExtRet(DataExtRetType value) {
        this.dataExtRet = value;
    }

}
