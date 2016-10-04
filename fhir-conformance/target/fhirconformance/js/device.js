(function(){
	var devicesuccesscount = 0;
	var devicetrigger = 0;
	var deviceByPatientId = "deviceByPatientId";
	var deviceCollapseByPatientId = "deviceCollapseByPatientId";
	var l = $( '#executebtn' ).ladda();
	deviceByPatId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		devicesuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/Device?patient="+patientid + "&_format=json";
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
	          	devicesuccesscount++;
	          	$('.devicerunByPatId').html('Passed');
	          	if(devicetrigger == 0){
	          		$('.devicerun').html('Passed');
	          	}
	          	$('.devicepass').html(devicesuccesscount);
	          	renderresults(data,strurl,xhr,deviceByPatientId,deviceCollapseByPatientId,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	devicetrigger = 1;
	        	l.ladda( 'stop' );
	        	$('.devicerunByPatId').html('Failed');
	        	$('.devicerun').html('Failed');
	        	$('.devicerun').parent().addClass('bg-danger');
	        	rendererror(e,deviceByPatientId);
	   		}
		})
	}
}).call(this);