/**
 * 
 */
package com.mozu.qbintegration.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Akshay
 *
 */
@XmlRootElement(name="Scheduler")
public class Scheduler {
	private int run;

	public int getRun() {
		return run;
	}
    @XmlElement(name="RunEveryNminutes")
	public void setRun(int run) {
		this.run = run;
	}
	
	
	

}
