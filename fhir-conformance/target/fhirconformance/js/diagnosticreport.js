(function(){
	var diagnosticreportsuccesscount = 0;
	var diagnostictrigger = 0;
	var diagnosticreportByPatientCategory = "diagnosticreportByPatientCategory";
	var diagnosticreportCollapseByPatientCategory = "diagnosticreportCollapseByPatientCategory";
	var diagnosticreportByPatientCode = "diagnosticreportByPatientCode";
	var diagnosticreportCollapseByPatientCode = "diagnosticreportCollapseByPatientCode";
	var diagnosticreportByPatientCategoryDate = "diagnosticreportByPatientCategoryDate";
	var diagnosticreportCollapseByPatientCategoryDate = "diagnosticreportCollapseByPatientCategoryDate";
	var diagnosticreportByPatientCategoryCodeDate = "diagnosticreportByPatientCategoryCodeDate";
	var diagnosticreportCollapseByPatientCategoryCodeDate = "diagnosticreportCollapseByPatientCategoryCodeDate";
	var l = $( '#executebtn' ).ladda();
	diagnosticreportByPatId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		diagnosticreportsuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/DiagnosticReport?patient="+patientid + "&category=LAB&_format=json";
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
	          	diagnosticreportsuccesscount++;
	          	$('.diagnosticreportrunByPatcategory').html('Passed');
	          	if(diagnostictrigger == 0){
	          		$('.diagnosticreportrun').html('Passed');
	          	}
	          	$('.diagnosticreportpass').html(diagnosticreportsuccesscount);
	          	diagnosticreportByPatCode(strurl,data,diagnosticreportByPatientCode,diagnosticreportCollapseByPatientCode);
	          	diagnosticreportByPatCategoryDate(strurl,data,diagnosticreportByPatientCategoryDate,diagnosticreportCollapseByPatientCategoryDate);
	          	diagnosticreportByPatCategoryCodeDate(strurl,data,diagnosticreportByPatientCategoryCodeDate,diagnosticreportCollapseByPatientCategoryCodeDate);
	          	renderresults(data,strurl,xhr,diagnosticreportByPatientCategory,diagnosticreportCollapseByPatientCategory,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	diagnostictrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.diagnosticreportrunByPatcategory').html('Failed');
	        	$('.diagnosticreportrun').html('Failed');
	        	$('.diagnosticreportrun').parent().addClass('bg-danger');
	        	rendererror(e,diagnosticreportByPatientCategory);
	   		}
		})
	}

	diagnosticreportByPatCode = function(strurl,data,diagnosticreportByPatientCode,diagnosticreportCollapseByPatientCode){
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
		strurl = strurl + "/DiagnosticReport?patient="+patientid+"&code="+codearr.join(',')+"&_format=json";
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
	          	diagnosticreportsuccesscount++;
	          	$('.diagnosticreportrunByPatCode').html('Passed');
	          	if(diagnostictrigger == 0){
	          		$('.diagnosticreportrun').html('Passed');
	          	}
	          	$('.diagnosticreportpass').html(diagnosticreportsuccesscount);
	          	renderresults(data,strurl,xhr,diagnosticreportByPatientCode,diagnosticreportCollapseByPatientCode,resType);
	        },
	        error:function(e){
	        	diagnostictrigger =1;
	        	l.ladda( 'stop' );
	        	$('.diagnosticreportrunByPatCode').html('Failed');
	        	$('.diagnosticreportrun').html('Failed');
	        	$('.diagnosticreportrun').parent().addClass('bg-danger');
	        	rendererror(e,diagnosticreportByPatientCode);
	   		}
		});
	}

	diagnosticreportByPatCategoryDate = function(strurl,data,diagnosticreportByPatientCategoryDate,diagnosticreportCollapseByPatientCategoryDate){
		var datearr;
			if(data.entry){
				datearr = data.entry[0].resource.effectiveDateTime;
			}else{
				datearr = data.effectiveDateTime;
			}
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/DiagnosticReport?patient="+patientid+"&category=LAB&date="+datearr+"&_format=json";
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
	          	diagnosticreportsuccesscount++;
	          	$('.diagnosticreportrunByPatCategoryDate').html('Passed');
	          	if(diagnostictrigger == 0){
	          		$('.diagnosticreportrun').html('Passed');
	          	}
	          	$('.diagnosticreportpass').html(diagnosticreportsuccesscount);
	          	renderresults(data,strurl,xhr,diagnosticreportByPatientCategoryDate,diagnosticreportCollapseByPatientCategoryDate,resType);
	        },
	        error:function(e){
	        	diagnostictrigger =1;
	        	l.ladda( 'stop' );
	        	$('.diagnosticreportrunByPatCategoryDate').html('Failed');
	        	$('.diagnosticreportrun').html('Failed');
	        	$('.diagnosticreportrun').parent().addClass('bg-danger');
	        	rendererror(e,diagnosticreportByPatientCategoryDate);
	   		}
		});
	}

	diagnosticreportByPatCategoryCodeDate = function(strurl,data,diagnosticreportByPatientCategoryCodeDate,diagnosticreportCollapseByPatientCategoryCodeDate){
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
		strurl = strurl + "/DiagnosticReport?patient="+patientid+"&category=LAB&code="+codearr.join(',')+"&date="+datearr+"&_format=json";
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
	          	diagnosticreportsuccesscount++;
	          	$('.diagnosticreportrunByPatCategoryCodeDate').html('Passed');
	          	if(diagnostictrigger == 0){
	          		$('.diagnosticreportrun').html('Passed');
	          	}
	          	$('.diagnosticreportpass').html(diagnosticreportsuccesscount);
	          	renderresults(data,strurl,xhr,diagnosticreportByPatientCategoryCodeDate,diagnosticreportCollapseByPatientCategoryCodeDate,resType);
	        },
	        error:function(e){
	        	diagnostictrigger =1;
	        	l.ladda( 'stop' );
	        	$('.diagnosticreportrunByPatCategoryCodeDate').html('Failed');
	        	$('.diagnosticreportrun').html('Failed');
	        	$('.diagnosticreportrun').parent().addClass('bg-danger');
	        	rendererror(e,diagnosticreportByPatientCategoryCodeDate);
	   		}
		});
	}
}).call(this);