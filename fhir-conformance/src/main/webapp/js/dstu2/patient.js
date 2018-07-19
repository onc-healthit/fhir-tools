(function(){
	var tr,td,collapsediv,resourceDiv,urlDiv,urlDivres,reqheadersDiv,reqheadersDivres,date,transferencoding,accessorigin,accessheaders,accessmethods,accessage,xpoweredby,lastmodified,contenttype;;
	var patientSuccessCount = 0;
	var trigger = 0;
	var patById = "patById";
	var patByIdentifier = "patByIdentifier";
	var patByNameGender = "patByNameGender";
	var patByFamilyGender = "patByFamilyGender";
	var patByGivenGender = "patByGivenGender";
	var patByNameBirthdate = "patByNameBirthdate";
	var colbyId = "byId";
	var colbyIdentifier = "byIdentifier";
	var colbyNameGender = "byNameGender";
	var colbyFamilyGender = "byFamilyGender";
	var colbyGivenGender = "byGivenGender";
	var colbyNameBirthdate = "byNameBirthdate";
	var l = $( '#executebtn' ).ladda();
	patientById =  function(strurl){
		l.ladda( 'start' );
		patientSuccessCount = 0;
    	access_token = localStorage.getItem("access_token");
    	var patientid = localStorage.getItem("patientid");
    	strurl = strurl+ "/Patient/" +patientid + "?_format=json"
      	$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/json");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	patientSuccessCount++;
	          	$('.patientrunById').html('Passed');
	          	if(trigger == 0){
	          		$('.patientrun').html('Passed');
	          	}
	          	$('.patientpass').html(patientSuccessCount);
	          	patientByIdentifier(strurl,data,patByIdentifier,colbyIdentifier);
	          	patientByNameandGender(strurl,data,patByNameGender,colbyNameGender);
	          	patientByFamilyandGender(strurl,data,patByFamilyGender,colbyFamilyGender);
	          	patientByGivenandGender(strurl,data,patByGivenGender,colbyGivenGender);
	          	patientByNameandBirthdate(strurl,data,patByNameBirthdate,colbyNameBirthdate);
	          	//renderresults(data,strurl,xhr,patById,colbyId).done(patientByIdentifier(strurl,data,patByIdentifier,colbyIdentifier)).done(patientByNameandGender(strurl,data,patByNameGender,colbyNameGender)).done(patientByFamilyandGender(strurl,data,patByFamilyGender,colbyFamilyGender)).done(patientByGivenandGender(strurl,data,patByGivenGender,colbyGivenGender)).done(patientByNameandBirthdate(strurl,data,patByNameBirthdate,colbyNameBirthdate));
	          	$('#authsuccess').html('');
		  		authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Server Authorized Successfully. Run a test by entering various values in the fileds.</strong></div>');
		        $('#authsuccess').append(authsuccess);
	          	renderresults(data,strurl,xhr,patById,colbyId,resType);
	          	l.ladda( 'stop' );
	        },
	        error:function(e){
	        	trigger = 1;
	        	l.ladda( 'stop' );
	        	$('.patientrunById').html('Failed');
	        	$('.patientrun').html('Failed');
	        	$('.patientrun').parent().addClass('bg-danger');
	        	rendererror(e,patById);
	   		}
		});
	};

	patientByIdentifier =  function(strurl,data,patByIdentifier,colbyIdentifier){
		var identifierSystem = data.identifier[0].system;
		var identifierValue = data.identifier[0].value;
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/" + data.resourceType + "/?identifier="+identifierSystem+"|"+identifierValue+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/json+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	patientSuccessCount++;
	          	$('.patientrunByIdentifier').html('Passed');
	          	if(trigger == 0){
	          		$('.patientrun').html('Passed');
	          	}
	          	$('.patientpass').html(patientSuccessCount);
	          	renderresults(data,strurl,xhr,patByIdentifier,colbyIdentifier,resType);
	        },
	        error:function(e){
	        	trigger = 1;
	        	l.ladda( 'stop' );
	        	$('.patientrunByIdentifier').html('Failed');
	        	$('.patientrun').html('Failed');
	        	$('.patientrun').parent().addClass('bg-danger');
	        	rendererror(e,patByIdentifier);
	   		}
		});
	}

	patientByNameandGender = function(strurl,data,patByNameGender,colbyNameGender){
		var patName = data.name[0].family;
		var patGender = data.gender;
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/" + data.resourceType + "/?name="+patName+"&gender="+patGender+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/json+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	patientSuccessCount++;
	          	$('.patientrunByNameGender').html('Passed');
	          	if(trigger == 0){
	          		$('.patientrun').html('Passed');
	          	}
	          	$('.patientpass').html(patientSuccessCount);
	          	renderresults(data,strurl,xhr,patByNameGender,colbyNameGender,resType);
	        },
	        error:function(e){
	        	trigger = 1;
	        	l.ladda( 'stop' );
	        	$('.patientrunByNameGender').html('Failed');
	        	$('.patientrun').html('Failed');
	        	$('.patientrun').parent().addClass('bg-danger');
	        	rendererror(e,patByNameGender);
	   		}
		});
	}

	patientByFamilyandGender = function(strurl,data,patByFamilyGender,colbyFamilyGender){
		var patFamilyName = data.name[0].family;
		var patGender = data.gender;
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/" + data.resourceType + "/?family="+patFamilyName+"&gender="+patGender+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/json+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	patientSuccessCount++;
	          	$('.patientrunByFamilyGender').html('Passed');
	          	if(trigger == 0){
	          		$('.patientrun').html('Passed');
	          	}
	          	$('.patientpass').html(patientSuccessCount);
	          	renderresults(data,strurl,xhr,patByFamilyGender,colbyFamilyGender,resType);
	        },
	        error:function(e){
	        	trigger = 1;
	        	l.ladda( 'stop' );
	        	$('.patientrunByFamilyGender').html('Failed');
	        	$('.patientrun').html('Failed');
	        	$('.patientrun').parent().addClass('bg-danger');
	        	rendererror(e,patByFamilyGender);
	   		}
		});
	}

	patientByGivenandGender = function(strurl,data,patByGivenGender,colbyGivenGender){
		var patGivenName = data.name[0].given;
		var patGender = data.gender;
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/" + data.resourceType + "/?given="+patGivenName+"&gender="+patGender+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/json+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	patientSuccessCount++;
	          	$('.patientrunByGivenGender').html('Passed');
	          	if(trigger == 0){
	          		$('.patientrun').html('Passed');
	          	}
	          	$('.patientpass').html(patientSuccessCount);
	          	renderresults(data,strurl,xhr,patByGivenGender,colbyGivenGender,resType);
	        },
	        error:function(e){
	        	trigger = 1;
	        	l.ladda( 'stop' );
	        	$('.patientrunByGivenGender').html('Failed');
	        	$('.patientrun').html('Failed');
	        	$('.patientrun').parent().addClass('bg-danger');
	        	rendererror(e,patByGivenGender);
	   		}
		});
	}

	patientByNameandBirthdate = function(strurl,data,patByNameBirthdate,colbyNameBirthdate){
		var patName = data.name[0].family;
		var patBirthdate = data.birthDate;
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/" + data.resourceType + "/?name="+patName+"&birthdate="+patBirthdate+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/json+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	patientSuccessCount++;
	          	$('.patientrunByNameBirthDate').html('Passed');
	          	if(trigger == 0){
	          		$('.patientrun').html('Passed');
	          	}
	          	$('.patientpass').html(patientSuccessCount);
	          	renderresults(data,strurl,xhr,patByNameBirthdate,colbyNameBirthdate,resType);
	        },
	        error:function(e){
	        	trigger = 1;
	        	l.ladda( 'stop' );
	        	$('.patientrunByNameBirthDate').html('Failed');
	        	$('.patientrun').html('Failed');
	        	$('.patientrun').parent().addClass('bg-danger');
	        	rendererror(e,patByNameBirthdate);
	   		}
		});
	}

	renderresults = function(data,strurl,xhr,trid,colbyIdentifier,resType){
			/*$('.removabletr').remove();*/
			tr = $('<tr class="patientremovabletr"></tr>');
			td = $('<td colspan="6" class="hiddenRow"></td>');
			collapsediv = $('<div class="accordian-body collapse" id='+colbyIdentifier+'><h5>Test Details</h5></div>');
			// Resource
			resourceDiv = $('<div class="col-md-12"><label class="col-md-3">Resource:</label></div>');
			resourceDivres = $('<div class="col-md-9" style="word-break:break-all">'+resType+'</div>');
			resourceDiv.append(resourceDivres);
			collapsediv.append(resourceDiv);

			//URL
			urlDiv = $('<div class="col-md-12"><label class="col-md-3">URL:</label></div>');
			urlDivres = $('<div class="col-md-9" style="word-break:break-all">'+strurl+'</div>');
			urlDiv.append(urlDivres);
			collapsediv.append(urlDiv);

			//Request Headers
			reqheadersDiv = $('<div class="col-md-12"><label class="col-md-3">Request Headers:</label></div>');
			reqheadersDivres = $('<div class="col-md-9" style="word-break:break-all"></div>');
			/* req headers start */
				useragent = $('<div class="header-text"><u>User-Agent</u>: FHIR Client</div>');
		    	contenttype = $('<div class="header-text"><u>Content-Type</u>: application/xml+fhir;charset=UTF-8</div>');
		    	acceptchar = $('<div class="header-text"><u>Accept-Charset</u>: UTF-8</div>');
		    	accept = $('<div class="header-text"><u>Accept</u>: application/xml+fhir</div>');
		    	format = $('<div class="header-text"><u>format</u>: application/xml+fhir</div>');
		    	authorization = $('<div class="header-text"><u>Authorization</u>: Bearer '+access_token+'</div>');
			/* req headers end */
			reqheadersDivres.append(useragent,contenttype,acceptchar,accept,format,authorization);
			reqheadersDiv.append(reqheadersDivres);
			collapsediv.append(reqheadersDiv);

			//Response Headers
			respheadersDiv = $('<div class="col-md-12"><label class="col-md-3">Response Headers:</label></div>');
			respheadersDivres = $('<div class="col-md-9" style="word-break:break-all"></div>');
			/* resp headers start */
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
			/* resp headers end */
			respheadersDivres.append(date,transferencoding,accessorigin,accessheaders,accessmethods,accessage,xpoweredby,lastmodified,contenttype);
			respheadersDiv.append(respheadersDivres);
			collapsediv.append(respheadersDiv);

			// Response Validation
			renderdstu2ResponseValidation(collapsediv, data);

			//Response Body
			respbodyDiv = $('<div class="col-md-12"><label class="col-md-3">Response Body:</label></div>');
			respbodyDivres = $('<div class="col-md-9" style="word-break:break-all"><pre class="comment more">'+JSON.stringify(data,undefined,2)+'</pre></div>');
			respbodyDiv.append(respbodyDivres);
			collapsediv.append(respbodyDiv);

			td.append(collapsediv);
			tr.append(td);
			$('#'+trid).after(tr);
		$('.comment').shorten();
	}

	rendererror = function(e,trid){
		console.log(e);
		var authfail;
		tr = $('<tr class="patientremovabletr"></tr>');
		td = $('<td colspan="6" class="hiddenRow"></td>');
		if(e.status == '401'){
	       	var parseHtml = $(e.responseText);
			var foundElement = parseHtml.find('u');

	        authfail = $('<div class="alert alert-danger" style="text-align:center;margin-bottom:0px;padding:12px"><strong>'+$(foundElement[0]).text()+'</strong></div>');
		}else{
	        authfail = $('<div class="alert alert-danger" style="text-align:center;margin-bottom:0px;padding:12px"><strong>'+e.responseJSON.resourceType+" - "+e.responseJSON.issue[0].diagnostics+'</strong></div>');
		}
		td.append(authfail);
		tr.append(td);
		$('#'+trid).after(tr);
	}
}).call(this);