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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SignonDesktopRqType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignonDesktopRqType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}ClientDateTime"/>
 *         &lt;group ref="{}DLogin"/>
 *         &lt;element ref="{}InstallationID" minOccurs="0"/>
 *         &lt;element ref="{}Language"/>
 *         &lt;element ref="{}AppID"/>
 *         &lt;element ref="{}AppVer"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requestID" type="{}STRTYPE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignonDesktopRqType", propOrder = {
    "clientDateTime",
    "applicationLogin",
    "connectionTicket",
    "installationID",
    "language",
    "appID",
    "appVer"
})
public class SignonDesktopRqType {

    @XmlElement(name = "ClientDateTime", required = true)
    protected String clientDateTime;
    @XmlElement(name = "ApplicationLogin", required = true)
    protected String applicationLogin;
    @XmlElement(name = "ConnectionTicket", required = true)
    protected String connectionTicket;
    @XmlElement(name = "InstallationID")
    protected String installationID;
    @XmlElement(name = "Language", required = true)
    protected String language;
    @XmlElement(name = "AppID", required = true)
    protected String appID;
    @XmlElement(name = "AppVer", required = true)
    protected String appVer;
    @XmlAttribute(name = "requestID")
    protected String requestID;

    /**
     * Gets the value of the clientDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientDateTime() {
        return clientDateTime;
    }

    /**
     * Sets the value of the clientDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientDateTime(String value) {
        this.clientDateTime = value;
    }

    /**
     * Gets the value of the applicationLogin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationLogin() {
        return applicationLogin;
    }

    /**
     * Sets the value of the applicationLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationLogin(String value) {
        this.applicationLogin = value;
    }

    /**
     * Gets the value of the connectionTicket property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectionTicket() {
        return connectionTicket;
    }

    /**
     * Sets the value of the connectionTicket property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectionTicket(String value) {
        this.connectionTicket = value;
    }

    /**
     * Gets the value of the installationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstallationID() {
        return installationID;
    }

    /**
     * Sets the value of the installationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstallationID(String value) {
        this.installationID = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the appID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppID() {
        return appID;
    }

    /**
     * Sets the value of the appID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppID(String value) {
        this.appID = value;
    }

    /**
     * Gets the value of the appVer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppVer() {
        return appVer;
    }

    /**
     * Sets the value of the appVer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppVer(String value) {
        this.appVer = value;
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

}
