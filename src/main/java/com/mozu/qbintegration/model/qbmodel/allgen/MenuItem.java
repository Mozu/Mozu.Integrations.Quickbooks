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
 *         &lt;element name="MenuText">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="50"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="EventTag">
 *           &lt;simpleType>
 *             &lt;restriction base="{}STRTYPE">
 *               &lt;maxLength value="50"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{}DisplayCondition" minOccurs="0"/>
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
    "menuText",
    "eventTag",
    "displayCondition"
})
@XmlRootElement(name = "MenuItem")
public class MenuItem {

    @XmlElement(name = "MenuText", required = true)
    protected String menuText;
    @XmlElement(name = "EventTag", required = true)
    protected String eventTag;
    @XmlElement(name = "DisplayCondition")
    protected DisplayCondition displayCondition;

    /**
     * Gets the value of the menuText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMenuText() {
        return menuText;
    }

    /**
     * Sets the value of the menuText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMenuText(String value) {
        this.menuText = value;
    }

    /**
     * Gets the value of the eventTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventTag() {
        return eventTag;
    }

    /**
     * Sets the value of the eventTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventTag(String value) {
        this.eventTag = value;
    }

    /**
     * Gets the value of the displayCondition property.
     * 
     * @return
     *     possible object is
     *     {@link DisplayCondition }
     *     
     */
    public DisplayCondition getDisplayCondition() {
        return displayCondition;
    }

    /**
     * Sets the value of the displayCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link DisplayCondition }
     *     
     */
    public void setDisplayCondition(DisplayCondition value) {
        this.displayCondition = value;
    }

}
