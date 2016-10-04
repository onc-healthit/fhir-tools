(function(){
	var smokingstatussuccesscount = 0;
	var smokingtrigger =0;
	var smokingstatusByPatientCategory = "smokingstatusByPatientCategory";
	var smokingstatusCollapseByPatientCategory = "smokingstatusCollapseByPatientCategory";
	var smokingstatusByPatientCode = "smokingstatusByPatientCode";
	var smokingstatusCollapseByPatientCode = "smokingstatusCollapseByPatientCode";
	var l = $( '#executebtn' ).ladda();
	smokingstatusByPatId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		obslaboratorysuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/Observation?patient="+patientid + "&category=social-history&_format=json";
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
	          	smokingstatussuccesscount++;
	          	$('.smokingstatusrunByPatcategory').html('Passed');
	          	if(smokingtrigger == 0){
	          		$('.smokingstatusrun').html('Passed');
	          	}
	          	$('.smokingstatuspass').html(smokingstatussuccesscount);
	          	smokingstatusByPatCode(strurl,data,smokingstatusByPatientCode,smokingstatusCollapseByPatientCode);
	          	renderresults(data,strurl,xhr,smokingstatusByPatientCategory,smokingstatusCollapseByPatientCategory,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	smokingtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.smokingstatusrunByPatcategory').html('Failed');
	        	$('.smokingstatusrun').html('Failed');
	        	$('.smokingstatusrun').parent().addClass('bg-danger');
	        	rendererror(e,smokingstatusByPatientCategory);
	   		}
		})
	}

	smokingstatusByPatCode = function(strurl,data,smokingstatusByPatientCode,smokingstatusCollapseByPatientCode){
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
		strurl = strurl + "/Observation?patient="+patientid+"&category=social-history&code="+codearr.join(',')+"&_format=json";
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
	          	smokingstatussuccesscount++;
	          	$('.smokingstatusrunByPatCode').html('Passed');
	          	if(smokingtrigger == 0){
	          		$('.smokingstatusrun').html('Passed');
	          	}
	          	$('.smokingstatuspass').html(smokingstatussuccesscount);
	          	renderresults(data,strurl,xhr,smokingstatusByPatientCode,smokingstatusCollapseByPatientCode,resType);
	        },
	        error:function(e){
	        	smokingtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.smokingstatusrunByPatCode').html('Failed');
	        	$('.smokingstatusrun').html('Failed');
	        	$('.smokingstatusrun').parent().addClass('bg-danger');
	        	rendererror(e,smokingstatusByPatientCode);
	   		}
		});
	}

}).call(this);	