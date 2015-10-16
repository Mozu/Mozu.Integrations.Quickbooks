//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.05 at 06:20:26 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.salesorderadd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SalesOrderLineGroupAddType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SalesOrderLineGroupAddType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ItemGroupRef" type="{}ItemGroupRefType"/>
 *         &lt;element name="Quantity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="UnitOfMeasure" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InventorySiteRef" type="{}InventorySiteRefType"/>
 *         &lt;element name="InventorySiteLocationRef" type="{}InventorySiteLocationRefType"/>
 *         &lt;element name="DataExt" type="{}DataExtType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SalesOrderLineGroupAddType", propOrder = {
    "itemGroupRef",
    "quantity",
    "unitOfMeasure",
    "inventorySiteRef",
    "inventorySiteLocationRef",
    "dataExt"
})
public class SalesOrderLineGroupAddType {

    @XmlElement(name = "ItemGroupRef", required = true)
    protected ItemGroupRefType itemGroupRef;
    @XmlElement(name = "Quantity", required = true)
    protected String quantity;
    @XmlElement(name = "UnitOfMeasure", required = true)
    protected String unitOfMeasure;
    @XmlElement(name = "InventorySiteRef", required = true)
    protected InventorySiteRefType inventorySiteRef;
    @XmlElement(name = "InventorySiteLocationRef", required = true)
    protected InventorySiteLocationRefType inventorySiteLocationRef;
    @XmlElement(name = "DataExt", required = true)
    protected DataExtType dataExt;

    /**
     * Gets the value of the itemGroupRef property.
     * 
     * @return
     *     possible object is
     *     {@link ItemGroupRefType }
     *     
     */
    public ItemGroupRefType getItemGroupRef() {
        return itemGroupRef;
    }

    /**
     * Sets the value of the itemGroupRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemGroupRefType }
     *     
     */
    public void setItemGroupRef(ItemGroupRefType value) {
        this.itemGroupRef = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantity(String value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the unitOfMeasure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    /**
     * Sets the value of the unitOfMeasure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitOfMeasure(String value) {
        this.unitOfMeasure = value;
    }

    /**
     * Gets the value of the inventorySiteRef property.
     * 
     * @return
     *     possible object is
     *     {@link InventorySiteRefType }
     *     
     */
    public InventorySiteRefType getInventorySiteRef() {
        return inventorySiteRef;
    }

    /**
     * Sets the value of the inventorySiteRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link InventorySiteRefType }
     *     
     */
    public void setInventorySiteRef(InventorySiteRefType value) {
        this.inventorySiteRef = value;
    }

    /**
     * Gets the value of the inventorySiteLocationRef property.
     * 
     * @return
     *     possible object is
     *     {@link InventorySiteLocationRefType }
     *     
     */
    public InventorySiteLocationRefType getInventorySiteLocationRef() {
        return inventorySiteLocationRef;
    }

    /**
     * Sets the value of the inventorySiteLocationRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link InventorySiteLocationRefType }
     *     
     */
    public void setInventorySiteLocationRef(InventorySiteLocationRefType value) {
        this.inventorySiteLocationRef = value;
    }

    /**
     * Gets the value of the dataExt property.
     * 
     * @return
     *     possible object is
     *     {@link DataExtType }
     *     
     */
    public DataExtType getDataExt() {
        return dataExt;
    }

    /**
     * Sets the value of the dataExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtType }
     *     
     */
    public void setDataExt(DataExtType value) {
        this.dataExt = value;
    }

}
