var homeViewModel = function() {
	var self = this;
	var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	self.buildVersion = ko.observable();

	self.save = function() {
		//identify which is the active tab
		
		var selectedTab;
		$(".tab").each(function(index) {
			if($(this).attr('class').indexOf(" active") > -1) {
				selectedTab = $(this).data("tab-id");
			}
		});

		if("generalTab" === selectedTab) {
			
			var dataObj = {};
			dataObj.wsURL = $("#wsUrl").val();
			dataObj.qbAccount = $("#qbUsername").val();
			dataObj.qbPassword = $("#qbPassword").val();
			dataObj.accepted = $("#acceptedCb").prop('checked');
			dataObj.completed = $("#completedCb").prop('checked');
			dataObj.cancelled = $("#cancelledCb").prop('checked');
			
			$.ajax({
				contentType: 'application/json; charset=UTF-8',
				url : "generalsettings?tenantId=" + $("#tenantIdHdn").text(),
				type : "POST",
				dataType : "json",
				data: JSON.stringify(dataObj),
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
					alert("hide");
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

	self.getPostedOrders = function() {
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

	}

	ko.bindingHandlers.chosen = {
		update : function(element) {
			$(element).chosen({
				width : "95%"
			});
			$(element).trigger('liszt:updated');
		}
	};

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

	function closeError() {
		$("#serverError").hide();
	}

}

$(function() {

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
		//For general settings, load the settings
		if("generalTab" === newTabId) {
			$.ajax({
				contentType: 'application/json; charset=UTF-8',
				url : "getgeneralsettings?tenantId=" + $("#tenantIdHdn").text(),
				type : "GET",
				dataType : "json",
				success : function(dataObj) {
					$("#wsUrl").val(dataObj.wsURL);
					$("#qbUsername").val(dataObj.qbAccount);
					$("#qbPassword").val(dataObj.qbPassword);
					$("#acceptedCb").prop('checked', dataObj.accepted);
					$("#completedCb").prop('checked', dataObj.completed);
					$("#cancelledCb").prop('checked', dataObj.cancelled);
				},
				error : function() {
					$("#content").hide();
				}
			});
		}
	});

	window.homeViewModel = new homeViewModel();
	ko.applyBindings(window.homeViewModel);

	window.homeViewModel.getVersion();

});
