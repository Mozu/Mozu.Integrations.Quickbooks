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
			homeViewModel.orderConflictDetails.removeAll();
			$(data).each(function(index) {				
				console.log(data[index]);
				homeViewModel.orderConflictDetails.push(data[index]);
			});
			
			var $table = $('#singleErrorDisplay').dataTable({ retrieve: true,bFilter: false, bInfo: false, bPaginate:false, bDestroy	: true});
			$table.fnDraw();
			
			//Now get the list of all products from EL - TODO - get only if user selects map to existing products
			getAllProductsFromEntityList();
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
			homeViewModel.orderCompareDetails.removeAll();
			saveDataToTable(data);
		},
		error : function() {
			$("#content").hide();
		}
	});
}

function saveDataToTable(data) {
	$(data).each(function(index) {				
		console.log(data[index]);
		homeViewModel.orderCompareDetails.push(data[index]);
	});
	
	$(data).promise().done(function() {
		$('#compareDisplay').dataTable({ retrieve: true,bFilter: false, bInfo: false, bPaginate:false});
		$('#compareDisplay').dataTable().fnDraw();
		
	});
	
}

function getAllProductsFromEntityList() {
	$.ajax({
		url : "getAllPostedProducts",
		type : "GET",
		data : {
			"tenantId" : $("#tenantIdHdn").val(),
			"siteId"	: $("#siteIdHdn").val()
		},
		dataType : "json",		
		success : function(data) {
			homeViewModel.allProductsInQB.removeAll();
			$(data).each(function(index) {				
				console.log(data[index]);
				homeViewModel.allProductsInQB.push(
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
    self.itemSalesDesc = ko.observable("");
    self.itemSalesPrice = ko.observable("");
    self.itemManuPartNum = ko.observable("");
    self.itemTaxCode = ko.observable("");
    self.itemExpenseAccount = ko.observable("");
    self.itemAssetAccount = ko.observable("");
    self.itemIncomeAccount = ko.observable("");
    self.selectedChoice = ko.observable();
}

var homeViewModel = function() {
	var self = this;
	self.buildVersion = ko.observable();
	self.settings = ko.mapping.fromJS(new Object());
	
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
    
    //For order update - select checkboxes
    self.selectedOrdersToUpdate = ko.observableArray([]); // Initially checked
    
    
    self.showDownload = ko.observable(false);
    
    self.showItemOptions = ko.computed(function() {
        return self.itemToFix.itemNameNumber() != "" ;
    }, self);
    
    self.enableNewItem = function() {
    	self.showItemCreate(true);
    	self.showItemMap(false);
    };
    
    self.enableExistingItem = function() {
    	self.showItemCreate(false);
    	self.showItemMap(true);
    }
    
    self.saveItemToQuickbooks = function() {
     	$.ajax({
			contentType: 'application/json; charset=UTF-8',
			url : "saveProductToQB?tenantId=" + $("#tenantIdHdn").val() + "&siteId=" + $("#siteIdHdn").val(),
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
			url : "mapProductToQB?tenantId=" + $("#tenantIdHdn").val() + "&siteId=" + $("#siteIdHdn").val(),
			type : "POST",
			dataType : "json",
			data:  ko.mapping.toJSON(productToMap), //ko.mapping.toJSON(self.selectedProductToMap()),
			success : function(data) {
				console.log(data);
			},
			error : function() {
			}
    	});
    };
    
    //TO show in the map product dropdown
    self.getAllProductsFromQB = function() {
    	$.ajax({
    		url : "getAllProductsFromQB",
    		type : "GET",
    		data : {
    			"tenantId" : $("#tenantIdHdn").val(),
    			"siteId"	: $("#siteIdHdn").val()
    		},
    		dataType : "json",		
    		success : function(data) {
    			console.log(data);
    			getAllProductsFromEntityList();
    		},error : function() {
    			$("#content").hide();
    		}
    	});
    	
    };
    
    //To post an updated order to quickbooks
    self.postUpdatedOrderToQB = function() {
    	console.log(console.log($('input:checkbox[name=allOrdersCheckbox]:checked').length));
    	
    	var $allCheckedUpdateBoxes = $('input:checkbox[name=allOrdersCheckbox]:checked');
    	$allCheckedUpdateBoxes.each(function(index) {
    		self.selectedOrdersToUpdate.push($(this).val());
    	});
    	
    	$allCheckedUpdateBoxes.promise().done(function() {
    		console.log(ko.mapping.toJSON(self.selectedOrdersToUpdate()));
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
        			
        		},error : function() {
        			$("#content").hide();
        		}
        	});
    		
    	});
    	
    };
    
    
	self.save = function() {
		//identify which is the active tab
		
		var selectedTab;
		$(".tab").each(function(index) {
			if($(this).attr('class').indexOf(" active") > -1) {
				selectedTab = $(this).data("tab-id");
			}
		});

		if("generalTab" === selectedTab) {
			$.ajax({
				contentType: 'application/json; charset=UTF-8',
				url : "api/config/settings?tenantId=" + $("#tenantIdHdn").val(),
				type : "POST",
				dataType : "json",
				data:  ko.mapping.toJSON(self.settings),
				success : function(data) {
					self.showDownload(true)
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
					
					var form = $("<form>");
					var element1 = $("<input>");
					form.attr("method", "POST");
					form.attr("action", "download");

					element1.attr("id", "qwcfilestr");
					element1.attr("name", "qwcfilestr");
					element1.attr("type", "hidden");
					element1.attr("value", data.qbxml);
					
					form.append(element1);
					var body = $(document.body);
					body.append(form);
					form.submit();
				},
				error : function() {
				}
			});
		}
		
	};

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
			"sort" : "position",
			"sSearch":true,
			"sAjaxSource" : "Orders/getPostedOrders?tenantId=" + $("#tenantIdHdn").val() + "&siteId=" + $("#siteIdHdn").val(),
			"aoColumns" : [

			{
				"mData" : "mozuOrderNumber"
			}, {
				"mData" : "quickbooksOrderListId"
			}, {
				"mData" : "customerEmail"
			}, {
				"mData" : "orderDate",
					 "mRender": function (data, type, row) {
					   
					 var myISODate =  new Date(data) ;
					
					      return myISODate.getDate()+'-'+
					      parseInt(myISODate.getMonth())+'-'+myISODate.getFullYear() 
					      +' '+myISODate.getHours()+':'+myISODate.getMinutes()
					      +':'+myISODate.getSeconds();
					   }
			}, 
			{
				"mData" : "orderUpdatedDate",
				"mRender": function (data, type, row) {
				    	
				 var myISODate =  new Date(data) ;
			
				      return myISODate.getDate()+'-'+
				      parseInt(myISODate.getMonth())+'-'+myISODate.getFullYear() 
				      +' '+myISODate.getHours()+':'+myISODate.getMinutes()
				      +':'+myISODate.getSeconds();
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
			"sort" : "position",
			"sSearch":true,
			"sAjaxSource" : "Orders/getConflictOrders?tenantId=" + $("#tenantIdHdn").val() + "&siteId=" + $("#siteIdHdn").val(),
			"aoColumns" : [

			            {    
		            	   "mData": "mozuOrderNumber",
		            	   "bSearchable": false,
		            	   "bSortable": false,
		            	   "mRender": function (data, type, full) {			
		            		   return '<input type="checkbox" id="allOrdersCheckbox' + data + '" name="allOrdersCheckbox" value ="'+ data +'" />';
			            }
			    },
			    {
			    	"mData" : "mozuOrderNumber"
			    }, 
				{
					"mData" : "customerEmail"
				}, 
				{
					"mData" : "orderDate",
						"mRender": function (data, type, row) {
					   
							var myISODate =  new Date(data) ;
					
						      return myISODate.getDate()+'-'+
						      parseInt(myISODate.getMonth())+'-'+myISODate.getFullYear() 
						      +' '+myISODate.getHours()+':'+myISODate.getMinutes()
						      +':'+myISODate.getSeconds();
						}
				}, 
				{
					"mData" : "orderUpdatedDate",
					"mRender": function (data, type, row) {
					    	
					 var myISODate =  new Date(data) ;
				
					      return myISODate.getDate()+'-'+
					      parseInt(myISODate.getMonth())+'-'+myISODate.getFullYear() 
					      +' '+myISODate.getHours()+':'+myISODate.getMinutes()
					      +':'+myISODate.getSeconds();
					}
				},
				{
					"mData" : "amount"
				},
				{    
				   //"mData": "conflictReason",
				    "mData": "mozuOrderNumber",
				    "bSearchable": false,
				    "bSortable": false,
				    "mRender": function (data, type, row) {
				    	var dataId = data ;
				    	return "<a href='javascript:funEdit(" + row.mozuOrderNumber + ")'>Edit</a>";
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
			"sort" : "position",
			"sSearch":true,
			"sAjaxSource" : "Orders/getUpdatedOrders?tenantId=" + $("#tenantIdHdn").val() + "&siteId=" + $("#siteIdHdn").val(),
			"aoColumns" : [

			            {    
		            	   "mData": "mozuOrderNumber",
		            	   "bSearchable": false,
		            	   "bSortable": false,
		            	   "mRender": function (data, type, full) {			
		            		   return '<input type="checkbox" id="allOrdersCheckbox' + data 
		            		   		+ '" name="allOrdersCheckbox" value ="'+ data +'"' + 
		            		   		' data-bind="click: maintainCBStateInArray"/>';
		            	   }
			            },
			            {
			            	"mData" : "mozuOrderNumber"
			            }, 
						{
							"mData" : "customerEmail"
						}, 
						{
							"mData" : "orderDate",
								"mRender": function (data, type, row) {
							   
									var myISODate =  new Date(data) ;
							
								      return myISODate.getDate()+'-'+
								      parseInt(myISODate.getMonth())+'-'+myISODate.getFullYear() 
								      +' '+myISODate.getHours()+':'+myISODate.getMinutes()
								      +':'+myISODate.getSeconds();
								}
						}, 
						{
							"mData" : "orderUpdatedDate",
							"mRender": function (data, type, row) {
							    	
							 var myISODate =  new Date(data) ;
						
							      return myISODate.getDate()+'-'+
							      parseInt(myISODate.getMonth())+'-'+myISODate.getFullYear() 
							      +' '+myISODate.getHours()+':'+myISODate.getMinutes()
							      +':'+myISODate.getSeconds();
							}
						},
						{
							"mData" : "amount"
						},
						{    
						   //"mData": "conflictReason",
						    "mData": "mozuOrderNumber",
						    "bSearchable": false,
						    "bSortable": false,
						    "mRender": function (data, type, row) {
						    	var dataId = data ;
						    	return "<a href='javascript:compareDetails(" + row.mozuOrderNumber + ")'>Review</a>";
						   }
						}
			]
		});
		$table.fnDraw();
	};
	
	self.getSettings = function() {
		$.ajax({
				contentType: 'application/json; charset=UTF-8',
				url : "getgeneralsettings?tenantId=" + $("#tenantIdHdn").val(),
				type : "GET",
				dataType : "json",
				success : function(data) {
					ko.mapping.fromJS(data, self.settings);
					ko.applyBindings(window.homeViewModel);

					
					if ($("#selectedTab").val() != "") {
						$("#"+$("#selectedTab").val()+"Tab").click();
					} else {
						window.homeViewModel.getVersion();
					}
				},
				error : function() {
					$("#content").hide();
				}
			});		
	};


	self.getSettings();
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


$(function() {

	
	function closeError() {
		$("#serverError").hide();
	}

	$.ajaxPrefilter(function(options, originalOptions, jqXHR) {
		console.log(originalOptions);
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

	window.homeViewModel = new homeViewModel();
	

});
