(function(){
	var immunizationsuccesscount = 0;
	var immunizationtrigger = 0;
	var immunizationByPatientId = "immunizationByPatientId";
	var immunizationCollapseByPatientId = "immunizationCollapseByPatientId";
	var l = $( '#executebtn' ).ladda();
	immunizationByPatId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		immunizationsuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/Immunization?patient="+patientid + "&_format=json";
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
	          	immunizationsuccesscount++;
	          	$('.immunizationrunByPatId').html('Passed');
	          	if(immunizationtrigger == 0){
	          		$('.immunizationrun').html('Passed');
	          	}
	          	$('.immunizationpass').html(immunizationsuccesscount);
	          	renderresults(data,strurl,xhr,immunizationByPatientId,immunizationCollapseByPatientId,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	immunizationtrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.immunizationrunByPatId').html('Failed');
	        	$('.immunizationrun').html('Failed');
	        	$('.immunizationrun').parent().addClass('bg-danger');
	        	rendererror(e,immunizationByPatientId);
	   		}
		})
	}
}).call(this);