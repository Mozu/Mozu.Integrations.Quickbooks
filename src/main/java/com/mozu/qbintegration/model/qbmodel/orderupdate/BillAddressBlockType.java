//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.04 at 03:01:40 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.orderupdate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BillAddressBlockType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BillAddressBlockType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Addr1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Addr2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Addr3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Addr4" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Addr5" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BillAddressBlockType", propOrder = {
    "addr1",
    "addr2",
    "addr3",
    "addr4",
    "addr5"
})
public class BillAddressBlockType {

    @XmlElement(name = "Addr1", required = true)
    protected String addr1;
    @XmlElement(name = "Addr2", required = true)
    protected String addr2;
    @XmlElement(name = "Addr3", required = true)
    protected String addr3;
    @XmlElement(name = "Addr4", required = true)
    protected String addr4;
    @XmlElement(name = "Addr5", required = true)
    protected String addr5;

    /**
     * Gets the value of the addr1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddr1() {
        return addr1;
    }

    /**
     * Sets the value of the addr1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddr1(String value) {
        this.addr1 = value;
    }

    /**
     * Gets the value of the addr2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddr2() {
        return addr2;
    }

    /**
     * Sets the value of the addr2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddr2(String value) {
        this.addr2 = value;
    }

    /**
     * Gets the value of the addr3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddr3() {
        return addr3;
    }

    /**
     * Sets the value of the addr3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddr3(String value) {
        this.addr3 = value;
    }

    /**
     * Gets the value of the addr4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddr4() {
        return addr4;
    }

    /**
     * Sets the value of the addr4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddr4(String value) {
        this.addr4 = value;
    }

    /**
     * Gets the value of the addr5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddr5() {
        return addr5;
    }

    /**
     * Sets the value of the addr5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddr5(String value) {
        this.addr5 = value;
    }

}
