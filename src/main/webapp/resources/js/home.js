function funEdit(orderNumber) {
	$('#ordConflict').hide().fadeOut(800);
	$('#ordConflictError').show().fadeIn(800);
	$.ajax({
		url : "Orders/getOrderConflictsDetails",
		type : "GET",
		data : {
			"mozuOrderNumber" : orderNumber,
			"tenantId" : $("#tenantIdHdn").val(),
			"siteId"	: $("#siteIdHdn").val()
		},
		dataType : "json",		
		success : function(data) {
			viewModel.orderConflictDetails.removeAll();
			$(data).each(function(index) {				
				viewModel.orderConflictDetails.push(data[index]);
			});
			
			var $table = $('#singleErrorDisplay').dataTable({ retrieve: true,bDestroy:true, bFilter: false, bInfo: false, bPaginate:false, bDestroy	: true});
			$table.fnDraw();
			
			//Now get the list of all products from EL - TODO - get only if user selects map to existing products
			getAllProductsFromEntityList();
			
			viewModel.loadQBData("taxcode", function(data) {
				ko.mapping.fromJS(data,{},viewModel.availableTaxCodes);
			});
			
			viewModel.loadQBData("vendor", function(data) {
				ko.mapping.fromJS(data,{},viewModel.availableVendors);
			});
			
			viewModel.loadQBData("account", function(data) {
				ko.mapping.fromJS(data,{},viewModel.availableAccounts);
			});
		},
		error : function() {
			$("#content").hide();
		}
	});
}

function showOrderConflict() {
	$('#ordConflictError').hide().fadeOut(800);
	$('#ordConflict').show().fadeIn(800);
}

function compareDetails(orderNumber) {
	$('#ordUpdated').hide().fadeOut(800);
	$('#ordUpdateDetails').show().fadeIn(800);
	
	$.ajax({
		url : "Orders/getOrderCompareDetails",
		type : "GET",
		data : {
			"mozuOrderNumber" : orderNumber,
			"tenantId" : $("#tenantIdHdn").val(),
			"siteId"	: $("#siteIdHdn").val()
		},
		dataType : "json",		
		success : function(data) {
			ko.mapping.fromJS(data, viewModel.compare);
		},
		error : function() {
			$("#content").hide();
		}
	});
}

function saveDataToTable(data) {
	$(data).each(function(index) {				
		console.log(data[index]);
		viewModel.orderCompareDetails.push(data[index]);
	});
	
	$(data).promise().done(function() {
		$('#compareDisplay').dataTable({ retrieve: true,bFilter: false, bInfo: false, bPaginate:false, bSort: false, "aaSorting": []});
		$('#compareDisplay').dataTable().fnDraw();
		
	});
	
}

function getAllProductsFromEntityList() {
	$.ajax({
		url : "api/qb/getAllPostedProducts?tenantId=" + $("#tenantIdHdn").val(),
		type : "GET",
		dataType : "json",		
		success : function(data) {
			viewModel.allProductsInQB.removeAll();
			$(data).each(function(index) {	
				viewModel.allProductsInQB.push(
						new ProductToMap(data[index].qbProductListID, 
								data[index].productName));
			});
		},error : function() {
			$("#content").hide();
		}
	});
		
}

function showOrderCompare() {
	$('#ordUpdateDetails').hide().fadeOut(800);
	$('#ordUpdated').show().fadeIn(800);
}

var ProductToMap = function(qbProductListIDVal, productNameVal) {
    this.qbProductListID = qbProductListIDVal;
    this.productName = productNameVal;
};

var qbItem = function(itemNumber) {
	var self = this;
    self.itemNameNumber = ko.observable(itemNumber);
    self.itemPurchaseDesc = ko.observable("");
    self.itemPurchaseCost = ko.observable("");
    self.itemSalesDesc = ko.observable("");
    self.itemSalesPrice = ko.observable("");
    self.itemManuPartNum = ko.observable("");
    self.selectedChoice = ko.observable();
    self.itemTaxCode = ko.observable();
    self.itemExpenseAccount = ko.observable();
    self.itemIncomeAccount = ko.observable();
    self.selectedVendor = ko.observable();
}

var compare = {
	postedOrder : "",
	updatedOrder : ""
}

var usState = function(abbreviation, name) {
	this.stateName = name;
	this.stateAbbreviation = abbreviation;
}

var dataType = function(id,name) {
	this.id = ko.observable(id);
	this.name = ko.observable(name);
}

var pageNumbers = [10, 25, 50, 100];

var homeViewModel = function() {
	var self = this;
	self.buildVersion = ko.observable();
	self.settings = ko.mapping.fromJS(new Object());
	self.selectedTab = ko.observable();
	self.availableTaxCodes = ko.observableArray([]);
	self.availableAccounts = ko.observableArray([]);
	self.availableVendors = ko.observableArray([]);
	self.compare = ko.mapping.fromJS(compare);
	self.dataTypes = ko.observableArray([]);
	self.selectedDataType = ko.observable();
	
	self.availableStates = ko.observableArray(
			[
			new usState("AL","Alabama"), 
			new usState("AK","Alaska"), 
			new usState("AZ","Arizona"), 
			new usState("AR","Arkansas"), 
			new usState("CA","California"), 
			new usState("CO","Colorado"), 
			new usState("CT","Connecticut"), 
			new usState("DE","Delaware"), 
			new usState("DC","District Of Columbia"), 
			new usState("FL","Florida"),
			new usState("GA","Georgia"), 
			new usState("HI","Hawaii"), 
			new usState("ID","Idaho"), 
			new usState("IL","Illinois"), 
			new usState("IN","Indiana"), 
			new usState("IA","Iowa"), 
			new usState("KS","Kansas"), 
			new usState("KY","Kentucky"), 
			new usState("LA","Louisiana"), 
			new usState("ME","Maine"), 
			new usState("MD","Maryland"), 
			new usState("MA","Massachusetts"), 
			new usState("MI","Michigan"), 
			new usState("MN","Minnesota"), 
			new usState("MS","Mississippi"), 
			new usState("MO","Missouri"), 
			new usState("MT","Montana"), 
			new usState("NE","Nebraska"), 
			new usState("NV","Nevada"), 
			new usState("NH","New Hampshire"), 
			new usState("NJ","New Jersey"), 
			new usState("NM","New Mexico"), 
			new usState("NY","New York"), 
			new usState("NC","North Carolina"), 
			new usState("ND","North Dakota"), 
			new usState("OH","Ohio"), 
			new usState("OK","Oklahoma"), 
			new usState("OR","Oregon"), 
			new usState("PA","Pennsylvania"), 
			new usState("RI","Rhode Island"), 
			new usState("SC","South Carolina"), 
			new usState("SD","South Dakota"), 
			new usState("TN","Tennessee"), 
			new usState("TX","Texas"), 
			new usState("UT","Utah"), 
			new usState("VT","Vermont"), 
			new usState("VA","Virginia"), 
			new usState("WA","Washington"), 
			new usState("WV","West Virginia"), 
			new usState("WI","Wisconsin"), 
			new usState("WY","Wyoming")
			]);
	
	//For the detail section
	self.orderConflictDetails = ko.observableArray([]);
	
	//For the compare section on Order Updates
	self.orderCompareDetails = ko.observableArray([]);
	
	//For the row click on conflict details
    self.clickedRow = function(item) {
       self.itemToFix.itemNameNumber(item.dataToFix);
    }
    
    self.itemToFix =  new qbItem("");
    
    self.showItemCreate = ko.observable(false);
    self.showItemMap = ko.observable(false);
    
    //For saving new item to quickbooks
    self.availableItemTypes = ko.observableArray(['Inventory Part', 'Non Inventory Part', 'Inventory Assembly']);
    
    //For order conflict - populating and selecting existing items to map to this not found product
    self.allProductsInQB = ko.observableArray([]);
    self.selectedProductToMap = ko.observable();
    
    //For order conflict - select checkboxes
    self.selectedConflictOrders = ko.observableArray([]);
    	
    //For order update - select checkboxes
    self.selectedOrdersToUpdate = ko.observableArray([]);
    
    self.showDownload = ko.observable(false);
    
    self.showItemOptions = ko.computed(function() {
        return self.itemToFix.itemNameNumber() != "" ;
    }, self);
    
    self.enableNewItem = function() {
    	self.showItemCreate(true);
    	self.showItemMap(false);
    	return true;
    };
    
    self.enableExistingItem = function() {
    	self.showItemCreate(false);
    	self.showItemMap(true);
    	return true;
    }
    
    self.saveItemToQuickbooks = function() {
    	console.log(ko.mapping.toJSON(self.itemToFix));
     	$.ajax({
			contentType: 'application/json; charset=UTF-8',
			url : "api/qb/saveProductToQB?tenantId=" + $("#tenantIdHdn").val(),
			type : "POST",
			dataType : "json",
			data:  ko.mapping.toJSON(self.itemToFix),
			success : function(data) {
				console.log(data);
			},
			error : function() {
			}
    	});
    };
    
    //Map existing product to QB
    self.mapItemToQuickbooks = function() {
    	var productToMap = {};
    	productToMap.selectedProductToMap = self.selectedProductToMap();
    	productToMap.toBeMappedItemNumber = self.itemToFix.itemNameNumber();
    	
    	$.ajax({
			contentType: 'application/json; charset=UTF-8',
			url : "api/qb/mapProductToQB?tenantId=" + $("#tenantIdHdn").val(),
			type : "POST",
			dataType : "json",
			data:  ko.mapping.toJSON(productToMap), //ko.mapping.toJSON(self.selectedProductToMap()),
			success : function(data) {
				//console.log(data);
			},
			error : function() {
			}
    	});
    };
    
    //TO show in the map product dropdown
    self.getAllProductsFromQB = function() {
    	$.ajax({
    		url : "api/qb/initiateProductRefresh?tenantId=" + $("#tenantIdHdn").val(),
    		type : "GET",
    		dataType : "json",		
    		success : function(data) {
    			//console.log(data);
    			getAllProductsFromEntityList();
    		},error : function() {
    			$("#content").hide();
    		}
    	});
    	
    };
    
    self.maintainCBStateInArray = function() {
    	
    };
    
    // To Post a Retry for an Order in CONFLICT status
    self.postRetryOrderToQB = function() {
    	
    	//Clear the checkboxes array
		self.selectedConflictOrders.removeAll();
		
    	console.log($('input:checkbox[name=allOrderConflictCheckbox]:checked').length);
    	var $allCheckedConflictBoxes = $('input:checkbox[name=allOrderConflictCheckbox]:checked');
    	$allCheckedConflictBoxes.each(function(index) {
    		self.selectedConflictOrders.push($(this).val());
    	});
    	
    	$allCheckedConflictBoxes.promise().done(function() {
    		console.log(ko.mapping.toJSON(self.selectedConflictOrders()));
    		$.ajax({
        		url : "Orders/postConflictOrderToQB",
        		type : "POST",
        		data : {
        			"mozuOrderNumbers": ko.mapping.toJSON(self.selectedConflictOrders()),
        			"tenantId" : $("#tenantIdHdn").val(),
        			"siteId"	: $("#siteIdHdn").val()
        		},
        		dataType : "json",		
        		success : function(data) {
        			$("#"+$("#selectedTab").val()+"Tab").click();
        		},error : function() {
        			//$("#content").hide();
        		}
        	});
    	});
    };
    
    //To post an updated order to quickbooks
    self.postUpdatedOrderToQB = function() {
    	
    	//Clear the checkboxes array
		self.selectedOrdersToUpdate.removeAll();
		
    	console.log($('input:checkbox[name=allOrdersCheckbox]:checked').length);
    	
    	var $allCheckedUpdateBoxes = $('input:checkbox[name=allOrdersCheckbox]:checked');
    	$allCheckedUpdateBoxes.each(function(index) {
    		self.selectedOrdersToUpdate.push($(this).val());
    	});
    	
    	$allCheckedUpdateBoxes.promise().done(function() {
    		//console.log(ko.mapping.toJSON(self.selectedOrdersToUpdate()));
        	$.ajax({
        		url : "Orders/postUpdatedOrderToQB",
        		type : "POST",
        		data : {
        			"mozuOrderNumbers": ko.mapping.toJSON(self.selectedOrdersToUpdate()),
        			"tenantId" : $("#tenantIdHdn").val(),
        			"siteId"	: $("#siteIdHdn").val()
        		},
        		dataType : "json",		
        		success : function(data) {
        			$("#"+$("#selectedTab").val()+"Tab").click();
        		},error : function() {
        			//$("#content").hide();
        		}
        	});
    		
    	});
    	
    };
    
    
	self.save = function() {
		if (self.selectedTab() == "paymentMappingTab") {
			$.ajax({
				contentType: 'application/json; charset=UTF-8',
				url : "api/qb/data?tenantId=" + $("#tenantIdHdn").val(),
				type : "POST",
				dataType : "json",
				data:  ko.mapping.toJSON(self.paymentMappings),
				success : function(data) {
					console.log(data);
				},
				error : function() {
				}
			});	
		} else {
			$.ajax({
				contentType: 'application/json; charset=UTF-8',
				url : "api/config/settings?tenantId=" + $("#tenantIdHdn").val(),
				type : "POST",
				dataType : "json",
				data:  ko.mapping.toJSON(self.settings),
				success : function(data) {
					self.showDownload(true)
					ko.mapping.fromJS(data, self.settings);
				},
				error : function() {
				}
			});			
		}
	};

	self.qwcFileContent = ko.observable();
	
	self.download = function() {
		$.ajax({
			contentType: 'application/json; charset=UTF-8',
			url : "api/config/qbefile?tenantId=" + $("#tenantIdHdn").val(),
			type : "GET",
			dataType : "json",
			success : function(data) {
					//data.qbxml has the xml string - to be sent to download
					
				self.qwcFileContent(data.qbxml);
				$("#downloadForm").submit();

				},
				error : function() {
				}
			});
		}
		
	self.getVersion = function() {
		$.ajax({
			url : "version",
			type : "GET",
			dataType : "text",
			success : function(data) {
				self.buildVersion(data)
			},
			error : function() {
				$("#content").hide();
			}
		});
	};

	self.getOrders = function() {
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
				"mData" : "amount"
			},

			]
		});
		
	
		$table.fnDraw();

	};
	
	//Display orders in conflict
	self.getOrderConflicts = function() {

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
					"mData" : "amount"
				},
				{    
				   "mData": "conflictReason"
				   /*"mRender": function (data, type, row) {
				    	var dataId = data ;
				    	return "<span title='"+data+""'>"+data.substring(0,10)+"...</span>";
				   }*/
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
	
	//Save orders updated in mozu, yet to go to QB
	self.getOrdersUpdated = function() {

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
							"mData" : "amount"
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
	
	self.getOrdersCancelled = function() {

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
								"mData" : "amount"
							}
			            ]
			});
		$table.fnDraw();
	};
	
	self.getOrdersQueue = function() {

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
	
	self.getSettings = function() {
		$.ajax({
				contentType: 'application/json; charset=UTF-8',
				url : "api/config/settings?tenantId=" + $("#tenantIdHdn").val(),
				type : "GET",
				dataType : "json",
				success : function(data) {
					ko.mapping.fromJS(data, self.settings);
					ko.applyBindings(viewModel);

					
						if ($("#selectedTab").val() != "") {
							$("#"+$("#selectedTab").val()+"Tab").click();
						} else {
							self.getVersion();
							if (self.settings.qbAccount() != null && self.settings.qbPassword() != null) {
								self.showDownload(true);
						}
								
						self.mozuPayments.push(new mozuPayment("Visa", "Visa") );
						self.mozuPayments.push(new mozuPayment("Amex", "American Express") );
						self.mozuPayments.push(new mozuPayment("MC", "Master Card") );
						self.mozuPayments.push(new mozuPayment("Check", "Check"));
						self.mozuPayments.push(new mozuPayment("Discover", "Discover"));
						self.mozuPayments.push(new mozuPayment("StoreCredit", "StoreCredit"));
						
						self.dataTypes.push(new dataType("account", "Accounts"));
						self.dataTypes.push(new dataType("vendor", "Vendors"));
						self.dataTypes.push(new dataType("taxcode", "Sales Taxcodes"));
					}
				},
				error : function() {
					$("#content").hide();
				}
			});		
	};
	
	self.initiateRefresh = function(type) {
		$.ajax({
				contentType: 'application/json; charset=UTF-8',
				url : "api/qb/initiateDataRefresh?tenantId=" + $("#tenantIdHdn").val()+"&type="+type,
				type : "PUT",
				dataType : "json",
				success : function(data) {
					//TODO show success msg
				},
				error : function() {
					$("#content").hide();
				}
			});		
	};
	
	
	self.selectedDataType.subscribe(function(newValue){
		self.loadData();
	}, self);
	
	self.qbData = ko.observableArray([]);
	self.loadData = function() {
		self.loadQBData(self.selectedDataType().id(), function(data) {
			ko.mapping.fromJS(data,{},self.qbData);
		});
	}
	
	self.refreshData = function() {
		self.initiateRefresh(self.selectedDataType().id());
	}
	
	self.loadQBData = function(type, callback) {
		$.ajax({
			contentType: 'application/json; charset=UTF-8',
			url : "api/qb/data?tenantId=" + $("#tenantIdHdn").val()+"&type="+type,
			type : "GET",
			dataType : "json",
			success : function(data) {
				callback(data);
			},
			error : function() {
				$("#content").hide();
			}
		});		
	}
	
	self.qbPaymentMethods =  ko.observableArray([]);
	self.loadPaymentMapping = function() {
		
		//Clear the existing mapping table
		self.paymentMappings.removeAll();
		
		self.loadQBData("paymentmethod", function(data) {
			console.log(data);
			ko.mapping.fromJS(data,{},self.qbPaymentMethods);
		});
		
		//Load any mappings already done
		self.getPaymentMappings(function(data) {
			
			$(data).each(function(index) {
				self.paymentMappings.push(
						new paymentMapping(data[index].mzData.id,
								data[index].mzData, data[index].qbData));
			});
			//ko.mapping.fromJS(data,{},self.paymentMappings);
		});
	};
	
	self.getPaymentMappings = function(callback) {
		$.ajax({
			contentType: 'application/json; charset=UTF-8',
			url : "api/qb/getPaymentMappings?tenantId=" + $("#tenantIdHdn").val(),
			type : "GET",
			dataType : "json",
			success : function(data) {
				callback(data);
			},
			error : function() {
				$("#content").hide();
			}
		});		
	}
	
	self.mapPayment = function() {
		var exists = false;
		for(var i=0;i<self.paymentMappings().length;i++) {
			if (self.selectedMozuPayment().id() == self.paymentMappings()[i].mozuId()) {
				exists = true;
			}	
		}
		
		if (!exists)
			self.paymentMappings.push(new paymentMapping(self.selectedMozuPayment().id(),self.selectedMozuPayment(), self.selectedQBPayment()));
	}
	
	self.mozuPayments = ko.observableArray([]);
	self.selectedMozuPayment = ko.observable();
	self.selectedQBPayment = ko.observable();
	self.paymentMappings = ko.observableArray([]);
	
	self.getSettings();
}


var mozuPayment = function(id, name) {
	this.id = ko.observable(id);
	this.name = ko.observable(name);
}

var paymentMapping = function(mozuId, mozuPayment, qbPayment) {
	this.type = ko.observable("payment");
	this.mozuId = ko.observable(mozuId);
	this.mzData = ko.observable(mozuPayment);
	this.qbData = ko.observable(qbPayment);
}


function unixToHumanTime(data) {
	return moment.unix(data/1000).format("YYYY-MM-DD HH:mm:ss")
}

function closeError() {
	$("#serverError").hide();
}

$(document).ajaxError(function(event, jqxhr, settings, exception) {
	console.log(exception);
	console.log(event);
	console.log(settings);
	console.log(jqxhr);
	if (jqxhr.status >= 200 && jqxhr.status <= 300)
		return;
	if (jqxhr.responseJSON != null)
		$("#serverErrorMessage").html(jqxhr.responseJSON.message);
	else if (jqxhr.responseText != null)
		$("#serverErrorMessage").html(jqxhr.responseText);
	else {
		$("#serverErrorMessage").html(jqxhr.statusText);
	}
	$("#serverError").show();
});

var viewModel;

$(function() {

	
	function closeError() {
		$("#serverError").hide();
	}

	$.ajaxPrefilter(function(options, originalOptions, jqXHR) {
		$("#serverError").hide();
		$("#progressIndicator").show();
		jqXHR.complete(function() {
			$("#progressIndicator").hide();
		});

	});
	

	$(".tabs a").click(function(e) {
		var tabElement = e.target.parentElement;
		var newTab = e.target;
		var parent = tabElement.parentElement;
		var activeTab = $(parent).find('.active');
		var activeTabId = activeTab.data('tab-id');
		var newTabId = $(newTab).data('tab-id');
		var hideSave = $(newTab).data('hide-save');

		if (activeTabId == newTabId)
			return;
		viewModel.selectedTab(newTabId);
		activeTab.removeClass('active');
		$(newTab).addClass('active');

		$('#' + activeTabId).fadeOut('fast', function() {
			$('#' + newTabId).fadeIn('fast');
		});

		if (hideSave) {
			$("#saveBtn").hide();
		} else {
			$("#saveBtn").show();
		}
		
	});

	
	$(".subTabs span").click(function (e) {
        var tabElement = e.target.parentElement;
        var newTab = e.target;
        var parent = tabElement.parentElement;
        var activeTab = $(parent).find('.selected');
        var activeTabId = activeTab.data('tab-id');
        var newTabId = $(newTab).data('tab-id');

        activeTab.removeClass('selected');
        $(newTab).addClass('selected');

        if (activeTabId != null) {
            $('#' + activeTabId).fadeOut('fast', function () {
                $('#' + newTabId).fadeIn('fast');
            });        	
        } else {
        	 $('#' + newTabId).fadeIn('fast');
        }


    });

	viewModel = new homeViewModel();
	
	$("#saveBtn").hide();
});
