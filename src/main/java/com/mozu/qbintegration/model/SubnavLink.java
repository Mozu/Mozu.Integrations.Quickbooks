package com.mozu.qbintegration.model;

import java.util.List;

public class SubnavLink {
	
	private String parentId;
	private String[] path;
	private String href;
	private String appId;
	private String windowTitle;
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String[] getPath() {
		return path;
	}
	public void setPath(String[] strings) {
		this.path = strings;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getWindowTitle() {
		return windowTitle;
	}
	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}
	
	

}
