(function(){
	var tr,td,collapsediv,resourceDiv,urlDiv,urlDivres,reqheadersDiv,reqheadersDivres,date,transferencoding,accessorigin,accessheaders,accessmethods,accessage,xpoweredby,lastmodified,contenttype;
	var obslaboratorysuccesscount = 0;
	var labtrigger= 0;
	var obslaboratoryByPatientCategory = "obslaboratoryByPatientCategory";
	var obslaboratoryCollapseByPatientCategory = "obslaboratoryCollapseByPatientCategory";
	var obslaboratoryByPatientCode = "obslaboratoryByPatientCode";
	var obslaboratoryCollapseByPatientCode = "obslaboratoryCollapseByPatientCode";
	var obslaboratoryByPatientCategoryDate = "obslaboratoryByPatientCategoryDate";
	var obslaboratoryCollapseByPatientCategoryDate = "obslaboratoryCollapseByPatientCategoryDate";
	var obslaboratoryByPatientCategoryCodeDate = "obslaboratoryByPatientCategoryCodeDate";
	var obslaboratoryCollapseByPatientCategoryCodeDate = "obslaboratoryCollapseByPatientCategoryCodeDate";
	var l = $( '#executebtn' ).ladda();
	obslaboratoryByPatId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		obslaboratorysuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/Observation?patient="+patientid + "&category=laboratory&_format=json";
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	obslaboratorysuccesscount++;
	          	$('.obslaboratoryrunByPatcategory').html('Passed');
	          	if(labtrigger == 0){
	          		$('.obslaboratoryrun').html('Passed');
	          	}
	          	$('.obslaboratorypass').html(obslaboratorysuccesscount);
	          	$('#authsuccess').html('');
		  		authsuccess = $('<div class="alert alert-success" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Server Authorized Successfully. Run a test by entering various values in the fileds.</strong></div>');
		        $('#authsuccess').append(authsuccess);
	          	obslaboratoryByPatCode(strurl,data,obslaboratoryByPatientCode,obslaboratoryCollapseByPatientCode);
	          	obslaboratoryByPatCategoryDate(strurl,data,obslaboratoryByPatientCategoryDate,obslaboratoryCollapseByPatientCategoryDate);
	          	obslaboratoryByPatCategoryCodeDate(strurl,data,obslaboratoryByPatientCategoryCodeDate,obslaboratoryCollapseByPatientCategoryCodeDate);
	          	renderobslaboratoryresults(data,strurl,xhr,obslaboratoryByPatientCategory,obslaboratoryCollapseByPatientCategory,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	labtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.obslaboratoryrunByPatcategory').html('Failed');
	        	$('.obslaboratoryrun').html('Failed');
	        	$('.obslaboratoryrun').parent().addClass('bg-danger');
	        	renderobslaboratoryerror(e,obslaboratoryByPatientCategory);
	   		}
		})
	}

	obslaboratoryByPatCode = function(strurl,data,obslaboratoryByPatientCode,obslaboratoryCollapseByPatientCode){
		var codearr=[];
			if(data.entry){
				for(var i=0; i< data.entry.length; i++){
					codearr.push(data.entry[i].resource.code.coding[0].code);
				}
			}else{
				codearr.push(data.code.coding[0].code);
			}
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Observation?patient="+patientid+"&category=laboratory&code="+codearr.join(',')+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	obslaboratorysuccesscount++;
	          	$('.obslaboratoryrunByPatCode').html('Passed');
	          	if(labtrigger == 0){
	          		$('.obslaboratoryrun').html('Passed');
	          	}
	          	$('.obslaboratorypass').html(obslaboratorysuccesscount);
	          	renderobslaboratoryresults(data,strurl,xhr,obslaboratoryByPatientCode,obslaboratoryCollapseByPatientCode,resType);
	        },
	        error:function(e){
	        	labtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.obslaboratoryrunByPatCode').html('Failed');
	        	$('.obslaboratoryrun').html('Failed');
	        	$('.obslaboratoryrun').parent().addClass('bg-danger');
	        	renderobslaboratoryerror(e,obslaboratoryByPatientCode);
	   		}
		});
	}

	obslaboratoryByPatCategoryDate = function(strurl,data,obslaboratoryByPatientCategoryDate,obslaboratoryCollapseByPatientCategoryDate){
		var datearr;
			if(data.entry){
				datearr = data.entry[0].resource.effectiveDateTime;
			}else{
				datearr = data.effectiveDateTime;
			}
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Observation?patient="+patientid+"&category=laboratory&date="+datearr+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	obslaboratorysuccesscount++;
	          	$('.obslaboratoryrunByPatCategoryDate').html('Passed');
	          	if(labtrigger == 0){
	          		$('.obslaboratoryrun').html('Passed');
	          	}
	          	$('.obslaboratorypass').html(obslaboratorysuccesscount);
	          	renderobslaboratoryresults(data,strurl,xhr,obslaboratoryByPatientCategoryDate,obslaboratoryCollapseByPatientCategoryDate,resType);
	        },
	        error:function(e){
	        	labtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.obslaboratoryrunByPatCategoryDate').html('Failed');
	        	$('.obslaboratoryrun').html('Failed');
	        	$('.obslaboratoryrun').parent().addClass('bg-danger');
	        	renderobslaboratoryerror(e,obslaboratoryByPatientCategoryDate);
	   		}
		});
	}

	obslaboratoryByPatCategoryCodeDate = function(strurl,data,obslaboratoryByPatientCategoryCodeDate,obslaboratoryCollapseByPatientCategoryCodeDate){
		var codearr= [];
		var datearr;
			if(data.entry){
				datearr = data.entry[0].resource.effectiveDateTime;
				for(var i=0; i< data.entry.length; i++){
					codearr.push(data.entry[i].resource.code.coding[0].code);
				}
			}else{
				datearr = data.effectiveDateTime;
				codearr.push(data.code.coding[0].code);
			}
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Observation?patient="+patientid+"&category=laboratory&code="+codearr.join(',')+"&date="+datearr+"&_format=json";
		access_token = localStorage.getItem("access_token");
		$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            if(localStorage.getItem("authtype") == 'auth'){
	        		xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	        	}
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	        	var resType='';
	        	if(data.entry){
	        		resType =  data.entry[0].resource.resourceType;
	        	}else if(data.resourceType){
					resType = data.resourceType;
	        	}
	          	obslaboratorysuccesscount++;
	          	$('.obslaboratoryrunByPatCategoryCodeDate').html('Passed');
	          	if(labtrigger == 0){
	          		$('.obslaboratoryrun').html('Passed');
	          	}
	          	$('.obslaboratorypass').html(obslaboratorysuccesscount);
	          	renderobslaboratoryresults(data,strurl,xhr,obslaboratoryByPatientCategoryCodeDate,obslaboratoryCollapseByPatientCategoryCodeDate,resType);
	        },
	        error:function(e){
	        	labtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.obslaboratoryrunByPatCategoryCodeDate').html('Failed');
	        	$('.obslaboratoryrun').html('Failed');
	        	$('.obslaboratoryrun').parent().addClass('bg-danger');
	        	renderobslaboratoryerror(e,obslaboratoryByPatientCategoryCodeDate);
	   		}
		});
	}

	renderobslaboratoryresults = function(data,strurl,xhr,trid,colbyIdentifier,resType){
			/*$('.removabletr').remove();*/
			tr = $('<tr class="obslaboratoryremovabletr"></tr>');
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

			//Response Body
			respbodyDiv = $('<div class="col-md-12"><label class="col-md-3">Response Body:</label></div>');
			respbodyDivres = $('<div class="col-md-9 comment more" style="word-break:break-all">'+JSON.stringify(data)+'</div>');
			respbodyDiv.append(respbodyDivres);
			collapsediv.append(respbodyDiv);

			td.append(collapsediv);
			tr.append(td);
			$('#'+trid).after(tr);
		$('.comment').shorten();
	}

	renderobslaboratoryerror = function(e,trid){
		var authfail;
		tr = $('<tr class="obslaboratoryremovabletr"></tr>');
		td = $('<td colspan="6" class="hiddenRow"></td>');
		if(e.status == '401'){
	        localStorage.removeItem("access_token");
	        authfail = $('<div class="alert alert-danger" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Unauthorized: Authentication token is expired.</strong></div>');
		}else{
	        authfail = $('<div class="alert alert-danger" style="text-align:center;margin-bottom:0px;padding:12px"><strong>'+e.responseJSON.resourceType+" - "+e.responseJSON.issue[0].diagnostics+'</strong></div>');
		}
		td.append(authfail);
		tr.append(td);
		$('#'+trid).after(tr);
	}
}).call(this);