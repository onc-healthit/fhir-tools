(function(){
	var careteamSuccessCount = 0;
	var careteamtrigger = 0;
	var careteamByPatientId = "careteamByPatientId";
	var careteamCollapseByPatientId = "careteamCollapseByPatientId";
	var careteamByPatCategoryStatus = "careteamByPatCategoryStatus";
	var careteamCollapseByPatientCategoryStatus = "careteamCollapseByPatientCategoryStatus";
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
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
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
	          	careteamByPatientCategoryStatus(strurl,data,careteamByPatCategoryStatus,careteamCollapseByPatientCategoryStatus);
	          	renderresults(data,strurl,xhr,careteamByPatientId,careteamCollapseByPatientId,resType);
	          	l.ladda( 'stop' );
	        },
	        error:function(e){
	        	careteamtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.careteamrunByPatId').html('Failed');
	        	$('.careteamrun').html('Failed');
	        	$('.careteampass').parent().addClass('bg-danger');
	        	rendererror(e,careteamByPatientId);
	   		}
		})
	}

	careteamByPatientCategoryStatus = function(strurl,data,careteamByPatCategoryStatus,careteamCollapseByPatientCategoryStatus){
		var category,status;
		if(data.entry){
	        category =  data.entry[0].resource.category[0].coding[0].code;
	        status = data.entry[0].resource.status;
	    }else{
			category = data.category[0].coding[0].code;
			status = data.status;
	    }
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/CarePlan?patient="+patientid+"&category="+category+"&status="+status+"&_format=json";
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
	          	careteamSuccessCount++;
	          	$('.careteamrunByPatCategoryStatus').html('Passed');
	          	if(careteamtrigger == 0){
	          		$('.careteamrun').html('Passed');
	          	}
	          	$('.careteampass').html(careteamSuccessCount);
	          	renderresults(data,strurl,xhr,careteamByPatCategoryStatus,careteamCollapseByPatientCategoryStatus,resType);
	        },
	        error:function(e){
	        	careteamtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.careteamrunByPatCategoryStatus').html('Failed');
	        	$('.careteamrun').html('Failed');
	        	$('.careteampass').parent().addClass('bg-danger');
	        	rendererror(e,careteamByPatCategoryStatus);
	   		}
		});
	}
}).call(this);