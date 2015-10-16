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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TimeReportQueryRqType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeReportQueryRqType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}TimeReportQuery"/>
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
@XmlType(name = "TimeReportQueryRqType", propOrder = {
    "timeReportType",
    "displayReport",
    "reportPeriod",
    "reportDateMacro",
    "reportEntityFilter",
    "reportItemFilter",
    "reportClassFilter",
    "summarizeColumnsBy",
    "includeColumn",
    "includeSubcolumns",
    "reportCalendar",
    "returnRows",
    "returnColumns"
})
public class TimeReportQueryRqType {

    @XmlElement(name = "TimeReportType", required = true)
    protected String timeReportType;
    @XmlElement(name = "DisplayReport")
    protected String displayReport;
    @XmlElement(name = "ReportPeriod")
    protected ReportPeriod reportPeriod;
    @XmlElement(name = "ReportDateMacro")
    protected String reportDateMacro;
    @XmlElement(name = "ReportEntityFilter")
    protected ReportEntityFilter reportEntityFilter;
    @XmlElement(name = "ReportItemFilter")
    protected ReportItemFilter reportItemFilter;
    @XmlElement(name = "ReportClassFilter")
    protected ReportClassFilter reportClassFilter;
    @XmlElement(name = "SummarizeColumnsBy")
    protected String summarizeColumnsBy;
    @XmlElement(name = "IncludeColumn")
    protected List<String> includeColumn;
    @XmlElement(name = "IncludeSubcolumns")
    protected String includeSubcolumns;
    @XmlElement(name = "ReportCalendar")
    protected String reportCalendar;
    @XmlElement(name = "ReturnRows")
    protected String returnRows;
    @XmlElement(name = "ReturnColumns")
    protected String returnColumns;
    @XmlAttribute(name = "requestID")
    protected String requestID;

    /**
     * Gets the value of the timeReportType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeReportType() {
        return timeReportType;
    }

    /**
     * Sets the value of the timeReportType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeReportType(String value) {
        this.timeReportType = value;
    }

    /**
     * Gets the value of the displayReport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayReport() {
        return displayReport;
    }

    /**
     * Sets the value of the displayReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayReport(String value) {
        this.displayReport = value;
    }

    /**
     * Gets the value of the reportPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link ReportPeriod }
     *     
     */
    public ReportPeriod getReportPeriod() {
        return reportPeriod;
    }

    /**
     * Sets the value of the reportPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportPeriod }
     *     
     */
    public void setReportPeriod(ReportPeriod value) {
        this.reportPeriod = value;
    }

    /**
     * Gets the value of the reportDateMacro property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportDateMacro() {
        return reportDateMacro;
    }

    /**
     * Sets the value of the reportDateMacro property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportDateMacro(String value) {
        this.reportDateMacro = value;
    }

    /**
     * Gets the value of the reportEntityFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ReportEntityFilter }
     *     
     */
    public ReportEntityFilter getReportEntityFilter() {
        return reportEntityFilter;
    }

    /**
     * Sets the value of the reportEntityFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportEntityFilter }
     *     
     */
    public void setReportEntityFilter(ReportEntityFilter value) {
        this.reportEntityFilter = value;
    }

    /**
     * Gets the value of the reportItemFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ReportItemFilter }
     *     
     */
    public ReportItemFilter getReportItemFilter() {
        return reportItemFilter;
    }

    /**
     * Sets the value of the reportItemFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportItemFilter }
     *     
     */
    public void setReportItemFilter(ReportItemFilter value) {
        this.reportItemFilter = value;
    }

    /**
     * Gets the value of the reportClassFilter property.
     * 
     * @return
     *     possible object is
     *     {@link ReportClassFilter }
     *     
     */
    public ReportClassFilter getReportClassFilter() {
        return reportClassFilter;
    }

    /**
     * Sets the value of the reportClassFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReportClassFilter }
     *     
     */
    public void setReportClassFilter(ReportClassFilter value) {
        this.reportClassFilter = value;
    }

    /**
     * Gets the value of the summarizeColumnsBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSummarizeColumnsBy() {
        return summarizeColumnsBy;
    }

    /**
     * Sets the value of the summarizeColumnsBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSummarizeColumnsBy(String value) {
        this.summarizeColumnsBy = value;
    }

    /**
     * Gets the value of the includeColumn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the includeColumn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncludeColumn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getIncludeColumn() {
        if (includeColumn == null) {
            includeColumn = new ArrayList<String>();
        }
        return this.includeColumn;
    }

    /**
     * Gets the value of the includeSubcolumns property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeSubcolumns() {
        return includeSubcolumns;
    }

    /**
     * Sets the value of the includeSubcolumns property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeSubcolumns(String value) {
        this.includeSubcolumns = value;
    }

    /**
     * Gets the value of the reportCalendar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportCalendar() {
        return reportCalendar;
    }

    /**
     * Sets the value of the reportCalendar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportCalendar(String value) {
        this.reportCalendar = value;
    }

    /**
     * Gets the value of the returnRows property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnRows() {
        return returnRows;
    }

    /**
     * Sets the value of the returnRows property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnRows(String value) {
        this.returnRows = value;
    }

    /**
     * Gets the value of the returnColumns property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnColumns() {
        return returnColumns;
    }

    /**
     * Sets the value of the returnColumns property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnColumns(String value) {
        this.returnColumns = value;
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
