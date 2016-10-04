(function(){
	var goalsuccesscount = 0;
	var goaltrigger = 0;
	var goalByPatientId = "goalByPatientId";
	var goalCollapseByPatientId = "goalCollapseByPatientId";
	var l = $( '#executebtn' ).ladda();
	goalByPatId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		goalsuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/Goal?patient="+patientid + "&_format=json";
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
	          	goalsuccesscount++;
	          	$('.goalrunByPatId').html('Passed');
	          	if(goaltrigger == 0){
	          		$('.goalrun').html('Passed');
	          	}
	          	$('.goalpass').html(goalsuccesscount);
	          	renderresults(data,strurl,xhr,goalByPatientId,goalCollapseByPatientId,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	goaltrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.goalrunByPatId').html('Failed');
	        	$('.goalrun').html('Failed');
	        	$('.goalrun').parent().addClass('bg-danger');
	        	rendererror(e,goalByPatientId);
	   		}
		})
	}
}).call(this);