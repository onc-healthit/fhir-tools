(function(){

	newsearchparam = function(obj){
		console.log($(obj).parent().parent().siblings().hasClass("searchparam"));
		if($(obj).parent().parent().siblings().hasClass("searchparam") == true){
			return false;
		}
		var formgroup,label,col5,col1,button,removebtn;
		var num = Math.floor(Math.random() * 90000) + 10000;
		formgroup = $("<div class='form-group searchparam'></div>");
		emptycol = $('<div class="col-sm-1"></div>');
		paramname = $('<div class="col-sm-3"><select class="form-control paramname" id="searchparams'+num+'" onchange="matchcriteria(this);"><option>Select Search Parameters</option></select></div>');
		matchparam = $('<div class="col-sm-3"><select class="form-control matchclass parammatch" id="matchcriteria'+num+'" disabled="disabled"><option>Select Search Parameters</option></select></div>');
		paramvalue = $('<div class="col-sm-3" id="date'+num+'"><input type="text" class="form-control paramvalue" id="searchvalue'+num+'"></div>');
		formgroup.append(emptycol,paramname,matchparam,paramvalue);
		$(obj).parent().parent().after( formgroup );
		var strHtml = '';
		for(var i=0; i<searchvals.length;i++){
			strHtml += "<option value='"+searchvals[i]+"'>"+searchvals[i]+"</option>";
		}
		$('#searchparams'+num).append(strHtml);
	};

	removesearchparam = function(obj){
		$(obj).parent().parent().parent().remove();
		var remsel = $('#'+obj.id).val();
		console.log(remsel);
	};

	loadsearchparam = function(){
		$('#searchparams').children().remove();
		$('#searchparams').append('<option>Select Search Parameters</option>');
		$('.paramvalue').each(function(i, obj) {
			var element = $(obj);
			element.val('');
		 });
		var fhir = $('#fhirresources').val();
		var data = searchparameters[fhir];
		var vals = [];
		vals = data.split(',');
		searchvals = data.split(',');
		var strHtml = '';
		for(var i=0; i<vals.length;i++){
			strHtml += "<option value='"+vals[i]+"'>"+vals[i]+"</option>";
		}
		$('#searchparams').append(strHtml);
		$('#searchparams').attr('disabled',false);
		$('#addbtn').attr('disabled',false);
		var Conformance = $('#confprofname option:selected').attr('value');
		var fhirresname = $('#fhirresources option:selected').attr('value');
		if($('#resourceid').val() == '' || Conformance == undefined || fhirresname == undefined){
			$('#runtestbtn').attr('disabled','disabled');
		}else{
			$('#runtestbtn').attr('disabled',false);
		}
	};

	removeselsearch = function(obj){
		console.log($('#'+obj.id).val());
		var removeItem = $('#'+obj.id).val();
		searchvals = $.grep(searchvals, function(value) {
		  	return value != removeItem;
		});
		console.log(searchvals);
	};

	loadfhirres = function(){
		$('#fhirresources').children().remove();
		$('#fhirresources').append('<option>Select FHIR Profile Name</option>');
		populateSelectIdentifiedById('fhirresources');
		$('#fhirresources').attr('disabled',false);
	};


	seloperator =  function(obj){
		var bool = $('.mainform').last().children().hasClass("searchparam");
		if(bool == false){
			alert("Please select the concat type");
			return false;
		}
		var formgroup,operators;
		mainformgroup = $("<div class='form-group mainform'></div>");
		formgroup = $("<div class='form-group seloperator'></div>");
		operators = $('<div class="col-sm-5"></div><div class="col-sm-1" style="padding:0px;"><select class="form-control" onchange="newsearchparam(this)"><option></option><option>AND</option><option>OR</option></select></div> <div class="col-sm-4"></div><div class="col-sm-1"><button type="button" class="btn btn-danger center-block" onclick="removesearchparam(this)">-</button></div> ');
		formgroup.append(operators);
		mainformgroup.append(formgroup);
		$('.mainform').last().after(mainformgroup);
	};

	matchcriteria = function(obj){
		var matchc = $('#'+obj.id).val();
		console.log(matchc);
		$(obj).parent().next().children().attr('disabled',false);
		$(obj).parent().next().children("select").children().remove();
		$(obj).parent().next().children("select").append('<option>Select Match Criteria</option>');
		var matchid = $(obj).parent().next().children("select").attr("id");
		var data = matchcriterian[matchc];
		console.log(data);
		var vals = [];
		vals = data.split(',');
		var strHtml = '';
		for(var i=0; i<vals.length;i++){
			strHtml += "<option value='"+vals[i]+"'>"+vals[i]+"</option>";
		}
		$('#'+matchid).append(strHtml);
		$('#runtestbtn').attr('disabled',false);
		/*console.log($(obj).parent().next().next().attr("id"));
		if(matchc == 'created' || matchc == 'period' || matchc == 'date'){
			console.log("in if");
			//$(obj).parent().next().next().addClass('input-append date');
			var dateid = $(obj).parent().next().next().children().attr("id");
			$('#'+dateid).datetimepicker({
				format: 'yyyy-mm-dd hh:ii:ss'
			})
		}*/
	};

	newhttprow = function(obj){
		var formgroup,label,col5,col1,button,removebtn;
		var num = Math.floor(Math.random() * 90000) + 10000;
		formgroup = $("<div class='form-group httpparam'></div>");
		emptycol = $('<div class="col-sm-1"></div>');
		paramname = $('<div class="col-sm-4"><select class="form-control httpclass" id="httpparams'+num+'" ><option>Select HTTP Header Parameters</option><option>Authorization Token</option></select></div>');
		paramvalue = $('<div class="col-sm-4"><input type="text" class="form-control" id="httpparamvalue"></div>');
		removebtn =$('<div class="col-sm-1"><button type="button" class="btn btn-danger center-block" onclick="removehttprow(this)">-</button></div> ');
		formgroup.append(emptycol,paramname,paramvalue,removebtn);
		$('.httpparam').last().after(formgroup);
	};

	newhttprowread = function(obj){
		var formgroup,label,col5,col1,button,removebtn;
		var num = Math.floor(Math.random() * 90000) + 10000;
		formgroup = $("<div class='form-group httpparamread'></div>");
		emptycol = $('<div class="col-sm-1"></div>');
		paramname = $('<div class="col-sm-4"><select class="form-control httpclassread" id="httpparamsread'+num+'" ><option>Select HTTP Header Parameters</option><option value="Authorization">Authorization Token</option></select></div>');
		paramvalue = $('<div class="col-sm-4"><input type="text" class="form-control" id="httpparamvalueread"></div>');
		removebtn =$('<div class="col-sm-1"><button type="button" class="btn btn-danger center-block" onclick="removehttprowread(this)">-</button></div> ');
		formgroup.append(emptycol,paramname,paramvalue,removebtn);
		$('.httpparamread').last().after(formgroup);
	};

	removehttprow = function(obj){
		$(obj).parent().parent().remove();
	};

	removehttprowread = function(obj){
		$(obj).parent().parent().remove();
	};

	runtest = function (){
		$(document).ready(function(){
		    $('[data-toggle="tooltip"]').tooltip();   
		});
		var searchparamarr = [];
		var searchparammatch = [];
		var searchparamval = [];
		var mainarr = [];
		var paramsobj = {};
		var state = Math.round(Math.random()*100000000).toString();
		sessionStorage.setItem("jsondata", false);
		var strurl  = baseurl = $('#serverurl').val().replace(/\/$/, '');
		//baseurl = baseurl.replace(/\/$/, '');
		console.log(baseurl);
		console.log(strurl);
		if(strurl == '' || strurl == undefined){
			bootbox.alert("Please enter FHIR Server URL");
			return false;
		}
		var clientid = $('#clientid').val();
		if(clientid == '' || clientid == undefined){
			bootbox.alert("Please enter Client ID");
			return false;
		}
		//sessionStorage.setItem("clientid", clientid);
		var clientsecret = $('#clientsecret').val();
		if(clientsecret == '' || clientsecret == undefined){
			//sessionStorage.setItem("clientsecret", clientsecret);
			bootbox.alert("Please eneter Client Secret");
			return false;
		}
		var clientscope = $('#clientscope option:selected').attr('value');
		var redirecturi = $('#redirecturi').val();
		if(redirecturi == '' || redirecturi == undefined){
			bootbox.alert("Please enter Redirect URI");
			return false;
		}
		//sessionStorage.setItem("redirecturi", redirecturi);
		var confprofname = $('#confprofname option:selected').attr('value');
		/*if(confprofname == undefined || confprofname == ''){
			$('#confprofname').tooltip({
				placement:"right",
				title:"Please select Conformance Profile"
			});
			return false;
		}*/
		//var fhirresourcename = $('#fhirresources').val();
		var fhirresourcename = $('#fhirresources option:selected').attr('value');
		
		strurl = strurl.replace(/\/$/, '');

		/* Search Paremters Tab*/
		if(currenttab == '#search'){
			$('.paramname').each(function(i, obj) {
				var element = $(obj);
				searchparamarr.push(element.val());
			 });
			$('.parammatch').each(function(i, obj) {
				var element = $(obj);
				searchparammatch.push(element.val());
			 });
			$('.paramvalue').each(function(i, obj) {
				var element = $(obj);
				searchparamval.push(element.val());
			 });
			for(var i=0; i<searchparamarr.length; i++){
				paramsobj={};
				paramsobj.key = searchparamarr[i];
				if(fhirresourcename == 'DocumentReference' && (searchparamarr[i] == 'created' || searchparamarr[i] == 'period')){
					paramsobj.value = searchparammatch[i]+searchparamval[i];
				}else{
					paramsobj.value = searchparamval[i];
				}
				
				mainarr.push(paramsobj);
			}

			/* Search HTTP Header Parameters */
			var httpparams = $('#httpparams option:selected').attr('value');
			var httpparamvalue = $('#httpparamvalue').val();
			/* Search HTTP Header Parameters */

			strurl = strurl + "/"+fhirresourcename+"/?_format=json";

			/* Add Parameters to URL */
			for(var m=0;m<mainarr.length;m++){
				strurl = addParam(strurl,mainarr[m].key,mainarr[m].value);
			}
			/* Add Parameters to URL */
			//sessionStorage.setItem("strurl", strurl);
			if(httpparamvalue == undefined || httpparamvalue == ''){
				var httpheaders = { "Content-Type" : "application/json", "Accept" : "application/json"};
			}else{
				var httpheaders = { "Content-Type" : "application/json", "Accept" : "application/json","Authorization": "Bearer "+httpparamvalue+""};
			}	
		}
		/* Search Paremters Tab*/

		/* Read Tab*/
		if(currenttab == '#read'){
			var resourceid = $('#resourceid').val();
			if(resourceid == '' || resourceid == undefined){
				strurl = strurl + "/"+fhirresourcename+"?_format=json";	
			}else{
				strurl = strurl + "/"+fhirresourcename+"/"+resourceid+"?_format=json";
			}
			//sessionStorage.setItem("strurl", strurl);
			/* Read HTTP Header Parameters */
			var httpparamsread = $('#httpparamsread option:selected').attr('value');
			var httpparamvalueread = $('#httpparamvalueread').val();
			/* read HTTP Header Parameters */

			if(httpparamvalueread == undefined || httpparamvalueread == ''){
				var httpheaders = { "Content-Type" : "application/json", "Accept" : "application/json"};
			}else{
				console.log("in else");
				var httpheaders = { "Content-Type" : "application/json", "Accept" : "application/json","Authorization": "Bearer "+httpparamvalueread+""};
				//var httpheaders = { "Content-Type" : "application/json", "Accept" : "application/json"};
			}
		}
		/* Read Tab*/
		var l = $( '#runtestbtn' ).ladda();
		l.ladda( 'start' );
		$.ajax({
			url:baseurl+"/metadata?_format=json",
			type:"GET",
			headers:{
	          "Content-Type":"application/json+fhir"
	        },
			success:function(data){
				console.log(data);
				//var data = JSON.parse(data);
				if(typeof data == "string"){
		            console.log("in if");
		            var data = JSON.parse(data);
		        }
		        console.log(data);
				var arg = data.rest[0].security.extension[0].extension;
		        for(var i=0;i<arg.length;i++){
		      	    if (arg[i].url === "register") {
		      	    	regurl = arg[i].valueUri;
		            } else if (arg[i].url === "authorize") {
		              	authurl = arg[i].valueUri;
		            } else if (arg[i].url === "token") {
		              	tokenurl = arg[i].valueUri;
		            }
		        }
		        //sessionStorage.setItem("tokenurl", tokenurl);
		        sessionStorage[state] = JSON.stringify({
	                clientid: clientid,
	                clientsecret : clientsecret,
	                strurl: strurl,
	                redirecturi: redirecturi,
	                tokenurl: tokenurl
	            });
          		fhiroauth(clientid,clientsecret,clientscope,redirecturi,authurl,baseurl,state);
          		l.ladda( 'stop' );
			},
			error:function(e){
				console.log(e);
				//l.ladda( 'stop' );
				if(e.responseText == ''){
					bootbox.alert("No Response from Server");
					return false;
				}
				var errordata = JSON.parse(e.responseText);
				//var errormessage = JSON.stringify(errordata.text.div);
				var errormessage = errordata.issue[0].details;
				bootbox.alert(errormessage);
				l.ladda( 'stop' );
			}
		});
	};

	fhiroauth = function(clientid,clientsecret,clientscope,redirecturi,authurl,baseurl,state) {
        var path = authurl+"?";
        var queryParams = ['client_id=' + clientid,
        	'client_secret='+clientsecret,
	        'redirect_uri=' + redirecturi,
	        'response_type=code',
	        'state='+state,
	        'scope=' + clientscope,
	        //'scope=*',
	        'aud='+baseurl
        ];
        var query = queryParams.join('&');
        var url = path + query;
        window.location.replace(url);
        //window.open(url, "", "width=500, height=500");
    };

	addParam = function(url, param, value) {
    	var a = document.createElement('a'), regex = /(?:\?|&amp;|&)+([^=]+)(?:=([^&]*))*/g;
       	var match, str = []; a.href = url; param = encodeURIComponent(param);
       	while (match = regex.exec(a.search))
           	if (param != match[1]) str.push(match[1]+(match[2]?"="+match[2]:""));
       			str.push(param+(value?"="+ encodeURIComponent(value):""));
       			a.search = str.join("&");
       	return a.href;
    };

    populateSelectIdentifiedById = function( selId ) {
		populateSelectControlWithAssociativeArray( $('#'+selId)[0], fhirresources.labels );
	};

	populateSelectControlWithAssociativeArray = function( selObj, arr ) {
		if( "undefined" == typeof(arr) || null == arr ) {
			return;
		}
		var opt;
		for( var key in arr ) {
			opt = new Option( arr[key], key );
			selObj.options[selObj.options.length] = opt;
		}
	};

	checkcode = function(variable){
	    var query = window.location.search.substring(1);
	    var vars = query.split("&");
	    for (var i=0;i<vars.length;i++) {
	      var pair = vars[i].split("=");
	      if (pair[0] == variable) {
	        return pair[1];
	      }
	    } 
	};

  	checkparam = function(){
	  	var jsondata = sessionStorage.getItem("jsondata");
	  	console.log(jsondata);
	  	if(jsondata == "true"){
	  		sessionStorage.setItem("jsondata",false);
	  		window.location.replace("http://localhost:8000/FHIR/");
	  		return;
	  	}
	    /*if(paramvar != undefined && paramvar != ''){
	      getoauthtoken(paramvar);
	    }*/
	    var tokenurl,clientid,clientsecret,strurl,redirecturi;
	    var state = getUrlParameter("state");
	    var code = getUrlParameter("code");
	    if(state != undefined && code != undefined){
	    	var params = JSON.parse(sessionStorage[state]);
	    	tokenurl = params.tokenurl;
	        clientid = params.clientid;
	        clientsecret = params.clientsecret;
	        strurl = params.strurl;
	        redirecturi = params.redirecturi;
	        getoauthtoken(code,tokenurl,clientid,clientsecret,redirecturi,strurl)
	    }
  	}

  	getUrlParameter = function(sParam){
        var sPageURL = window.location.search.substring(1);
        var sURLVariables = sPageURL.split('&');
        for (var i = 0; i < sURLVariables.length; i++) {
            var sParameterName = sURLVariables[i].split('=');
            if (sParameterName[0] == sParam) {
                return decodeURIComponent(sParameterName[1]);
            }
        }
    };
  
  	getoauthtoken = function(paramvar,tokenurl,clientid,clientsecret,redirecturi,strurl){
	  	$("#spinner").show();
	    //var tokenurl = sessionStorage.getItem("tokenurl");
	    //var clientsecret = sessionStorage.getItem("clientsecret");
	    //var clientid = sessionStorage.getItem("clientid");
	    //var redirecturi = sessionStorage.getItem("redirecturi");
	    var data = { "code" : paramvar , "client_id" : clientid ,"client_secret": clientsecret , "grant_type" : "authorization_code" , "redirect_uri" : redirecturi }
	    /*console.log(clientsecret);
	    if(clientsecret == null){
	    	var data = { "code" : paramvar , "client_id" : clientid , "grant_type" : "authorization_code" , "redirect_uri" : redirecturi }
	    }else{
	    	var data = { "code" : paramvar , "client_id" : clientid ,"client_secret": clientsecret , "grant_type" : "authorization_code" , "redirect_uri" : redirecturi }
	    }*/
	    $.ajax({
	      url:tokenurl,
	      type:"POST",
	      data:data,
	      success:function(data){
	        access_token = data.access_token;
	        console.log(access_token);
	        getoauthjson(strurl);
	      },
	      error:function(e){
	        console.log(e);
	      }
	    })
  	}

    getoauthjson = function(strurl){
      //var baseurl = sessionStorage.getItem("baseurl");
      //var strurl = sessionStorage.getItem("strurl");
      $.ajax({
      	url:strurl,
        type:"GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
        },
        success:function(data){
          console.log(data);
          $('#jsondata').html(JSON.stringify(data));
          sessionStorage.setItem("jsondata", true);
          $("#spinner").hide();
          sessionStorage.removeItem("clientsecret");
          sessionStorage.removeItem("clientid");
          sessionStorage.removeItem("redirecturi");
          sessionStorage.removeItem("tokenurl");
          sessionStorage.removeItem("baseurl");
        },
        error:function(e){
          	console.log(e);
          	$("#spinner").hide();
          	if(e.responseText == ''){
				bootbox.alert("No Response from Server");
				return false;
			}
			var errordata = JSON.parse(e.responseText);
			//var errormessage = JSON.stringify(errordata.text.div);
			var errormessage = errordata.issue[0].details;
			bootbox.alert(errormessage);
        }
      })
    }

	loadread = function(){
		$('#read').load("read.html");
		$('#runtestbtn').attr('disabled',"disabled");
	}
	loadsearch = function(){
		$('#search').load("search.html");
		$('#runtestbtn').attr('disabled',"disabled");
	};

	enabletest = function(){
		var Conformance = $('#confprofname option:selected').attr('value');
		var fhirresname = $('#fhirresources option:selected').attr('value');
		if($('#resourceid').val() == '' || Conformance == undefined || fhirresname == undefined){
			$('#runtestbtn').attr('disabled','disabled');
		}else{
			$('#runtestbtn').attr('disabled',false);
		}
	}

}).call(this);