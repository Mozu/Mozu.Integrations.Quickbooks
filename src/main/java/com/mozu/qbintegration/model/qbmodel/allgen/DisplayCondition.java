//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.07 at 08:01:35 PM IST 
//


package com.mozu.qbintegration.model.qbmodel.allgen;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{}VisibleIf" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}VisibleIfNot" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}EnabledIf" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}EnabledIfNot" maxOccurs="unbounded" minOccurs="0"/>
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
    "visibleIf",
    "visibleIfNot",
    "enabledIf",
    "enabledIfNot"
})
@XmlRootElement(name = "DisplayCondition")
public class DisplayCondition {

    @XmlElement(name = "VisibleIf")
    protected List<String> visibleIf;
    @XmlElement(name = "VisibleIfNot")
    protected List<String> visibleIfNot;
    @XmlElement(name = "EnabledIf")
    protected List<String> enabledIf;
    @XmlElement(name = "EnabledIfNot")
    protected List<String> enabledIfNot;

    /**
     * Gets the value of the visibleIf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the visibleIf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVisibleIf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getVisibleIf() {
        if (visibleIf == null) {
            visibleIf = new ArrayList<String>();
        }
        return this.visibleIf;
    }

    /**
     * Gets the value of the visibleIfNot property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the visibleIfNot property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVisibleIfNot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getVisibleIfNot() {
        if (visibleIfNot == null) {
            visibleIfNot = new ArrayList<String>();
        }
        return this.visibleIfNot;
    }

    /**
     * Gets the value of the enabledIf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the enabledIf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEnabledIf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getEnabledIf() {
        if (enabledIf == null) {
            enabledIf = new ArrayList<String>();
        }
        return this.enabledIf;
    }

    /**
     * Gets the value of the enabledIfNot property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the enabledIfNot property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEnabledIfNot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getEnabledIfNot() {
        if (enabledIfNot == null) {
            enabledIfNot = new ArrayList<String>();
        }
        return this.enabledIfNot;
    }

}
