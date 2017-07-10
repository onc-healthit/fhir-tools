(function(){

	newsearchparam = function(obj){
		//console.log($(obj).parent().parent().siblings().hasClass("searchparam"));
		/*if($(obj).parent().parent().siblings().hasClass("searchparam") == true){
			return false;
		}*/
		var formgroup,label,col5,col1,button,removebtn;
		var num = Math.floor(Math.random() * 90000) + 10000;
		formgroup = $("<div class='form-group searchparam'></div>");
		emptycol = $('<div class="col-sm-1"></div>');
		paramname = $('<div class="col-sm-3"><select class="form-control paramname" id="searchparams'+num+'" onchange="matchcriteria(this);"><option>Select Search Parameters</option></select></div>');
		matchparam = $('<div class="col-sm-3"><select class="form-control matchclass parammatch" id="matchcriteria'+num+'" disabled="disabled"><option>Select Modifiers </option></select></div>');
		paramvalue = $('<div class="col-sm-3" id="date'+num+'"><input type="text" class="form-control paramvalue" id="searchvalue'+num+'"></div>');
		remparam = $('<div class="col-sm-1"><button type="button" class="btn btn-danger center-block" onclick="removesearchparam(this)">-</button></div> ');
		formgroup.append(emptycol,paramname,matchparam,paramvalue,remparam);
		//$(obj).parent().parent().last().after( formgroup );
		$('.searchparam').last().after(formgroup);
		var strHtml = '';
		for(var i=0; i<searchvals.length;i++){
			strHtml += "<option value='"+searchvals[i]+"'>"+searchvals[i]+"</option>";
		}
		$('#searchparams'+num).append(strHtml);
	};

	removesearchparam = function(obj){
		//$(obj).parent().parent().parent().remove();
		$(obj).parent().parent().remove();
		var remsel = $('#'+obj.id).val();
	};

	loadsearchparam = function(){
		$('#searchparams').children().remove();
		$('#matchcriterias').children().remove();
		$('#matchcriterias').append('<option>Select Modifiers</option>');
		$('#searchparams').append('<option>Select Search Parameters</option>');
		if($('.searchparam').length > 1){
			$('.searchparam').not(':first').remove();
		}
		$('#resourceid').val('');
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
		//$('#runtestbtn').attr('disabled',false);
		if(currenttab == "#read"){
			$('#runtestbtn').attr('disabled',false);
		}
	};

	removeselsearch = function(obj){
		var removeItem = $('#'+obj.id).val();
		searchvals = $.grep(searchvals, function(value) {
		  	return value != removeItem;
		});
	};

	loadfhirres = function(){
		$('#fhirresources').children().remove();
		$('#fhirresources').append('<option>Select US Core Profile</option>');
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
		$(obj).parent().next().children().attr('disabled',false);
		$(obj).parent().next().next().children().attr('disabled',false);
		$(obj).parent().next().children("select").children().remove();
		$(obj).parent().next().children("select").append('<option>Select Modifiers</option>');
		var matchid = $(obj).parent().next().children("select").attr("id");
		var data = matchcriterian[matchc];
		var vals = [];
		vals = data.split(',');
		var strHtml = '';
		for(var i=0; i<vals.length;i++){
			strHtml += "<option value='"+vals[i]+"'>"+vals[i]+"</option>";
		}
		$('#'+matchid).append(strHtml);
		//$('#runtestbtn').attr('disabled',false);
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

	authorizeserver = function(){
		localStorage.removeItem('access_token');
		localStorage.removeItem('patientId');
	    localStorage.removeItem('acctoken');
	    localStorage.setItem("acctoken",false);
	    localStorage.removeItem('strurl');
	    var typeofauth = 'Confidential';
		var state = Math.round(Math.random()*100000000).toString();
		var strurl = baseurl = $('#serverurl').val().replace(/\/$/, '');
		strurl = baseurl = strurl.trim();
		if(baseurl == '' || baseurl == undefined){
			bootbox.alert("Please enter FHIR Server URL");
			return false;
		}
		var clientid = $('#clientid').val();
		if(clientid == '' || clientid == undefined){
			bootbox.alert("Please enter Client ID");
			return false;
		}
		
		var clientsecret = $('#clientsecret').val();
		if(clientsecret == '' || clientsecret == undefined){
			bootbox.alert("Please enter Client Secret");
			return false;
		}

		/*var scopes = [];
        $.each($("#clientscope option:selected"), function(){            
            scopes.push($(this).val());
        });*/
        var val = [];
        $('.confscopes :checkbox:checked').each(function(i){
          val[i] = $(this).val();
        });
        console.log(val);
        if(val.length <= 0){
        	bootbox.alert("Please select atleast one Scope");
        	return false;
        }
        var clientscope = val.join(",");
        console.log(clientscope);
        var redirecturi = location.protocol + '//' + location.host + location.pathname;
		/*var redirecturi = $('#redirecturi').val();
		if(redirecturi == '' || redirecturi == undefined){
			bootbox.alert("Please enter Redirect URI");
			return false;
		}*/
		$.ajax({
			url:baseurl+"/metadata?_format=json",
			type:"GET",
			headers:{
	          "Content-Type":"application/json+fhir"
	        },
			success:function(data){
				//var data = JSON.parse(data);
				if(typeof data == "string"){
		            var data = JSON.parse(data);
		        }
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
		        sessionStorage[state] = JSON.stringify({
	                clientid: clientid,
	                clientsecret : clientsecret,
	                redirecturi: redirecturi,
	                tokenurl: tokenurl,
	                strurl: strurl,
	                clientscope: clientscope,
	                typeofauth: typeofauth
	            });
          		fhiroauth(clientid,clientscope,redirecturi,authurl,baseurl,state);
			},
			error:function(e){
				//l.ladda( 'stop' );
				console.log()
				if(e.responseText == ''){
					bootbox.alert("Invalid Authentication - Please provide valid details");
					return false;
				}else if(e.status == '404'){
					bootbox.alert("Invalid Authentication - Please provide valid details");
					return false;
				}else{
					bootbox.alert("Invalid Authentication - Please provide valid details");
					return false;
				}
			}
		});
	}

	fhiroauth = function(clientid,clientscope,redirecturi,authurl,baseurl,state) {
        var path = authurl+"?";
        var queryParams = ['client_id=' + clientid,
        	//'client_secret='+clientsecret,
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
  		var authbtndiv,authsuccess;
	  	var acctoken = localStorage.getItem("acctoken");
	  	var access_token = localStorage.getItem("access_token");
	  	if(access_token != undefined && access_token != ''){
	  		$('#authbtn').toggleClass('btn-danger btn-success');
	  		$('#authsuccess').html('');
	  		authbtndiv = $('<button type="button" class="btn btn-success" data-target="#authorize-modal" data-toggle="modal" data-original-title="" data-toggle="tooltip" id="authbtn" data-backdrop="static"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span> Authorize Server</button>');
	  		authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>Server Authorized successfully. Run a test by entering various values in the fileds.</strong></div>');
	        $('#authsuccess').append(authsuccess);
	        $('#authbtndiv').append(authbtndiv);
	    }else{
	    	authbtndiv = $('<button type="button" class="btn btn-danger" data-target="#authorize-modal" data-toggle="modal" data-original-title="" data-toggle="tooltip" id="authbtn" data-backdrop="static"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span> Authorize Server</button>');
	  		authsuccess = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>Authorize your FHIR Server and then enter the various values and run a test.</strong></div>');
	        $('#authsuccess').append(authsuccess);
	        $('#authbtndiv').append(authbtndiv);

	    }
	  	if(acctoken == "true"){
	  		localStorage.setItem("acctoken",false);
	  		window.location.replace(location.protocol + '//' + location.host + location.pathname);
	  		return;
	  	}
	    var tokenurl,clientid,clientsecret,strurl,redirecturi;
	    var state = getUrlParameter("state");
	    var code = getUrlParameter("code");
	    var error = getUrlParameter("error");
	    if(error != '' && error != null && error != undefined){
	    	localStorage.setItem('acctoken',true);
	    	 $('#authsuccess').html('');
	        var authsuccess = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>'+getUrlParameter("error_description")+'</strong></div>');
	        $('#authsuccess').append(authsuccess);
	        if(state != undefined){
		    	var params = JSON.parse(sessionStorage[state]);
		    	tokenurl = params.tokenurl;
		        clientid = params.clientid;
		        clientsecret = params.clientsecret;
		        redirecturi = params.redirecturi;
		        strurl = params.strurl;
		        clientscope = params.clientscope;
		        typeofauth = params.typeofauth;
		        if(typeofauth == 'Confidential'){
		        	$('#clientid').val(clientid);
			        $('#clientsecret').val(clientsecret);
			        $('#serverurl').val(strurl);
			        var scopearr = clientscope.split(',');
			        for(var i=0; i< scopearr.length; i++){
			        	$('input:checkbox.confscopesclass').each(function () {
					      	if($(this).val() == scopearr[i]){
					      		$(this).prop('checked',true);
					      	}
					  	});
			        }
		        }else if(typeofauth =='Public'){
		        	$('#publicclientid').val(clientid);
			        $('#publicserverurl').val(strurl);
			        var scopearr = clientscope.split(',');
			        for(var i=0; i< scopearr.length; i++){
			        	$('input:checkbox.publicscopesclass').each(function () {
					      	if($(this).val() == scopearr[i]){
					      		$(this).prop('checked',true);
					      	}
					  	});
			        }
		        }
		    }
	        return false;
	    }
	    if(state != undefined && code != undefined){
	    	var params = JSON.parse(sessionStorage[state]);
	    	tokenurl = params.tokenurl;
	        clientid = params.clientid;
	        clientsecret = params.clientsecret;
	        redirecturi = params.redirecturi;
	        strurl = params.strurl;
	        clientscope = params.clientscope;
	        typeofauth = params.typeofauth;
	        getoauthtoken(code,tokenurl,clientid,clientsecret,redirecturi,state,strurl,clientscope,typeofauth);
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
  
  	getoauthtoken = function(paramvar,tokenurl,clientid,clientsecret,redirecturi,state,strurl,clientscope,typeofauth){
	    //var data = { "code" : paramvar , "client_id" : clientid ,"client_secret": clientsecret , "grant_type" : "authorization_code" , "redirect_uri" : redirecturi }
	    //var data = { "code" : paramvar , "client_id" : clientid , "grant_type" : "authorization_code" , "redirect_uri" : redirecturi }
	    if(clientsecret == null || clientsecret == ''){
	    	var data = { "code" : paramvar , "client_id" : clientid , "grant_type" : "authorization_code" , "redirect_uri" : redirecturi }
	    }else{
	    	var data = { "code" : paramvar , "client_id" : clientid ,"client_secret": clientsecret , "grant_type" : "authorization_code" , "redirect_uri" : redirecturi }
	    }
	    $.ajax({
	      url:tokenurl,
	      type:"POST",
	      data:data,
	      success:function(data){
	        access_token = data.access_token;
	        var patientId = ''
	        if(clientscope.indexOf("launch/patient") != -1){
	        	patientId =  data.patient;
	        	localStorage.setItem('patientId',patientId);
	        }
	        $('#authbtn').toggleClass('btn-danger btn-success');
	        $('#authsuccess').html('');
	        var authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>Server Authorized successfully. Run a test by entering various values in the fields.</strong></div>');
	        $('#authsuccess').append(authsuccess);
	        localStorage.setItem('access_token',access_token);
	        localStorage.setItem('acctoken',true);
	        localStorage.setItem('strurl',strurl);
	        localStorage.setItem("authtype","auth");
	        if(typeofauth == 'Confidential'){
	        	$('#clientid').val(clientid);
		        $('#clientsecret').val(clientsecret);
		        $('#serverurl').val(strurl);
		        var scopearr = clientscope.split(',');
		        for(var i=0; i< scopearr.length; i++){
		        	$('input:checkbox.confscopesclass').each(function () {
				      	if($(this).val() == scopearr[i]){
				      		$(this).prop('checked',true);
				      	}
				  	});
		        }
	        }else if(typeofauth =='Public'){
	        	$('#publicclientid').val(clientid);
		        $('#publicserverurl').val(strurl);
		        var scopearr = clientscope.split(',');
		        for(var i=0; i< scopearr.length; i++){
		        	$('input:checkbox.publicscopesclass').each(function () {
				      	if($(this).val() == scopearr[i]){
				      		$(this).prop('checked',true);
				      	}
				  	});
		        }
	        }
	        //getoauthjson(strurl);
	      },
	      error:function(e){
	      	localStorage.setItem('acctoken',true);
	        if(e.status == '403'){
		        bootbox.alert("Unauthorized: Invalid Authentication.");
	        }else{
	        	bootbox.alert("Unauthorized: Invalid Authentication.");
	        }
	      }
	    })
  	}

  	runresourcetest =  function(){
        $('#serverResponse').html('');
  		$(document).ready(function(){
		    $('[data-toggle="tooltip"]').tooltip();   
		});
		var searchparamarr = [];
		var searchparammatch = [];
		var searchparamval = [];
		var mainarr = [];
		var paramsobj = {};
  		var strurl = localStorage.getItem("strurl");
  		var confprofname = $('#confprofname option:selected').attr('value');
  		var observCategory = null;
		fhirresourcename = $('#fhirresources option:selected').attr('value');

		/*if(fhirresourcename == 'Observation(Laboratory)' || fhirresourcename == 'Observation(SmokingStatus)' || fhirresourcename == 'Observation(VitalSigns)'){
			fhirresourcename = 'Observation';
		}*/
		if(fhirresourcename == 'Observation(Laboratory)'){
			fhirresourcename = 'Observation';
			observCategory = 'laboratory';
			localStorage.setItem("observCategory",observCategory);
		}else if(fhirresourcename == 'Observation(SmokingStatus)'){
			fhirresourcename = 'Observation';
			observCategory = 'social-history';
			localStorage.setItem("observCategory",observCategory);
		}else if(fhirresourcename == 'Observation(VitalSigns)') {
		   fhirresourcename = 'Observation';
		   observCategory = 'vital-signs';
		   localStorage.setItem("observCategory",observCategory);
		  }
		
		strurl = strurl.replace(/\/$/, '');

		/* Search Paremters Tab*/
		if(currenttab == '#search'){
			$('.paramname').each(function(i, obj) {
				var element = $(obj);
				searchparamarr.push(element.val());
			 });
			$('.parammatch').each(function(i, obj) {
				var element = $(obj);
				if(element.val() == 'Select Modifiers'){
					searchparammatch.push('');
				}else{
					searchparammatch.push(element.val());
				}
			 });
			$('.paramvalue').each(function(i, obj) {
				var element = $(obj);
				searchparamval.push(element.val());
			 });
			for(var i=0; i<searchparamarr.length; i++){
				paramsobj={};
				paramsobj.key = searchparamarr[i];
				paramsobj.match = searchparammatch[i];
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

			/*strurl = strurl + "/"+fhirresourcename+"/?_format=json";*/
			if(fhirresourcename == 'Observation' && observCategory != null){
				strurl = strurl + "/"+fhirresourcename+"/?_format=json&category="+observCategory;
			}else{
				strurl = strurl + "/"+fhirresourcename+"/?_format=json";
			}

			/* Add Parameters to URL */
			var extraurl ='';
			for(var m=0;m<mainarr.length;m++){
				if(mainarr[m].match==''){
					strurl = strurl + '&' + mainarr[m].key + "=" + mainarr[m].value;	
				}else if(mainarr[m].key == 'birthdate' || mainarr[m].key == 'date'){
					strurl = strurl + '&' + mainarr[m].key + "=" + mainarr[m].match+mainarr[m].value;	
				}else{
					strurl = strurl + '&' + mainarr[m].key + ":" + mainarr[m].match+ "=" + mainarr[m].value;	
				}
			}
			/* Add Parameters to URL */
		}
		/* Search Paremters Tab*/

		/* Read Tab*/
		if(currenttab == '#read'){
			resourceid = $('#resourceid').val();
			if(resourceid == '' || resourceid == undefined){
				strurl = strurl + "/"+fhirresourcename+"?_format=json";	
			}else if(fhirresourcename =='Observation' && observCategory !=null){
				strurl = strurl + "/"+fhirresourcename+"/?"+observCategory+"="+resourceid+"&_format=json";
			}else{
				strurl = strurl + "/"+fhirresourcename+"/"+resourceid+"?_format=json";
			}
			//sessionStorage.setItem("strurl", strurl);
		}
		/* Read Tab*/

		var l = $( '#runtestbtn' ).ladda();
		localStorage.setItem("fhirresourcename",fhirresourcename);
		localStorage.setItem("currenttab",currenttab);
		l.ladda( 'start' );
		getoauthjson(strurl,searchparamarr,searchparamval,l);
  	}

    getoauthjson = function(strurl,searchparamarr,searchparamval,l){
    	if(access_token == '' || access_token == null){
    		access_token = localStorage.getItem("access_token");
    	}
    	var resourcename = localStorage.getItem("fhirresourcename");
    	var tab = localStorage.getItem("currenttab");
    	if(localStorage.getItem("authtype") == 'auth'){
    		$.ajax({
		      	url:strurl,
		        type:"GET",
		        beforeSend: function (xhr) {
		            xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
		            xhr.setRequestHeader("Content-Type","application/json+fhir");
		        },
		        success:function(data,status,xhr){
		        	l.ladda( 'stop' );
		        	$('#jsondata').html('');
		          /*$('#jsondata').html(JSON.stringify(data));*/
		          loadtableview(resourcename,data,tab);
		          $.JSONView(JSON.stringify(data), $('#jsondata'));
		          $('#resurl').html("<b>GET</b> "+strurl);
		          $("#spinner").hide();
		          	$('pre code').each(function(i, block) {
		    			hljs.highlightBlock(block);
		  			});
		  			displayrequestheaders(searchparamarr,searchparamval);
		  			displayresponseheaders(xhr);
		  			//$('#responsedatamodal').modal('show');
		  			$('#authsuccess').html('');
	  				authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>Server Authorized successfully. Run a test by entering various values in the fileds.</strong></div>');
	        		$('#authsuccess').append(authsuccess);
		  			$('#responsedatamodal').modal({
					    backdrop: 'static',
					    keyboard: false
					})
		        },
		        error:function(e){
		        	l.ladda( 'stop' );
		        	$('#resurl').html(strurl);
		          	$("#spinner").hide();
		          	if(e.status == '401'){
		          		localStorage.removeItem("access_token");
		          		if($('#authbtn').hasClass('btn-success')){
		          			$('#authbtn').toggleClass('btn-success btn-danger');
		          		}
		          		$('#authsuccess').html('');
		          		var authfail = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>Unauthorized: Authentication token is expired.</strong></div>');
			        	$('#authsuccess').append(authfail);
		          	}else{
		          		$('#serverResponse').html('');
		          		var authfail = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>'+e.responseJSON.resourceType+" - "+e.responseJSON.issue[0].diagnostics+'</strong></div>');
			        	$('#serverResponse').append(authfail);
		          	}
			        //window.scrollTo(0, 0);
		        }
		    })
    	}else if(localStorage.getItem("authtype") == 'noauth'){
    		$.ajax({
		      	url:strurl,
		        type:"GET",
		        beforeSend: function (xhr) {
		            xhr.setRequestHeader("Content-Type","application/json+fhir");
		        },
		        success:function(data,status,xhr){
		        	l.ladda( 'stop' );
		        	$('#jsondata').html('');
		          loadtableview(resourcename,data,tab);
		          $.JSONView(JSON.stringify(data), $('#jsondata'));
		          $('#resurl').html("<b>GET</b> "+strurl);
		          $("#spinner").hide();
		          	$('pre code').each(function(i, block) {
		    			hljs.highlightBlock(block);
		  			});

                    $('#authsuccess').html('');
                    var authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>Run a test by entering various values in the fields.</strong></div>');
                    $('#authsuccess').append(authsuccess);
		  			displayrequestheaders(searchparamarr,searchparamval);
		  			displayresponseheaders(xhr);
		  			$('#responsedatamodal').modal({
					    backdrop: 'static',
					    keyboard: false
					})

		        },
		        error:function(e){
		        	l.ladda( 'stop' );
		        	$('#resurl').html(strurl);
		          	$("#spinner").hide();
		          	if(e.status == '401'){
		          		localStorage.removeItem("access_token");
		          		if($('#authbtn').hasClass('btn-success')){
		          			$('#authbtn').toggleClass('btn-success btn-danger');
		          		}
		          		$('#authsuccess').html('');
		          		var authfail = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>Unauthorized: Authentication token is expired.</strong></div>');
			        	$('#authsuccess').append(authfail);
		          	}else{
		          		$('#serverResponse').html('');
		          		var authfail = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>'+e.responseJSON.resourceType+" - "+e.responseJSON.issue[0].diagnostics+'</strong></div>');
			        	$('#serverResponse').append(authfail);
		          	}
			        //window.scrollTo(0, 0);
		        }
		    })
    	}

    }

    displayrequestheaders = function(searchparamarr,searchparamval){
    	$('#reqhead').html('');
    	var useragent,contenttype,acceptchar,accept,format,authorization,id,resource,searchpar;
    	useragent = $('<div class="header-text"><u>User-Agent</u>: FHIR Client</div>');
    	contenttype = $('<div class="header-text"><u>Content-Type</u>: application/xml+fhir;charset=UTF-8</div>');
    	acceptchar = $('<div class="header-text"><u>Accept-Charset</u>: UTF-8</div>');
    	accept = $('<div class="header-text"><u>Accept</u>: application/xml+fhir</div>');
    	format = $('<div class="header-text"><u>format</u>: application/xml+fhir</div>');
    	authorization = $('<div class="header-text"><u>Authorization</u>: Bearer '+access_token+'</div>');
    	if(currenttab == '#read' && resourceid != null && resourceid != ''){
    		id = $('<div class="header-text"><u>id</u>: '+resourceid+'</div>');
    	}
    	if(fhirresourcename != null && fhirresourcename != ''){
    		resource = $('<div class="header-text"><u>resource</u>: FHIR::'+fhirresourcename+'</div>');
    	}
    	if(currenttab == '#search' && searchparamarr.length>0 && searchparamval.length>0){
    		var params = '{';
    		for(var i=0; i<searchparamarr.length; i++){
    			if(i == searchparamarr.length-1){
    				params = params + ':'+searchparamarr[i]+'=>"'+searchparamval[i]+'"';
    			}else{
    				params = params + ':'+searchparamarr[i]+'=>"'+searchparamval[i]+'",';
    			}
    		}
    		params = params + '}';
    		searchpar = $('<div class="header-text"><u>search</u>: {:parameters=>'+params+'}</div>');
    	}
    	$('#reqhead').append(useragent,contenttype,acceptchar,accept,id,resource,format,searchpar,authorization);
    }

    displayresponseheaders = function(xhr){
    	$('#resphead').html('');
    	var date,transferencoding,accessorigin,accessheaders,accessmethods,accessage,xpoweredby,lastmodified,contenttype;
    	if(xhr.getResponseHeader('Date') != null){
    		date = $('<div class="header-text"><u>date</u>: '+xhr.getResponseHeader('Date')+'</div>');	
    	}
    	if(xhr.getResponseHeader('Transfer-Encoding') != null){
    		transferencoding = $('<div class="header-text"><u>transfer-encoding</u>: '+xhr.getResponseHeader('Transfer-Encoding')+'</div>');	
    	}
    	if(xhr.getResponseHeader('Access-Control-Allow-Origin') != null){
    		accessorigin = $('<div class="header-text"><u>access-control-allow-origin</u>: '+xhr.getResponseHeader('Access-Control-Allow-Origin')+'</div>');	
    	}
    	if(xhr.getResponseHeader('Access-Control-Allow-Headers') != null){
    		accessheaders = $('<div class="header-text"><u>access-control-allow-headers</u>: '+xhr.getResponseHeader('Access-Control-Allow-Headers')+'</div>');	
    	}
    	if(xhr.getResponseHeader('Access-Control-Allow-Methods') != null){
    		accessmethods = $('<div class="header-text"><u>access-control-allow-methods</u>: '+xhr.getResponseHeader('Access-Control-Allow-Methods')+'</div>');	
    	}
    	if(xhr.getResponseHeader('Access-Control-Max-Age') != null){
    		accessage = $('<div class="header-text"><u>access-control-allow-age</u>: '+xhr.getResponseHeader('Access-Control-Max-Age')+'</div>');	
    	}
    	if(xhr.getResponseHeader('X-Powered-By') != null){
    		xpoweredby = $('<div class="header-text"><u>x-powered-by</u>: '+xhr.getResponseHeader('X-Powered-By')+'</div>');	
    	}
    	if(xhr.getResponseHeader('Last-Modified') != null){
    		lastmodified = $('<div class="header-text"><u>last-modified</u>: '+xhr.getResponseHeader('Last-Modified')+'</div>');	
    	}
    	if(xhr.getResponseHeader('Content-Type') != null){
    		contenttype = $('<div class="header-text"><u>content-type</u>: '+xhr.getResponseHeader('Content-Type')+'</div>');	
    	}
    	$('#resphead').append(date,transferencoding,accessorigin,accessheaders,accessmethods,accessage,xpoweredby,lastmodified,contenttype);
    }
	loadread = function(){
		$('#read').load("read.html");
		$('#runtestbtn').attr('disabled',"disabled");
	}
	loadsearch = function(){
		$('#search').load("search.html");
		$('#runtestbtn').attr('disabled',"disabled");
		var fhirresourcename = $('#fhirresources option:selected').attr('value');
		if(fhirresourcename != undefined){
			loadsearchparam();
		}
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

	openserver = function(){
		var openserverurl = $('#openserverurl').val();
		if(openserverurl == null || openserverurl == '' || openserverurl == undefined){
			bootbox.alert("Please enter FHIR Server URL");
			return;
		}
		localStorage.removeItem("strurl");
		localStorage.removeItem("access_token");
		localStorage.removeItem("patientid");
		localStorage.setItem("strurl",openserverurl);
		localStorage.setItem("authtype","noauth");
		var val = [];
        $('.openscopes :checkbox:checked').each(function(i){
          val[i] = $(this).val();
        });
        console.log(val);
        var openclientscope = val.join(",");
        $('#noauthorize-modal').modal('hide');
        $('#authsuccess').html('');
        var authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:6px 12px;"><strong>Run a test by entering various values in the fields.</strong></div>');
	    $('#authsuccess').append(authsuccess);
	}

}).call(this);