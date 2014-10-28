var pageNumbers = [10, 25, 50, 100];

homeViewModel.prototype.getOrdersQueue = function() {
	var self = this;
	var $table = $('#orderQueueTable').dataTable({
		"bProcessing" : true,
		"bServerSide" : true,
		"bDestroy"	: true,
		"bFilter" : false,
		"bSort" : false,
		"bInfo" : false,
		"sAjaxSource" : "Orders/getOrdersQueue?tenantId=" + $("#tenantIdHdn").val(),
		"aoColumns" : [
			            {    
		            	   "mData": "createDate",
		            	   "mRender": function (data, type, row) {
						    	return moment(data).format('YYYY-MM-DD HH:mm:ss');
							}
			            },
			            {
							"mData" : "status"
						}, 
						{
							"mData" : "type"
						}, 
						{
							"mData" : "action"
						},
						{
							"mData" : "currentStep"
						},
						{
							"mData" : "id"
						}
		            ]
		});
	$table.fnDraw();
};


homeViewModel.prototype.getOrdersCancelled = function() {
	var self = this;
	var $table = $('#orderCancelledTable').dataTable({
		"bProcessing" : true,
		"bServerSide" : true,
		"bDestroy"	: true,
		"aLengthMenu": [pageNumbers, pageNumbers],
		"bSort": false,
		"sSearch":true,
		"sAjaxSource" : "Orders/getOrdersFilteredByAction?action=CANCELLED&tenantId=" + $("#tenantIdHdn").val() + "&siteId=" + $("#siteIdHdn").val(),
		"aoColumns" : [
			            {    
		            	   "mData": "orderNumber",
			            },
			            {
							"mData" : "customerEmail"
						}, 
						{
							"mData" : "createDate",
								"mRender": function (data, type, row) {
							   
									return unixToHumanTime(data);
								}
						}, 
						{
							"mData" : "updatedDate",
							"mRender": function (data, type, row) {
							    	
								return unixToHumanTime(data);
							}
						},
						{
							"mData" : "amount",
							"mRender": function (data, type, row) {
								return numeral(data).format('$0,0.00'); 
							}
						}
		            ]
		});
	$table.fnDraw();
};

homeViewModel.prototype.getOrdersUpdated = function() {
	var self = this;
	var $table = $('#orderUpdatedTable').dataTable({
		"bProcessing" : true,
		"bServerSide" : true,
		"bDestroy"	: true,
		"aLengthMenu": [pageNumbers, pageNumbers],
		"bSort": false,
		"sSearch":true,
		"sAjaxSource" : "Orders/getOrdersFilteredByAction?action=UPDATED&tenantId=" + $("#tenantIdHdn").val() + "&siteId=" + $("#siteIdHdn").val(),
		"aoColumns" : [

		            {    
	            	   "mData": "id",
	            	   "bSearchable": false,
	            	   "bSortable": false,
	            	   "mRender": function (data, type, full) {			
	            		   return '<input type="checkbox" id="allOrdersCheckbox' + data 
	            		   		+ '" name="allOrdersCheckbox" value ="'+ data +'"' + 
	            		   		' data-bind="click: maintainCBStateInArray"/>';
	            	   }
		            },
		            {
		            	"mData" : "orderNumber"
		            }, 
					{
						"mData" : "customerEmail"
					}, 
					{
						"mData" : "createDate",
							"mRender": function (data, type, row) {
						   
								return unixToHumanTime(data);
							}
					}, 
					{
						"mData" : "updatedDate",
						"mRender": function (data, type, row) {
						    	
							return unixToHumanTime(data);
						}
					},
					{
						"mData" : "amount",
						"mRender": function (data, type, row) {
							return numeral(data).format('$0,0.00'); 
						}
					},
					{    
					   //"mData": "conflictReason",
					    "mData": "id",
					    "bSearchable": false,
					    "bSortable": false,
					    "mRender": function (data, type, row) {
					    	var dataId = data ;
					    	return "<a href='javascript:compareDetails(\"" + row.id + "\")'>Review</a>";
					   }
					}
		]
	});
	$table.fnDraw();
};

homeViewModel.prototype.getOrderConflicts = function() {
	var self = this;
	var $table = $('#orderConflictsTable').dataTable({
		"bProcessing" : true,
		"bServerSide" : true,
		"bDestroy"	: true,
		"aLengthMenu": [pageNumbers, pageNumbers],
		"bSort": false,
		"sSearch":true,
		"sAjaxSource" : "Orders/getOrdersFilteredByAction?action=CONFLICT&tenantId=" + $("#tenantIdHdn").val() + "&siteId=" + $("#siteIdHdn").val(),
		"aoColumns" : [
            {    
        	   "mData": "id",
        	   "bSearchable": false,
        	   "bSortable": false,
        	   "mRender": function (data, type, full) {			
        		   return '<input type="checkbox" id="allOrderConflictCheckbox' + data + '" name="allOrderConflictCheckbox" value ="'+ data +'" />';
        	   }
		    },
		    {
		    	"mData" : "orderNumber"
		    }, 
			{
				"mData" : "customerEmail"
			}, 
			{
				"mData" : "createDate",
					"mRender": function (data, type, row) {
				   
						return unixToHumanTime(data);
					}
			}, 
			{
				"mData" : "updatedDate",
				"mRender": function (data, type, row) {
				    	
					return unixToHumanTime(data);
				}
			},
			{
				"mData" : "amount",
				"mRender": function (data, type, row) {
					return numeral(data).format('$0,0.00'); 
				}
			},
			{    
			   "mData": "conflictReason"
			},
			{    
			   //"mData": "conflictReason",
			    "mData": "id",
			    "bSearchable": false,
			    "bSortable": false,
			    "mRender": function (data, type, row) {
			    	var dataId = data ;
			    	return "<a href='javascript:funEdit(\"" + row.id + "\")'>Edit</a>";
			   }
			 }
		]
	});
	$table.fnDraw();
};

homeViewModel.prototype.getOrders = function() {
	var self = this;
	var $table = $('#orderTable').dataTable({
		"bProcessing" : true,
		"bServerSide" : true,
		"bDestroy"	: true,
		"aLengthMenu": [pageNumbers, pageNumbers],
		"bSort": false,
		"sSearch":true,
		"sAjaxSource" : "Orders/getOrdersFilteredByAction?action=POSTED&tenantId=" + $("#tenantIdHdn").val() + "&siteId=" + $("#siteIdHdn").val(),
		"aoColumns" : [

		{
			"mData" : "orderNumber"
		}, {
			"mData" : "customerEmail"
		}, {
			"mData" : "createDate",
			 "mRender": function (data, type, row) {
				 return unixToHumanTime(data);
			   }
		}, 
		{
			"mData" : "updatedDate",
			"mRender": function (data, type, row) {
				return unixToHumanTime(data);
			}
		},
		{
			"mData" : "amount",
			"mRender": function (data, type, row) {
				return numeral(data).format('$0,0.00'); 
			}
		},

		]
	});
	

	$table.fnDraw();

};

//To Post a Retry for an Order in CONFLICT status
homeViewModel.prototype.postRetryOrderToQB = function(action) {
	var self = this;
	//Clear the checkboxes array
	self.selectedConflictOrders.removeAll();
	
	var $allCheckedConflictBoxes = $('input:checkbox[name=allOrderConflictCheckbox]:checked');
	$allCheckedConflictBoxes.each(function(index) {
		self.selectedConflictOrders.push($(this).val());
	});
	
	if (self.selectedConflictOrders().length > 0) {
		$.ajax({
			url : "Orders/postConflictOrderToQB?tenantId="+$("#tenantIdHdn").val()+"&action="+action,
			type : "POST",
			data : ko.mapping.toJSON(self.selectedConflictOrders()),
			contentType: "application/json; charset=utf-8",
			success : function(data) {
				viewModel.getOrderConflicts();
			}
		});
	}
};

//To post an updated order to quickbooks
homeViewModel.prototype.postUpdatedOrderToQB = function(action) {
	var self = this;
	//Clear the checkboxes array
	self.selectedOrdersToUpdate.removeAll();
	
	var $allCheckedUpdateBoxes = $('input:checkbox[name=allOrdersCheckbox]:checked');
	$allCheckedUpdateBoxes.each(function(index) {
		self.selectedOrdersToUpdate.push($(this).val());
	});
	
	if (self.selectedOrdersToUpdate().length > 0) {
		$.ajax({
			url : "Orders/postUpdatedOrderToQB?tenantId="+$("#tenantIdHdn").val()+"&action="+action,
			type : "POST",
			data :  ko.mapping.toJSON(self.selectedOrdersToUpdate()),
			contentType: "application/json; charset=utf-8",
			success : function(data) {
				viewModel.getOrdersUpdated();
			}
		});
	}
	
};


homeViewModel.prototype.saveItemToQuickbooks = function() {
	var self = this;
 	$.ajax({
		contentType: 'application/json; charset=UTF-8',
		url : "api/qb/saveProductToQB?tenantId=" + $("#tenantIdHdn").val(),
		type : "POST",
		dataType : "json",
		data:  ko.mapping.toJSON(self.itemToFix),
		success : function(data) {
			console.log(data);
		}
	});
};

//Map existing product to QB
homeViewModel.prototype.mapItemToQuickbooks = function() {
	var self = this;
	var productToMap = {};
	productToMap.selectedProductToMap = self.selectedProductToMap();
	productToMap.toBeMappedItemNumber = self.itemToFix.itemNameNumber();
	displayError = false;
	$.ajax({
		contentType: 'application/json; charset=UTF-8',
		url : "api/qb/mapProductToQB?tenantId=" + $("#tenantIdHdn").val(),
		type : "POST",
		//dataType : "json",
		data:  ko.mapping.toJSON(productToMap), //ko.mapping.toJSON(self.selectedProductToMap()),
		success : function(data) {
			$("#conflictSuccessDiv").show();
			$("#conflictErrorDiv").hide();
			$("#conflictSuccessMessage").text(data.success);
		}, error : function(data) {
			$("#conflictSuccessDiv").hide();
			$("#conflictErrorDiv").show();
			if (data.responseJSON != null)
				$("#conflictErrorMessage").text(data.responseJSON.error);
			else
				$("#conflictErrorMessage").text(data.responseText);
		}	
	});
};

//TO show in the map product dropdown
homeViewModel.prototype.getAllProductsFromQB = function() {
	var self = this;
	$.ajax({
		url : "api/qb/initiateProductRefresh?tenantId=" + $("#tenantIdHdn").val(),
		type : "GET",
		dataType : "json",		
		success : function(data) {
			//console.log(data);
			getAllProductsFromEntityList();
		}
	});
	
};
