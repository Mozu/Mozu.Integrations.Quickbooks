//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.04 at 02:45:08 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.qborderquery;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SalesOrderLineRetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SalesOrderLineRetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TxnLineID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ItemRef" type="{}ItemRefType"/>
 *         &lt;element name="Desc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Quantity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="UnitOfMeasure" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OverrideUOMSetRef" type="{}OverrideUOMSetRefType"/>
 *         &lt;element name="Rate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="RatePercent" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ClassRef" type="{}ClassRefType"/>
 *         &lt;element name="Amount" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InventorySiteRef" type="{}InventorySiteRefType"/>
 *         &lt;element name="InventorySiteLocationRef" type="{}InventorySiteLocationRefType"/>
 *         &lt;element name="SerialNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LotNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SalesTaxCodeRef" type="{}SalesTaxCodeRefType"/>
 *         &lt;element name="Invoiced" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="IsManuallyClosed" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Other1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Other2" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "SalesOrderLineRetType", propOrder = {
    "txnLineID",
    "itemRef",
    "desc",
    "quantity",
    "unitOfMeasure",
    "overrideUOMSetRef",
    "rate",
    "ratePercent",
    "classRef",
    "amount",
    "inventorySiteRef",
    "inventorySiteLocationRef",
    "serialNumber",
    "lotNumber",
    "salesTaxCodeRef",
    "invoiced",
    "isManuallyClosed",
    "other1",
    "other2",
    "dataExtRet"
})
public class SalesOrderLineRetType {

    @XmlElement(name = "TxnLineID", required = true)
    protected String txnLineID;
    @XmlElement(name = "ItemRef", required = true)
    protected ItemRefType itemRef;
    @XmlElement(name = "Desc", required = true)
    protected String desc;
    @XmlElement(name = "Quantity", required = true)
    protected String quantity;
    @XmlElement(name = "UnitOfMeasure", required = true)
    protected String unitOfMeasure;
    @XmlElement(name = "OverrideUOMSetRef", required = true)
    protected OverrideUOMSetRefType overrideUOMSetRef;
    @XmlElement(name = "Rate", required = true)
    protected String rate;
    @XmlElement(name = "RatePercent", required = true)
    protected String ratePercent;
    @XmlElement(name = "ClassRef", required = true)
    protected ClassRefType classRef;
    @XmlElement(name = "Amount", required = true)
    protected String amount;
    @XmlElement(name = "InventorySiteRef", required = true)
    protected InventorySiteRefType inventorySiteRef;
    @XmlElement(name = "InventorySiteLocationRef", required = true)
    protected InventorySiteLocationRefType inventorySiteLocationRef;
    @XmlElement(name = "SerialNumber", required = true)
    protected String serialNumber;
    @XmlElement(name = "LotNumber", required = true)
    protected String lotNumber;
    @XmlElement(name = "SalesTaxCodeRef", required = true)
    protected SalesTaxCodeRefType salesTaxCodeRef;
    @XmlElement(name = "Invoiced", required = true)
    protected String invoiced;
    @XmlElement(name = "IsManuallyClosed", required = true)
    protected String isManuallyClosed;
    @XmlElement(name = "Other1", required = true)
    protected String other1;
    @XmlElement(name = "Other2", required = true)
    protected String other2;
    @XmlElement(name = "DataExtRet", required = true)
    protected DataExtRetType dataExtRet;

    /**
     * Gets the value of the txnLineID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTxnLineID() {
        return txnLineID;
    }

    /**
     * Sets the value of the txnLineID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTxnLineID(String value) {
        this.txnLineID = value;
    }

    /**
     * Gets the value of the itemRef property.
     * 
     * @return
     *     possible object is
     *     {@link ItemRefType }
     *     
     */
    public ItemRefType getItemRef() {
        return itemRef;
    }

    /**
     * Sets the value of the itemRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemRefType }
     *     
     */
    public void setItemRef(ItemRefType value) {
        this.itemRef = value;
    }

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesc(String value) {
        this.desc = value;
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
     * Gets the value of the overrideUOMSetRef property.
     * 
     * @return
     *     possible object is
     *     {@link OverrideUOMSetRefType }
     *     
     */
    public OverrideUOMSetRefType getOverrideUOMSetRef() {
        return overrideUOMSetRef;
    }

    /**
     * Sets the value of the overrideUOMSetRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link OverrideUOMSetRefType }
     *     
     */
    public void setOverrideUOMSetRef(OverrideUOMSetRefType value) {
        this.overrideUOMSetRef = value;
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
     * Gets the value of the ratePercent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRatePercent() {
        return ratePercent;
    }

    /**
     * Sets the value of the ratePercent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRatePercent(String value) {
        this.ratePercent = value;
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
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAmount(String value) {
        this.amount = value;
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
     * Gets the value of the invoiced property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvoiced() {
        return invoiced;
    }

    /**
     * Sets the value of the invoiced property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiced(String value) {
        this.invoiced = value;
    }

    /**
     * Gets the value of the isManuallyClosed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsManuallyClosed() {
        return isManuallyClosed;
    }

    /**
     * Sets the value of the isManuallyClosed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsManuallyClosed(String value) {
        this.isManuallyClosed = value;
    }

    /**
     * Gets the value of the other1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOther1() {
        return other1;
    }

    /**
     * Sets the value of the other1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOther1(String value) {
        this.other1 = value;
    }

    /**
     * Gets the value of the other2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOther2() {
        return other2;
    }

    /**
     * Sets the value of the other2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOther2(String value) {
        this.other2 = value;
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
