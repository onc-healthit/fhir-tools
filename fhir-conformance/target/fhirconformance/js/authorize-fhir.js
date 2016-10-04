(function(){

	authorizeserver = function(){
		localStorage.removeItem('access_token');
		localStorage.removeItem('patientId');
	    localStorage.removeItem('acctoken');
	    localStorage.removeItem('strurl');
		var state = Math.round(Math.random()*100000000).toString();
		var strurl = baseurl = $('#serverurl').val().replace(/\/$/, '');
		
		var clientid = $('#clientid').val();
		if(clientid == '' || clientid == undefined){
			bootbox.alert("Please enter Client ID");
			return false;
		}
		
		var clientsecret = $('#clientsecret').val();
		if(clientsecret == '' || clientsecret == undefined){
			bootbox.alert("Please eneter Client Secret");
			return false;
		}

		if(baseurl == '' || baseurl == undefined){
			bootbox.alert("Please enter FHIR Server URL");
			return false;
		}
        var val = [];
        $('.col-sm-5 :checkbox:checked').each(function(i){
          val[i] = $(this).val();
        });
        console.log(val);
        if(val.length <= 0){
        	bootbox.alert("Please select atleast one Scope");
        	return false;
        }
        var clientscope = val.join(",");

        var patientid = $('#patientid').val();
		/*if(patientid == '' || patientid == undefined){
			bootbox.alert("Please eneter Client Secret");
			return false;
		}*/
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
	                patientid : patientid,
	                redirecturi: redirecturi,
	                tokenurl: tokenurl,
	                strurl: strurl,
	                clientscope: clientscope
	            });
          		fhiroauth(clientid,clientscope,patientid,redirecturi,authurl,baseurl,state);
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
				/*var errordata = JSON.parse(e.responseText);
				//var errormessage = JSON.stringify(errordata.text.div);
				var errormessage = errordata.issue[0].details;
				bootbox.alert(errormessage);*/
			}
		});
	}

	fhiroauth = function(clientid,clientscope,patientid,redirecturi,authurl,baseurl,state) {
        var path = authurl+"?";
        var queryParams = ['client_id=' + clientid,
	        'redirect_uri=' + redirecturi,
	        'response_type=code',
	        'state='+state,
	        'scope=' + clientscope,
	        'aud='+baseurl
        ];
        var query = queryParams.join('&');
        var url = path + query;
        console.log(url);
        window.location.replace(url);
    };

    checkparam = function(){
  		var authbtndiv,authsuccess;
	  	var acctoken = localStorage.getItem("acctoken");
	  	var access_token = localStorage.getItem("access_token");
	  	var execution = localStorage.getItem("execution");
	  	if(execution == "true"){
	  		localStorage.setItem("execution",false);
	  	}
	  	if(access_token != undefined && access_token != ''){
	  		$('#authbtn').toggleClass('btn-danger btn-success');
	  		$('#authsuccess').html('');
	  		authbtndiv = $('<button type="button" class="btn btn-success" data-target="#authorize-modal" data-toggle="modal" data-original-title="" data-toggle="tooltip" id="authbtn" data-backdrop="static"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span> Authorize Server</button>');
	  		/*authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Authentication Token is Present. Run a test by entering various values in the fileds.</strong></div>');
	        $('#authsuccess').append(authsuccess);*/
	        $('#authbtndiv').append(authbtndiv);
	    }else{
	    	authbtndiv = $('<button type="button" class="btn btn-danger" data-target="#authorize-modal" data-toggle="modal" data-original-title="" data-toggle="tooltip" id="authbtn" data-backdrop="static"><span class="glyphicon glyphicon-lock" aria-hidden="true"></span> Authorize Server</button>');
	  		authsuccess = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Authorize your FHIR Server and then run various tests.</strong></div>');
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
	        var authsuccess = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>'+getUrlParameter("error_description")+'</strong></div>');
	        $('#authsuccess').append(authsuccess);
	        return false;
	    }
	    if(state != undefined && code != undefined){
	    	var params = JSON.parse(sessionStorage[state]);
	    	tokenurl = params.tokenurl;
	        clientid = params.clientid;
	        clientsecret = params.clientsecret;
	        patientid = params.patientid;
	        redirecturi = params.redirecturi;
	        strurl = params.strurl;
	        clientscope = params.clientscope;
	        getoauthtoken(code,tokenurl,clientid,clientsecret,patientid,redirecturi,state,strurl,clientscope);
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

    getoauthtoken = function(paramvar,tokenurl,clientid,clientsecret,patientid,redirecturi,state,strurl,clientscope){
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
	        var authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Server Authorized successfully. Run a test by entering various values in the fields.</strong></div>');
	        $('#authsuccess').append(authsuccess);
	        localStorage.setItem('access_token',access_token);
	        localStorage.setItem('acctoken',true);
	        localStorage.setItem('strurl',strurl);
	        localStorage.setItem('patientid',patientid);
	        localStorage.setItem('authtype','auth');
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

  	openserver = function(){
		var openserverurl = $('#openserverurl').val();
		if(openserverurl == null || openserverurl == '' || openserverurl == undefined){
			bootbox.alert("Please enter FHIR Server URL");
			return;
		}
		var openpatid =  $('#openpatid').val();
		if(openpatid == null || openpatid == '' || openpatid == undefined){
			bootbox.alert("Please enter Patient Id");
			return;
		}
		localStorage.removeItem("strurl");
		localStorage.removeItem("access_token");
		localStorage.removeItem("patientid");
		localStorage.setItem("strurl",openserverurl);
		localStorage.setItem("patientid",openpatid);
		localStorage.setItem("authtype","noauth");
		var val = [];
        $('.openscopes :checkbox:checked').each(function(i){
          val[i] = $(this).val();
        });
        console.log(val);
        var openclientscope = val.join(",");
        $('#noauthorize-modal').modal('hide');
        $('#authsuccess').html('');
        var authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Run a test by entering various values in the fields.</strong></div>');
	    $('#authsuccess').append(authsuccess);
	}

}).call(this);