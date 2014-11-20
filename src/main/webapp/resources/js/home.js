function funEdit(orderNumber) {
	$('#ordConflict').hide().fadeOut(800);
	$('#ordConflictError').show().fadeIn(800);
	$("#conflictSuccessDiv").hide();
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
			
			//viewModel.itemToFix( new qbItem(""));
			viewModel.showItemCreate(false);
			viewModel.showItemMap(false);
			//$("#singleErrorDisplay").dataTable().fnDestroy();
			//var $table = $('#singleErrorDisplay').dataTable({ retrieve: true,bDestroy:true, bFilter: false, bInfo: false, bPaginate:false, bDestroy	: true});
			//$table.fnDraw();
			
			
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
    self.itemPurchaseCost = ko.observable().extend({ numeric: 2 });
    self.itemSalesDesc = ko.observable("");
    self.itemSalesPrice = ko.observable().extend({ numeric: 2 });
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


ko.extenders.numeric = function(target, precision) {
    //create a writable computed observable to intercept writes to our observable
    var result = ko.computed({
        read: target,  //always return the original observable's value
        write: function(newValue) {
            var current = target(),
                roundingMultiplier = Math.pow(10, precision),
                newValueAsNum = isNaN(newValue) ? 0 : parseFloat(+newValue),
                valueToWrite = Math.round(newValueAsNum * roundingMultiplier) / roundingMultiplier;
 
            //only write if it changed
            if (valueToWrite !== current) {
                target(valueToWrite);
            } else {
                //if the rounded value is the same, but a different value was written, force a notification for the current field
                if (newValue !== current) {
                    target.notifySubscribers(valueToWrite);
                }
            }
        }
    }).extend({ notify: 'always' });
 
    //initialize with current value to make sure it is rounded appropriately
    result(target());
 
    //return the new computed observable
    return result;
};


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
    
    //Check if a product job is currently running
    self.isJobCurrentlyRunning = ko.observable(false);
    
    self.showItemOptions = ko.computed(function() {
        return self.itemToFix.itemNameNumber() != "" ;
    }, self);
    
    self.productCodeAutoComplete = function() {
    	$( "#qbproductsearch" ).autocomplete({
    	      source: function (request, response) {
    	    	  
    	    	  displaySpinner = false;
    	          $.get("api/qb/getProductCodes", {
    	        	  productCodeTerm: request.term, 
    	        	  tenantId: $("#tenantIdHdn").val()
    	          }, function (data) {
    	              response(data);
    	          });
    	      },
    	      minLength: 2
    	});
    	return true;
    };
    
    self.enableNewItem = function() {
    	self.showItemCreate(true);
    	self.showItemMap(false);
    	$("#conflictSuccessDiv").hide();
    	return true;
    };
    
    self.enableExistingItem = function() {
    	self.showItemCreate(false);
    	self.showItemMap(true);
    	$("#conflictSuccessDiv").hide();
    	
    	//Also get the status of any existing Product job in running status for display.
    	self.getProductRefreshStatus();
    	return true;
    }
    
    self.getProductRefreshStatus = function() {
    	$.ajax({
			contentType: 'application/json; charset=UTF-8',
			url : "api/qb/getProductRefreshStatus?tenantId=" + $("#tenantIdHdn").val(),
			type : "GET",
			dataType : "json",
			success : function(data) {
				console.log(data.jobStatus);
				if(data.jobStatus) {
					self.isJobCurrentlyRunning(true);
				} else {
					self.isJobCurrentlyRunning(false);
				}
			},
			error : function () {
				
			}
		});
    }
    
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
			}
		});
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
								

						self.mozuPayments.push(new mozuPayment("Amex", "American Express") );
						self.mozuPayments.push(new mozuPayment("Check", "Check"));
						self.mozuPayments.push(new mozuPayment("Discover", "Discover"));
						self.mozuPayments.push(new mozuPayment("Delta", "Delta"));
						self.mozuPayments.push(new mozuPayment("Diners", "Diners"));
						self.mozuPayments.push(new mozuPayment("Electron", "Electron"));
						self.mozuPayments.push(new mozuPayment("FirePay", "FirePay"));
						self.mozuPayments.push(new mozuPayment("JCB", "JCB"));
						self.mozuPayments.push(new mozuPayment("Laser", "Laser"));
						self.mozuPayments.push(new mozuPayment("Maestro", "Maestro"));
						self.mozuPayments.push(new mozuPayment("MC", "Master Card") );
						self.mozuPayments.push(new mozuPayment("PaypalExpress", "PaypalExpress"));
						self.mozuPayments.push(new mozuPayment("Solo", "Solo"));
						self.mozuPayments.push(new mozuPayment("StoreCredit", "StoreCredit"));
						self.mozuPayments.push(new mozuPayment("Switch", "Switch"));
						self.mozuPayments.push(new mozuPayment("Visa", "Visa") );						
						
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
	
	self.unmapPayment = function(data) {
		for(var i=0;i<self.paymentMappings().length;i++) {
			if (self.paymentMappings()[i].mozuId() == data.mozuId()) {
				self.paymentMappings.remove(self.paymentMappings()[i]);
				break;
			}	
		}
	}
	
	self.mozuPayments = ko.observableArray([]);
	self.selectedMozuPayment = ko.observable();
	self.selectedQBPayment = ko.observable();
	self.paymentMappings = ko.observableArray([]);
	
	self.getSettings();
	
	//initialize the product code autocomplete
	self.productCodeAutoComplete();
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

ko.bindingHandlers.money = { 
	    update: function(element, valueAccessor, allBindingsAccessor) { 
	        var value = valueAccessor(), allBindings = allBindingsAccessor(); 
	        var valueUnwrapped = ko.utils.unwrapObservable(value); 
	        
	        var m = ""; 
	        if (valueUnwrapped) {        
            	 m = numeral(valueUnwrapped).format('$0,0.00'); 
	        }        
	        $(element).text(m);    
	    } 
	}; 


var displaySpinner = true;
var displayError = true;

$(document).ajaxError(function(event, jqxhr, settings, exception) {
	if (jqxhr.status >= 200 && jqxhr.status <= 300)
		return;
	
	if (displayError) {
		if (jqxhr.responseJSON != null)
			$("#serverErrorMessage").html(jqxhr.responseJSON.message);
		else if (jqxhr.responseText != null)
			$("#serverErrorMessage").html(jqxhr.responseText);
		else {
			$("#serverErrorMessage").html(jqxhr.statusText);
		}
		$("#serverError").show();
	}
});



var viewModel;

$(function() {

	
	function closeError() {
		$("#serverError").hide();
	}

	$.ajaxPrefilter(function(options, originalOptions, jqXHR) {
		$("#serverError").hide();
		if (displaySpinner)
			$("#progressIndicator").show();
		jqXHR.complete(function() {
			if (displaySpinner)
				$("#progressIndicator").hide();
			displaySpinner = true;
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