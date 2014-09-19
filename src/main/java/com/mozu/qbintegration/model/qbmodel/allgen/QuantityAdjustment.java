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
 *         &lt;choice>
 *           &lt;element ref="{}NewQuantity"/>
 *           &lt;element ref="{}QuantityDifference"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="SerialNumber" minOccurs="0">
 *             &lt;simpleType>
 *               &lt;restriction base="{}STRTYPE">
 *                 &lt;maxLength value="4095"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/element>
 *           &lt;element name="LotNumber" minOccurs="0">
 *             &lt;simpleType>
 *               &lt;restriction base="{}STRTYPE">
 *                 &lt;maxLength value="40"/>
 *               &lt;/restriction>
 *             &lt;/simpleType>
 *           &lt;/element>
 *         &lt;/choice>
 *         &lt;element ref="{}InventorySiteLocationRef" minOccurs="0"/>
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
    "newQuantity",
    "quantityDifference",
    "serialNumber",
    "lotNumber",
    "inventorySiteLocationRef"
})
@XmlRootElement(name = "QuantityAdjustment")
public class QuantityAdjustment {

    @XmlElement(name = "NewQuantity")
    protected String newQuantity;
    @XmlElement(name = "QuantityDifference")
    protected String quantityDifference;
    @XmlElement(name = "SerialNumber")
    protected String serialNumber;
    @XmlElement(name = "LotNumber")
    protected String lotNumber;
    @XmlElement(name = "InventorySiteLocationRef")
    protected InventorySiteLocationRef inventorySiteLocationRef;

    /**
     * Gets the value of the newQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewQuantity() {
        return newQuantity;
    }

    /**
     * Sets the value of the newQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewQuantity(String value) {
        this.newQuantity = value;
    }

    /**
     * Gets the value of the quantityDifference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantityDifference() {
        return quantityDifference;
    }

    /**
     * Sets the value of the quantityDifference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantityDifference(String value) {
        this.quantityDifference = value;
    }

    /**
     * Gets the value of the serialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the value of the serialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerialNumber(String value) {
        this.serialNumber = value;
    }

    /**
     * Gets the value of the lotNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLotNumber() {
        return lotNumber;
    }

    /**
     * Sets the value of the lotNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLotNumber(String value) {
        this.lotNumber = value;
    }

    /**
     * Gets the value of the inventorySiteLocationRef property.
     * 
     * @return
     *     possible object is
     *     {@link InventorySiteLocationRef }
     *     
     */
    public InventorySiteLocationRef getInventorySiteLocationRef() {
        return inventorySiteLocationRef;
    }

    /**
     * Sets the value of the inventorySiteLocationRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link InventorySiteLocationRef }
     *     
     */
    public void setInventorySiteLocationRef(InventorySiteLocationRef value) {
        this.inventorySiteLocationRef = value;
    }

}
