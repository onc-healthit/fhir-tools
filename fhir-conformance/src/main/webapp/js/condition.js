(function(){
	var conditionSuccessCount = 0;
	var conditiontrigger = 0;
	var conditionByPatientId = "conditionByPatientId";
	var conditionCollapseByPatientId = "conditionCollapseByPatientId";
	var conditionByPatientandCategory = "conditionByPatientandCategory";
	var conditionCollapseByPatientandCategory = "conditionCollapseByPatientandCategory";
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
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
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
	          	ConditionByPatientCategory(strurl,data,conditionByPatientandCategory,conditionCollapseByPatientandCategory);
	          	conditionByPatientClinicalStatus(strurl,data,conditionByPatientandClinicalStatus,conditionCollapseByPatientandClinicalStatus);
	          	renderresults(data,strurl,xhr,conditionByPatientId,conditionCollapseByPatientId,resType);
	          	l.ladda( 'stop' );
	        },
	        error:function(e){
	        	conditiontrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.conditionrunByPatId').html('Failed');
	        	$('.conditionrun').html('Failed');
	        	$('.conditionpass').parent().addClass('bg-danger');
	        	rendererror(e,conditionByPatientId);
	   		}
		})
	}

	ConditionByPatientCategory = function(strurl,data,conditionByPatientandCategory,conditionCollapseByPatientandCategory){
		var category;
		if(data.entry){
	        category =  data.entry[0].resource.category.coding[0].code;
	    }else{
			category = data.category.coding[0].code;
	    }
		var patientid = localStorage.getItem("patientid");
		var strurl = localStorage.getItem("strurl");
		strurl = strurl + "/Condition?patient="+patientid+"&category="+category+"&_format=json";
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
	          	conditionSuccessCount++;
	          	$('.conditionrunByPatCategory').html('Passed');
	          	if(conditiontrigger == 0){
	          		$('.conditionrun').html('Passed');
	          	}
	          	$('.conditionpass').html(conditionSuccessCount);
	          	renderresults(data,strurl,xhr,conditionByPatientandCategory,conditionCollapseByPatientandCategory,resType);
	        },
	        error:function(e){
	        	conditiontrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.conditionrunByPatCategory').html('Failed');
	        	$('.conditionrun').html('Failed');
	        	$('.conditionpass').parent().addClass('bg-danger');
	        	rendererror(e,conditionByPatientandCategory);
	   		}
		});
	}

	conditionByPatientClinicalStatus = function (strurl,data,conditionByPatientandClinicalStatus,conditionCollapseByPatientandClinicalStatus){
		var status;
		if(data.entry){
	        status =  data.entry[0].resource.clinicalStatus;
	    }else{
			status = data.clinicalStatus;
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
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
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
	          	renderresults(data,strurl,xhr,conditionByPatientandClinicalStatus,conditionCollapseByPatientandClinicalStatus,resType);
	        },
	        error:function(e){
	        	conditiontrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.conditionrunByPatClinicalStatus').html('Failed');
	        	$('.conditionrun').html('Failed');
	        	$('.conditionpass').parent().addClass('bg-danger');
	        	rendererror(e,conditionByPatientandClinicalStatus);
	   		}
		});
	}
}).call(this);