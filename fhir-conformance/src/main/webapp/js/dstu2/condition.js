(function(){
	var tr,td,collapsediv,resourceDiv,urlDiv,urlDivres,reqheadersDiv,reqheadersDivres,date,transferencoding,accessorigin,accessheaders,accessmethods,accessage,xpoweredby,lastmodified,contenttype;;
	var conditionSuccessCount = 0;
	var conditiontrigger = 0;
	var conditionByPatientId = "conditionByPatientId";
	var conditionCollapseByPatientId = "conditionCollapseByPatientId";
	var conditionByPatientandCategory = "conditionByPatientandCategory";
	var conditionCollapseByPatientandCategory = "conditionCollapseByPatientandCategory";
	var conditionByPatientandCategoryhealthconcern = "conditionByPatientandCategoryhealthconcern";
	var conditionCollapseByPatientandCategoryhealthconcern = "conditionCollapseByPatientandCategoryhealthconcern";
	var conditionByPatientandClinicalStatus = "conditionByPatientandClinicalStatus";
	var conditionCollapseByPatientandClinicalStatus = "conditionCollapseByPatientandClinicalStatus";
	var l = $( '#executebtn' ).ladda();
	conditionByPatient =  function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		conditionSuccessCount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/Condition?patient="+patientid + "&_format=json";
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
	          	conditionSuccessCount++;
	          	$('.conditionrunByPatId').html('Passed');
	          	if(conditiontrigger == 0){
	          		$('.conditionrun').html('Passed');
	          	}
	          	$('.conditionpass').html(conditionSuccessCount);
	          	$('#authsuccess').html('');
		  		authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Server Authorized Successfully. Run a test by entering various values in the fileds.</strong></div>');
		        $('#authsuccess').append(authsuccess);
	          	ConditionByPatientCategoryproblem(strurl,data,conditionByPatientandCategory,conditionCollapseByPatientandCategory);
	          	ConditionByPatientCategoryhealthconcern(strurl,data,conditionByPatientandCategoryhealthconcern,conditionCollapseByPatientandCategoryhealthconcern);
	          	conditionByPatientClinicalStatus(strurl,data,conditionByPatientandClinicalStatus,conditionCollapseByPatientandClinicalStatus);
	          	renderconditionresults(data,strurl,xhr,conditionByPatientId,conditionCollapseByPatientId,resType);
	          	l.ladda( 'stop' );
	        },
	        error:function(e){
	        	conditiontrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.conditionrunByPatId').html('Failed');
	        	$('.conditionrun').html('Failed');
	        	$('.conditionpass').parent().addClass('bg-danger');
	        	renderconditionerror(e,conditionByPatientId);
	   		}
		})
	}

	ConditionByPatientCategoryproblem = function(strurl,data,conditionByPatientandCategory,conditionCollapseByPatientandCategory){
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Condition?patient="+patientid+"&category=problem&_format=json";
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
	          	conditionSuccessCount++;
	          	$('.conditionrunByPatCategory').html('Passed');
	          	if(conditiontrigger == 0){
	          		$('.conditionrun').html('Passed');
	          	}
	          	$('.conditionpass').html(conditionSuccessCount);
	          	renderconditionresults(data,strurl,xhr,conditionByPatientandCategory,conditionCollapseByPatientandCategory,resType);
	        },
	        error:function(e){
	        	conditiontrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.conditionrunByPatCategory').html('Failed');
	        	$('.conditionrun').html('Failed');
	        	$('.conditionpass').parent().addClass('bg-danger');
	        	renderconditionerror(e,conditionByPatientandCategory);
	   		}
		});
	}

	ConditionByPatientCategoryhealthconcern = function(strurl,data,conditionByPatientandCategoryhealthconcern,conditionCollapseByPatientandCategoryhealthconcern){
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Condition?patient="+patientid+"&category=health-concern&_format=json";
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
	          	conditionSuccessCount++;
	          	$('.conditionrunByPatCategoryhealthconcern').html('Passed');
	          	if(conditiontrigger == 0){
	          		$('.conditionrun').html('Passed');
	          	}
	          	$('.conditionpass').html(conditionSuccessCount);
	          	renderconditionresults(data,strurl,xhr,conditionByPatientandCategoryhealthconcern,conditionCollapseByPatientandCategoryhealthconcern,resType);
	        },
	        error:function(e){
	        	conditiontrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.conditionrunByPatCategoryhealthconcern').html('Failed');
	        	$('.conditionrun').html('Failed');
	        	$('.conditionpass').parent().addClass('bg-danger');
	        	renderconditionerror(e,conditionByPatientandCategoryhealthconcern);
	   		}
		});
	}

	conditionByPatientClinicalStatus = function (strurl,data,conditionByPatientandClinicalStatus,conditionCollapseByPatientandClinicalStatus){
		var status;
		if(data.entry){
	        status =  data.entry[0].resource.clinicalStatus;
	    }else{
	    	if(data.clinicalStatus){
	    		status = data.clinicalStatus;
	    	}else{
	    		return;
	    	}
	    }
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Condition?patient="+patientid+"&clinicalstatus="+status+"&_format=json";
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
	          	conditionSuccessCount++;
	          	$('.conditionrunByPatClinicalStatus').html('Passed');
	          	if(conditiontrigger == 0){
	          		$('.conditionrun').html('Passed');
	          	}
	          	$('.conditionpass').html(conditionSuccessCount);
	          	renderconditionresults(data,strurl,xhr,conditionByPatientandClinicalStatus,conditionCollapseByPatientandClinicalStatus,resType);
	        },
	        error:function(e){
	        	conditiontrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.conditionrunByPatClinicalStatus').html('Failed');
	        	$('.conditionrun').html('Failed');
	        	$('.conditionpass').parent().addClass('bg-danger');
	        	renderconditionerror(e,conditionByPatientandClinicalStatus);
	   		}
		});
	}

	renderconditionresults = function(data,strurl,xhr,trid,colbyIdentifier,resType){
			/*$('.removabletr').remove();*/
			tr = $('<tr class="conditionremovabletr"></tr>');
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

	renderconditionerror = function(e,trid){
		var authfail;
		tr = $('<tr class="conditionremovabletr"></tr>');
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