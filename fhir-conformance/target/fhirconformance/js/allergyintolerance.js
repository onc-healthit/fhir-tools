(function(){
	var allergysuccesscount = 0;
	var allergytrigger = 0;
	var allergyByPatId = "allergyByPatId";
	var colallergyByPatId = "byPatId";
	var l = $( '#executebtn' ).ladda();
	allergyByPatientId = function(strurl){
		var patientid = localStorage.getItem("patientid");
		l.ladda('start');
		allergysuccesscount = 0;
		access_token = localStorage.getItem("access_token");
		strurl = strurl+ "/AllergyIntolerance?patient="+patientid + "&_format=json";
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
	          	allergysuccesscount++;
	          	$('.allergyrunByPatId').html('Passed');
	          	if(allergytrigger == 0){
	          		$('.allergyrun').html('Passed');
	          	}
	          	$('.allergypass').html(allergysuccesscount);
	          	renderresults(data,strurl,xhr,allergyByPatId,colallergyByPatId,resType);
	          	l.ladda('stop');
	        },
	        error:function(e){
	        	allergytrigger =1;
	        	l.ladda( 'stop' );
	        	$('.allergyrunByPatId').html('Failed');
	        	$('.allergyrun').html('Failed');
	        	$('.allergyrun').parent().addClass('bg-danger');
	        	rendererror(e,allergyByPatId);
	   		}
		})
	}
}).call(this);