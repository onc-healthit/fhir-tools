(function(){
	var medStatementsuccesscount = 0;
	var medstatementtrigger = 0;
	var medStatementByPatientId = "medStatementByPatientId";
	var medStatementCollapseByPatientId = "medStatementCollapseByPatientId";
	var l = $( '#executebtn' ).ladda();
	medicationStatementByPatientId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		medStatementsuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/MedicationStatement?patient="+patientid + "&_format=json";
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
	          	medStatementsuccesscount++;
	          	$('.medStatementrunByPatId').html('Passed');
	          	if(medstatementtrigger == 0){
	          		$('.medStatementrun').html('Passed');
	          	}
	          	$('.medStatementpass').html(medStatementsuccesscount);
	          	renderresults(data,strurl,xhr,medStatementByPatientId,medStatementCollapseByPatientId,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	medstatementtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.medStatementrunByPatId').html('Failed');
	        	$('.medStatementrun').html('Failed');
	        	$('.medStatementrun').parent().addClass('bg-danger');
	        	rendererror(e,medStatementByPatientId);
	   		}
		})
	}
}).call(this);