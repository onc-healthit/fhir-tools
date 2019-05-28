(function(){
	geturl = function() {
		var protocol,context,host,port, strurl;
		protocol = window.location.protocol;
		host= window.location.host;
		port = window.location.port;
		context = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
		/*if(port != '' || port != null || port != undefined){
			strurl = protocol+"//"+host+":"+port+context;
		}else{
			strurl = protocol+"//"+host+context;
		}*/
		strurl = protocol+"//"+host+context;
		return strurl;
	}
}).call(this);