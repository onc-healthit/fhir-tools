(function(){
	var patientid = localStorage.getItem("patientid");
	runtest =  function(){
		$('.removabletr').remove();
		$('.pass').html('');
		$('.run').html('');
		localStorage.setItem("execution",true);
		var checkedValues = $('.table input:checkbox:checked').map(function() {
		    return this.value;
		}).get();
		if(checkedValues.length <= 0){
			bootbox.alert("Please Select atleast one Resource");
			return;
		}
		/*if($('#selectall').prop("checked") == true){
			console.log(checkedValues.slice(1));
		}else{
			console.log(checkedValues);
		}*/
		for(var i=0; i<checkedValues.length; i++){
			var strurl = localStorage.getItem("strurl");
			if(checkedValues[i] == "Patient"){
				$('.patientpass').html('');
				$('.patientrun').html('Running...');
				$('.patientrun').parent().removeClass('bg-danger');
				patientById(strurl);
			}
			if(checkedValues[i] == "AllergyIntolerance"){
				$('.allergypass').html('');
				$('.allergyrun').html('Running...');
				$('.allergyrun').parent().removeClass('bg-danger');
				allergyByPatientId(strurl);
			}
			if(checkedValues[i] == "Condition"){
				$('.conditionpass').html('');
				$('.conditionrun').html('Running...');
				$('.conditionrun').parent().removeClass('bg-danger');
				conditionByPatient(strurl);
			}
			if(checkedValues[i] == "CarePlan"){
				$('.careteampass').html('');
				$('.careteamrun').html('Running...');
				$('.careteamrun').parent().removeClass('bg-danger');
				careteamByPatient(strurl);
			}
			if(checkedValues[i] == "MedicationStatement"){
				$('.medStatementpass').html('');
				$('.medStatementrun').html('Running...');
				$('.medStatementrun').parent().removeClass('bg-danger');
				medicationStatementByPatientId(strurl);
			}
			if(checkedValues[i] == "Device"){
				$('.devicepass').html('');
				$('.devicerun').html('Running...');
				$('.devicerun').parent().removeClass('bg-danger');
				deviceByPatId(strurl);
			}
			if(checkedValues[i] == "Goal"){
				$('.goalpass').html('');
				$('.goalrun').html('Running...');
				$('.goalrun').parent().removeClass('bg-danger');
				goalByPatId(strurl);
			}
			if(checkedValues[i] == "Immunization"){
				$('.immunizationpass').html('');
				$('.immunizationrun').html('Running...');
				$('.immunizationrun').parent().removeClass('bg-danger');
				immunizationByPatId(strurl);
			}
			if(checkedValues[i] == "DiagnosticReport"){
				$('.diagnosticreportpass').html('');
				$('.diagnosticreportrun').html('Running...');
				$('.diagnosticreportrun').parent().removeClass('bg-danger');
				diagnosticreportByPatId(strurl);
			}
			if(checkedValues[i] == "MedicationOrder"){
				$('.medOrderpass').html('');
				$('.medOrderrun').html('Running...');
				$('.medOrderrun').parent().removeClass('bg-danger');
				medicationOrderByPatientId(strurl);
			}
			if(checkedValues[i] == "Observation(Laboratory)"){
				$('.obslaboratorypass').html('');
				$('.obslaboratoryrun').html('Running...');
				$('.obslaboratoryrun').parent().removeClass('bg-danger');
				obslaboratoryByPatId(strurl);
			}
			if(checkedValues[i] == "Observation(Smoking Status)"){
				$('.smokingstatuspass').html('');
				$('.smokingstatusrun').html('Running...');
				$('.smokingstatusrun').parent().removeClass('bg-danger');
				smokingstatusByPatId(strurl);
			}
			if(checkedValues[i] == "Observation(Vital Signs)"){
				$('.vitalsignspass').html('');
				$('.vitalsignsrun').html('Running...');
				$('.vitalsignsrun').parent().removeClass('bg-danger');
				vitalsignsByPatId(strurl);
			}
		}
	}

	getoauthjson = function(strurl){
		var patientSuccessCount = 0;
    	access_token = localStorage.getItem("access_token");
      	$.ajax({
      		url:strurl,
        	type:"GET",
	        beforeSend: function (xhr) {
	            xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	          	console.log(data);
	          	console.log(status);
	          	console.log(xhr);
	          	patientSuccessCalls++;
	          	console.log(patientSuccessCount);
	        },
	        error:function(e){
	        	l.ladda( 'stop' );
	        	$('#resurl').html(strurl);
	          	$("#spinner").hide();
	          	if(e.status == '401'){
	          		localStorage.removeItem("access_token");
	          		$('#authbtn').toggleClass('btn-danger btn-success');
	          		$('#authsuccess').html('');
	          		var authfail = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>Unauthorized: Authentication token is expired.</strong></div>');
		        	$('#authsuccess').append(authfail);
	          	}else{
	          		$('#authsuccess').html('');
	          		var authfail = $('<div class="alert alert-danger" id="serverauthorized" style="text-align:center;margin-bottom:0px;padding:12px"><strong>'+e.responseJSON.resourceType+" - "+e.responseJSON.issue[0].diagnostics+'</strong></div>');
		        	$('#authsuccess').append(authfail);
	          	}
		        window.scrollTo(0, 0);
	          	/*if(e.responseText == ''){
					bootbox.alert("No Response from Server");
					return false;
				}
				var errordata = JSON.parse(e.responseText);
				//var errormessage = JSON.stringify(errordata.text.div);
				var errormessage = errordata.issue[0].details;
				bootbox.alert(errormessage);*/
	   		}
		})
   	}

   	getPatByID = function(strurl, resourcename){
   		$.ajax({
      		url:strurl+ "/" + resourcename + "/" +patientid + "?_format=json&_pretty=json",
        	type:"GET",
	        beforeSend: function (xhr) {
	            xhr.setRequestHeader ("Authorization", "Bearer "+access_token);
	            xhr.setRequestHeader("Content-Type","application/josn+fhir");
	        },
	        success:function(data,status,xhr){
	          	console.log(data);
	          	console.log(status);
	          	console.log(xhr);
	          	patientSuccessCalls++;
	          	console.log(patientSuccessCount);
	        },
	        error:function(e){
	        	
	   		}
		})
   	}

}).call(this);