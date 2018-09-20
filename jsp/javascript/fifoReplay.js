var server = '';
var currentLittleCloudVersion = "LittleCloudJDBC_2.0.21.3";
$(document).ready(function() {
	init();
});
var overlay;
function init(){
	$(".fifoReplayButton").click(function(e){
		$(".fifoReplayResult").text("processing ...");
		fifoReplayButtonClick(e);
        overlay = new ItpOverlay();
        overlay.show("body");
            
	});
	$(".fifoExport2TextFileButton").click(function(e){
        overlay = new ItpOverlay();
        overlay.show("body");
		fifoExport2TextFileButtonClick(e);	
        setTimeout ( 
                function(){
             	   overlay.hide("body");
                }
             , 20000 );
		});
	
	 var icons = {
			 header: "ui-icon-circle-arrow-e",
			 activeHeader: "ui-icon-circle-arrow-s"
			 };
	$(".resultContentDiv1").accordion({
		icons: icons,
		collapsible: true
	});
	$(".resultContentDiv").accordion({
		icons: icons,
		collapsible: true
	});	
		
	server = readCookie("mon_server");
}
function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function fifoReplayButtonClick(_e){
	var cDateFrom = $(".fTimestampFrom").val();
	var cDateTo = $(".fTimestampTo").val();
	var orgId = $(".fOrgId").val();
	var networkId = $(".fNetworkId").val();
	var sn = $(".fSn").val();

	var parametersObj = createParametersObject();
	
	$(".fMessageType").each(function(){
		var valString = $(this).val();
		parametersObj.messageTypeList.push(valString);
	});
	
	parametersObj.createAtFrom = cDateFrom;
	parametersObj.createAtTo = cDateTo;
	parametersObj.orgId = orgId;
	parametersObj.networkId = networkId;
	parametersObj.sn = sn;
	
	post2DoFifoReplay(_e, parametersObj);
}

function fifoExport2TextFileButtonClick(_e){

	var cDateFrom = $(".fTimestampFrom").val();
	var cDateTo = $(".fTimestampTo").val();
	var orgId = $(".fOrgId").val();
	var networkId = $(".fNetworkId").val();
	var sn = $(".fSn").val();

	var parametersObj = createParametersObject();
	
	$(".fMessageType").each(function(){
		var valString = $(this).val();
		parametersObj.messageTypeList.push(valString);
	});
	
	parametersObj.createAtFrom = cDateFrom;
	parametersObj.createAtTo = cDateTo;
	parametersObj.orgId = orgId;
	parametersObj.networkId = networkId;
	parametersObj.sn = sn;

	get2RetrieveTextFile(_e, parametersObj);
}

function post2DoFifoReplay(_e, _parametersObj){
	var f1Url = "http://"+serverType.value+"-1"+server+":8080/LittleCloud/monitor/fiforeplay_do";
	var f2Url = "http://"+serverType.value+"-2"+server+":8080/LittleCloud/monitor/fiforeplay_do";	
	var f3Url = "http://"+serverType.value+"-3"+server+":8080/LittleCloud/monitor/fiforeplay_do";	
	var data = JSON.stringify(_parametersObj, null, 2);
	var dataString = '{' + '"parameters":' + data +'}';
	
	if (_e.altKey){
		f1Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay_do";
		f2Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay_do";	
		f3Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay_do";	
	}
	
	$.post(f2Url + '?action_type=ACTION_TYPE_DO_FIFO_REPLAY', { data: dataString }, doFifoReplayResult, "text");
}
function doFifoReplayResult(res){
	var obj = JSON.parse(res);
	var noOfMessage = 0;
	if (obj != undefined && obj.noOfMsgReplay != undefined){
		noOfMessage = obj.noOfMsgReplay;
	}
	$(".fifoReplayResult").text("no of messages done:" + noOfMessage);
	overlay.hide("body");
}

function createParametersObject(){
	var parameterObj = {};
	parameterObj.createAtFrom = '';
	parameterObj.createAtTo = '';
	parameterObj.orgId = '';
	parameterObj.networkId = '';
	parameterObj.sn = '';
	parameterObj.messageTypeList = [];
	return parameterObj;
}

function get2RetrieveTextFile(_e, _parametersObj){
	var f1Url = "http://"+serverType.value+"-1"+server+":8080/LittleCloud/monitor/fiforeplay_gettext";
	var f2Url = "http://"+serverType.value+"-2"+server+":8080/LittleCloud/monitor/fiforeplay_gettext";	
	var f3Url = "http://"+serverType.value+"-3"+server+":8080/LittleCloud/monitor/fiforeplay_gettext";	
//	var data = JSON.stringify(_parametersObj, null, 2);
//	var dataString = '{' + '"parameters":' + data +'}';
	
	if (_e.altKey){
		f1Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay_gettext";
		f2Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay_gettext";
		f3Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay_gettext";
	}
	
	var createAtFrom = _parametersObj.createAtFrom;
	var createAtTo = _parametersObj.createAtTo;
	
	var paramString = "?action_type=ACTION_TYPE_GET_CAPWAP_TEXT_FILE&createAtFrom=" + createAtFrom + "&createAtTo=" + createAtTo + "&orgId=" + _parametersObj.orgId;

	if ( _parametersObj.messageTypeList != undefined){
		for (var i = 0; i < _parametersObj.messageTypeList.length; i++){
			if (_parametersObj.messageTypeList[i].length > 0){
				paramString += "&messageType=" + _parametersObj.messageTypeList[i];
			}
		}
	}
		
	if (_parametersObj.networkId != undefined && _parametersObj.networkId.length > 0){
		paramString += "&networkId=" + _parametersObj.networkId;
	}
	
	if (_parametersObj.sn != undefined && _parametersObj.sn.length > 0){
		paramString += "&sn=" + _parametersObj.sn;
	}

	document.location.href = f2Url + paramString;

}
function post2RetrieveTextFile(_e, _parametersObj){
	var f1Url = "http://"+serverType.value+"-1"+server+":8080/LittleCloud/monitor/fiforeplay_gettext";
	var f2Url = "http://"+serverType.value+"-2"+server+":8080/LittleCloud/monitor/fiforeplay_gettext";	
	var f3Url = "http://"+serverType.value+"-3"+server+":8080/LittleCloud/monitor/fiforeplay_gettext";	
	var data = JSON.stringify(_parametersObj, null, 2);
	var dataString = '{' + '"parameters":' + data +'}';
	
	if (_e.altKey){
		f1Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay_gettext";
		f2Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay_gettext";	
		f3Url = "http://localhost:8080/" + currentLittleCloudVersion +"/monitor/fiforeplay_gettext";	
	}
	
	$.post(f2Url + '?action_type=ACTION_TYPE_GET_CAPWAP_TEXT_FILE', { data: dataString }, getAllMessageTextFileResult, "text");
}

function getAllMessageTextFileResult(res){
	//var obj = JSON.parse(res);
//	if (obj != undefined){
//		obj.forEach(function (entry){
//			alert(entry);
//		})
//	}
	window.location.href = res; 
	messageTypeArray = obj;
	refreshAutoComplete();
}