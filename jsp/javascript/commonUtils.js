function getCurrentDocumentUrlServer(){
	var str = document.URL;
	var rePattern = new RegExp("\\b(http|https)://\\b(.*)/LittleCloud");
	var result = '';
	var matched= rePattern.exec(str);
    // matched[0] should be: http://<server_name>:8080/LittleCloud
    // matched[1] should be: (http/https)
    // matched[2] should be: <server_name>:8080
	if (matched != undefined && matched[2] != undefined){
		result = matched[2];
	}
	return result;
}

function getTodayInString(){
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!
	var yyyy = today.getFullYear();

	if(dd<10) {
	    dd='0'+dd
	} 

	if(mm<10) {
	    mm='0'+mm
	} 

	today = mm+'/'+dd+'/'+yyyy;
	return today;
}