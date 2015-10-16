package com.mozu.qbintegration.model;

import java.util.List;

import com.mozu.qbintegration.tasks.WorkTask;

public class OrderQueueDataTable extends OrderDatatableObject {
	List<WorkTask> aaData;

	public List<WorkTask> getAaData() {
		return aaData;
	}

	public void setAaData(List<WorkTask> aaData) {
		this.aaData = aaData;
	}
}
