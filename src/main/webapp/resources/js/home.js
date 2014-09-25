function funEdit(orderNumber) {
	$('#ordConflict').hide().fadeOut(800);
	$('#ordConflictError').show().fadeIn(800);
	
	$.ajax({
		url : "getOrderConflictsDetails",
		type : "GET",
		data : {
			"mozuOrderNumber" : orderNumber,
			"tenantId" : $("#tenantIdHdn").text(),
			"siteId"	: $("#siteIdHdn").text()
		},
		dataType : "json",		
		success : function(data) {
			console.log(data);
			console.log(data[0].conflictReason);
			$("#exampleDisplay").html("<ul><li>" + data[0].conflictReason + "</li></ul>")	
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

var homeViewModel = function() {
	var self = this;
	self.buildVersion = ko.observable();
	self.settings = ko.mapping.fromJS(new Object());;
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
				url : "generalsettings?tenantId=" + $("#tenantIdHdn").text(),
				type : "POST",
				dataType : "json",
				data:  ko.mapping.toJSON(self.settings),
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
			"sAjaxSource" : "getPostedOrders?tenantId=" + $("#tenantIdHdn").text() + "&siteId=" + $("#siteIdHdn").text(),
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
	
	self.getOrderConflicts = function() {

		var $table = $('#orderConflictsTable').dataTable({
			"bProcessing" : true,
			"bServerSide" : true,
			"bDestroy"	: true,
			"sort" : "position",
			"sSearch":true,
			"sAjaxSource" : "getConflictOrders?tenantId=" + $("#tenantIdHdn").text() + "&siteId=" + $("#siteIdHdn").text(),
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
			    	"mData" : "quickbooksOrderListId"
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
					"mData" : "conflictReason"
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
	
	self.getSettings = function() {
		$.ajax({
				contentType: 'application/json; charset=UTF-8',
				url : "getgeneralsettings?tenantId=" + $("#tenantIdHdn").text(),
				type : "GET",
				dataType : "json",
				success : function(data) {
					ko.mapping.fromJS(data, self.settings);
					ko.applyBindings(window.homeViewModel);
	
					window.homeViewModel.getVersion();
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
