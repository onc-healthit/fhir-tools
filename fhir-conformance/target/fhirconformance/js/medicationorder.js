(function(){
	var medOrdersuccesscount = 0;
	var medordertrigger = 0;
	var medOrderByPatientId = "medOrderByPatientId";
	var medOrderCollapseByPatientId = "medOrderCollapseByPatientId";
	var l = $( '#executebtn' ).ladda();
	medicationOrderByPatientId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		medOrdersuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/MedicationOrder?patient="+patientid + "&_format=json";
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
	          	medOrdersuccesscount++;
	          	$('.medOrderrunByPatId').html('Passed');
	          	if(medordertrigger == 0){
	          		$('.medOrderrun').html('Passed');
	          	}
	          	$('.medOrderpass').html(medOrdersuccesscount);
	          	renderresults(data,strurl,xhr,medOrderByPatientId,medOrderCollapseByPatientId,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	medordertrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.medOrderrunByPatId').html('Failed');
	        	$('.medOrderrun').html('Failed');
	        	$('.medOrderrun').parent().addClass('bg-danger');
	        	rendererror(e,medOrderByPatientId);
	   		}
		})
	}
}).call(this);