<html>
<head>
<style type="text/css">
<!--
iframe {
float:left;
margin:20px;
}
-->
</style>
<style type="text/css">

.tg  {border-collapse:collapse;border-spacing:0;margin:5px;width:100%;}
.tf  {border-collapse:collapse;border-spacing:0; width:33%;height:98%;border-style:solid;border-width:1px;margin:1px;}
.tf-text  {border-collapse:collapse;border-spacing:0; width:33%;height:20px;border-style:solid;border-width:1px;margin:1px;color:#999999;}
.tg td{font-family:Arial, sans-serif;font-size:14px;height:10px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;height:10px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
.tg .tg-e3zv{font-weight:bold;}
.tg .tg-bsv2{background-color:#efefef;font-weight:bold;}
.tg .tg-2sfh{font-weight:bold;background-color:#efefef;width:110px;}
.tg .tg-bsv3{background-color:#efafaf;font-weight:bold}
.tg .tg-3sfh{background-color:#efafaf;width:110px;}
.tg .tg-031e{width:110px;}
a, a:link, a:hover, a:visited, a:active {text-decoration: none; color:#1212ff;}
</style>

<style>
html, body {
	margin: 0;
	padding: 0;
	width: 100%;
	height: 100%; 
}

.content {
	min-height: 100%;
	position: relative;
	overflow: auto;
	z-index: 0; 
}

.background {
	position: absolute;
	z-index: -1;
	top: 0;
	bottom: 0;
	margin: 0;
	padding: 0;
}

.top_block {
	width: 100%;
	display: block; 
}

.bottom_block {
	position: absolute;
	width: 100%;
	display: block;
	bottom: 0; 
}

.left_block {
	display: block;
	float: left; 
}

.right_block {
	display: block;
	float: right; 
}

.center_block {
	display: block;
	width: auto; 
}
.url_block {
	display: inline;
	width: auto; 
}
.background.right_menu {
	height: auto !important;
	padding-bottom: 0;
	right: 0;
	width: 16%;
	background-color: #ccddcc; 
}

.right_menu {
	height: auto;
	width: 16%;
	padding-bottom: 0px;
}

.background.main {
	height: auto !important;
	padding-bottom: 0;
	left: 0;
	right: 0;
	background-color: #eeeeee;
	margin-right: 16%; 
}

.main {
	width: auto;
	height: auto;
	padding-bottom: 0px;
}
</style>

<script type="text/javascript" src="javascript/jquery.js"></script>
<script type="text/javascript" src="javascript/jquery-ui.js"></script>
<script type="text/javascript" src="javascript/commonUtils.js"></script>
<link href="css/jquery-ui.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="javascript/monitor.js"></script>
<script type="text/javascript" src="javascript/json2.js"></script>
<link href="css/monitor.css" rel="stylesheet" type="text/css"/>
<script>
function GetIFrameUrl() {
    f1_url.value = document.getElementById("f1").src;
    f2_url.value = document.getElementById("f2").src;
    f3_url.value = document.getElementById("f3").src;
    
    f1_url.value = f1_url.value.replace(/pass=(.*)/, "");
    f2_url.value = f2_url.value.replace(/pass=(.*)/, "");
    f3_url.value = f3_url.value.replace(/pass=(.*)/, "");
}

function GoUrl(fid, uid) {	
	document.getElementById(fid).src=uid.value;	
}

function saveServer() {
	var server = $("#server").val();
	createCookie("mon_server", server, 365);
}

function loadServer() {
	var server = readCookie("mon_server");
	$("#server").val(server);
}

function saveServerType() {
	var serverType = $("#serverType").val();
	createCookie("mon_serverType", serverType, 365);
}

function loadServerType() {
	var serverType = readCookie("mon_serverType");
	$("#serverType").val(serverType);
}

function savePass() {	
	var password = $("#pass").val();
	createCookie("mon_password", password, 365);	
}

function loadPass() {
	var password = readCookie("mon_password");
	$("#pass").val(password);
}

//Cookies
function createCookie(name, value, days) {
	var expires = "";
	    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toGMTString();
    }
    
    document.cookie = name + "=" + value + expires + "; path=/";
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

function eraseCookie(name) {
    createCookie(name, "", -1);
}


function urlChange(type) {
	var serverPath1 = serverType.value+"-1"+server.value;
	var serverPath2 = serverType.value+"-2"+server.value;
	var serverPath3 = serverType.value+"-3"+server.value;
	if (serverType.value == "rs")
	{
		serverPath1 = serverType.value+"1"+server.value;
		serverPath2 = serverType.value+"2"+server.value;
		serverPath3 = serverType.value+"3"+server.value;		
	}
	
	switch(type) {
	case 1:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/queue?pass="+pass.value;
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/queue?pass="+pass.value;
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/queue?pass="+pass.value;
	    break;
	case 2:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/queue?type=cache&pass="+pass.value;
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/queue?type=cache&pass="+pass.value;
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/queue?type=cache&pass="+pass.value;
	    break;
	case 3:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/db";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/db";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/db";
	    break;
	case 4:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/cache?pass="+pass.value;
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/cache?pass="+pass.value;
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/cache?pass="+pass.value;
	    break;
	case 5:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/sysMonitor";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/sysMonitor";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/sysMonitor";
	    break;
	case 6:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/wtp";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/wtp";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/wtp";
	    break;
	case 7:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/DataWs/config?skip=1";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/DataWs/config?skip=1";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/DataWs/config?skip=1";
	    break;
	case 8:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/DataWs/config?skip=0";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/DataWs/config?skip=0";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/DataWs/config?skip=0";
	    break;
	case 9:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/cache?pass="+pass.value+"&dev="+cache_devkey.value;
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/cache?pass="+pass.value+"&dev="+cache_devkey.value;
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/cache?pass="+pass.value+"&dev="+cache_devkey.value;
	    break;
	case 10:
		if(cache_type.value.length<5){
            alert("Type at least 5 characters needed!");
        }
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/cache?pass="+pass.value+"&type="+cache_type.value;
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/cache?pass="+pass.value+"&type="+cache_type.value;
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/cache?pass="+pass.value+"&type="+cache_type.value;
	    break;
	case 11:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/deleteCache?pass="+passadmin.value+"&type="+cache_type1.value;		
	    break;
	case 12:
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/deleteCache?pass="+passadmin.value+"&type="+cache_type2.value;
		break;
	case 13:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/schedule_manager.jsp";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/schedule_manager.jsp";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/schedule_manager.jsp";
		break;
	case 14:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/wtpinfo";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/wtpinfo";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/wtpinfo";
		break;
	case 15:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/captive_portal.jsp";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/captive_portal.jsp";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/captive_portal.jsp";
		break;
	case 16:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/consolidate";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/consolidate";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/consolidate";
		break;
	case 17:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/wsTasksMonitor";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/wsTasksMonitor";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/wsTasksMonitor";
	    break;
	case 18:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/monitor/threadpool";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/monitor/threadpool";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/monitor/threadpool";
	    break;
	case 19:
		document.getElementById('f1').src = "http://"+serverPath1+":8080/LittleCloud/healthCheckHistoryMonitor";
		document.getElementById('f2').src = "http://"+serverPath2+":8080/LittleCloud/healthCheckHistoryMonitor";
		document.getElementById('f3').src = "http://"+serverPath3+":8080/LittleCloud/healthCheckHistoryMonitor";
	    break;
	default:
	    break;
	}

	GetIFrameUrl();
}
</script>
</head>
<body onLoad="loadPass();loadServer();urlChange(5);">

	<div class="content">
		<div class="background right_menu">
		</div>
		<div class="right_block right_menu">
			<div class="content">
			<table class="tg">
			    <tr>
			    <th class="tg-bsv2">Platform</th>
			    <th class="tg-2sfh">
			    	<select size="1" id="server" onchange="saveServer();" style="width:155px;">
						<option value="" selected>prod</option>
						<option value=".dev">dev</option>
						<option value=".stag">stag</option>
					</select>
				</th>
			  </tr>  
			   <tr>
			    <th class="tg-bsv2">ServerType</th>
			    <th class="tg-2sfh">
			    	<select size="1" id="serverType" onchange="saveServerType()" style="width:155px;">
						<option value="rs">root</option>						
						<option value="ac1" selected>branch1</option>
						<option value="ac2">branch2</option>
						<option value="ac3">branch3</option>
					</select>
				</th>
			  </tr>
			  <tr>
			    <th class="tg-bsv2">password</th>
			    <th class="tg-2sfh"><input type="password" placeholder="enter a password" id="pass" onkeypress="savePass();"></th>
			  </tr>
			  <tr>
			    <th class="tg-bsv2">password2</th>
			    <th class="tg-2sfh"><input type="password" placeholder="enter a password" id="passadmin"></th>
			  </tr>
			  <tr>
			    <td class="tg-e3zv">wtp msg</td>
			    <td class="tg-031e">
			    <a href="#" onClick="javascript:urlChange(8);">[start]</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
			    <a href="#" onClick="javascript:urlChange(7);"><font color="#ff0000">[stop]</font></a>
			    </td>
			  </tr>
			  <tr>
			    <td class="tg-e3zv">queue</td>
			    <td class="tg-031e">
			    <a href="#" onClick="javascript:urlChange(2);">[cache]</a>&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
			    <a href="#" onClick="javascript:urlChange(1);">[db]</a>
			    </td>
			  </tr>
			 
			  <tr>
			    <td class="tg-e3zv">wtp</td>
			    <td class="tg-031e">
			    <a href="#" onClick="javascript:urlChange(6);">[pool]</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
			    <a href="#" onClick="javascript:urlChange(14);">[info]</a>
			    </td>
			  </tr>
			<tr>
			    <td class="tg-e3zv">ThreadPool</td>
			    <td class="tg-031e"><a href="#" onClick="javascript:urlChange(18);">go</a></td>
			  </tr>   
			  <tr>
			    <td class="tg-e3zv">DbUtil</td>
			    <td class="tg-031e"><a href="#" onClick="javascript:urlChange(3);">go</a></td>
			  </tr>
			  
			  <tr>
			    <td class="tg-e3zv">WsTasks</td>
			    <td class="tg-031e"><a href="#" onClick="javascript:urlChange(17);">go</a></td>
			  </tr>
			  <tr>
			    <td class="tg-e3zv">Scheduler</td>
			    <td class="tg-031e"><a href="#" onClick="javascript:urlChange(13);">go</a></td>
			  </tr>
			  <tr>
			    <td class="tg-e3zv">HealthCheck</td>
			    <td class="tg-031e">
			    <a href="#" onClick="javascript:urlChange(5);">[status]</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;
			    <a href="#" onClick="javascript:urlChange(19);">[history]</a>
			    </td>
			  </tr>
				
			  
			  <tr>
			  	<td class="tg-e3zv">FifoReplay</td>
			    <td class="tg-031e"><a class="alinkFifoReplay" href="#" >go</a></td>
			  </tr>			  			  
			  <tr>
			  	<td class="tg-e3zv">CaptivePortal</td>
			    <td class="tg-031e"><a class="alinkCaptivePortal" href="#" >go</a></td>
			  </tr>
			  <tr>
			  	<td class="tg-e3zv">Consolidate</td>
			    <td class="tg-031e"><a href="#" onClick="javascript:urlChange(16);">go</a></td>
			  </tr>
			  <tr>
			    <td class="tg-bsv3">cache</td>
			    <td class="tg-3sfh"><a href="#" onClick="javascript:urlChange(4);">go</a></td>
			  </tr>
			  <tr>
			    <td class="tg-bsv3"><a href="#" onClick="javascript:urlChange(9);">cache(dev)</a></td>
			    <td class="tg-3sfh"><input type="text" placeholder="enter a serial / in_sn" id="cache_devkey"></td>
			  </tr>
			  <tr>
			    <td class="tg-bsv3"><a href="#" onClick="javascript:urlChange(10);">cache(type)</a></td>
			    <td class="tg-3sfh"><input type="text" placeholder="enter a type" id="cache_type"></td>
			  </tr>
			  <tr>
			    <td class="tg-bsv3"><font color="#ff0000"><a href="#" onClick="javascript:urlChange(11);">delete(type) [ac1-1]</a></font></td>
			    <td class="tg-3sfh"><input type="text" placeholder="enter a type" id="cache_type1"></td>
			  </tr>
			  <tr>
			    <td class="tg-bsv3"><font color="#ff0000"><a href="#" onClick="javascript:urlChange(12);">delete(type) [ac1-2]</a></font></td>
			    <td class="tg-3sfh"><input type="text" placeholder="enter a type" id="cache_type2"></td>
			  </tr>
			</table>
			</div>
		</div>
		<div class="background main">
		</div>
		<div class="center_block main">
			<div class="content">
				<div class="url_block">
				<input class="tf-text" type="url" id="f1_url" onChange="GoUrl('f1', this);"></input>
				<input class="tf-text" type="url" id="f2_url" onChange="GoUrl('f2', this);"></input>
				<input class="tf-text" type="url" id="f3_url" onChange="GoUrl('f3', this);"></input>
				</div>
				<iframe class="tf" src="" name="f1" id="f1"></iframe>
				<iframe class="tf" src="" name="f2" id="f2"></iframe>
				<iframe class="tf" src="" name="f3" id="f3"></iframe>
			</div>
		</div>
	</div>
</body>
</html>