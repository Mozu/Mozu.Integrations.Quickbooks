package com.mozu.qbintegration.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 */

/**
 * @author Akshay
 * 
 */
@XmlRootElement(name = "QBWCXML")
public class QuickWebConnector {

	private String name;
	private String url;
	private int id;
	private String userName;
	private String Description;
	private String support;
	private String ownerId;
	private String fileId;
	private String qbType;
	private String qbStyle;
	private String qbAuthFlags;
	private Scheduler scheduler;

	public String getName() {
		return name;
	}

	@XmlElement(name = "AppName")
	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	@XmlElement(name = "AppID")
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}

	@XmlElement(name = "AppURL")
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDescription() {
		return Description;
	}

	@XmlElement(name = "AppDescription")
	public void setDescription(String description) {
		Description = description;
	}
	
	public String getSupport() {
		return support;
	}

	@XmlElement(name = "AppSupport")
	public void setSupport(String support) {
		this.support = support;
	}
	
	public String getUserName() {
		return userName;
	}

	@XmlElement(name = "UserName")
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getOwnerId() {
		return ownerId;
	}

	@XmlElement(name = "OwnerID")
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public String getFileId() {
		return fileId;
	}

	@XmlElement(name = "FileID")
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public String getQbType() {
		return qbType;
	}

	@XmlElement(name = "QBType")
	public void setQbType(String qbType) {
		this.qbType = qbType;
	}
	
	/**
	 * @return the qbStyle
	 */
	public String getQbStyle() {
		return qbStyle;
	}

	/**
	 * @param qbStyle the qbStyle to set
	 */
	@XmlElement(name = "Style")
	public void setQbStyle(String qbStyle) {
		this.qbStyle = qbStyle;
	}

	/**
	 * @return the qbAuthFlags
	 */
	public String getQbAuthFlags() {
		return qbAuthFlags;
	}

	/**
	 * @param qbAuthFlags the qbAuthFlags to set
	 */
	@XmlElement(name = "AuthFlags")
	public void setQbAuthFlags(String qbAuthFlags) {
		this.qbAuthFlags = qbAuthFlags;
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}

	@XmlElement(name = "Scheduler")
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

}
