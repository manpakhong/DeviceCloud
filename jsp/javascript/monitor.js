var currentLittleCloudVersion = "LittleCloudJDBC_2.0.21.3";
var messageTypeAutoCompleteObjects = [];
var messageTypeArray;
var PASSWORD = "abc778899";
var is_back_end_password_verified = true;
var PARAM_TYPE_CAPTIVE_PORTAL = "CAPTIVE_PORTAL";
var PARAM_TYPE_FIFO_REPLAY = "FIFO_REPLAY";
var current_server_name;
$(document).ready(function() {
	init();
	current_server_name = getCurrentDocumentUrlServer();
	$(".alinkCaptivePortal").click(function(e) {
		if (!is_back_end_password_verified){
			if (verifiedPassword()){
				var dialog = createCaptivePortalModalDialog(e);
				dialog.dialog("open");	
			}
		} else {
			collectPasswordInputNPost(e, PARAM_TYPE_CAPTIVE_PORTAL);			
		}
	});
	
	$(".alinkFifoReplay").click(function(e){
		if (!is_back_end_password_verified){
			if (verifiedPassword()){
				var fifoDialog = createFifoReplayModalDialog(e);
				fifoDialog.dialog("open");
				if (messageTypeArray == undefined){
					post2GetMessageTypes(e);
				} else {
					refreshAutoComplete();	
				}
			}
		} else {
			collectPasswordInputNPost(e, PARAM_TYPE_FIFO_REPLAY);
		}
	});
	
	$(".tg a").click(function(e){
		$("#f2_url").css("display", "inline");
		$("#f2").css("display", "inline");
		$("#f3_url").css("display", "inline");
		$("#f3").css("display", "inline");
		$("#f1_url").css("width", "32%");
		$("#f1").css("width", "32%");
	});
	
});


function refreshAutoComplete(){
    destroyMultipleAutoCompleted();
    createMultipleAutoCompleted();
}
function init(){
	appendCaptivePortalDialog();
	appendFifoReplayDialog();
	createDatePicker();
}

function destroyMultipleAutoCompleted(){
	for (var i=0; i < messageTypeAutoCompleteObjects.length; i++){
		var obj = messageTypeAutoCompleteObjects[i];
		obj.autocomplete("destroy");
	}
	messageTypeAutoCompleteObjects = [];
}

function createMultipleAutoCompleted(){
	$(".fifoReplayDialog .message_type").each(function(){
		var autocomplete = $(this).autocomplete({source: messageTypeArray});
		messageTypeAutoCompleteObjects.push(autocomplete);
	});
}
function isThatUrl(_url){
	var isThatUrl = false;
	if (current_server_name != undefined){
		var n = _url.indexOf(current_server_name);
		if (n != -1){
			isThatUrl = true;
		}
	}
	return isThatUrl;
}

function post2GetMessageTypes(_e){
	var f1Url = "http://"+serverType.value+"-1"+server.value+":8080/LittleCloud/monitor/fiforeplay";
	var f2Url = "http://"+serverType.value+"-2"+server.value+":8080/LittleCloud/monitor/fiforeplay";
	var f3Url = "http://"+serverType.value+"-3"+server.value+":8080/LittleCloud/monitor/fiforeplay";
	var url = f1Url;
	if (isThatUrl(f2Url)){
		url = f2Url;
	}
	if (isThatUrl(f3Url)){
		url = f3Url;
	}
	var dataString = '{}';
	
	if (_e.altKey){
		f1Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay";
		f2Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay";
		f3Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay";
	}
	
	$.post(url + '?action_type=ACTION_TYPE_GET_MESSAGE_TYPES', { data: dataString }, getMessageTypesResultHandler, "text");
}

function getMessageTypesResultHandler(res){
	var obj = JSON.parse(res);
//	if (obj != 'undefined'){
//		obj.forEach(function (entry){
//			alert(entry);
//		})
//	}
	messageTypeArray = obj;
	refreshAutoComplete();
}

function createFifoReplayModalDialog(_e){
	var f1Url = "http://"+serverType.value+"-1"+server.value+":8080/LittleCloud/fifo_replay.jsp";
	var f2Url = "http://"+serverType.value+"-2"+server.value+":8080/LittleCloud/fifo_replay.jsp";
	var f3Url = "http://"+serverType.value+"-3"+server.value+":8080/LittleCloud/fifo_replay.jsp";
	var url = f1Url;
	if (isThatUrl(f2Url)){
		url = f2Url;
	}
	if (isThatUrl(f3Url)){
		url = f3Url;
	}
	if (_e.altKey){
		f1Url = "http://localhost:8080/" + currentLittleCloudVersion +"/fifo_replay.jsp";
		f2Url = "http://localhost:8080/" + currentLittleCloudVersion +"/fifo_replay.jsp";
		f3Url = "http://localhost:8080/" + currentLittleCloudVersion +"/fifo_replay.jsp";
	}
	
	//var links = [f1Url, f2Url];
	var links = [url];
	var dialog = $( ".fifoReplayDialog" ).dialog({
        autoOpen: false,
        height: 568,
        width: 1154,
        modal: true,
        dialogClass: "fifoReplayDialog",
        buttons: {
          "submit": function(e) {
        	 fifoReplayDialogClick(e, links);
            $(this).dialog("destroy");
            destroyMultipleAutoCompleted();
          },
          Cancel: function() {
            $(this).dialog("destroy");
            destroyMultipleAutoCompleted();
          }
        },
        close: function() {
        	$(this).dialog("destroy");
            destroyMultipleAutoCompleted();
        }
      });
	return dialog;
	
}

function createCaptivePortalModalDialog(_e){
	var f1Url = "http://"+serverType.value+"-1"+server.value+":8080/LittleCloud/captive_portal.jsp";
	var f2Url = "http://"+serverType.value+"-2"+server.value+":8080/LittleCloud/captive_portal.jsp";	
	var f3Url = "http://"+serverType.value+"-3"+server.value+":8080/LittleCloud/captive_portal.jsp";	
	var url = f1Url;
	if (isThatUrl(f2Url)){
		url = f2Url;
	}
	if (isThatUrl(f3Url)){
		url = f3Url;
	}
	if (_e.altKey){
		f1Url = "http://localhost:8080/" + currentLittleCloudVersion +"/captive_portal.jsp";
		f2Url = "http://localhost:8080/" + currentLittleCloudVersion +"/captive_portal.jsp";
		f3Url = "http://localhost:8080/" + currentLittleCloudVersion +"/captive_portal.jsp";
	}

	//var links = [f1Url, f2Url];
	var links = [url];
	var dialog = $( ".captivePortalDialog" ).dialog({
        autoOpen: false,
        height: 568,
        width: 1154,
        modal: true,
        dialogClass: "captivePortalDialog",
        buttons: {
          "submit": function(e) {
        	 captivePortalLinkClick(e, links);
            $(this).dialog("destroy");

          },
          Cancel: function() {
            $(this).dialog("destroy");

          }
        },
        close: function() {
          $(this).dialog("destroy");

        }
      });
	return dialog;
}

function createDatePicker(){
	 $( ".cDateFrom" ).datepicker({
		 defaultDate: "+1w",
		 changeMonth: true,
		 numberOfMonths: 3,
		 onClose: function( selectedDate ) {
			 $( ".cDateTo" ).datepicker( "option", "minDate", selectedDate );
		 }
	 });
	 $( ".cDateTo" ).datepicker({
		 defaultDate: "+1w",
		 changeMonth: true,
		 numberOfMonths: 3,
		 onClose: function( selectedDate ) {
			 $( ".cDateFrom" ).datepicker( "option", "maxDate", selectedDate );
		 }
	});
}

var dynamicMessageTypeHtml = 				
	'<tr>' +
		'<td>' +
		'<label for="message_type">message type</label>' +
	'</td>' +
	'<td>' +
		'<input type="text" class="dataClass2 message_type messageType" name="message_type" value="" />' +
		'<input type="button" class="normalButton messageTypeAddBtn" value="Add" onclick="addMessageTypeHtml(event)" />' +
		'<input type="button" class="normalButton messageTypeDeleteBtn" value="Delete" onclick="delMessageTypeHtml(event)" />' +
		'<br/>' +
	'</td>' +
	'</tr>';
function addMessageTypeHtml(_e){
	var obj = _e.target;
	var tabObj = $(obj).parent().parent().parent();
	if (tabObj != undefined){
		tabObj.append(dynamicMessageTypeHtml);
	}
	
	refreshAutoComplete();
}
function delMessageTypeHtml(_e){
    destroyMultipleAutoCompleted();
	var obj = _e.target;
	var trObj = $(obj).parent().parent();
	if (trObj != undefined){
		trObj.remove();
	}
    createMultipleAutoCompleted();
}

function verifiedPassword(){
	var is_verified = false;
	var pass1 = $("#pass").val();
	var pass2 = $("#passadmin").val();
	if (pass1.length > 0 && pass2.length > 0){
		if (pass1 == PASSWORD && pass2 == PASSWORD){
			is_verified = true;
		}
	}
	if (!is_verified){
		alert("password not correct!");
	}
	
	return is_verified;
}

function collectPasswordInputNPost(_e, _param_type){
	var pass1 = $("#pass").val();
	var pass2 = $("#passadmin").val();
	if (pass1.length > 0 && pass2.length > 0){
		if (pass1 != pass2){
			alert("password and password2 not matched!");
		} else {
			var parameterObj = createParametersObject();
			parameterObj.password = pass1;
			parameterObj.type = _param_type;
			post2DoBackEndPasswordVerification(_e, parameterObj);
		}
	} else {
		alert("password and password2 must have data input!");
	}
	
}

function createParametersObject(){
	var parameterObj = {};
	parameterObj.password = "";
	parameterObj.type = PARAM_TYPE_CAPTIVE_PORTAL;
	return parameterObj;
}
var event = undefined;
function post2DoBackEndPasswordVerification(_e, _parametersObj){
	var f1Url = "http://"+serverType.value+"-1"+server.value+":8080/LittleCloud/monitor/password_check";
	var f2Url = "http://"+serverType.value+"-2"+server.value+":8080/LittleCloud/monitor/password_check";	
	var f3Url = "http://"+serverType.value+"-3"+server.value+":8080/LittleCloud/monitor/password_check";	
	var url = f1Url;
	
	if (isThatUrl(f2Url)){
		url = f2Url;
	}
	if (isThatUrl(f3Url)){
		url = f3Url;
	}
	var data = JSON.stringify(_parametersObj, null, 2);
	var dataString = '{' + '"parameters":' + data +'}';
	
	if (_e.altKey){
		f1Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/password_check";
		f2Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/password_check";
		f3Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/password_check";
	}
	event = _e;
	
	if(_parametersObj.type == PARAM_TYPE_CAPTIVE_PORTAL){
		$.post(url + '?action_type=ACTION_TYPE_CHECK_PASSWORD', { data: dataString }, doBackEndPasswordVerificationCaptivePortal, "text");
	} else if(_parametersObj.type == PARAM_TYPE_FIFO_REPLAY){
		$.post(url + '?action_type=ACTION_TYPE_CHECK_PASSWORD', { data: dataString }, doBackEndPasswordVerificationFifoReplay, "text");
	} else {
		alert("Please specify parameterObj.type!!!");
	}
}
function doBackEndPasswordVerificationFifoReplay(res){
	var obj = JSON.parse(res);
	var isPasswordCorrected = false;
	if (obj != undefined && obj.isPasswordCorrected != undefined){
		isPasswordCorrected = obj.isPasswordCorrected;
	}
	if (isPasswordCorrected){
		var fifoDialog = createFifoReplayModalDialog(event);
		fifoDialog.dialog("open");
		if (messageTypeArray == undefined){
			post2GetMessageTypes(event);
		} else {
			refreshAutoComplete();	
		}
	} else {
		alert("Password is not corrected!!!");
	}
}
function doBackEndPasswordVerificationCaptivePortal(res){
	var obj = JSON.parse(res);
	var isPasswordCorrected = false;
	if (obj != undefined && obj.isPasswordCorrected != undefined){
		isPasswordCorrected = obj.isPasswordCorrected;
	}
	if (isPasswordCorrected){
		if (event != undefined){
			var dialog = createCaptivePortalModalDialog(event);
			dialog.dialog("open");	
		}
	} else {
		alert("Password is not corrected!!!");
	}
}

function appendFifoReplayDialog(){
	
	// need page include commonUtils.js
	var todayString = getTodayInString();
	var divHtml = 
		'<div class="fifoReplayDialog" title="FIFO Replay Query Parameter(s) Input">' +
			'<form><fieldset>' +
			'<table>' +
				'<tr>' +
					'<td>' +
						'<label for="organizationId">Org Id</label>' +
					'</td>' +
					'<td>' +
						'<input type="text" class="normalInputClass orgId" name="orgId" value="riMA5x" />' + '<br/>' +
					'</td>' +
				'</tr>' +
				'<tr>' +
					'<td>' +
						'<label for="from">From</label>' +
					'</td>' +
					'<td>' +
						'<input type="text" class="dateClass cDateFrom" name="fromDate" value="' + todayString +'"/>' +				
						'<input type="text" class="timeClass cHrFrom" name="hrFrom" value="00"/>:' + 
						'<input type="text" class="timeClass cMinFrom" name="minFrom" value="00" />:' +
						'<input type="text" class="timeClass cSecFrom" name="secFrom" value="00" />' + 
						'<label for="to">to</label>' +
						'<input type="text" class="dateClass cDateTo" name="toDate"  value="' + todayString +'"/>' +
						'<input type="text" class="timeClass cHrTo" name="hrTo" value="23" />:' + 
						'<input type="text" class="timeClass cMinTo" name="minTo" value="59" />:' +
						'<input type="text" class="timeClass cSecTo" name="secTo" value="59" />' + 	'<br/>' +
					'</td>' +
				'</tr>' +
				'<tr>' +
					'<td>' +
						'<label for="network_id">Network Id</label>' +
					'</td>' +
					'<td>' +
						'<input type="text" class="dataClass networkId" name="networkId" value="" />' + '<br/>' +
					'</td>' +
				'</tr>' +
				'<tr>' +
					'<td>' +
						'<label for="sn">sn</label>' +
					'</td>' +
					'<td>' +
						'<input type="text" class="dataClass sn" name="sn" value="" />' + '<br/>' +
					'</td>' +
				'</tr>' +
					dynamicMessageTypeHtml +
				'</div>' +
			'</table>' +
			'</fieldset></form>' +
		'</div>';
	var isExisted = $(".fifoReplayDialog").length;
	if (!isExisted){
		$("body").append(divHtml);		
		$(".fifoReplayDialog").hide();
	}
}

function appendCaptivePortalDialog(){
	
	// need page include commonUtils.js
	var todayString = getTodayInString();
	
	var divHtml = 
		'<div class="captivePortalDialog" title="Captive Portal Query Parameter(s) Input">' +
			'<form><fieldset>' +
				'<table>' +
				'<tr><td>' +
				'<label for="organizationId">Org Id</label>' +
				'</td>' +
				'<td>' +
				'<input type="text" class="normalInputClass orgId" name="orgId" value="riMA5x" />' + '<br/>' +
				'</td></tr>' +
				'<tr><td>' +
				'<label for="from">From</label>' +
				'</td>' +
				'<td>' +
				'<input type="text" class="dateClass cDateFrom" name="fromDate" value="' + todayString +'"/>' +
				'<input type="text" class="timeClass cHrFrom" name="hrFrom" value="00"/>:' + 
				'<input type="text" class="timeClass cMinFrom" name="minFrom" value="00" />:' +
				'<input type="text" class="timeClass cSecFrom" name="secFrom" value="00" />' + 
				'<label for="to">to</label>' +
				'<input type="text" class="dateClass cDateTo" name="toDate"  value="' + todayString +'"/>' +
				'<input type="text" class="timeClass cHrTo" name="hrTo" value="23" />:' + 
				'<input type="text" class="timeClass cMinTo" name="minTo" value="59" />:' +
				'<input type="text" class="timeClass cSecTo" name="secTo" value="59" />' + 	'<br/>' +
				'</td></tr>' +
				'<tr><td>' +
				'<label for="lblSsid">Ssid</label>' +
				'</td>' +
				'<td>' +
				'<input type="text" class="dataClass cSsid" name="ssid" value="" />' + '<br/>' +
				'</td></tr>' +
				'<tr><td>' +
				'<label for="lblMac">MAC</label>' +
				'</td>' +
				'<td>' +
				'<input type="text" class="dataClass cMac" name="mac" value="" />' +
				'</td>' +
				'</tr>' +
				'</table>' +
			'</fieldset></form>' +
		'</div>';
	var isExisted = $(".captivePortalDialog").length;
	if (!isExisted){
		$("body").append(divHtml);
		$(".captivePortalDialog").hide();
	}
}
function captivePortalLinkClick(_e, _links){
	var cDateFrom = $(".captivePortalDialog .cDateFrom").val();
	var cHrFrom = $(".captivePortalDialog .cHrFrom").val();
	var cMinFrom = $(".captivePortalDialog .cMinFrom").val();
	var cSecFrom = $(".captivePortalDialog .cSecFrom").val();
	var cDateTo = $(".captivePortalDialog .cDateTo").val();
	var cHrTo = $(".captivePortalDialog .cHrTo").val();
	var cMinTo = $(".captivePortalDialog .cMinTo").val();
	var cSecTo = $(".captivePortalDialog .cSecTo").val();
	var orgId = $(".captivePortalDialog .orgId").val();
	
	var ssid = $(".captivePortalDialog .cSsid").val();
	var mac = $(".captivePortalDialog .cMac").val();
	

	
	var createAtFrom = cDateFrom + " " + cHrFrom + ":" + cMinFrom + ":" + cSecFrom;
	var createAtTo = cDateTo + " " + cHrTo + ":" + cMinTo + ":" + cSecTo;
	
	var paramString = "?createAtFrom=" + createAtFrom + "&createAtTo=" + createAtTo + "&orgId=" + orgId;
	
	if (ssid.length > 0){
		paramString += "&ssid=" + ssid;
	}
	
	if (mac.length > 0){
		paramString += "&mac=" + mac;
	}
	
	for (var i=0; i< _links.length; i++){
		$("#f" + (i + 1)).attr('src', _links[i] + paramString);
		$("#f" + (i + 1) + "_url").val(_links[i] + paramString);		
	}
	$("#f2_url").css("display", "none");
	$("#f2").css("display", "none");
	$("#f3_url").css("display", "none");
	$("#f3").css("display", "none");
	$("#f1_url").css("width", "100%");
	$("#f1").css("width", "100%");
}

function fifoReplayDialogClick(_e, _links){

	var cDateFrom = $(".fifoReplayDialog .cDateFrom").val();
	var cHrFrom = $(".fifoReplayDialog .cHrFrom").val();
	var cMinFrom = $(".fifoReplayDialog .cMinFrom").val();
	var cSecFrom = $(".fifoReplayDialog .cSecFrom").val();
	var cDateTo = $(".fifoReplayDialog .cDateTo").val();
	var cHrTo = $(".fifoReplayDialog .cHrTo").val();
	var cMinTo = $(".fifoReplayDialog .cMinTo").val();
	var cSecTo = $(".fifoReplayDialog .cSecTo").val();
	var orgId = $(".fifoReplayDialog .orgId").val();

	var networkId = $(".fifoReplayDialog .networkId").val();
	var sn = $(".fifoReplayDialog .sn").val();
		
	var createAtFrom = cDateFrom + " " + cHrFrom + ":" + cMinFrom + ":" + cSecFrom;
	var createAtTo = cDateTo + " " + cHrTo + ":" + cMinTo + ":" + cSecTo;
	
	var paramString = "?createAtFrom=" + createAtFrom + "&createAtTo=" + createAtTo + "&orgId=" + orgId;
	var indexCount = 0;
	$(".fifoReplayDialog .messageType").each(function(){
		var valString = $(this).val();
		if (valString.length > 0){
			paramString += "&messageType=" + valString;
			indexCount += 1;
		}
	});
	
	if (networkId.length > 0){
		paramString += "&networkId=" + networkId;
	}
	
	if (sn.length > 0){
		paramString += "&sn=" + sn;
	}
	
	for (var i=0; i< _links.length; i++){
		$("#f" + (i + 1)).attr('src', _links[i] + paramString);
		$("#f" + (i + 1) + "_url").val(_links[i] + paramString);		
	}
	$("#f2_url").css("display", "none");
	$("#f2").css("display", "none");
	$("#f3_url").css("display", "none");
	$("#f3").css("display", "none");
	$("#f1_url").css("width", "100%");
	$("#f1").css("width", "100%");
}