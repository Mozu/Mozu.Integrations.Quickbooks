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
 *         &lt;element name="Name">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="31"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}BarCode" minOccurs="0"/>
 *         &lt;element ref="{}IsActive" minOccurs="0"/>
 *         &lt;element ref="{}ClassRef" minOccurs="0"/>
 *         &lt;element ref="{}ParentRef" minOccurs="0"/>
 *         &lt;element ref="{}IsTaxIncluded" minOccurs="0"/>
 *         &lt;element ref="{}SalesTaxCodeRef" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{}SalesOrPurchase"/>
 *           &lt;element ref="{}SalesAndPurchase"/>
 *         &lt;/choice>
 *         &lt;element ref="{}ExternalGUID" minOccurs="0"/>
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
    "name",
    "barCode",
    "isActive",
    "classRef",
    "parentRef",
    "isTaxIncluded",
    "salesTaxCodeRef",
    "salesOrPurchase",
    "salesAndPurchase",
    "externalGUID"
})
@XmlRootElement(name = "ItemOtherChargeAdd")
public class ItemOtherChargeAdd {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "BarCode")
    protected BarCode barCode;
    @XmlElement(name = "IsActive")
    protected String isActive;
    @XmlElement(name = "ClassRef")
    protected ClassRef classRef;
    @XmlElement(name = "ParentRef")
    protected ParentRef parentRef;
    @XmlElement(name = "IsTaxIncluded")
    protected String isTaxIncluded;
    @XmlElement(name = "SalesTaxCodeRef")
    protected SalesTaxCodeRef salesTaxCodeRef;
    @XmlElement(name = "SalesOrPurchase")
    protected SalesOrPurchase salesOrPurchase;
    @XmlElement(name = "SalesAndPurchase")
    protected SalesAndPurchase salesAndPurchase;
    @XmlElement(name = "ExternalGUID")
    protected String externalGUID;

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
     * Gets the value of the barCode property.
     * 
     * @return
     *     possible object is
     *     {@link BarCode }
     *     
     */
    public BarCode getBarCode() {
        return barCode;
    }

    /**
     * Sets the value of the barCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link BarCode }
     *     
     */
    public void setBarCode(BarCode value) {
        this.barCode = value;
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
     * Gets the value of the parentRef property.
     * 
     * @return
     *     possible object is
     *     {@link ParentRef }
     *     
     */
    public ParentRef getParentRef() {
        return parentRef;
    }

    /**
     * Sets the value of the parentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParentRef }
     *     
     */
    public void setParentRef(ParentRef value) {
        this.parentRef = value;
    }

    /**
     * Gets the value of the isTaxIncluded property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsTaxIncluded() {
        return isTaxIncluded;
    }

    /**
     * Sets the value of the isTaxIncluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsTaxIncluded(String value) {
        this.isTaxIncluded = value;
    }

    /**
     * Gets the value of the salesTaxCodeRef property.
     * 
     * @return
     *     possible object is
     *     {@link SalesTaxCodeRef }
     *     
     */
    public SalesTaxCodeRef getSalesTaxCodeRef() {
        return salesTaxCodeRef;
    }

    /**
     * Sets the value of the salesTaxCodeRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesTaxCodeRef }
     *     
     */
    public void setSalesTaxCodeRef(SalesTaxCodeRef value) {
        this.salesTaxCodeRef = value;
    }

    /**
     * Gets the value of the salesOrPurchase property.
     * 
     * @return
     *     possible object is
     *     {@link SalesOrPurchase }
     *     
     */
    public SalesOrPurchase getSalesOrPurchase() {
        return salesOrPurchase;
    }

    /**
     * Sets the value of the salesOrPurchase property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesOrPurchase }
     *     
     */
    public void setSalesOrPurchase(SalesOrPurchase value) {
        this.salesOrPurchase = value;
    }

    /**
     * Gets the value of the salesAndPurchase property.
     * 
     * @return
     *     possible object is
     *     {@link SalesAndPurchase }
     *     
     */
    public SalesAndPurchase getSalesAndPurchase() {
        return salesAndPurchase;
    }

    /**
     * Sets the value of the salesAndPurchase property.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesAndPurchase }
     *     
     */
    public void setSalesAndPurchase(SalesAndPurchase value) {
        this.salesAndPurchase = value;
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

}
