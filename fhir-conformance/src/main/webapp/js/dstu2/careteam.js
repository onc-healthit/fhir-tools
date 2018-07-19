(function(){
	var tr,td,collapsediv,resourceDiv,urlDiv,urlDivres,reqheadersDiv,reqheadersDivres,date,transferencoding,accessorigin,accessheaders,accessmethods,accessage,xpoweredby,lastmodified,contenttype;;
	var careteamSuccessCount = 0;
	var careteamtrigger = 0;
	var careteamByPatientId = "careteamByPatientId";
	var careteamCollapseByPatientId = "careteamCollapseByPatientId";
	var careteamByPatCategoryStatus = "careteamByPatCategoryStatus";
	var careteamCollapseByPatientCategoryStatus = "careteamCollapseByPatientCategoryStatus";
	var careteamByPatCategoryassess = "careteamByPatCategoryassess";
	var careteamCollapseByPatientCategoryassess = "careteamCollapseByPatientCategoryassess";
	var careteamByPatCategoryassessDate = "careteamByPatCategoryassessDate";
	var careteamCollapseByPatientCategoryassessDate = "careteamCollapseByPatientCategoryassessDate";
	var careteamByPatCategoryassessStatus = "careteamByPatCategoryassessStatus";
	var careteamCollapseByPatientCategoryassessStatus = "careteamCollapseByPatientCategoryassessStatus";
	var careteamByPatCategoryassessStatusDate = "careteamByPatCategoryassessStatusDate";
	var careteamCollapseByPatientCategoryassessStatusDate = "careteamCollapseByPatientCategoryassessStatusDate";
	var l = $( '#executebtn' ).ladda();
	careteamByPatient =  function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		careteamSuccessCount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/CarePlan?patient="+patientid + "&_format=json";
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
	          	careteamSuccessCount++;
	          	$('.careteamrunByPatId').html('Passed');
	          	if(careteamtrigger == 0){
	          		$('.careteamrun').html('Passed');
	          	}
	          	$('.careteampass').html(careteamSuccessCount);
	          	$('#authsuccess').html('');
		  		authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Server Authorized Successfully. Run a test by entering various values in the fileds.</strong></div>');
		        $('#authsuccess').append(authsuccess);
	          	careteamByPatientCategoryStatus(strurl,data,careteamByPatCategoryStatus,careteamCollapseByPatientCategoryStatus);
	          	careteamByPatientCategoryassess(strurl,data,careteamByPatCategoryassess,careteamCollapseByPatientCategoryassess);
	          	careteamByPatientCategoryassessDate(strurl,data,careteamByPatCategoryassessDate,careteamCollapseByPatientCategoryassessDate);
	          	careteamByPatientCategoryassessStatus(strurl,data,careteamByPatCategoryassessStatus,careteamCollapseByPatientCategoryassessStatus);
	          	careteamByPatientCategoryassessStatusDate(strurl,data,careteamByPatCategoryassessStatusDate,careteamCollapseByPatientCategoryassessStatusDate);
	          	rendercareteamresults(data,strurl,xhr,careteamByPatientId,careteamCollapseByPatientId,resType);
	          	l.ladda( 'stop' );
	        },
	        error:function(e){
	        	careteamtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.careteamrunByPatId').html('Failed');
	        	$('.careteamrun').html('Failed');
	        	$('.careteampass').parent().addClass('bg-danger');
	        	rendercareteamerror(e,careteamByPatientId);
	   		}
		})
	}

	careteamByPatientCategoryStatus = function(strurl,data,careteamByPatCategoryStatus,careteamCollapseByPatientCategoryStatus){
		var category,status;
		if(data.entry){
	        category =  data.entry[0].resource.category[0].coding[0].code;
	        status = data.entry[0].resource.status;
	    }else{
	    	if(data.category){
	    		category = data.category[0].coding[0].code;
	    	}else{
					return;
				}
	    	if(data.status){
	    		status = data.status;
	    	}else{
					return;
				}
	    }
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/CarePlan?patient="+patientid+"&category=careteam&status="+status+"&_format=json";
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
	          	careteamSuccessCount++;
	          	$('.careteamrunByPatCategoryStatus').html('Passed');
	          	if(careteamtrigger == 0){
	          		$('.careteamrun').html('Passed');
	          	}
	          	$('.careteampass').html(careteamSuccessCount);
	          	rendercareteamresults(data,strurl,xhr,careteamByPatCategoryStatus,careteamCollapseByPatientCategoryStatus,resType);
	        },
	        error:function(e){
	        	careteamtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.careteamrunByPatCategoryStatus').html('Failed');
	        	$('.careteamrun').html('Failed');
	        	$('.careteampass').parent().addClass('bg-danger');
	        	rendercareteamerror(e,careteamByPatCategoryStatus);
	   		}
		});
	}

	careteamByPatientCategoryassess = function(strurl,data,careteamByPatCategoryassess,careteamCollapseByPatientCategoryassess){
		var category,status;
		if(data.entry){
	        category =  data.entry[0].resource.category[0].coding[0].code;
	        status = data.entry[0].resource.status;
	    }else{
			if(data.category){
	    		category = data.category[0].coding[0].code;
	    	}else{
					return;
				}
	    	if(data.status){
	    		status = data.status;
	    	}else{
					return;
				}
	    }
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/CarePlan?patient="+patientid+"&category=assess-plan&_format=json";
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
	          	careteamSuccessCount++;
	          	$('.careteamrunByPatCategoryassess').html('Passed');
	          	if(careteamtrigger == 0){
	          		$('.careteamrun').html('Passed');
	          	}
	          	$('.careteampass').html(careteamSuccessCount);
	          	rendercareteamresults(data,strurl,xhr,careteamByPatCategoryassess,careteamCollapseByPatientCategoryassess,resType);
	        },
	        error:function(e){
	        	careteamtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.careteamrunByPatCategoryassess').html('Failed');
	        	$('.careteamrun').html('Failed');
	        	$('.careteampass').parent().addClass('bg-danger');
	        	rendercareteamerror(e,careteamByPatCategoryassess);
	   		}
		});
	}

	careteamByPatientCategoryassessDate = function(strurl,data,careteamByPatCategoryassessDate,careteamCollapseByPatientCategoryassessDate){
		var category,status,date;
		if(data.entry){
	        category =  data.entry[0].resource.category[0].coding[0].code;
	        status = data.entry[0].resource.status;
	        date = data.entry[0].resource.period.start;
	    }else{
			if(data.category){
	    		category = data.category[0].coding[0].code;
	    	}else{
					return;
				}
	    	if(data.status){
	    		status = data.status;
	    	}else{
					return;
				}
	    	if(data.period){
	    		date = data.period.start;
	    	}else{ return; }
	    }
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/CarePlan?patient="+patientid+"&category=assess-plan&date="+date+"&_format=json";
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
	          	careteamSuccessCount++;
	          	$('.careteamrunByPatCategoryassessDate').html('Passed');
	          	if(careteamtrigger == 0){
	          		$('.careteamrun').html('Passed');
	          	}
	          	$('.careteampass').html(careteamSuccessCount);
	          	rendercareteamresults(data,strurl,xhr,careteamByPatCategoryassessDate,careteamCollapseByPatientCategoryassessDate,resType);
	        },
	        error:function(e){
	        	careteamtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.careteamrunByPatCategoryassessDate').html('Failed');
	        	$('.careteamrun').html('Failed');
	        	$('.careteampass').parent().addClass('bg-danger');
	        	rendercareteamerror(e,careteamByPatCategoryassessDate);
	   		}
		});
	}

	careteamByPatientCategoryassessStatus = function(strurl,data,careteamByPatCategoryassessStatus,careteamCollapseByPatientCategoryassessStatus){
		var category,status;
		if(data.entry){
	        category =  data.entry[0].resource.category[0].coding[0].code;
	        status = data.entry[0].resource.status;
	    }else{
			if(data.category){
	    		category = data.category[0].coding[0].code;
	    	}else{ return; }
	    	if(data.status){
	    		status = data.status;
	    	}else{ return; }
	    }
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/CarePlan?patient="+patientid+"&category=assess-plan&status="+status+"&_format=json";
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
	          	careteamSuccessCount++;
	          	$('.careteamrunByPatCategoryassessStatus').html('Passed');
	          	if(careteamtrigger == 0){
	          		$('.careteamrun').html('Passed');
	          	}
	          	$('.careteampass').html(careteamSuccessCount);
	          	rendercareteamresults(data,strurl,xhr,careteamByPatCategoryassessStatus,careteamCollapseByPatientCategoryassessStatus,resType);
	        },
	        error:function(e){
	        	careteamtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.careteamrunByPatCategoryassessStatus').html('Failed');
	        	$('.careteamrun').html('Failed');
	        	$('.careteampass').parent().addClass('bg-danger');
	        	rendercareteamerror(e,careteamByPatCategoryassessStatus);
	   		}
		});
	}

	careteamByPatientCategoryassessStatusDate = function(strurl,data,careteamByPatCategoryassessStatusDate,careteamCollapseByPatientCategoryassessStatusDate){
		var category,status,date;
		if(data.entry){
	        category =  data.entry[0].resource.category[0].coding[0].code;
	        status = data.entry[0].resource.status;
	        date = data.entry[0].resource.period.start;
	    }else{
			if(data.category){
	    		category = data.category[0].coding[0].code;
	    	}else{ return; }
	    	if(data.status){
	    		status = data.status;
	    	}else{ return; }
			if(data.period){
	    		date = data.period.start;
	    	}else{ return; }
	    }
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/CarePlan?patient="+patientid+"&category=assess-plan&status="+status+"&date="+date+"&_format=json";
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
	          	careteamSuccessCount++;
	          	$('.careteamrunByPatCategoryassessStatusDate').html('Passed');
	          	if(careteamtrigger == 0){
	          		$('.careteamrun').html('Passed');
	          	}
	          	$('.careteampass').html(careteamSuccessCount);
	          	rendercareteamresults(data,strurl,xhr,careteamByPatCategoryassessStatusDate,careteamCollapseByPatientCategoryassessStatusDate,resType);
	        },
	        error:function(e){
	        	careteamtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.careteamrunByPatCategoryassessStatus').html('Failed');
	        	$('.careteamrun').html('Failed');
	        	$('.careteampass').parent().addClass('bg-danger');
	        	rendercareteamerror(e,careteamByPatCategoryassessStatusDate);
	   		}
		});
	}

	rendercareteamresults = function(data,strurl,xhr,trid,colbyIdentifier,resType){
			/*$('.removabletr').remove();*/
			tr = $('<tr class="careteamremovabletr"></tr>');
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

	rendercareteamerror = function(e,trid){
		var authfail;
		tr = $('<tr class="careteamremovabletr"></tr>');
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